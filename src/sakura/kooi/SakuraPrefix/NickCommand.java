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
package sakura.kooi.SakuraPrefix;

import org.bukkit.command.CommandSender;

import sakura.kooi.SakuraPrefix.task.update.PlayerNickTask;
import sakura.kooi.Utils.Bukkit.command.CommandHandler;

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
