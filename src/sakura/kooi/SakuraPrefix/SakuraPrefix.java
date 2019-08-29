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
package sakura.kooi.SakuraPrefix;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import lombok.Getter;
import sakura.kooi.SakuraPrefix.hooks.NametagHook;
import sakura.kooi.SakuraPrefix.hooks.NickHook;
import sakura.kooi.SakuraPrefix.hooks.SkinHook;
import sakura.kooi.SakuraPrefix.hooks.VaultHook;
import sakura.kooi.SakuraUtils.plugin.SakuraUtils;
import sakura.kooi.Utils.Bukkit.YamlConfig;
import sakura.kooi.Utils.exception.ExceptionUtils;

public class SakuraPrefix extends JavaPlugin implements Listener {
	@Getter private static SakuraPrefix instance;
	private static CommandSender logger;
	private String mysqlServer;
	private String mysqlPort;
	private String mysqlDatabase;
	private String mysqlUser;
	private String mysqlPassword;
	@Getter private boolean isNameTagEnabled;
	@Getter private boolean isOverwriteNameTag;
	@Getter private boolean isNickEnabled;

	@Getter private static PrefixManager prefixManager;
	@Getter private VaultHook vaultHook;
	@Getter private NametagHook nametagHook;
	@Getter private NickHook nickHook;
	@Getter private SkinHook skinHook;

	@Override
	public void onEnable() {
		instance = this;
		logger = Bukkit.getConsoleSender();
		SakuraPrefix.sendConsoleMessage("&b正在加载 &dSakuraPrefix &bv"+getDescription().getVersion());
		SakuraUtils.requireVersion(instance, 35);
		try {
			SakuraPrefix.sendConsoleMessage("&e正在加载 &9-> &6配置文件");
			loadConfig();
		} catch (final IOException e1) {
			SakuraPrefix.sendConsoleMessage("&c错误: 读取配置文件失败.");
			setEnabled(false);
			return;
		}
		SakuraPrefix.sendConsoleMessage("&e正在加载 &9-> &6挂钩Vault");
		vaultHook = new VaultHook();
		SakuraPrefix.sendConsoleMessage("&e正在加载 &9-> &6Nametag支持");
		nametagHook = new NametagHook();
		SakuraPrefix.sendConsoleMessage("&e正在加载 &9-> &6Nick支持");
		nickHook = new NickHook();
		if (isNameTagEnabled && Bukkit.getPluginManager().isPluginEnabled("NametagEdit")) {
			Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("NametagEdit"));
			sendConsoleMessage("&c注意: 您开启了NameTag支持, 此功能与NametagEdit插件不兼容, 已自动禁用NametagEdit");
		}
		skinHook = new SkinHook();
		SakuraPrefix.sendConsoleMessage("&e正在连接数据库...");
		prefixManager = new PrefixManager();
		try {
			prefixManager.connect(mysqlServer,mysqlPort,mysqlDatabase,mysqlUser,mysqlPassword);
		} catch (final SQLException e) {
			ExceptionUtils.printStacktrace(e);
			SakuraPrefix.sendConsoleMessage("&c数据库连接失败, 请检查配置文件.");
			setEnabled(false);
			return;
		}
		SakuraPrefix.sendConsoleMessage("&a数据库连接成功~");
		getCommand("sakuraprefix").setExecutor(new PrefixCommand());
		getCommand("nick").setExecutor(new NickCommand());
		Bukkit.getPluginManager().registerEvents(this, this);
		try {
			for (final Player player : Bukkit.getOnlinePlayers()) {
				final PrefixData data = prefixManager.getCachedPrefixData(player.getName());
				vaultHook.update(data);
				nametagHook.update(data);
				new BukkitRunnable() {
					@Override
					public void run() {
						nickHook.update(data);
						skinHook.update(data);
					}
				}.runTaskLaterAsynchronously(SakuraPrefix.getInstance(), 10);
			}
		} catch (final SQLException e) {
			ExceptionUtils.printStacktrace(e);
			SakuraPrefix.sendConsoleMessage("&c数据库请求出错.");
		}
		SakuraPrefix.sendConsoleMessage("&b欢迎使用 &dSakuraPrefix &bv"+getDescription().getVersion()+"~ By.Ldcr");
		SakuraUtils.sendPrivateMessage("§d§lSakuraPrefix");
	}

	@Override
	public void onDisable() {
		if (prefixManager!=null) {
			prefixManager.disconnect();
		}
		if (isNameTagEnabled) {
			final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
			for (final Team team : scoreboard.getTeams()) {
				if (team.getName().startsWith("LuP_")) {
					team.unregister();
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onJoin(final PlayerJoinEvent e) {
		e.setJoinMessage(null);
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					final PrefixData data = prefixManager.getPlayerPrefix(e.getPlayer().getName());
					vaultHook.update(data);
					nametagHook.update(data);
					new BukkitRunnable() {
						@Override
						public void run() {
							nickHook.update(data);
							skinHook.update(data);
						}
					}.runTaskLaterAsynchronously(SakuraPrefix.getInstance(), 10);
					Bukkit.broadcastMessage("§7[§a§l+§7] §7"+e.getPlayer().getDisplayName());
				} catch (final SQLException e) {
					SakuraPrefix.sendConsoleMessage("&c数据库请求出错.");
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(this);
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onLeave(final PlayerQuitEvent e) {
		e.setQuitMessage("§7[§c§l-§7] §7"+e.getPlayer().getDisplayName());
	}

	public void loadConfig() throws IOException {
		final File configFile = new File(getDataFolder(),"config.yml");
		if (!configFile.exists()) {
			saveDefaultConfig();
		}
		final YamlConfig config = YamlConfig.loadYaml(configFile);
		mysqlServer = config.getString("mysql.server","localhost");
		mysqlPort = config.getString("mysql.port","3306");
		mysqlDatabase = config.getString("mysql.database","sakuraprefix");
		mysqlUser = config.getString("mysql.user","root");
		mysqlPassword = config.getString("mysql.password","password");
		isNameTagEnabled = config.getBoolean("isNameTagEnabled", false);
		isOverwriteNameTag = config.getBoolean("OverwriteNameTag", false);
		isNickEnabled = config.getBoolean("isNickEnabled", false);
	}
	public static void sendConsoleMessage(final String... messages) {
		for (final String str : messages) {
			logger.sendMessage("§d§lSakuraPrefix §7>> §e"+str.replace('&', '§').replace("§§", "&"));
		}
	}
}
