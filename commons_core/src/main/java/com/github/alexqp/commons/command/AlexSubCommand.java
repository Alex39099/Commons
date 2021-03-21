/*
 * Copyright (C) 2019-2021 Alexander Schmid
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.alexqp.commons.command;

import com.github.alexqp.commons.messages.ConsoleMessage;
import com.github.alexqp.commons.messages.SetDebugable;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @deprecated use {@link com.github.alexqp.commons.command.better.AlexSubCommand}
 */
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
     * @see AlexSubCommand#sendColorMessage(CommandSender, String)
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

    protected SetDebugable debugable = new SetDebugable() {

        private boolean debug = false;

        @Override
        public String getName() {
            return "[ALEXSUBCOMMAND-API]";
        }

        @Override
        public boolean getDebug() {
            return debug;
        }

        @Override
        public void setDebug(boolean debug) {
            this.debug = debug;
        }
    };


    private String name;
    private String prefix = "";

    private String helpLine;
    private String cmdParamLine = "";

    private String permission;
    private String noPermissionLine = "";

    private String usageLine = "";
    private String usagePrefixDummy = "";

    private boolean isPlayerCmd = true;
    private boolean isConsoleCmd = true;

    private boolean hasExtraFirstArgument = false;

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

    /**
     * Constructor for heritage. This will inherit isPlayerCmd, isConsoleCmd, prefix, usagePrefixDummy, noPermissionLine. (also debug)
     * @param name the name
     * @param helpLine the helpLine
     * @param parent the parent subCommand from which values are inherited
     */
    protected AlexSubCommand(String name, String helpLine, AlexSubCommand parent) {
        this(name, helpLine, parent.isPlayerCmd, parent.isConsoleCmd);
        this.prefix = parent.getPrefix();
        this.usagePrefixDummy = parent.usagePrefixDummy;
        this.noPermissionLine = parent.noPermissionLine;
        this.debugable = parent.debugable;
    }

    /**
     * Constructor for heritage of an AlexCommand. This will inherit prefix, noPermisisonLine and usagePrefixDummy. (also debug)
     * @param name the subCommand name
     * @param helpLine the helpLine
     * @param alexCommand the alexCommand from which values are inherited
     */
    protected AlexSubCommand(String name, String helpLine, AlexCommand alexCommand) {
        this(name, helpLine);
        this.prefix = alexCommand.getPrefix();
        this.noPermissionLine = alexCommand.getNoPermissionLine();
        this.usagePrefixDummy = ((AlexSubCommand) alexCommand).usagePrefixDummy;
        this.debugable = alexCommand.debugable;
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
    public String getHelpLine() {
        return this.helpLine;
    }
    public String getCmdParamLine() {
        return this.cmdParamLine;
    }
    public String getPermission() {
        return this.permission;
    }
    public String getUsageLine() {
        return this.usageLine;
    }
    /**
     * Gets the usagePrefixDummy (can be used for sub-Cmd usageLines). This is not supposed to contain the cmdPrefix
     * @return the usagePrefixDummy ready to send it to (subCmd#setUsageLine(THIS + usageLine)
     */
    public String getUsagePrefixDummy() {
        return this.usagePrefixDummy;
    }
    public String getNoPermissionLine() {
        return this.noPermissionLine;
    }
    protected Map<String, AlexSubCommand> getSubCommands() {
        return this.subCommands;
    }


    /**
     * Sets the usagePrefixDummy
     * @param prefix the prefix (blanket gets added)
     * @return the instance
     */
    public AlexSubCommand setUsagePrefixDummy(String prefix) {
        this.usagePrefixDummy = prefix + " ";
        return this;
    }

    /**
     * Sets the helpLine.
     * <p>This line is used in the corresponding main AlexCommand to give a brief description of the command on /help. However this will only be used if this is a sub-cmd directly after AlexCommand.
     * @param helpLine the helpLine
     * @return the instance
     * @see AlexSubCommand#setCmdParamLine(String)
     */
    public AlexSubCommand setHelpLine(String helpLine) {
        this.helpLine = helpLine;
        return this;
    }

    /**
     * Sets the CmdParamLine used in the help-cmd. (/cmd name *this*: helpLine)
     * @param paramLine the parameter line
     * @return the instance
     * @see AlexSubCommand#setHelpLine(String)
     */
    public AlexSubCommand setCmdParamLine(String paramLine) {
        this.cmdParamLine = " " + paramLine;
        return this;
    }

    /**
     * Sets the usageLine sent when execute returns false.
     * @param usageLine the raw usageLine
     * @return the edited AlexSubCommand
     */
    public AlexSubCommand setUsageLine(String usageLine) {
        this.usageLine = usageLine;
        return this;
    }

    /**
     * Sets the noPermissionLine sent when a cmdSender has no permission.
     * @param noPermissionLine the line to send.
     * @return the edited AlexSubCommand
     */
    public AlexSubCommand setNoPermissionLine(String noPermissionLine) {
        this.noPermissionLine = noPermissionLine;
        return this;
    }

    /**
     * Sets the permission needed to perform the alexSubCmd.
     * @param permission the permission-string
     * @return the edited AlexSubCommand
     */
    public AlexSubCommand setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Sets if players should be able to perform the subCommand.
     * @param isPlayerCmd should they?
     * @return the edited AlexSubCommand.
     */
    public AlexSubCommand setIsPlayerCmd(boolean isPlayerCmd) {
        this.isPlayerCmd = isPlayerCmd;
        return this;
    }

    /**
     * Sets if console should be able to perform the subCommand.
     * @param isConsoleCmd should it?
     * @return the edited AlexSubCommand.
     */
    public AlexSubCommand setIsConsoleCmd(boolean isConsoleCmd) {
        this.isConsoleCmd = isConsoleCmd;
        return this;
    }

    /**
     * Sets the prefix of the cmd.
     * @param prefix really just the prefix (without blanket)
     * @return the instance
     */
    @SuppressWarnings("UnusedReturnValue")
    public AlexSubCommand setPrefix(String prefix) {
        this.prefix = prefix + " ";
        return this;
    }

    public AlexSubCommand setHasExtraFirstArgument(boolean hasExtra) {
        this.hasExtraFirstArgument = hasExtra;
        return this;
    }

    // =========================================================================================

    /**
     * Get if a sender can execute the cmd.
     * <p>Note: The permission check will return true if perm is null.
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
    private void internalExecute(CommandSender sender, String label, String extraArgument, String[] args) {

        if(hasExtraFirstArgument && args.length == 0) {
            sendPrefixColorMessage(sender, this.getUsageLine());
            return;
        }

        if (hasExtraFirstArgument) {
            ConsoleMessage.debug(this.getClass(), debugable, "subCmd has extra argument, adjusting args...");
            extraArgument = args[0];
            args = Arrays.copyOfRange(args, 1, args.length);
        }


        if (!this.checkForSubCommands(sender, label, extraArgument, args) && this.checkForPermission(sender)) {
            ConsoleMessage.debug(this.getClass(), debugable, "sender has permission, proceed with execute method...");
            if (!this.execute(sender, label, extraArgument, args)) {
                sendColorMessage(sender, prefix + this.getUsageLine());
            }
        }
    }

    // checks for subCommands for the given args (used in internalExecute)
    // returns true if a subCmd was found
    private boolean checkForSubCommands(CommandSender sender, String label, String extraArgument, String[] args) {

        // if performing subCmd has extra argument, args will already be shortened!
        if (args.length > 0) {
            AlexSubCommand subCommand = this.getSubCommandForString(args[0]);
            if (subCommand != null) {
                ConsoleMessage.debug(this.getClass(), debugable, "found sub-command" + subCommand.getName());
                subCommand.internalExecute(sender, label, extraArgument, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }
        ConsoleMessage.debug(this.getClass(), debugable, "found no sub-command, proceed with internalExecute...");
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
     * @param extraArgument the extraArgument if last subCmd had extraArgument.
     * @param args the args that may concern this subCommand
     * @return true if usage was right, false otherwise
     */
    protected abstract boolean execute(CommandSender sender, String label, String extraArgument, String[] args);

    /**
     * Adds additional tabCompleterOptions.
     * <p>Gets called when no subCommand for this subCommand was found and adds the returned list to the available subCommandNames on tab-complete.
     * <p>Note: These options should be additional possibilities for certain args in the execute method and get added to the available sub-cmd names on tab-complete.
     * @param sender the CommandSender
     * @return a list of additional options
     * @see AlexSubCommand#getTabCompletion(CommandSender, String, String[])
     */
    @NotNull
    protected List<String> additionalTabCompleterOptions(CommandSender sender) {
        return new ArrayList<>();
    }

    /**
     * Adds additional tabCompleterOptions for extra argument.
     * <p>This is only relevant if this sub-cmd has an extra argument.
     * @param sender the CommandSender
     * @return a list of additional options
     */
    @NotNull
    protected List<String> additionalTabCompleterOptionsExtraArgument(CommandSender sender) {
        return new ArrayList<>();
    }

    /**
     * Gets the tabCompletion by args and may call another tabCompletion with shorten args, this should only get overwritten by last subCmd!
     * <p>This method should only get overwritten by last sub-cmd! To add another TabCompletion option use {@link AlexSubCommand#additionalTabCompleterOptions(CommandSender)}
     * @param sender the consoleSender
     * @param args the args (without args used in other subCommands)
     * @param extraArgument the extraArgument, only changed if a subCmd has an extraArgument.
     * @return a list of tabCompletions
     */
    @SuppressWarnings({"WeakerAccess"})
    protected List<String> getTabCompletion(CommandSender sender, String extraArgument, String[] args) {
        List<String> completions = new ArrayList<>();

        int i = 0;

        if (hasExtraFirstArgument) {
            i = 1;

            if (args.length >= 1)
                extraArgument = args[0];
        }


        if (args.length > i) {

            // check for subCommands
            AlexSubCommand subCommand = this.getSubCommandForString(args[i]);
            if (subCommand != null) {

                ConsoleMessage.debug(this.getClass(), debugable, "found subCommand " + subCommand.getName() + " for tabCompletion.");
                return subCommand.getTabCompletion(sender, extraArgument, Arrays.copyOfRange(args, i + 1, args.length));
            }

            // get possibilities out of arg
            StringUtil.copyPartialMatches(args[i], this.getSubCommandNames(sender), completions);
            StringUtil.copyPartialMatches(args[i], this.additionalTabCompleterOptions(sender), completions);
        } else if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], this.additionalTabCompleterOptionsExtraArgument(sender), completions);
        }
        Collections.sort(completions);
        return completions;
    }

    /**
     * Do not touch. Re-directs method to internalExecute.
     * @param sender the commandSender
     * @param command the command
     * @param label the label
     * @param args the args
     * @return always true
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ConsoleMessage.debug(this.getClass(), debugable, "onCommand called.");
        this.internalExecute(sender, label, "", args);
        return true;
    }

    /**
     * Do not touch. Re-directs method to getTabCompletion.
     * @param sender the commandSender
     * @param command the command
     * @param alias the alias (not used)
     * @param args the args
     * @return a list of tabCompletions returned by getTabCompletion
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ConsoleMessage.debug(this.getClass(), debugable, "onTabComplete called");
        return this.getTabCompletion(sender, "", args);
    }
}