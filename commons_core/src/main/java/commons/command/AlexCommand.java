package commons.command;

import commons.messages.DebugMessage;
import commons.messages.Debugable;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class AlexCommand extends AlexSubCommand {

    private List<String> credits = new ArrayList<>();
    private List<String> helpCmdHeader = new ArrayList<>();

    private String usagePrefix = "";

    public AlexCommand(String name, JavaPlugin plugin, ChatColor pluginColor) {
        super(name, "");
        this.setPrefix("[" + pluginColor + plugin.getName() + ChatColor.RESET + "] ");
        credits.add("version " + plugin.getDescription().getVersion() + ", author alex_qp");
    }

    public AlexCommand(String name, List<String> credits, List<String> helpCmdHeader, String perm, String usageLine, String noPermissionLine, boolean isPlayerCmd, boolean isConsoleCmd) {
        super(name, "", perm, usageLine, noPermissionLine, isPlayerCmd, isConsoleCmd);
        this.credits = credits;
        this.helpCmdHeader = helpCmdHeader;
    }

    public String getUsagePrefixDummy() {
        return this.usagePrefix;
    }

    public AlexCommand setUsagePrefixDummy(String prefix) {
        this.usagePrefix = prefix;
        return this;
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

    protected void credits(CommandSender sender) {
        for (String creditLine : credits) {
            sendColorMessage(sender, this.getPrefix() + creditLine);
        }
    }

    protected static String getCommandLine(String label, String cmdName, String explanation) {
        String prefix = "" + ChatColor.BOLD + ChatColor.GOLD + "/" + label;
        return prefix + " " + cmdName + ": " + ChatColor.WHITE + explanation;
    }

    protected String getCommandLineWithPrefix(String label, String cmdName, String explanation) {
        return this.getPrefix() + getCommandLine(label, cmdName, explanation);
    }

    protected void help(CommandSender sender, String label) {
        for (String helpLine : this.helpCmdHeader) {
            sendColorMessage(sender, this.getPrefix() + helpLine);
        }

        for (AlexSubCommand subCommand : this.getSubCommands().values()) {
            if (subCommand.canExecute(sender)) {
                sendColorMessage(sender, this.getCommandLineWithPrefix(label, subCommand.getName() + subCommand.getCmdParamLine(), subCommand.getHelpLine()));
            } else {
                new DebugMessage(this.getClass(), debugable, "Help: sender has no permission for subCommand " + subCommand.getName() + ", skipped therefore for help-output");
            }

        }
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            new DebugMessage(this.getClass(), debugable, "Execute: args.length == 0 -> send credits");
            this.credits(sender);
            return true;
        }


        new DebugMessage(this.getClass(), debugable, "Execute: at least one arg -> check for help");
        if (args[0].equalsIgnoreCase("help")) {
            new DebugMessage(this.getClass(), debugable, "Execute: args[0] equals help, proceed with help...");
            this.help(sender, label);
            return true;
        }
        return false;
    }

    @Override
    protected List<String> additionalTabCompleterOptions(CommandSender sender) {
        List<String> list = new ArrayList<>();
        list.add("help");
        return list;
    }
}