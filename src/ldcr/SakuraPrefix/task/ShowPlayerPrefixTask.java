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
package ldcr.SakuraPrefix.task;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import ldcr.SakuraPrefix.PrefixData;
import ldcr.SakuraPrefix.SakuraPrefix;
import ldcr.Utils.exception.ExceptionUtils;

public class ShowPlayerPrefixTask	implements Runnable {
	private final CommandSender callback;
	private final String player;
	public ShowPlayerPrefixTask(final CommandSender callback, final String player) {
		this.callback = callback;
		this.player = player;
		Bukkit.getScheduler().runTaskAsynchronously(SakuraPrefix.getInstance(), this);
	}
	@Override
	public void run() {
		String playerName = player;
		@SuppressWarnings("deprecation")
		final OfflinePlayer offp = Bukkit.getOfflinePlayer(player);
		if (offp!=null) {
			playerName = offp.getName();
		}
		try {
			final PrefixData data = SakuraPrefix.getPrefixManager().getPlayerPrefix(player);
			final String name = data.getNick().isEmpty() ? playerName : data.getNick();
			callback.sendMessage("§d§lSakuraPrefix §7>> §a玩家 "+playerName+" 的当前称号: §r"+data.getPrefix()+name+data.getSuffix());
		} catch (final SQLException e) {
			callback.sendMessage("§d§lSakuraPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacktrace(e);
		}
	}
}
