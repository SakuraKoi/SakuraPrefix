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
package sakura.kooi.SakuraPrefix.task.nick;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import sakura.kooi.SakuraPrefix.SakuraPrefix;
import sakura.kooi.Utils.exception.ExceptionUtils;

public class ListNickBlacklistTask	implements Runnable {
	private final CommandSender callback;

	public ListNickBlacklistTask(final CommandSender callback) {
		this.callback = callback;
		Bukkit.getScheduler().runTaskAsynchronously(SakuraPrefix.getInstance(), this);
	}
	@Override
	public void run() {
		final List<String> data;
		try {
			data = SakuraPrefix.getPrefixManager().getNickBlacklists();
		} catch (final SQLException e) {
			callback.sendMessage("§d§lSakuraPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacktrace(e);
			return;
		}
		if (data.isEmpty()) {
			callback.sendMessage("§d§lSakuraPrefix §7>> §c当前没有定义任何Nick黑名单!");
			return;
		}
		final ArrayList<String> list = new ArrayList<>();
		list.add("§d§lSakuraPrefix §7>> §a共有 "+data.size()+" 条Nick黑名单");
		final StringBuilder builder = new StringBuilder();
		for (final String black : data) {
			builder.append("§e, §c");
			builder.append(black);
		}
		list.add("§d§lSakuraPrefix §7>> §c"+builder.substring(6, builder.length()));
		callback.sendMessage(list.toArray(new String[0]));
	}
}
