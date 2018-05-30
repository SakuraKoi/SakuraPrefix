package ldcr.LuckyPrefix.task.preset;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.LuckyPrefix;
import ldcr.LuckyPrefix.PrefixData;
import ldcr.Utils.ExceptionUtils;

public class UpdatePresetTask implements Runnable {
	private final CommandSender callback;
	private final String tag;
	private final String value;
	public UpdatePresetTask(final CommandSender callback, final String tag, final String value) {
		this.callback = callback;
		this.tag = tag;
		this.value = PrefixData.processColor(value);
		Bukkit.getScheduler().runTaskAsynchronously(LuckyPrefix.instance, this);
	}
	@Override
	public void run() {
		try {
			LuckyPrefix.getPrefixManager().addPreset(tag, value);
		} catch (final SQLException e) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacetrace(e);
			return;
		}
		callback.sendMessage("§b§lLuckyPrefix §7>> §a预设称号 §e"+tag+" §a已更新: §r"+value);
	}

}
