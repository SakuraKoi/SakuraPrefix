package ldcr.LuckyPrefix.hooks;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import ldcr.LuckyPrefix.LuckyPrefix;
import ldcr.LuckyPrefix.PrefixData;
import net.milkbowl.vault.chat.Chat;

public class VaultHook {
	private Chat chat;
	public VaultHook() {
		hook();
	}
	private boolean hook() {
		if (chat==null) {
			final RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
			if (rsp==null) {
				LuckyPrefix.sendConsoleMessage("&c未能挂钩至Vault, 称号系统将不工作!");
				return true;
			}
			LuckyPrefix.sendConsoleMessage("&aVault挂钩完成, 称号系统生效.");
			chat = rsp.getProvider();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public void update(final PrefixData data) {
		if (hook()) return;
		final OfflinePlayer player = Bukkit.getOfflinePlayer(data.getPlayer());
		if (player==null) return;
		if (player.isOnline()) {
			chat.setPlayerPrefix(player.getPlayer(), data.getPrefix());
			chat.setPlayerSuffix(player.getPlayer(), data.getSuffix());
			player.getPlayer().setDisplayName(data.getPrefix()+(LuckyPrefix.instance.enableNick ? (data.getNick().isEmpty()? player.getName() : data.getNick()) :player.getName())+data.getSuffix());
		}
	}
}
