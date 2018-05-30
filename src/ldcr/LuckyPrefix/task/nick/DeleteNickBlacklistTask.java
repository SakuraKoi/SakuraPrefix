package ldcr.LuckyPrefix.task.nick;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.LuckyPrefix;
import ldcr.LuckyPrefix.PrefixData;
import ldcr.Utils.ExceptionUtils;

public class DeleteNickBlacklistTask implements Runnable {
	private final CommandSender callback;
	private final String value;
	public DeleteNickBlacklistTask(final CommandSender callback, final String value) {
		this.callback = callback;
		this.value = PrefixData.processColor(value);
		Bukkit.getScheduler().runTaskAsynchronously(LuckyPrefix.instance, this);
	}
	@Override
	public void run() {
		try {
			if (LuckyPrefix.getPrefixManager().delNickBlacklist(value)) {
				callback.sendMessage("§b§lLuckyPrefix §7>> §aNick黑名单已删除: §r"+value);
			} else {
				callback.sendMessage("§b§lLuckyPrefix §7>> §cNick黑名单不存在: §r"+value);
			}
		} catch (final SQLException e) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacetrace(e);
			return;
		}
	}

}
