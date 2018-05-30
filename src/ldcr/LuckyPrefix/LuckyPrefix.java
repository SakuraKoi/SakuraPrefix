package ldcr.LuckyPrefix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
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

import ldcr.LuckyPrefix.hooks.NametagHook;
import ldcr.LuckyPrefix.hooks.NickHook;
import ldcr.LuckyPrefix.hooks.VaultHook;
import ldcr.Utils.ExceptionUtils;
import lombok.Getter;

public class LuckyPrefix extends JavaPlugin implements Listener {
	public static LuckyPrefix instance;
	private static CommandSender logger;
	private String mysqlServer;
	private String mysqlPort;
	private String mysqlDatabase;
	private String mysqlUser;
	private String mysqlPassword;
	public boolean enableNameTag;
	public boolean overwriteNameTag;
	public boolean enableNick;
	@Getter private static PrefixManager prefixManager;
	public VaultHook vaultHook;
	public NametagHook nametagHook;
	public NickHook nickHook;

	@Override
	public void onEnable() {
		instance = this;
		logger = Bukkit.getConsoleSender();
		try {
			loadConfig();
		} catch (final IOException e1) {
			LuckyPrefix.sendConsoleMessage("&c读取配置文件失败, 无法连接到数据库.");
			setEnabled(false);
			return;
		}
		vaultHook = new VaultHook();
		nametagHook = new NametagHook();
		sendConsoleMessage("&aNameTag支持已"+(enableNameTag? "开启" : "关闭"));
		nickHook = new NickHook();
		sendConsoleMessage("&aNick支持已"+(enableNick? "开启" : "关闭"));
		if (enableNameTag) {
			if (Bukkit.getPluginManager().isPluginEnabled("NametagEdit")) {
				Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("NametagEdit"));
				sendConsoleMessage("&c注意: 您开启了NameTag支持, 此功能与NametagEdit插件不兼容, 已自动禁用NametagEdit");
			}
		}
		sendConsoleMessage("&a覆写NameTag已"+(overwriteNameTag? "开启" : "关闭"));
		prefixManager = new PrefixManager();
		try {
			prefixManager.connect(mysqlServer,mysqlPort,mysqlDatabase,mysqlUser,mysqlPassword);
		} catch (final SQLException e) {
			ExceptionUtils.printStacetrace(e);
			LuckyPrefix.sendConsoleMessage("&c数据库连接失败, 请检查配置文件.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		getCommand("luckyprefix").setExecutor(new PrefixCommand());
		getCommand("nick").setExecutor(new NickCommand());
		Bukkit.getPluginManager().registerEvents(this, this);
		try {
			for (final Player player : Bukkit.getOnlinePlayers()) {
				final PrefixData data = prefixManager.getCachedPrefixData(player.getName());
				vaultHook.update(data);
				nametagHook.update(data);
			}
		} catch (final SQLException e) {
			LuckyPrefix.sendConsoleMessage("&c数据库请求出错.");
			e.printStackTrace();
		}
	}
	@Override
	public void onDisable() {
		prefixManager.disconnect();
		if (enableNameTag) {
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
						}
					}.runTaskLaterAsynchronously(LuckyPrefix.instance, 10);
					Bukkit.broadcastMessage("§7[§a§l+§7] §7"+e.getPlayer().getDisplayName());
				} catch (final SQLException e) {
					LuckyPrefix.sendConsoleMessage("&c数据库请求出错.");
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(this);
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onLeave(final PlayerQuitEvent e) {
		e.setQuitMessage("§7[§c§l-§7] §7"+e.getPlayer().getDisplayName());
	}
	public void loadConfig() throws FileNotFoundException {
		final File configFile = new File(getDataFolder(),"config.yml");
		if (!configFile.exists()) {
			saveDefaultConfig();
		}
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)));
		mysqlServer = config.getString("mysql.server","localhost");
		mysqlPort = config.getString("mysql.port","3306");
		mysqlDatabase = config.getString("mysql.database","luckyprefix");
		mysqlUser = config.getString("mysql.user","root");
		mysqlPassword = config.getString("mysql.password","password");
		enableNameTag = config.getBoolean("enableNameTag", false);
		overwriteNameTag = config.getBoolean("OverwriteNameTag", false);
		enableNick = config.getBoolean("enableNick", false);
	}
	public static void sendConsoleMessage(final String... messages) {
		for (final String str : messages) {
			logger.sendMessage("§b§lLuckyPrefix §7>> §e"+str.replace('&', '§').replace("§§", "&"));
		}
	}
}
