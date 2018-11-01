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
package ldcr.SakuraPrefix;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import ldcr.Utils.database.mysql.Column;
import ldcr.Utils.database.mysql.Column.ColumnString;
import ldcr.Utils.database.mysql.MysqlDataSource;

public class PrefixManager {
	private MysqlDataSource conn = null;
	private static final String PLAYER_TABLE_NAME = "sakuraprefix_player";
	private static final String PRESET_TABLE_NAME = "sakuraprefix_named";
	private static final String NICK_BLACKLIST_TABLE_NAME = "sakuraprefix_nickblacklist";
	private static final String NICK_USING_TABLE_NAME = "sakuraprefix_nick";
	private static final int CACHE_CLEAR_INTERVAL = 20*60*30;

	// 数据库连接
	public void connect(final String mysqlServer, final String mysqlPort, final String mysqlDatabase, final String mysqlUser, final String mysqlPassword) throws SQLException {
		if (conn!=null) {
			disconnect();
		}
		SakuraPrefix.sendConsoleMessage("&a正在连接Mysql数据库 "+mysqlServer+":"+mysqlPort+" ...");
		conn = new MysqlDataSource(mysqlServer, mysqlPort, mysqlUser, mysqlPassword, mysqlDatabase, SakuraPrefix.getInstance());
		try {
			conn.connectDatabase();
		} catch (final SQLException e) {
			throw new SQLException("Failed connect Database", e);
		}
		if (conn.isConnected()) {
			try {
				conn.createTable(PLAYER_TABLE_NAME, new Column("player", ColumnString.TEXT), new Column("prefix", ColumnString.TEXT), new Column("suffix", ColumnString.TEXT), new Column("tagPrefix", ColumnString.TEXT), new Column("tagSuffix", ColumnString.TEXT), new Column("nick", ColumnString.TEXT), new Column("locked", ColumnString.TEXT));
				conn.createTable(PRESET_TABLE_NAME, new Column("tag", ColumnString.TEXT), new Column("value", ColumnString.TEXT));
				conn.createTable(NICK_BLACKLIST_TABLE_NAME, new Column("nick", ColumnString.TEXT));
				conn.createTable(NICK_USING_TABLE_NAME, new Column("player", ColumnString.TEXT), new Column("nick", ColumnString.TEXT));
			} catch (final SQLException e) {
				throw new SQLException("Failed initiate Tables");
			}
		} else throw new SQLException("Failed connect Database");
		Bukkit.getScheduler().runTaskTimer(SakuraPrefix.getInstance(), new CacheCleanTask(), CACHE_CLEAR_INTERVAL, CACHE_CLEAR_INTERVAL);
	}

	public void disconnect() {
		if (conn!=null && conn.isConnected()) {
			SakuraPrefix.sendConsoleMessage("&e正在关闭数据库连接...");
			conn.disconnectDatabase();
			SakuraPrefix.sendConsoleMessage("&a已与数据库断线.");
		}
	}

	// 预设模版
	public void addPreset(final String tag, final String value) throws SQLException {
		if (!conn.isExists(PRESET_TABLE_NAME, "tag", tag)) {
			conn.intoValue(PRESET_TABLE_NAME, tag, value);
		} else {
			conn.setValue(PRESET_TABLE_NAME, "tag", tag, "value", value);
		}
	}

	public void delPreset(final String tag) throws SQLException {
		conn.deleteValue(PRESET_TABLE_NAME, "tag", tag);
	}

	public Map<String, String> getAllPresets() throws SQLException {
		final LinkedList<HashMap<String, Object>> list = conn.getValues(PRESET_TABLE_NAME, -1, "tag","value");
		final HashMap<String, String> result = new HashMap<>();
		for (final HashMap<String, Object> map : list) {
			result.put(map.get("tag").toString(), map.get("value").toString());
		}
		return result;
	}

	public String getPreset(final String tag) throws SQLException {
		if (conn.isExists(PRESET_TABLE_NAME, "tag", tag)) return conn.getValue(PRESET_TABLE_NAME, "tag", tag, "value").toString();
		return null;
	}

	// 称号
	public void updatePlayerPrefix(String player, final PrefixData prefix) throws SQLException {
		player = player.toLowerCase();
		if (!conn.isExists(PLAYER_TABLE_NAME, "player", player)) {
			conn.intoValue(PLAYER_TABLE_NAME, player, prefix.getPrefix(), prefix.getSuffix(), prefix.getTagPrefix(), prefix.getTagSuffix(), prefix.getNick(), prefix.isLocked()?"true":"false");
		} else {
			if (prefix.isChangedPrefix()) {
				conn.setValue(PLAYER_TABLE_NAME, "player", player, "prefix", prefix.getPrefix());
			}
			if (prefix.isChangedSuffix()) {
				conn.setValue(PLAYER_TABLE_NAME, "player", player, "suffix", prefix.getSuffix());
			}
			if (prefix.isChangedTagSuffix()) {
				conn.setValue(PLAYER_TABLE_NAME, "player", player, "tagPrefix", prefix.getTagPrefix());
			}
			if (prefix.isChangedTagSuffix()) {
				conn.setValue(PLAYER_TABLE_NAME, "player", player, "tagSuffix", prefix.getTagSuffix());
			}
			if (prefix.isChangedNick()) {
				conn.setValue(PLAYER_TABLE_NAME, "player", player, "nick", prefix.getNick());
			}
			if (prefix.isChangedLocked()) {
				conn.setValue(PLAYER_TABLE_NAME, "player", player, "locked", prefix.isLocked()?"true":"false");
			}
		}
		SakuraPrefix.getInstance().getNametagHook().update(prefix);
		SakuraPrefix.getInstance().getVaultHook().update(prefix);
	}

	public void updatePlayerNick(final String player, final PrefixData prefix) throws SQLException {
		updatePlayerPrefix(player, prefix);
		if (!prefix.getNick().isEmpty()) {
			useNick(player, prefix.getNick());
		}
		SakuraPrefix.getInstance().getNickHook().update(prefix);
		SakuraPrefix.getInstance().getSkinHook().update(prefix);
	}

	public PrefixData getPlayerPrefix(String player) throws SQLException {
		player = player.toLowerCase();
		PrefixData prefix;
		if (conn.isExists(PLAYER_TABLE_NAME, "player", player)) {
			final HashMap<String, Object> data = conn.getValue(PLAYER_TABLE_NAME, "player", player, "prefix","suffix","tagPrefix","tagSuffix", "nick", "locked");
			prefix = new PrefixData(player,
					data.get("prefix") == null ? "" : data.get("prefix").toString(),
							data.get("suffix") == null ? "" : data.get("suffix").toString(),
									data.get("tagPrefix") == null ? "" : data.get("tagPrefix").toString(),
											data.get("tagSuffix") == null ? "" : data.get("tagSuffix").toString(),
													data.get("nick") == null ? "" : data.get("nick").toString(),
															"true".equals(data.get("locked")));
		} else {
			prefix = new PrefixData(player, "","","","", "", false);
		}
		cachedPrefix.put(player, prefix);
		return prefix;
	}

	// 缓存数据
	private final HashMap<String,PrefixData> cachedPrefix = new HashMap<>();
	public PrefixData getCachedPrefixData(String player) throws SQLException {
		player = player.toLowerCase();
		if (cachedPrefix.containsKey(player)) return cachedPrefix.get(player);
		else return getPlayerPrefix(player);
	}

	// Nick
	public boolean isNickInUse(final String nick) throws SQLException {
		return conn.isExists(PLAYER_TABLE_NAME, "nick", nick.toLowerCase()) || conn.isExists(PLAYER_TABLE_NAME, "player", nick.toLowerCase());
	}

	public void useNick(final String player, final String nick) throws SQLException {
		conn.intoValue(NICK_USING_TABLE_NAME, player.toLowerCase(), nick.toLowerCase());
	}

	public void releaseNick(final String player) throws SQLException {
		conn.deleteValue(NICK_USING_TABLE_NAME, "player", player.toLowerCase());
	}

	public Map<String, String> getAllNickedPlayer() throws SQLException {
		final LinkedList<HashMap<String, Object>> list = conn.getValues(NICK_USING_TABLE_NAME, -1, "player","nick");
		final HashMap<String, String> result = new HashMap<>();
		for (final HashMap<String, Object> map : list) {
			result.put(map.get("player").toString(), map.get("nick").toString());
		}
		return result;
	}

	// Nick黑名单
	public boolean isNickBlacklisted(final String nick) throws SQLException {
		return conn.isExists(NICK_BLACKLIST_TABLE_NAME, "nick", nick.toLowerCase());
	}

	public boolean addNickBlacklist(final String nick) throws SQLException {
		if (isNickBlacklisted(nick)) return false;
		conn.intoValue(NICK_BLACKLIST_TABLE_NAME, nick.toLowerCase());
		return true;
	}

	public List<String> getNickBlacklists() throws SQLException {
		final LinkedList<HashMap<String, Object>> list = conn.getValues(NICK_BLACKLIST_TABLE_NAME, -1, "nick");
		final ArrayList<String> result = new ArrayList<>();
		for (final HashMap<String, Object> map : list) {
			result.add(map.get("nick").toString());
		}
		return result;
	}

	public boolean delNickBlacklist(final String nick) throws SQLException {
		if (!isNickBlacklisted(nick)) return false;
		conn.deleteValue(NICK_BLACKLIST_TABLE_NAME, "nick", nick.toLowerCase());
		return true;
	}

	class CacheCleanTask implements Runnable {
		@Override
		public void run() {
			cachedPrefix.clear();
		}
	}
}
