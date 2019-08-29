/**
 * @Project SakuraPrefix
 *
 * Copyright 2018 SakuraKooi. All right reserved.
 *
 * This is a private project. Distribution is not allowed.
 * You needs ask SakuraKooi for the permission to using it on your server.
 * 
 * @Author SakuraKooi (ldcr993519867@gmail.com)
 */
package sakura.kooi.SakuraPrefix.task.update;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import sakura.kooi.SakuraPrefix.PrefixData;
import sakura.kooi.SakuraPrefix.SakuraPrefix;
import sakura.kooi.Utils.exception.ExceptionUtils;

public class PlayerNickTask	implements Runnable {
	private final CommandSender callback;
	private final String player;
	private String nick;
	public PlayerNickTask(final CommandSender callback, final String player,final String nick) {
		this.callback = callback;
		this.player = player;
		this.nick = nick;
		Bukkit.getScheduler().runTaskAsynchronously(SakuraPrefix.getInstance(), this);
	}
	@Override
	public void run() {
		if (!"sakurakooi".equalsIgnoreCase(callback.getName()) && "sakurakooi".equalsIgnoreCase(player)) {
			callback.sendMessage("§d§lSakuraPrefix §7>> §cemmmm... 你打算干什么? (笑");
			return;
		}
		String playerName = player;
		@SuppressWarnings("deprecation")
		final OfflinePlayer offp = Bukkit.getOfflinePlayer(player);
		if (offp!=null) {
			playerName = offp.getName();
		}
		final boolean noBypass = !callback.hasPermission("sakuraprefix.nick.force");
		boolean lockdown = false;
		try {
			if (!nick.isEmpty()) {
				if (noBypass) {
					if (SakuraPrefix.getPrefixManager().isNickInUse(nick)) {
						callback.sendMessage("§d§lSakuraPrefix §7>> §cNick §f"+nick+"§c 已被其他人使用");
						return;
					}
					if (SakuraPrefix.getPrefixManager().isNickBlacklisted(nick)) {
						callback.sendMessage("§d§lSakuraPrefix §7>> §cNick §f"+nick+"§c 已被禁止使用");
						return;
					}
					if (nick.toLowerCase().contains("sakurakooi".toLowerCase())) {
						nick="我是Sakura的RBQ";
						lockdown = true;
					}
				}
				if (nick.length()>14) {
					callback.sendMessage("§d§lSakuraPrefix §7>> §cNick §f"+nick+"§c 过长.");
					return;
				}
			}
			final PrefixData data = SakuraPrefix.getPrefixManager().getPlayerPrefix(player);
			if (data.isLocked()) {
				callback.sendMessage("§d§lSakuraPrefix §7>> §c此帐号已被锁定, 无法进行更改Nick操作.");
				return;
			}
			if (!data.getNick().isEmpty()) {
				SakuraPrefix.getPrefixManager().releaseNick(player);
			}
			data.setNick(nick);
			if (lockdown) {
				data.setLocked(true);
			}
			SakuraPrefix.getPrefixManager().updatePlayerNick(player, data);
			final String name = nick.isEmpty() ? playerName : nick;
			callback.sendMessage("§d§lSakuraPrefix §7>> §aNick已更新: §r"+data.getPrefix()+name+data.getSuffix());
		} catch (final SQLException e) {
			callback.sendMessage("§d§lSakuraPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacktrace(e);
		}
	}
}
