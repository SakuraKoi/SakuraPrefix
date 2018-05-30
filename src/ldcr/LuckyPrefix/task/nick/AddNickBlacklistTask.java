package ldcr.LuckyPrefix.task.nick;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.LuckyPrefix;
import ldcr.LuckyPrefix.PrefixData;
import ldcr.Utils.ExceptionUtils;

public class AddNickBlacklistTask implements Runnable {
	private final CommandSender callback;
	private final String value;
	public AddNickBlacklistTask(final CommandSender callback, final String value) {
		this.callback = callback;
		this.value = PrefixData.processColor(value);
		Bukkit.getScheduler().runTaskAsynchronously(LuckyPrefix.instance, this);
	}
	@Override
	public void run() {
		try {
			if (LuckyPrefix.getPrefixManager().addNickBlacklist(value)) {
				callback.sendMessage("§b§lLuckyPrefix §7>> §aNick黑名单已添加: §r"+value);
			} else {
				callback.sendMessage("§b§lLuckyPrefix §7>> §cNick黑名单已存在: §r"+value);
			}
		} catch (final SQLException e) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacetrace(e);
			return;
		}
	}

}
