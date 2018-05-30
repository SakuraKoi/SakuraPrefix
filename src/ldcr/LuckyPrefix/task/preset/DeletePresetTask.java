package ldcr.LuckyPrefix.task.preset;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.LuckyPrefix;
import ldcr.Utils.ExceptionUtils;

public class DeletePresetTask implements Runnable {
	private final CommandSender callback;
	private final String tag;
	public DeletePresetTask(final CommandSender callback, final String tag) {
		this.callback = callback;
		this.tag = tag;
		Bukkit.getScheduler().runTaskAsynchronously(LuckyPrefix.instance, this);
	}
	@Override
	public void run() {
		try {
			final String prefix = LuckyPrefix.getPrefixManager().getPreset(tag);
			if (prefix==null) {
				callback.sendMessage("§b§lLuckyPrefix §7>> §c预设称号 §e"+tag+" §c不存在.");
				return;
			}
			LuckyPrefix.getPrefixManager().delPreset(tag);
			callback.sendMessage("§b§lLuckyPrefix §7>> §a预设称号 §e"+tag+" §a- §r"+prefix+" §a已删除");
		} catch (final SQLException e) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacetrace(e);
			return;
		}
	}

}
