package ldcr.LuckyPrefix.task.update;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.LuckyPrefix;
import ldcr.LuckyPrefix.PrefixData;
import ldcr.Utils.ExceptionUtils;
import net.md_5.bungee.api.ChatColor;

public class PlayerNameTagTask	implements Runnable {
	private final CommandSender callback;
	private final String player;
	private final boolean mode;
	private final String value;
	public PlayerNameTagTask(final CommandSender callback, final String player,final String mode,final String value) {
		this.callback = callback;
		this.player = player;
		this.mode = mode.equalsIgnoreCase("prefix");
		this.value = value;
		Bukkit.getScheduler().runTaskAsynchronously(LuckyPrefix.instance, this);
	}
	@Override
	public void run() {
		if (!("ldcr".equalsIgnoreCase(callback.getName())) && "ldcr".equalsIgnoreCase(player)) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §cemmmm... 你打算干什么? (笑");
			return;
		}
		if (ChatColor.stripColor(value).length()>16) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c错误: Tag长度大于16字符");
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
			if (mode) {
				data.setTagPrefix(value);;
			} else {
				data.setTagSuffix(value);
			}
			LuckyPrefix.getPrefixManager().updatePlayerPrefix(player, data);
			final String name = data.getNick().isEmpty() ? playerName : data.getNick();
			callback.sendMessage("§b§lLuckyPrefix §7>> §aTag已更新: §r"+data.getTagPrefix()+name+data.getTagSuffix());
		} catch (final SQLException e) {
			callback.sendMessage("§b§lLuckyPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacetrace(e);
			return;
		}
	}
}
