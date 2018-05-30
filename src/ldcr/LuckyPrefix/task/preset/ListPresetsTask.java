package ldcr.LuckyPrefix.task.preset;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.LuckyPrefix;
import ldcr.Utils.ExceptionUtils;

public class ListPresetsTask	implements Runnable {
	private final CommandSender callback;

	public ListPresetsTask(final CommandSender callback) {
		this.callback = callback;
		Bukkit.getScheduler().runTaskAsynchronously(LuckyPrefix.instance, this);
	}
	@Override
	public void run() {
		final HashMap<String, String> data;
		try {
			data = LuckyPrefix.getPrefixManager().getAllPresets();
		} catch (final SQLException e) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacetrace(e);
			return;
		}
		if (data.isEmpty()) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c当前没有定义任何预设称号!");
			return;
		}
		final ArrayList<String> list = new ArrayList<String>();
		list.add("§b§lLuckyPrefix §7>> §a共有 "+data.size()+" 条预设称号");
		for (final Entry<String, String> entry : data.entrySet()) {
			list.add("§b§lLuckyPrefix §7>> §d"+entry.getKey()+"§e = §r"+entry.getValue());
		}
		callback.sendMessage(list.toArray(new String[0]));
	}
}
