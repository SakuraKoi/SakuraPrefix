package ldcr.LuckyPrefix.task.nick;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.LuckyPrefix;
import ldcr.Utils.ExceptionUtils;

public class ListNickBlacklistTask	implements Runnable {
	private final CommandSender callback;

	public ListNickBlacklistTask(final CommandSender callback) {
		this.callback = callback;
		Bukkit.getScheduler().runTaskAsynchronously(LuckyPrefix.instance, this);
	}
	@Override
	public void run() {
		final ArrayList<String> data;
		try {
			data = LuckyPrefix.getPrefixManager().getNickBlacklists();
		} catch (final SQLException e) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacetrace(e);
			return;
		}
		if (data.isEmpty()) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c当前没有定义任何Nick黑名单!");
			return;
		}
		final ArrayList<String> list = new ArrayList<String>();
		list.add("§b§lLuckyPrefix §7>> §a共有 "+data.size()+" 条Nick黑名单");
		final StringBuilder builder = new StringBuilder();
		for (final String black : data) {
			builder.append("§e, §c");
			builder.append(black);
		}
		list.add("§b§lLuckyPrefix §7>> §c"+builder.substring(6, builder.length()).toString());
		callback.sendMessage(list.toArray(new String[0]));
	}
}
