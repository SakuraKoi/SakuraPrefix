/**
 * @Project SakuraPrefix
 *
 * Copyright 2018 Ldcr. All right reserved.
 *
 * This is a private project. Distribution is not allowed.
 * You needs ask Ldcr for the permission to using it on your server.
 * 
 * @Author Ldcr (ldcr993519867@gmail.com)
 */
package ldcr.SakuraPrefix.task.update;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import ldcr.SakuraPrefix.PrefixData;
import ldcr.SakuraPrefix.SakuraPrefix;
import ldcr.Utils.exception.ExceptionUtils;

public class PlayerCustomPrefixTask	implements Runnable {
	private final CommandSender callback;
	private final String player;
	private final boolean mode;
	private final String value;
	public PlayerCustomPrefixTask(final CommandSender callback, final String player,final String mode,final String value) {
		this.callback = callback;
		this.player = player;
		this.mode = mode.equalsIgnoreCase("prefix");
		this.value = value;
		Bukkit.getScheduler().runTaskAsynchronously(SakuraPrefix.getInstance(), this);
	}
	@Override
	public void run() {
		if (!"ldcr".equalsIgnoreCase(callback.getName()) && "ldcr".equalsIgnoreCase(player)) {
			callback.sendMessage("§d§lSakuraPrefix §7>> §cemmmm... 你打算干什么? (笑");
			return;
		}
		String playerName = player;
		@SuppressWarnings("deprecation")
		final OfflinePlayer offp = Bukkit.getOfflinePlayer(player);
		if (offp!=null) {
			playerName = offp.getName();
		}
		try {
			final PrefixData data = SakuraPrefix.getPrefixManager().getPlayerPrefix(player);
			if (data.isLocked()) {
				callback.sendMessage("§d§lSakuraPrefix §7>> §c此帐号已被锁定, 无法进行更改称号操作.");
				return;
			}
			if (mode) {
				data.setPrefix(value);
			} else {
				data.setSuffix(value);
			}
			SakuraPrefix.getPrefixManager().updatePlayerPrefix(player, data);
			final String name = data.getNick().isEmpty() ? playerName : data.getNick();
			callback.sendMessage("§d§lSakuraPrefix §7>> §a称号已更新: §r"+data.getPrefix()+name+data.getSuffix());
		} catch (final SQLException e) {
			callback.sendMessage("§d§lSakuraPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacktrace(e);
		}
	}
}
