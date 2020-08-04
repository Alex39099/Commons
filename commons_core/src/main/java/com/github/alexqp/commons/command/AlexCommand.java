/*
 * Copyright (C) 2019-2020 Alexander Schmid
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
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @deprecated use {@link com.github.alexqp.commons.command.better.AlexCommand}
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class AlexCommand extends AlexSubCommand {

    private List<String> credits = new ArrayList<>();
    private List<String> helpCmdHeader = new ArrayList<>();

    private String usagePrefix = "";

    /**
     * Constructs a cmd, sets the prefix and a first creditline (version, author)
     * @param name the (sub-)command name
     * @param plugin the javaPlugin
     * @param pluginColor the pluginColor (used in prefix)
     */
    public AlexCommand(String name, JavaPlugin plugin, ChatColor pluginColor) {
        super(name, "");
        this.setPrefix("[" + pluginColor + plugin.getName() + ChatColor.RESET + "]");
        credits.add("version " + plugin.getDescription().getVersion() + ", author alex_qp");
    }

    /**
     * Sets all credits.
     * @param credits the credit lines
     * @return the instance
     */
    public AlexCommand setCredits(List<String> credits) {
        this.credits = credits;
        return this;
    }

    /**
     * Sets the first lines of the help sub-cmd.
     * @param helpCmdHeader the header lines
     * @return the instance
     */
    public AlexCommand setHelpCmdHeader(List<String> helpCmdHeader) {
        this.helpCmdHeader = helpCmdHeader;
        return this;
    }

    /**
     * Adds a credit line to the current list.
     * @param line the credit line
     * @return the instance
     * @see AlexCommand#addAllCreditLine(Collection)
     */
    public AlexCommand addCreditLine(String line) {
        credits.add(line);
        return this;
    }

    /**
     * Adds a collection of credit lines to the current list.
     * @param lines the collection of credit lines
     * @return the instance
     * @see AlexCommand#addCreditLine(String)
     */
    public AlexCommand addAllCreditLine(Collection<? extends String> lines) {
        credits.addAll(lines);
        return this;
    }

    /**
     * Adds a header line to the header of the help sub-cmd.
     * @param line the header line
     * @return the instance
     * @see AlexCommand#addAllHelpCmdHeaderLine(Collection)
     */
    public AlexCommand addHelpCmdHeaderLine(String line) {
        helpCmdHeader.add(line);
        return this;
    }

    /**
     * Adds a collection of header lines to the header of the help sub-cmd.
     * @param lines the collection of header lines
     * @return the instance
     * @see AlexCommand#addHelpCmdHeaderLine(String)
     */
    public AlexCommand addAllHelpCmdHeaderLine(Collection<? extends String> lines) {
        helpCmdHeader.addAll(lines);
        return this;
    }

    /**
     * Removes a specified credit line if found.
     * @param line the credit line
     * @return true if it got removed, false otherwise
     */
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
     * @see AlexCommand#getCommandLineWithPrefix(String, String, String)
     */
    protected static String getCommandLine(String label, String cmdName, String explanation) {
        String prefix = "" + ChatColor.BOLD + ChatColor.GOLD + "/" + label;
        return prefix + " " + cmdName + ": " + ChatColor.WHITE + explanation;
    }

    /**
     * Gets the cmd line with prefix.
     * @param label the cmd label used
     * @param cmdName the (sub-)cmd name (may + subCmd#getCmdParamLine)
     * @param explanation the explanation
     * @return the helpLine with prefix ready to be sent to the sender
     * @see AlexCommand#getCommandLine(String, String, String)
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
                ConsoleMessage.debug(this.getClass(), debugable, "Help: sender has no permission for subCommand " + subCommand.getName() + ", skipped therefore for help-output");
            }

        }
    }

    /**
     * Checks for subCommands or help.
     * @param sender the CommandSender
     * @param label the used label
     * @param extraArgument should always be empty
     * @param args the args that may concern this subCommand
     * @return false if no subCommand was found or if it was not "help" for first argument.
     */
    @Override
    protected boolean execute(CommandSender sender, String label, String extraArgument, String[] args) {
        if (args.length == 0) {
            ConsoleMessage.debug(this.getClass(), debugable, "Execute: args.length == 0 -> send credits");
            this.credits(sender);
            return true;
        }

        ConsoleMessage.debug(this.getClass(), debugable, "Execute: at least one arg -> check for help");
        if (args[0].equalsIgnoreCase("help")) {
            ConsoleMessage.debug(this.getClass(), debugable, "Execute: args[0] equals help, proceed with help...");
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