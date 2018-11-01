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
package ldcr.SakuraPrefix.hooks;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import ldcr.SakuraPrefix.PrefixData;
import ldcr.SakuraPrefix.SakuraPrefix;
import skinsrestorer.bukkit.SkinsRestorer;

public class SkinHook {
	private boolean hooked;
	public void hook() {
		if (Bukkit.getPluginManager().isPluginEnabled("SkinRestorer")) {
			hooked = true;
			return;
		}
		hooked = false;
	}
	@SuppressWarnings("deprecation")
	public void update(final PrefixData data) {
		if (hooked) {
			final OfflinePlayer offp = Bukkit.getOfflinePlayer(data.getPlayer());
			if (offp==null) return;
			if (offp.isOnline()) {
				new BukkitRunnable() {
					@Override
					public void run() {
						SkinsRestorer.getInstance().getFactory().updateSkin(offp.getPlayer());
					}
				}.runTaskLater(SakuraPrefix.getInstance(), 10);

			}
		}
	}
}
