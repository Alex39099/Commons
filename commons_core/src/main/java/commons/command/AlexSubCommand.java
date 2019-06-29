package commons.command;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AlexSubCommand implements TabExecutor {

    // TEST may problems with sendColorMessage!!!
    public static String getPrefix(JavaPlugin plugin, ChatColor pluginColor) {
        return "[" + pluginColor + plugin.getName() + ChatColor.RESET + "]";
    }

    protected static void sendColorMessage(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    private String name;

    private String prefix = "";
    private String helpLine;
    private Permission permission;
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

    protected AlexSubCommand(String name, String helpLine, Permission perm, String usageLine, String noPermissionLine, boolean isPlayerCmd, boolean isConsoleCmd) {
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
    protected Permission getPermission() {
        return this.permission;
    }
    private String getUsageLine() {
        return this.usageLine;
    }
    private String getNoPermissionLine() {
        return this.noPermissionLine;
    }
    protected Map<String, AlexSubCommand> getSubCommands() {
        return this.subCommands;
    }

    public AlexSubCommand setHelpLine(String helpLine) {
        this.helpLine = helpLine;
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
    public AlexSubCommand setPermission(Permission permission) {
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

    public AlexSubCommand setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    // =========================================================================================

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

    @Nullable private AlexSubCommand getSubCommandForString(String arg) {
        for (String name : this.getSubCommands().keySet()) {
            if (name.equalsIgnoreCase(arg))
                return this.getSubCommands().get(name);
        }
        return null;
    }

    public boolean addSubCommand(AlexSubCommand subCommand) {
        if (subCommands.containsKey(subCommand.getName()))
            return false;
        subCommands.put(subCommand.getName(), subCommand);
        return true;
    }

    private void internalExecute(CommandSender sender, String label, String[] args) {

        if (!this.checkForSubCommands(sender, label, args) && !this.checkForPermission(sender)) {
            if (!this.execute(sender, label, args)) {
                sendColorMessage(sender, prefix + this.getUsageLine());
            }
        }
    }

    private boolean checkForSubCommands(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            AlexSubCommand subCommand = this.getSubCommandForString(args[0]);
            if (subCommand != null) {
                subCommand.internalExecute(sender, label, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }
        return false;
    }

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

    // this should get overwritten for last subcommand!
    @SuppressWarnings({"WeakerAccess"})
    protected List<String> getTabCompletion(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length > 0) {

            // check for subCommands
            AlexSubCommand subCommand = this.getSubCommandForString(args[0]);
            if (subCommand != null)
                return subCommand.getTabCompletion(sender, Arrays.copyOfRange(args, 1, args.length));

            // get possibilities out of arg
            StringUtil.copyPartialMatches(args[0], this.getSubCommandNames(sender), completions);
        } else {
            completions = new ArrayList<>(this.getSubCommandNames(sender));
        }
        Collections.sort(completions);
        return completions;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.internalExecute(sender, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return this.getTabCompletion(sender, args);
    }
}