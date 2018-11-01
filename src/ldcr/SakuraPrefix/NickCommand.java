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

import ldcr.SakuraPrefix.task.update.PlayerNickTask;
import ldcr.Utils.Bukkit.command.CommandHandler;

public class NickCommand extends CommandHandler {
	public NickCommand() {
		super(SakuraPrefix.getInstance(), "§d§lSakuraPrefix");
	}

	@Override
	public void onCommand(final CommandSender sender, final String[] args) {
		if (checkPermission(sender, "sakuraprefix.nick")) return;
		String nick;
		if (args.length==0) {
			nick = "";
		} else {
			final StringBuilder builder = new StringBuilder();
			for (int i = 0;i<args.length;i++) {
				builder.append(' ');
				builder.append(args[i]);
			}
			nick = builder.toString().substring(1, builder.length());
		}
		new PlayerNickTask(sender, sender.getName(), nick);
	}

}
