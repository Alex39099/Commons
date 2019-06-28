package commons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused"})
public class AlexCommand extends AlexSubCommand {

    private List<String> credits = new ArrayList<>();
    private List<String> helpCmdHeader = new ArrayList<>();

    public AlexCommand(String name, JavaPlugin plugin, ChatColor pluginColor) {
        super(name, "");
        this.setPrefix("[" + pluginColor + plugin.getName() + ChatColor.RESET + "] ");
        credits.add("version " + plugin.getDescription().getVersion() + ", author alex_qp");
    }

    public AlexCommand(String name, List<String> credits, List<String> helpCmdHeader, Permission perm, String usageLine, String noPermissionLine, boolean isPlayerCmd, boolean isConsoleCmd) {
        super(name, "", perm, usageLine, noPermissionLine, isPlayerCmd, isConsoleCmd);
        this.credits = credits;
        this.helpCmdHeader = helpCmdHeader;
    }

    public AlexCommand setCredits(List<String> credits) {
        this.credits = credits;
        return this;
    }
    public AlexCommand setHelpCmdHeader(List<String> helpCmdHeader) {
        this.helpCmdHeader = helpCmdHeader;
        return this;
    }
    public AlexCommand addCreditLine(String line) {
        credits.add(line);
        return this;
    }
    public AlexCommand addHelpCmdHeaderLine(String line) {
        helpCmdHeader.add(line);
        return this;
    }

    public boolean removeCreditLine(String line) {
        return credits.remove(line);
    }
    public boolean removeHelpCmdHeaderLine(String line) {
        return helpCmdHeader.remove(line);
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