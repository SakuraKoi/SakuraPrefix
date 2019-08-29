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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import sakura.kooi.SakuraPrefix.PrefixData;
import sakura.kooi.SakuraPrefix.SakuraPrefix;
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
