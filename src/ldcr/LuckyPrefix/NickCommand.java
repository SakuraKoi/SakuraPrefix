package ldcr.LuckyPrefix;

import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.task.update.PlayerNickTask;
import ldcr.Utils.Bukkit.command.CommandHandler;

public class NickCommand extends CommandHandler {
	public NickCommand() {
		super(LuckyPrefix.instance, "§b§lLuckyPrefix");
	}

	@Override
	public void onCommand(final CommandSender sender, final String[] args) {
		if (checkPermission(sender, "luckyprefix.nick")) return;
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
