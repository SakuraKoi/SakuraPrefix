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

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import sakura.kooi.SakuraPrefix.PrefixData;
import sakura.kooi.SakuraPrefix.SakuraPrefix;
import sakura.kooi.SakuraUtils.plugin.SakuraUtils;
import sakura.kooi.Utils.exception.ExceptionUtils;

public class NickHook {
	public class NickPacketListener extends PacketAdapter {
		public NickPacketListener() {
			super(SakuraPrefix.getInstance(), PacketType.Play.Server.PLAYER_INFO, PacketType.Play.Server.TAB_COMPLETE, PacketType.Play.Server.SCOREBOARD_TEAM);
		}
		@Override
		public void onPacketSending(final PacketEvent e) {
			if (e.isCancelled()) return;
			final boolean hasBypass = e.getPlayer().hasPermission("sakuraprefix.nick.bypass");
			final PacketContainer packet = e.getPacket();
			if (e.getPacketType()==PacketType.Play.Server.PLAYER_INFO) {
				final List<PlayerInfoData> data = packet.getPlayerInfoDataLists().read(0);
				if (data==null) return;
				final ArrayList<PlayerInfoData> profiles = new ArrayList<>();
				try {
					for (final PlayerInfoData info : data) {
						final WrappedGameProfile profile = info.getProfile();
						final PrefixData nickData = SakuraPrefix.getPrefixManager().getCachedPrefixData(profile.getName());
						final WrappedGameProfile newProfile;
						if (nickData.getNick().isEmpty()) {
							newProfile = profile;
						} else {
							String nick = nickData.getNick();
							if (hasBypass) {
								nick = "§n"+nick;
							}
							newProfile = profile.withName(nick);
						}
						final PlayerInfoData newData = new PlayerInfoData(newProfile, info.getLatency(), info.getGameMode(), info.getDisplayName());
						profiles.add(newData);
					}
					packet.getPlayerInfoDataLists().write(0, profiles);
				} catch (final SQLException e1) {
					SakuraPrefix.sendConsoleMessage("&c数据库请求出错.");
					ExceptionUtils.printStacktrace(e1);
				}
			} else if (e.getPacketType()==PacketType.Play.Server.TAB_COMPLETE) {
				if (hasBypass) return;
				final String[] matches = packet.getStringArrays().read(0);
				PrefixData data;
				try {
					for (int i = 0;i<matches.length;i++) {
						if (Bukkit.getPlayer(matches[i])!=null) {
							data = SakuraPrefix.getPrefixManager().getCachedPrefixData(matches[i]);
							if (data.getNick().isEmpty()) {
								continue;
							}
							matches[i] = data.getNick();
						}
					}
					packet.getStringArrays().write(0, matches);
				} catch (final SQLException e1) {
					SakuraPrefix.sendConsoleMessage("&c数据库请求出错.");
					ExceptionUtils.printStacktrace(e1);
				}
			} else if (e.getPacketType()==PacketType.Play.Server.SCOREBOARD_TEAM) {
				@SuppressWarnings("unchecked")
				final Collection<String> players = packet.getSpecificModifier(Collection.class).read(0);
				try {
					final ArrayList<String> result = new ArrayList<>();
					for (final String player : players) {
						final PrefixData nickData = SakuraPrefix.getPrefixManager().getCachedPrefixData(player);
						String nick;
						if (nickData.getNick().isEmpty()) {
							nick = player;
						} else {
							nick = nickData.getNick();
							if (hasBypass) {
								nick = "§n"+nick;
							}
						}
						result.add(nick);
					}
					packet.getSpecificModifier(Collection.class).write(0, result);
				} catch (final SQLException e1) {
					SakuraPrefix.sendConsoleMessage("&c数据库请求出错.");
					ExceptionUtils.printStacktrace(e1);
				}
			}
		}
	}
	private boolean hooked;
	private ProtocolManager protocolManager;
	public NickHook() {
		hook();
	}
	private void hook() {
		if (SakuraPrefix.getInstance().isNickEnabled()) {
			if (!SakuraUtils.hasProtocolLib()) {
				SakuraPrefix.sendConsoleMessage("&c错误: 加载Nick支持失败, 请检查是否安装ProtocolLib");
				hooked = false;
				return;
			}
			protocolManager = SakuraUtils.getProtocolManager();
			protocolManager.addPacketListener(new NickPacketListener());
			hooked = true;
			SakuraPrefix.sendConsoleMessage("&aNick支持已加载");
			return;
		}
		SakuraPrefix.sendConsoleMessage("&eNick支持已在配置文件中关闭");
		hooked = false;
	}
	@SuppressWarnings("deprecation")
	public void update(final PrefixData data) {
		if (!hooked) return;
		final OfflinePlayer offp = Bukkit.getOfflinePlayer(data.getPlayer());
		if (offp==null) return;
		if (offp.isOnline()) {
			final Player player = offp.getPlayer();
			final String nick = data.getNick().isEmpty() ? player.getName() : data.getNick();
			sendRemovePlayerPacket(player, nick);
			new BukkitRunnable() {
				@Override
				public void run() {
					sendRespawnPacket(player, nick);
					player.setAllowFlight(player.getAllowFlight());
					player.setFlying(player.isFlying());
					player.teleport(player.getLocation());
					player.updateInventory();
					player.setLevel(player.getLevel());
					player.setExp(player.getExp());
					player.setMaxHealth(player.getMaxHealth());
					player.setHealth(player.getHealth());
					sendAddPlayerPacket(player, nick);
				}
			}.runTaskLater(SakuraPrefix.getInstance(), 1);

			final ArrayList<Player> hidden = new ArrayList<>();
			new BukkitRunnable() {
				@Override
				public void run() {
					for (final Player player1 : Bukkit.getOnlinePlayers()) {
						if (player1.canSee(player)) {
							player1.hidePlayer(player);
							hidden.add(player1);
						}
					}
				}
			}.runTaskLater(SakuraPrefix.getInstance(), 10);
			new BukkitRunnable() {
				@Override
				public void run() {
					for (final Player player1 : hidden) {
						player1.showPlayer(player);
					}
				}
			}.runTaskLater(SakuraPrefix.getInstance(), 20);
		}
	}
	private void sendRemovePlayerPacket(final Player player, final String nick) {
		final PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
		final PlayerInfoAction action = EnumWrappers.PlayerInfoAction.UPDATE_LATENCY;
		packet.getPlayerInfoAction().write(0, action);
		final ArrayList<PlayerInfoData> list = new ArrayList<>();
		list.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(player), 0, NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromText(nick)));
		packet.getPlayerInfoDataLists().write(0, list);
		try {
			protocolManager.sendServerPacket(player, packet);
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	private void sendRespawnPacket(final Player player, final String nick) {
		final PacketContainer packet = new PacketContainer(PacketType.Play.Server.RESPAWN);
		packet.getIntegers().write(0, 0);
		packet.getDifficulties().write(0, EnumWrappers.Difficulty.valueOf(player.getWorld().getDifficulty().name()));
		packet.getGameModes().write(0, NativeGameMode.fromBukkit(player.getGameMode()));
		packet.getWorldTypeModifier().write(0, player.getWorld().getWorldType());
		try {
			protocolManager.sendServerPacket(player, packet);
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	private void sendAddPlayerPacket(final Player player, final String nick) {
		final PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
		final PlayerInfoAction action = EnumWrappers.PlayerInfoAction.ADD_PLAYER;
		packet.getPlayerInfoAction().write(0, action);
		final ArrayList<PlayerInfoData> list = new ArrayList<>();
		list.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(player), 0, NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromText(nick)));
		packet.getPlayerInfoDataLists().write(0, list);
		try {
			protocolManager.sendServerPacket(player, packet);
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
