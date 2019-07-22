package commons.command;

import commons.messages.DebugMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class AlexCommand extends AlexSubCommand {

    private List<String> credits = new ArrayList<>();
    private List<String> helpCmdHeader = new ArrayList<>();

    private String usagePrefix = "";

    /**
     * Constructs a new cmd, sets the prefix and a first creditline (version, author)
     * @param name the (sub-)command name
     * @param plugin the javaPlugin
     * @param pluginColor the pluginColor (used in prefix)
     */
    public AlexCommand(String name, JavaPlugin plugin, ChatColor pluginColor) {
        super(name, "");
        this.setPrefix("[" + pluginColor + plugin.getName() + ChatColor.RESET + "]");
        credits.add("version " + plugin.getDescription().getVersion() + ", author alex_qp");
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

    /**
     * Gets executed on blank cmdUsage
     * @param sender the commandSender
     */
    protected void credits(CommandSender sender) {
        for (String creditLine : credits) {
            sendColorMessage(sender, this.getPrefix() + creditLine);
        }
    }

    /**
     * Gets used to get the helpLines for each sub-cmd
     * @param label the cmd label used
     * @param cmdName the (sub-)cmd name (may + subCmd#getCmdParamLine)
     * @param explanation the explanation
     * @return the helpLine without prefix
     */
    protected static String getCommandLine(String label, String cmdName, String explanation) {
        String prefix = "" + ChatColor.BOLD + ChatColor.GOLD + "/" + label;
        return prefix + " " + cmdName + ": " + ChatColor.WHITE + explanation;
    }

    /**
     *
     * @param label the cmd label used
     * @param cmdName the (sub-)cmd name (may + subCmd#getCmdParamLine)
     * @param explanation the explanation
     * @return the helpLine with prefix ready to be sent to the sender
     */
    protected String getCommandLineWithPrefix(String label, String cmdName, String explanation) {
        return this.getPrefix() + getCommandLine(label, cmdName, explanation);
    }

    /**
     * Gets executed on help cmd.
     * @param sender the commandSender
     * @param label the used cmd-label
     */
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

    /**
     * Checks for subCommands or help.
     * @param sender the CommandSender
     * @param label the used label
     * @param args the args that may concern this subCommand
     * @return false if no subCommand was found or if it was not "help" for first argument.
     */
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

    /**
     * Adds help to the tabCompleterOptions. Please re-add "help" if you overwrite this method.
     * @param sender the CommandSender
     * @return the list of tab completions
     */
    @Override
    @NotNull
    protected List<String> additionalTabCompleterOptions(CommandSender sender) {
        List<String> list = new ArrayList<>();
        list.add("help");
        return list;
    }
}