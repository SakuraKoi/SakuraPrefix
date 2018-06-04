package ldcr.LuckyPrefix.hooks;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import ldcr.LuckyPrefix.LuckyPrefix;
import ldcr.LuckyPrefix.PrefixData;
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
						//	SkinsRestorer.getInstance().getFactory().updateSkin(offp.getPlayer());
					}
				}.runTaskLater(LuckyPrefix.instance, 10);

			}
		}
	}
}
