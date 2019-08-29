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
package sakura.kooi.SakuraPrefix.task.nick;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import sakura.kooi.SakuraPrefix.PrefixData;
import sakura.kooi.SakuraPrefix.SakuraPrefix;
import sakura.kooi.Utils.exception.ExceptionUtils;

public class AddNickBlacklistTask implements Runnable {
	private final CommandSender callback;
	private final String value;
	public AddNickBlacklistTask(final CommandSender callback, final String value) {
		this.callback = callback;
		this.value = PrefixData.processColor(value);
		Bukkit.getScheduler().runTaskAsynchronously(SakuraPrefix.getInstance(), this);
	}
	@Override
	public void run() {
		try {
			if (SakuraPrefix.getPrefixManager().addNickBlacklist(value)) {
				callback.sendMessage("§d§lSakuraPrefix §7>> §aNick黑名单已添加: §r"+value);
			} else {
				callback.sendMessage("§d§lSakuraPrefix §7>> §cNick黑名单已存在: §r"+value);
			}
		} catch (final SQLException e) {
			callback.sendMessage("§d§lSakuraPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacktrace(e);
		}
	}

}
