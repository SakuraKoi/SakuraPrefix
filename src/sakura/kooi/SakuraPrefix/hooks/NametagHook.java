/**
 * @Project SakuraPrefix
 *
 * Copyright 2018 SakuraKooi. All right reserved.
 *
 * This is a private project. Distribution is not allowed.
 * You needs ask SakuraKooi for the permission to using it on your server.
 * 
 * @Author SakuraKooi (ldcr993519867@gmail.com)
 */
package sakura.kooi.SakuraPrefix.hooks;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import sakura.kooi.SakuraPrefix.PrefixData;
import sakura.kooi.SakuraPrefix.SakuraPrefix;

public class NametagHook implements Listener {
	private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
	public NametagHook() {
		if (SakuraPrefix.getInstance().isNameTagEnabled()) {
			Bukkit.getPluginManager().registerEvents(this, SakuraPrefix.getInstance());
			SakuraPrefix.sendConsoleMessage("&aNametag支持已激活");
		} else {
			SakuraPrefix.sendConsoleMessage("&eNametag支持已在配置文件中关闭");
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onJoin(final PlayerChangedWorldEvent e) {
		try {
			for (final Player p : Bukkit.getOnlinePlayers()) {
				this.update(p, SakuraPrefix.getPrefixManager().getCachedPrefixData(p.getName()));
			}
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}
	@EventHandler
	public void onLeave(final PlayerQuitEvent e) {
		final Team team = scoreboard.getTeam("LuP_"+e.getPlayer().getName().hashCode());
		if (team!=null) {
			team.unregister();
		}
	}

	@SuppressWarnings("deprecation")
	public void update(final PrefixData data) {
		if (SakuraPrefix.getInstance().isNameTagEnabled()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					final OfflinePlayer player = Bukkit.getOfflinePlayer(data.getPlayer());
					if (player==null) return;
					if (player.isOnline()) {
						update(player.getPlayer(),data);
					}
				}
			}.runTaskLater(SakuraPrefix.getInstance(), 5);
		}
	}
	private void update(final Player player, final PrefixData data) {
		String prefix;
		String suffix;
		if (SakuraPrefix.getInstance().isOverwriteNameTag()) {
			prefix = data.getTagPrefix().isEmpty() || data.getTagPrefix().charAt(0)=='§' && data.getTagPrefix().length()==2 ? data.getPrefix() : data.getTagPrefix();
			suffix = data.getTagSuffix().isEmpty() ? data.getSuffix() : data.getTagSuffix();
			if (prefix.length()<=16 && suffix.length()<=16) {
				setNameTag(player, prefix, suffix);
			} else {
				prefix = data.getTagPrefix();
				suffix = data.getTagSuffix();
				if (prefix.length()<=16 && suffix.length()<=16) {
					setNameTag(player, prefix, suffix);
				}
			}
		} else {
			prefix = data.getTagPrefix();
			suffix = data.getTagSuffix();
			if (prefix.length()<=16 && suffix.length()<=16) {
				setNameTag(player, prefix, suffix);
			}
		}
	}
	private void setNameTag(final Player player, final String prefix, final String suffix) {
		Team team = scoreboard.getTeam("LuP_"+player.getName().hashCode());
		if (team==null) {
			team = scoreboard.registerNewTeam("LuP_"+player.getName().hashCode());
		} else {
			team.removeEntry(player.getName());
		}
		team.setPrefix(prefix);
		team.setSuffix(suffix);
		team.addEntry(player.getName());
	}
}
