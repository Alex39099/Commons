package commons.command;

import commons.messages.DebugMessage;
import commons.messages.Debugable;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AlexSubCommand implements TabExecutor {

    /**
     * Sends a colored message (WITHOUT PREFIX) to the given sender (translateAlternate..)
     * @param sender the commandSender
     * @param msg the msg with color codes
     */
    protected static void sendColorMessage(CommandSender sender, String msg) {
        if (msg == null || msg.isEmpty())
            return;
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    /**
     * Sends multiple colored messages (WITHOUT PREFIX) to the given sender (translateAlternate..)
     * @param sender the commandSender
     * @param messages a list of messages with color codes
     */
    protected static void sendColorMessage(CommandSender sender, List<String> messages) {
        for (String msg : messages) {
            sendColorMessage(sender, msg);
        }
    }

    /**
     * Sends a colored message (with prefix) to the given sender (translateAlternate..)
     * @param sender the commandSender
     * @param msg the msg with colorCodes
     */
    protected void sendPrefixColorMessage(CommandSender sender, String msg) {
        if (msg == null || msg.isEmpty())
            return;
        sendColorMessage(sender, this.getPrefix() + msg);
    }

    /**
     * Sends multiple colored messages (with prefix) to the given sender (translateAlternate..)
     * @param sender the commandSender
     * @param messages a list of messages with color codes
     */
    protected void sendPrefixColorMessage(CommandSender sender, List<String> messages) {
        for (String msg : messages) {
            sendPrefixColorMessage(sender, msg);
        }
    }

    protected final Debugable debugable = new Debugable() {

        private boolean debug = false;

        @Override
        public String getName() {
            return "ALEXSUBCOMMAND-API";
        }

        @Override
        public boolean getDebug() {
            return debug;
        }

        public void setDebug(boolean debug) {
            this.debug = debug;
        }
    };



    private String name;

    private String prefix = "";
    private String helpLine;
    private String cmdParamLine = "";
    private String permission;
    private String usageLine = "";
    private String noPermissionLine = "";

    private boolean isPlayerCmd = true;
    private boolean isConsoleCmd = true;

    private HashMap<String, AlexSubCommand> subCommands = new HashMap<>();

    protected AlexSubCommand(String name, String helpLine) {
        this.name = name;
        this.helpLine = helpLine;
    }

    protected AlexSubCommand(String name, String helpLine, boolean isPlayerCmd, boolean isConsoleCmd) {
        this(name, helpLine);
        this.isPlayerCmd = isPlayerCmd;
        this.isConsoleCmd = isConsoleCmd;
    }

    protected AlexSubCommand(String name, String helpLine, String perm, String usageLine, String noPermissionLine, boolean isPlayerCmd, boolean isConsoleCmd) {
        this(name, helpLine, isPlayerCmd, isConsoleCmd);
        this.permission = perm;
        this.usageLine = usageLine;
        this.noPermissionLine = noPermissionLine;
    }

    /**
     * Constructor for heritage
     * @param name the name
     * @param helpLine the helpLine
     * @param parent the parent subCommand from which values are inherited
     */
    protected AlexSubCommand(String name, String helpLine, AlexSubCommand parent) {
        this(name, helpLine, parent.getPermission(), parent.getUsageLine(), parent.getNoPermissionLine(), parent.isPlayerCmd, parent.isConsoleCmd);
        this.prefix = parent.getPrefix();
    }

    /**
     * Constructor for heritage of an AlexCommand. This will inherit prefix, noPermisisonLine and set usageLine to the usageLinePrefixDummy
     * @param name the subCommand name
     * @param helpLine the helpLine
     * @param alexCommand the alexCommand from which values are inherited
     */
    protected AlexSubCommand(String name, String helpLine, AlexCommand alexCommand) {
        this(name, helpLine);
        this.prefix = alexCommand.getPrefix();
        this.noPermissionLine = alexCommand.getNoPermissionLine();
        this.usageLine = alexCommand.getUsagePrefixDummy();
    }

    // =========================================================================================
    //  GETTER / SETTER
    // =========================================================================================

    public String getName() {
        return this.name;
    }
    public String getPrefix() {
        return this.prefix;
    }
    protected String getHelpLine() {
        return this.helpLine;
    }
    protected String getCmdParamLine() {
        return this.cmdParamLine;
    }
    protected String getPermission() {
        return this.permission;
    }
    protected String getUsageLine() {
        return this.usageLine;
    }
    protected String getNoPermissionLine() {
        return this.noPermissionLine;
    }
    protected Map<String, AlexSubCommand> getSubCommands() {
        return this.subCommands;
    }

    public AlexSubCommand setHelpLine(String helpLine) {
        this.helpLine = helpLine;
        return this;
    }
    public AlexSubCommand setCmdParamLine(String paramLine) {
        this.cmdParamLine = " " + paramLine;
        return this;
    }
    public AlexSubCommand setUsageLine(String usageLine) {
        this.usageLine = usageLine;
        return this;
    }
    public AlexSubCommand setNoPermissionLine(String noPermissionLine) {
        this.noPermissionLine = noPermissionLine;
        return this;
    }
    public AlexSubCommand setPermission(String permission) {
        this.permission = permission;
        return this;
    }
    public AlexSubCommand setIsPlayerCmd(boolean isPlayerCmd) {
        this.isPlayerCmd = isPlayerCmd;
        return this;
    }
    public AlexSubCommand setIsConsoleCmd(boolean isConsoleCmd) {
        this.isConsoleCmd = isConsoleCmd;
        return this;
    }

    /**
     * Sets the prefix of the cmd
     * @param prefix the prefix (should have blank at the end)
     * @return the instance
     */
    @SuppressWarnings("UnusedReturnValue")
    public AlexSubCommand setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    // =========================================================================================

    /**
     * Get if a sender can execute the cmd
     * @param sender the commandSender
     * @return if commandSender is able to perform this cmd (does not check permission if not set)
     */
    protected boolean canExecute(CommandSender sender) {
        if (((sender instanceof Player) && this.isPlayerCmd)
                || (sender instanceof ConsoleCommandSender) && this.isConsoleCmd)
            return permission == null || sender.hasPermission(this.getPermission());
        return false;
    }


    // gets the subCommandNames the sender has permission to perform
    private Set<String> getSubCommandNames(CommandSender sender) {
        HashSet<String> list = new HashSet<>();
        for (AlexSubCommand subCommand: this.getSubCommands().values()) {
            if (subCommand.canExecute(sender))
                list.add(subCommand.getName());
        }
        return list;
    }

    /**
     * Gets a set subCommand by name
     * @param arg the subCmd's name
     * @return the subCmd or null if not found
     */
    @Nullable private AlexSubCommand getSubCommandForString(String arg) {
        for (String name : this.getSubCommands().keySet()) {
            if (name.equalsIgnoreCase(arg))
                return this.getSubCommands().get(name);
        }
        return null;
    }

    /**
     * Adds a subCommand if not yet present.
     * @param subCommand the subCommand to add
     * @return true if subCmd was added
     */
    public boolean addSubCommand(AlexSubCommand subCommand) {
        if (subCommands.containsKey(subCommand.getName()))
            return false;
        subCommands.put(subCommand.getName(), subCommand);
        return true;
    }

    // executes at first, should check for other subCommands and then for permission -> execute method -> may send usageLine
    private void internalExecute(CommandSender sender, String label, String[] args) {

        if (!this.checkForSubCommands(sender, label, args) && this.checkForPermission(sender)) {
            new DebugMessage(this.getClass(), debugable, "sender has permission, proceed with execute method...");
            if (!this.execute(sender, label, args)) {
                sendColorMessage(sender, prefix + this.getUsageLine());
            }
        }
    }

    // checks for subCommands for the given args (used in internalExecute)
    // returns true if a subCmd was found
    private boolean checkForSubCommands(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            AlexSubCommand subCommand = this.getSubCommandForString(args[0]);
            if (subCommand != null) {
                new DebugMessage(this.getClass(), debugable, "found sub-command" + subCommand.getName());
                subCommand.internalExecute(sender, label, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }
        new DebugMessage(this.getClass(), debugable, "found no sub-command, proceed with internalExecute...");
        return false;
    }

    // checks for permission of sender, sends messages accordingly.
    // returns if sender can perform this subCmd
    private boolean checkForPermission(CommandSender sender) {
        if ((sender instanceof Player) && !this.isPlayerCmd) {
            sendColorMessage(sender, prefix + this.getNoPermissionLine());

        } else if ((sender instanceof ConsoleCommandSender) && !this.isConsoleCmd) {
            sendColorMessage(sender, prefix + "&4You have to be a player to perform that command.");

        } else if (this.permission != null && !sender.hasPermission(this.permission)) {
            sendColorMessage(sender, prefix + this.getNoPermissionLine());
        } else {
            return true;
        }
        return false;
    }

    /**
     * Gets called when no subCommand for this subCommand was found and sender can perform the command.
     * @param sender the CommandSender
     * @param label the used label
     * @param args the args that may concern this subCommand
     * @return true if usage was right, false otherwise
     */
    protected abstract boolean execute(CommandSender sender, String label, String[] args);

    /**
     * Gets called when no subCommand for this subCommand was found and adds the returned list to the available subCommandNames on tab-complete.
     * These options should be additional possibilities for certain args in the execute method.
     * @param sender the CommandSender
     * @return a list of additional options added to the available subCommandNames on tab-complete
     */
    protected List<String> additionalTabCompleterOptions(CommandSender sender) {
        return new ArrayList<>();
    }

    /**
     * Gets the tabCompletion by args and may call another tabCompletion with shorten args, this should only get overwritten by last subCmd!
     * @param sender the consoleSender
     * @param args the args
     * @return a list of tabCompletions
     */
    @SuppressWarnings({"WeakerAccess"})
    protected List<String> getTabCompletion(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length > 0) {

            // check for subCommands
            AlexSubCommand subCommand = this.getSubCommandForString(args[0]);
            if (subCommand != null) {
                new DebugMessage(this.getClass(), debugable, "found subCommand " + subCommand.getName() + " for tabCompletion.");
                return subCommand.getTabCompletion(sender, Arrays.copyOfRange(args, 1, args.length));
            }

            // get possibilities out of arg
            StringUtil.copyPartialMatches(args[0], this.getSubCommandNames(sender), completions);
            StringUtil.copyPartialMatches(args[0], this.additionalTabCompleterOptions(sender), completions);
        }
        Collections.sort(completions);
        return completions;
    }

    /**
     * Re-directs method to internalExecute. Do not touch
     * @param sender the commandSender
     * @param command the command
     * @param label the label
     * @param args the args
     * @return always true
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        new DebugMessage(this.getClass(), debugable, "onCommand called.");
        this.internalExecute(sender, label, args);
        return true;
    }

    /**
     * Re-directs method to getTabCompletion. Do not touch
     * @param sender the commandSender
     * @param command the command
     * @param alias the alias (not used)
     * @param args the args
     * @return a list of tabCompletions returned by getTabCompletion
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        new DebugMessage(this.getClass(), debugable, "onTabComplete called");
        return this.getTabCompletion(sender, args);
    }
}