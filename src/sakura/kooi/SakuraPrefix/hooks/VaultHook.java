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
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import sakura.kooi.SakuraPrefix.PrefixData;
import sakura.kooi.SakuraPrefix.SakuraPrefix;

public class VaultHook {
	private Chat chat;
	public VaultHook() {
		hook();
	}
	private boolean hook() {
		if (chat==null) {
			final RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
			if (rsp==null) {
				SakuraPrefix.sendConsoleMessage("&c错误: 挂钩Vault聊天失败, 请检查是否安装Vault及受支持的聊天插件.");
				return true;
			}
			SakuraPrefix.sendConsoleMessage("&aVault挂钩完成, 聊天称号生效");
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
			player.getPlayer().setDisplayName(data.getPrefix()+(SakuraPrefix.getInstance().isNickEnabled() ? data.getNick().isEmpty() ? player.getName() : data.getNick() :player.getName())+data.getSuffix());
		}
	}
}
