package ldcr.LuckyPrefix;

import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.task.ShowPlayerPrefixTask;
import ldcr.LuckyPrefix.task.nick.AddNickBlacklistTask;
import ldcr.LuckyPrefix.task.nick.DeleteNickBlacklistTask;
import ldcr.LuckyPrefix.task.nick.ListNickBlacklistTask;
import ldcr.LuckyPrefix.task.nick.ListNickedPlayerTask;
import ldcr.LuckyPrefix.task.preset.DeletePresetTask;
import ldcr.LuckyPrefix.task.preset.ListPresetsTask;
import ldcr.LuckyPrefix.task.preset.UpdatePresetTask;
import ldcr.LuckyPrefix.task.update.PlayerCustomPrefixTask;
import ldcr.LuckyPrefix.task.update.PlayerNameTagTask;
import ldcr.LuckyPrefix.task.update.PlayerNickTask;
import ldcr.LuckyPrefix.task.update.PlayerPresetPrefixTask;
import ldcr.Utils.Bukkit.command.CommandHandler;

public class PrefixCommand extends CommandHandler {
	public PrefixCommand() {
		super(LuckyPrefix.instance, "§b§lLuckyPrefix");
	}

	@Override
	public void onCommand(final CommandSender sender, final String[] args) {
		if (checkPermission(sender, "luckyprefix.edit")) return;
		if (args.length == 0) {
			sendMessage(sender,
			            "§aLuckyPrefix 称号系统 v"+LuckyPrefix.instance.getDescription().getVersion()+" §bBy.Ldcr",
			            "§e/luckyprefix player <玩家> <prefix/suffix> <称号> §a更改玩家称号",
			            "§e/luckyprefix tag <玩家> <prefix/suffix> <称号>    §a更改玩家Tag",
			            "§e/luckyprefix set <标识> <称号>                  §a修改预设称号",
			            "§e/luckyprefix del <标识>                         §a删除预设称号",
			            "§e/luckyprefix update <玩家> <prefix/suffix> <标识> §a应用预设称号",
			            "§e/luckyprefix nick <玩家> <Nick>          §a更改玩家Nick"
					);
			return;
		}
		switch (args[0].toLowerCase()) {
		case "player": { // player <player> <suffix>
			if (args.length<2) {
				sendMessage(sender, "§e/luckyprefix player <玩家> <prefix/suffix> <称号>  §a更改玩家称号");
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
				sendMessage(sender, "§e/luckyprefix tag <玩家> <prefix/suffix> <称号>  §a更改玩家Tag");
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
				sendMessage(sender, "§e/luckyprefix set <标识> <称号>  §a修改预设称号");
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
				sendMessage(sender, "§e/luckyprefix del <标识>  §a删除预设称号");
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
				sendMessage(sender, "§e/luckyprefix update <玩家> <prefix/suffix> <标识>  应用预设称号");
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
				sendMessage(sender, "§e/luckyprefix nick <玩家> <Nick>         §a更改玩家Nick");
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
				            "§e/luckyprefix nickblacklist add <Nick>        §a添加Nick黑名单",
				            "§e/luckyprefix nickblacklist del <Nick>        §a删除Nick黑名单",
				            "§e/luckyprefix nickblacklist list              §a列出Nick黑名单"

						);
				return;
			}
			switch (args[1].toLowerCase()) {
			case "add": {
				if (args.length<3) {
					sendMessage(sender, "§e/luckyprefix nickblacklist add <Nick>        §a添加Nick黑名单");
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
					sendMessage(sender, "§e/luckyprefix nickblacklist del <Nick>        §a删除Nick黑名单");
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
			}
		}
		case "nicklist": {
			new ListNickedPlayerTask(sender, false);
			return;
		}
		case "nicks": {
			new ListNickedPlayerTask(sender, true);
			return;
		}
		}
		return;
	}

}
