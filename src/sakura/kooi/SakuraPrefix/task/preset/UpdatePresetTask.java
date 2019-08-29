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
package sakura.kooi.SakuraPrefix.task.preset;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import sakura.kooi.SakuraPrefix.PrefixData;
import sakura.kooi.SakuraPrefix.SakuraPrefix;
import sakura.kooi.Utils.exception.ExceptionUtils;

public class UpdatePresetTask implements Runnable {
	private final CommandSender callback;
	private final String tag;
	private final String value;
	public UpdatePresetTask(final CommandSender callback, final String tag, final String value) {
		this.callback = callback;
		this.tag = tag;
		this.value = PrefixData.processChar(PrefixData.processColor(value)).replace("[", "『").replace("]", "』");
		Bukkit.getScheduler().runTaskAsynchronously(SakuraPrefix.getInstance(), this);
	}
	@Override
	public void run() {
		try {
			SakuraPrefix.getPrefixManager().addPreset(tag, value);
		} catch (final SQLException e) {
			callback.sendMessage("§d§lSakuraPrefix §7>> §c错误: 数据库操作出错, 请检查后台日志");
			ExceptionUtils.printStacktrace(e);
			return;
		}
		callback.sendMessage("§d§lSakuraPrefix §7>> §a预设称号 §e"+tag+" §a已更新: §r"+value);
	}

}
