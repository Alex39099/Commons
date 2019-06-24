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
    private String usageLine;
    private String noPermissionLine;

    private boolean isPlayerCmd;
    private boolean isConsoleCmd;

    private HashMap<String, AlexSubCommand> subCommands = new HashMap<>();

    protected AlexSubCommand(String name, String helpLine, Permission perm, String usageLine, String noPermissionLine, boolean isPlayerCmd, boolean isConsoleCmd) {
        this.name = name;
        this.helpLine = helpLine;
        this.permission = perm;
        this.usageLine = usageLine;
        this.noPermissionLine = noPermissionLine;
        this.isPlayerCmd = isPlayerCmd;
        this.isConsoleCmd = isConsoleCmd;
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
    String getHelpLine() {
        return this.helpLine;
    }
    Permission getPermission() {
        return this.permission;
    }
    private String getUsageLine() {
        return this.usageLine;
    }
    private String getNoPermissionLine() {
        return this.noPermissionLine;
    }
    Map<String, AlexSubCommand> getSubCommands() {
        return this.subCommands;
    }

    public void setHelpLine(String helpLine) {
        this.helpLine = helpLine;
    }
    public void setUsageLine(String usageLine) {
        this.usageLine = usageLine;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    // =========================================================================================

    boolean canExecute(CommandSender sender) {
        if (((sender instanceof Player) && this.isPlayerCmd)
                || (sender instanceof ConsoleCommandSender) && this.isConsoleCmd)
            return sender.hasPermission(this.getPermission());
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

        // check for subcommands
        if (args.length > 0) {
            AlexSubCommand subCommand = this.getSubCommandForString(args[0]);
            if (subCommand != null) {
                subCommand.internalExecute(sender, label, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        if ((sender instanceof Player) && !this.isPlayerCmd)
            sendColorMessage(sender, prefix + this.getNoPermissionLine());

        else if ((sender instanceof ConsoleCommandSender) && !this.isConsoleCmd)
            sendColorMessage(sender, prefix + "&4You have to be a player to perform that command.");

        else if (!sender.hasPermission(this.permission))
            sendColorMessage(sender, prefix + this.getNoPermissionLine());

        else
            if (this.execute(sender, label, args))
                sendColorMessage(sender, prefix + this.getUsageLine());
    }


    // returns if usage was right

    /**
     * Gets called when no subCommand for this subCommand was found and sender can perform the command.
     * @param sender the CommandSender
     * @param label the used label
     * @param args the args that may concern this subCommand
     * @return true if usage was right, false otherwise
     */
    abstract boolean execute(CommandSender sender, String label, String[] args);

    /**
     * Gets called when no subCommand for this subCommand was found and adds the returned list to the available subCommandNames on tab-complete.
     * These options should be additional possibilities for certain args in the execute method.
     * @param sender the CommandSender
     * @return a list of additional options added to the available subCommandNames on tab-complete
     */
    List<String> additionalTabCompleterOptions(CommandSender sender) {
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