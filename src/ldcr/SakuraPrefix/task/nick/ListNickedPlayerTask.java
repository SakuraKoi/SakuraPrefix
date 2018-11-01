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
package ldcr.SakuraPrefix.task.nick;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import ldcr.SakuraPrefix.SakuraPrefix;
import ldcr.Utils.exception.ExceptionUtils;

public class ListNickedPlayerTask implements Runnable {
	private final CommandSender callback;
	private final boolean onlyOnline;
	public ListNickedPlayerTask(final CommandSender callback, final boolean onlyOnline) {
		this.callback = callback;
		this.onlyOnline = onlyOnline;
		Bukkit.getScheduler().runTaskAsynchronously(SakuraPrefix.getInstance(), this);
	}
	@Override
	@SuppressWarnings("deprecation")
	public void run() {
		try {
			final Map<String, String> nicked = SakuraPrefix.getPrefixManager().getAllNickedPlayer();
			final ArrayList<String> message = new ArrayList<>();
			int i = 0;
			StringBuilder builder;
			for (final Entry<String,String> entry : nicked.entrySet()) {
				builder = new StringBuilder("§d§lSakuraPrefix §7>> ");
				final OfflinePlayer offp = Bukkit.getOfflinePlayer(entry.getKey());
				if (offp==null || !offp.isOnline()) {
					if (onlyOnline) {
						continue;
					}
					builder.append("§7");
				} else {
					builder.append("§a");
				}
				builder.append(entry.getKey()+"§e [ §r"+entry.getValue()+" §e]");
				message.add(builder.toString());
				i++;
			}
			message.add(0, "§d§lSakuraPrefix §7>> §a共有 "+i+" 个玩家Nick了");
			callback.sendMessage(message.toArray(new String[0]));
		} catch (final SQLException e) {
			callback.sendMessage("§d§lSakuraPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacktrace(e);
		}
	}
}
