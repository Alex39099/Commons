package commons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

@SuppressWarnings({"unused"})
public class AlexCommand extends AlexSubCommand {

    private String[] credits;
    private String[] helpCmdHeader;

    public AlexCommand(String name, String[] credits, String[] helpCmdHeader, Permission perm, String usageLine, String noPermissionLine, boolean isPlayerCmd, boolean isConsoleCmd) {
        super(name, "", perm, usageLine, noPermissionLine, isPlayerCmd, isConsoleCmd);
        this.credits = credits;
        this.helpCmdHeader = helpCmdHeader;
    }

    private void credits(CommandSender sender) {
        for (String creditLine : credits) {
            sendColorMessage(sender, this.getPrefix() + creditLine);
        }
    }

    private static String getCommandLine(String label, String cmdName, String explanation) {
        String prefix = "" + ChatColor.BOLD + ChatColor.GOLD + "/" + label;
        return prefix + cmdName + ": " + ChatColor.WHITE + explanation;
    }

    private String getCommandLineWithPrefix(String label, String cmdName, String explanation) {
        return this.getPrefix() + getCommandLine(label, cmdName, explanation);
    }

    private void help(CommandSender sender, String label) {
        for (String helpLine : this.helpCmdHeader) {
            sendColorMessage(sender, this.getPrefix() + helpLine);
        }

        for (AlexSubCommand subCommand : this.getSubCommands().values()) {
            if (subCommand.canExecute(sender))
                sendColorMessage(sender, this.getCommandLineWithPrefix(label, subCommand.getName(), subCommand.getHelpLine()));
        }
    }

    @Override
    boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0)
            this.credits(sender);

        if (args.length >= 1) {

            if (args[0].equalsIgnoreCase("help"))
                this.help(sender, label);

        }
        return false;
    }
}