package ldcr.LuckyPrefix.task;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.LuckyPrefix;
import ldcr.LuckyPrefix.PrefixData;
import ldcr.Utils.ExceptionUtils;

public class ShowPlayerPrefixTask	implements Runnable {
	private final CommandSender callback;
	private final String player;
	public ShowPlayerPrefixTask(final CommandSender callback, final String player) {
		this.callback = callback;
		this.player = player;
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
		try {
			final PrefixData data = LuckyPrefix.getPrefixManager().getPlayerPrefix(player);
			final String name = data.getNick().isEmpty() ? playerName : data.getNick();
			callback.sendMessage("§b§lLuckyPrefix §7>> §a玩家 "+playerName+" 的当前称号: §r"+data.getPrefix()+name+data.getSuffix());
		} catch (final SQLException e) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacetrace(e);
			return;
		}
	}
}
