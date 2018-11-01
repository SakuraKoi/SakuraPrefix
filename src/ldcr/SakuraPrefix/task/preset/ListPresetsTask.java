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
package ldcr.SakuraPrefix.task.preset;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import ldcr.SakuraPrefix.SakuraPrefix;
import ldcr.Utils.exception.ExceptionUtils;

public class ListPresetsTask	implements Runnable {
	private final CommandSender callback;

	public ListPresetsTask(final CommandSender callback) {
		this.callback = callback;
		Bukkit.getScheduler().runTaskAsynchronously(SakuraPrefix.getInstance(), this);
	}
	@Override
	public void run() {
		final Map<String, String> data;
		try {
			data = SakuraPrefix.getPrefixManager().getAllPresets();
		} catch (final SQLException e) {
			callback.sendMessage("§d§lSakuraPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacktrace(e);
			return;
		}
		if (data.isEmpty()) {
			callback.sendMessage("§d§lSakuraPrefix §7>> §c当前没有定义任何预设称号!");
			return;
		}
		final ArrayList<String> list = new ArrayList<>();
		list.add("§d§lSakuraPrefix §7>> §a共有 "+data.size()+" 条预设称号");
		for (final Entry<String, String> entry : data.entrySet()) {
			list.add("§d§lSakuraPrefix §7>> §d"+entry.getKey()+"§e = §r"+entry.getValue());
		}
		callback.sendMessage(list.toArray(new String[0]));
	}
}
