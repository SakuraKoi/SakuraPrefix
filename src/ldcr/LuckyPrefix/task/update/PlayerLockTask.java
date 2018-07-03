package ldcr.LuckyPrefix.task.update;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.LuckyPrefix;
import ldcr.LuckyPrefix.PrefixData;
import ldcr.Utils.ExceptionUtils;

public class PlayerLockTask	implements Runnable {
	private final CommandSender callback;
	private final String player;
	private final boolean locked;
	public PlayerLockTask(final CommandSender callback, final String player, final boolean locked) {
		this.callback = callback;
		this.player = player;
		this.locked = locked;
		Bukkit.getScheduler().runTaskAsynchronously(LuckyPrefix.instance, this);
	}
	@Override
	public void run() {
		if (!("ldcr".equalsIgnoreCase(callback.getName())) && "ldcr".equalsIgnoreCase(player)) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §cemmmm... 你打算干什么? (笑");
			return;
		}
		String playerName = player;
		@SuppressWarnings("deprecation")
		final OfflinePlayer offp = Bukkit.getOfflinePlayer(player);
		if (offp!=null) {
			playerName = offp.getName();
		}
		try {
			final PrefixData data = LuckyPrefix.getPrefixManager().getPlayerPrefix(player);
			data.setLocked(locked);
			LuckyPrefix.getPrefixManager().updatePlayerPrefix(player, data);
			final String name = data.getNick().isEmpty() ? playerName : data.getNick();
			callback.sendMessage("§b§lLuckyPrefix §7>> §a已"+(locked?"锁定":"解锁")+"玩家: §r"+data.getPrefix()+name+data.getSuffix());
		} catch (final SQLException e) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacetrace(e);
			return;
		}
	}
}
