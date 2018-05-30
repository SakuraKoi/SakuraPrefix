package ldcr.LuckyPrefix;

import org.bukkit.command.CommandSender;

import ldcr.LuckyPrefix.task.update.PlayerNickTask;
import ldcr.Utils.Bukkit.command.CommandHandler;

public class NickCommand extends CommandHandler {
	public NickCommand() {
		super(LuckyPrefix.instance);
	}

	@Override
	public void onCommand(final CommandSender sender, final String[] args) {
		if (!sender.hasPermission("luckyprefix.nick")) {
			sender.sendMessage("§b§lLuckyPrefix §7>> §c你没有权限执行此命令");
			return;
		}
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
