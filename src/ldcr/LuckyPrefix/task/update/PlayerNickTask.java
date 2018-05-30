package ldcr.LuckyPrefix.task.update;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.LuckyPrefix;
import ldcr.LuckyPrefix.PrefixData;
import ldcr.Utils.ExceptionUtils;

public class PlayerNickTask	implements Runnable {
	private final CommandSender callback;
	private final String player;
	private String nick;
	public PlayerNickTask(final CommandSender callback, final String player,final String nick) {
		this.callback = callback;
		this.player = player;
		this.nick = nick;
		System.out.println(nick);
		Bukkit.getScheduler().runTaskAsynchronously(LuckyPrefix.instance, this);
	}
	@Override
	public void run() {
		String playerName = player;
		@SuppressWarnings("deprecation")
		final OfflinePlayer offp = Bukkit.getOfflinePlayer(player);
		if (offp!=null) {
			playerName = offp.getName();
		}
		final boolean noBypass = !callback.hasPermission("luckyprefix.nick.force");
		try {
			if (!nick.isEmpty()) {
				if (noBypass) {
					if (LuckyPrefix.getPrefixManager().isNickInUse(nick)) {
						callback.sendMessage("§b§lLuckyPrefix §7>> §cNick §f"+nick+"§c 已被其他人使用");
						return;
					}
					if (LuckyPrefix.getPrefixManager().isNickBlacklisted(nick)) {
						callback.sendMessage("§b§lLuckyPrefix §7>> §cNick §f"+nick+"§c 已被禁止使用");
						return;
					}
				}
				if (nick.toLowerCase().contains("ldcr".toLowerCase())) {
					nick="我是Ldcr的RBQ";
				}
				if (nick.length()>14) {
					callback.sendMessage("§b§lLuckyPrefix §7>> §cNick §f"+nick+"§c 过长.");
					return;
				}
			}
			final PrefixData data = LuckyPrefix.getPrefixManager().getPlayerPrefix(player);
			if (!data.getNick().isEmpty()) {
				if (noBypass && data.getNick().equals("我是Ldcr的RBQ")) {
					callback.sendMessage("§b§lLuckyPrefix §7>> §c挺好看的, 别改了 (笑");
					return;
				}
				LuckyPrefix.getPrefixManager().releaseNick(player);
			}
			data.setNick(nick);
			LuckyPrefix.getPrefixManager().updatePlayerNick(player, data);
			final String name = nick.isEmpty() ? playerName : nick;
			callback.sendMessage("§b§lLuckyPrefix §7>> §aNick已更新: §r"+data.getPrefix()+name+data.getSuffix());
		} catch (final SQLException e) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacetrace(e);
			return;
		}
	}
}
