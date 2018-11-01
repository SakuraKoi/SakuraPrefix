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

import org.bukkit.command.CommandSender;

import ldcr.SakuraPrefix.task.ShowPlayerPrefixTask;
import ldcr.SakuraPrefix.task.nick.AddNickBlacklistTask;
import ldcr.SakuraPrefix.task.nick.DeleteNickBlacklistTask;
import ldcr.SakuraPrefix.task.nick.ListNickBlacklistTask;
import ldcr.SakuraPrefix.task.nick.ListNickedPlayerTask;
import ldcr.SakuraPrefix.task.preset.DeletePresetTask;
import ldcr.SakuraPrefix.task.preset.ListPresetsTask;
import ldcr.SakuraPrefix.task.preset.UpdatePresetTask;
import ldcr.SakuraPrefix.task.update.PlayerCustomPrefixTask;
import ldcr.SakuraPrefix.task.update.PlayerLockTask;
import ldcr.SakuraPrefix.task.update.PlayerNameTagTask;
import ldcr.SakuraPrefix.task.update.PlayerNickTask;
import ldcr.SakuraPrefix.task.update.PlayerPresetPrefixTask;
import ldcr.Utils.Bukkit.command.CommandHandler;

public class PrefixCommand extends CommandHandler {
	public PrefixCommand() {
		super(SakuraPrefix.getInstance(), "§d§lSakuraPrefix");
	}

	@Override
	public void onCommand(final CommandSender sender, final String[] args) {
		if (checkPermission(sender, "sakuraprefix.edit")) return;
		if (args.length == 0) {
			sendMessage(sender,
					"§dSakuraPrefix 称号系统 v"+SakuraPrefix.getInstance().getDescription().getVersion()+" §bBy.Ldcr",
					"§e /sakuraprefix player <玩家> <prefix/suffix> <称号> §a更改玩家称号",
					"§e /sakuraprefix tag <玩家> <prefix/suffix> <称号>    §a更改玩家Tag",
					"§e /sakuraprefix set <标识> <称号>                    §a修改预设称号",
					"§e /sakuraprefix del <标识>                           §a删除预设称号",
					"§e /sakuraprefix update <玩家> <prefix/suffix> <标识> §a应用预设称号",
					"§e /sakuraprefix nick <玩家> <Nick>                   §a更改玩家Nick",
					"§e /sakuraprefix nickblacklist                        §a编辑Nick黑名单"
					);
			return;
		}
		switch (args[0].toLowerCase()) {
		case "player": { // player <player> <suffix>
			if (args.length<2) {
				sendMessage(sender, "§e/sakuraprefix player <玩家> <prefix/suffix> <称号>  §a更改玩家称号");
				return;
			}
			final String playerName = args[1];
			if (args.length==2) {
				new ShowPlayerPrefixTask(sender, playerName);
				return;
			}
			final String mode = args[2];
			String value;
			if (args.length<4) {
				value="";
			} else {
				final StringBuilder builder = new StringBuilder();
				for (int i = 3;i<args.length;i++) {
					builder.append(' ');
					builder.append(args[i]);
				}
				value = builder.toString().substring(1, builder.length());
			}
			new PlayerCustomPrefixTask(sender, playerName, mode, value);
			return;
		}
		case "tag": { // player <player> <suffix>
			if (args.length<3) {
				sendMessage(sender, "§e/sakuraprefix tag <玩家> <prefix/suffix> <称号>  §a更改玩家Tag");
				return;
			}
			final String playerName = args[1];
			final String mode = args[2];
			String value;
			if (args.length<4) {
				value="";
			} else {
				final StringBuilder builder = new StringBuilder();
				for (int i = 3;i<args.length;i++) {
					builder.append(' ');
					builder.append(args[i]);
				}
				value = builder.toString().substring(1, builder.length());
			}
			new PlayerNameTagTask(sender, playerName, mode, value);
			return;
		}
		case "set": { // add sign prefix
			if (args.length<3) {
				sendMessage(sender, "§e/sakuraprefix set <标识> <称号>  §a修改预设称号");
				return;
			}
			final StringBuilder builder = new StringBuilder();
			for (int i = 2;i<args.length;i++) {
				builder.append(' ');
				builder.append(args[i]);
			}
			final String value = builder.toString().substring(1, builder.length());
			new UpdatePresetTask(sender,args[1],value);
			return;
		}
		case "del": { // del sign
			if (args.length<2) {
				sendMessage(sender, "§e/sakuraprefix del <标识>  §a删除预设称号");
				return;
			}
			new DeletePresetTask(sender,args[1]);
			return;
		}
		case "listall": {
			new ListPresetsTask(sender);
			return;
		}
		case "update": {
			if (args.length<3) {
				sendMessage(sender, "§e/sakuraprefix update <玩家> <prefix/suffix> <标识>  应用预设称号");
				return;
			}
			final String playerName = args[1];
			final String mode = args[2];
			String value;
			if (args.length<4) {
				value="";
			} else {
				value=args[3];
			}
			new PlayerPresetPrefixTask(sender, playerName, mode, value);
			return;
		}
		case "nick": { // nick <player> <nick>
			if (args.length<2) {
				sendMessage(sender, "§e/sakuraprefix nick <玩家> <Nick>         §a更改玩家Nick");
				return;
			}
			final String playerName = args[1];
			String value;
			if (args.length<3) {
				value="";
			} else {
				final StringBuilder builder = new StringBuilder();
				for (int i = 2;i<args.length;i++) {
					builder.append(' ');
					builder.append(args[i]);
				}
				value = builder.toString().substring(1, builder.length());
			}
			new PlayerNickTask(sender, playerName, value);
			return;
		}
		case "nickblacklist": {
			if (args.length==1) {
				sendMessage(sender,
						"§e/sakuraprefix nickblacklist add <Nick>        §a添加Nick黑名单",
						"§e/sakuraprefix nickblacklist del <Nick>        §a删除Nick黑名单",
						"§e/sakuraprefix nickblacklist list              §a列出Nick黑名单"

						);
				return;
			}
			switch (args[1].toLowerCase()) {
			case "add": {
				if (args.length<3) {
					sendMessage(sender, "§e/sakuraprefix nickblacklist add <Nick>        §a添加Nick黑名单");
					return;
				}
				final StringBuilder builder = new StringBuilder();
				for (int i = 2;i<args.length;i++) {
					builder.append(' ');
					builder.append(args[i]);
				}
				final String nick = builder.toString().substring(1, builder.length());
				new AddNickBlacklistTask(sender, nick);
				return;
			}
			case "del": {
				if (args.length<3) {
					sendMessage(sender, "§e/sakuraprefix nickblacklist del <Nick>        §a删除Nick黑名单");
					return;
				}
				final StringBuilder builder = new StringBuilder();
				for (int i = 2;i<args.length;i++) {
					builder.append(' ');
					builder.append(args[i]);
				}
				final String nick = builder.toString().substring(1, builder.length());
				new DeleteNickBlacklistTask(sender, nick);
				return;
			}
			case "list": {
				new ListNickBlacklistTask(sender);
				return;
			}
			default: {
				sendMessage(sender, "§c未知命令");
			}
			}
			return;
		}
		case "nicklist": {
			if (args.length==1) {
				new ListNickedPlayerTask(sender, false);
			} else {
				if(args[1].equalsIgnoreCase("online")) {
					new ListNickedPlayerTask(sender, true);
				} else {
					new ListNickedPlayerTask(sender, false);
				}
			}
			return;
		}
		case "locked": {
			if (checkPermission(sender, "sakuraprefix.lock")) return;
			if (args.length!=3) {
				sendMessage(sender, "§e/sakuraprefix locked <Player> <true/false>        §a锁定/解锁玩家");
				return;
			}
			final String player = args[1];
			final boolean lock = "true".equalsIgnoreCase(args[2]);
			new PlayerLockTask(sender, player, lock);
			return;
		}
		default: {
			sendMessage(sender, "§c未知命令");
		}
		}
	}

}
