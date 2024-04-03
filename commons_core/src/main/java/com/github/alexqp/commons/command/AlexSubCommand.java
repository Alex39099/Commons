/*
 * Copyright (C) 2019-2022 Alexander Schmid
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
import com.github.alexqp.commons.messages.Debugable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.apiguardian.api.API;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class is a part of the AlexCommand API. Instances of this class are supposed to be either direct or indirect children of an {@link AlexCommand}. It is designed to provide a relatively easy way of building a plugin command.
 * <p>The messaging system utilizes the <a href="https://ci.md-5.net/job/BungeeCord/ws/chat/target/apidocs/net/md_5/bungee/api/chat/package-summary.html">Chat Component API of Bungee</a>. A basic tutorial can be found in the <a href="https://www.spigotmc.org/wiki/the-chat-component-api/">Spigot wiki</a>. You may want to use {@link com.github.alexqp.commons.messages.MessageTranslator#translateBukkitColorCodes(String) to translate (legacy) color coded messages.}
 * <p><b>Help-Command:</b> Every instance will have its own help command via "/... subCmd help" and will show the output of {@link AlexSubCommand#getHelpLine(String)} for every direct child the sender has permission to. To add a header before that list of commands, use {@link AlexSubCommand#addHelpCmdHeaderLine(BaseComponent)}.
 * <p><b>Finalization:</b> If an instance is finalized (for example) by {@link AlexSubCommand#makeFinal()} it will format every message by default in order to save computing power when the message is requested. Finalizing custom messages should be done by overwriting {@link AlexSubCommand#makeFinal()}. {@link AlexSubCommand#getPrefixMessage(BaseComponent[])} may help with prefixing.
 */
public class AlexSubCommand {

    /**
     * Controls debug messages for this API.
     * <p>Note: Debugging is inherited by the parent or deactivated.
     * @see AlexCommand#debugable
     */
    protected @NotNull Debugable debugable = new Debugable() {

        @Override
        public String getName() {
            return "ALEXCOMMAND-API";
        }

        @Override
        public boolean getDebug() {
            return false;
        }
    };

    private final @NotNull String name;
    private @NotNull BaseComponent prefix = new TextComponent();

    private @NotNull List<BaseComponent> helpCmdHeader = new ArrayList<>(); // gets finalized

    private @Nullable TextComponent cmdChain;
    private @NotNull BaseComponent helpLine; // gets finalized
    private @Nullable BaseComponent cmdParamLine;

    private @Nullable BaseComponent usageLine; // gets finalized, not null after
    private @NotNull BaseComponent usagePrefix = new TextComponent(); // must be TextComponent in order to inherit colors correctly

    private boolean isPlayerCmd = true;
    private boolean isConsoleCmd = true;
    private @Nullable String permission;
    private @Nullable BaseComponent noPermissionLine; // gets finalized, mandatory, not null after

    private final List<HashSet<String>> extraArgumentOptions = new ArrayList<>();

    private final @NotNull HashMap<String, AlexSubCommand> subCommands = new HashMap<>();
    private boolean isFinal = false; // will be set to true if a subCmd is added. Blocks all critical set functions.

    /**
     * Creates an AlexSubCommand and copies nearly everything from the (non-final) parent.
     * <p>Especially not inherited are: usageLine, output of help
     * <p>Note: The permission is inherited like "parentPermission.subCmdName" by default.
     * @see AlexSubCommand#isFinal()
     * @see AlexSubCommand#makeFinal()
     * @param name the name
     * @param helpLine the helpLine aka. description
     * @param parent the parent subCommand
     * @throws IllegalArgumentException if parent is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    protected AlexSubCommand(@NotNull String name, @NotNull TextComponent helpLine, @NotNull AlexSubCommand parent) throws IllegalArgumentException {
        this(name, helpLine);
        if (parent.isFinal)
            throw new IllegalArgumentException("parent must not be final");

        this.debugable = parent.debugable;
        this.prefix = parent.getPrefix();

        if (parent.cmdChain != null) {
            this.cmdChain = new TextComponent(parent.cmdChain);
            this.cmdChain.addExtra(" ");
            this.cmdChain.addExtra(name);
        } else {
            this.cmdChain = new TextComponent(name);
        }
        this.usagePrefix = parent.getUsagePrefix();

        this.isPlayerCmd = parent.isPlayerCmd;
        this.isConsoleCmd = parent.isConsoleCmd;
        if (parent.getPermission() != null) {
            this.permission = parent.getPermission() + "." + name.toLowerCase();
        }
        this.noPermissionLine = parent.getNoPermissionLine();
    }

    /**
     * Creates an AlexSubCommand and copies nearly everything from the (non-final) parent.
     * <p>Especially not inherited are: usageLine, output of help
     * <p>Note: The permission is inherited like "parentPermission.subCmdName" by default.
     * @see AlexSubCommand#isFinal()
     * @see AlexSubCommand#makeFinal()
     * @param name the name
     * @param helpLine the helpLine aka. description
     * @param parent the parent subCommand
     * @throws IllegalArgumentException if parent is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    protected AlexSubCommand(@NotNull String name, @NotNull String helpLine, @NotNull AlexSubCommand parent) throws IllegalArgumentException {
        this(name, new TextComponent(helpLine), parent);
    }

    /**
     * Creates an AlexSubCommand
     * <p>IMPORTANT: This constructor is strictly internal. DO NOT TOUCH.
     * @param name the name
     * @param helpLine the helpLine
     */
    @API(status = API.Status.INTERNAL, since="1.8.0")
    AlexSubCommand(@NotNull String name, @NotNull TextComponent helpLine) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.helpLine = helpLine.duplicate();
    }

    /**
     * Sends a message to the sender.
     * <p>Note: The idea is to use <a href="https://ci.md-5.net/job/BungeeCord/ws/chat/target/apidocs/net/md_5/bungee/api/chat/ComponentBuilder.html">ComponentBuilder</a> in order to get the baseComponents.
     * @see <a href="https://ci.md-5.net/job/BungeeCord/ws/chat/target/apidocs/net/md_5/bungee/api/chat/ComponentBuilder.html">ComponentBuilder</a>
     * @param sender the commandSender
     * @param baseComponents the message
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public static void sendMessage(@NotNull CommandSender sender, @NotNull BaseComponent[] baseComponents) {
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(baseComponents);
        } else {
            StringBuilder string = new StringBuilder();
            for (BaseComponent component : baseComponents) {
                string.append(component.toLegacyText());
            }
            sender.sendMessage(string.toString());
        }
    }

    /**
     * Sends a message to the sender.
     * @param sender the commandSender
     * @param baseComponent the message
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public static void sendMessage(@NotNull CommandSender sender, @NotNull BaseComponent baseComponent) {
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(baseComponent);
        } else {
            sender.sendMessage(baseComponent.toLegacyText());
        }
    }

    /**
     * Returns a message with the prefix upfront.
     * @param baseComponent all before the prefix
     * @return the prefixed message
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    @NotNull
    public BaseComponent getPrefixMessage(@NotNull BaseComponent baseComponent) {
        BaseComponent prefix = this.getPrefix();
        if (!prefix.toPlainText().isEmpty())
            prefix.addExtra(" ");
        prefix.addExtra(baseComponent);
        return prefix;
    }

    /**
     * Returns a message with the prefix upfront.
     * @param baseComponents all before the prefix
     * @return the prefixed message
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    @NotNull
    public BaseComponent getPrefixMessage(@NotNull BaseComponent[] baseComponents) {
        BaseComponent prefix = this.getPrefix();
        if (!prefix.toPlainText().isEmpty())
            prefix.addExtra(" ");
        prefix.addExtra(new TextComponent(baseComponents));
        return prefix;
    }

    // ================================================================================================================================================
    //  GETTER
    // ================================================================================================================================================

    @Nullable
    private AlexSubCommand getChild(String name) {
        return this.subCommands.get(name.toLowerCase());
    }

    /**
     * Gets the name.
     * @return the name
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Gets the helpLine (changes upon finalization!).
     * <p>Note: This line is sent if it gets requested by a parent's help command. It changes up finalization.
     * <p>Note 2: The default helpLine will be build/finalized using the prefix, the label, the cmdChain, the internal parameters, the cmdParamLine and finally the set helpLine.
     * <p>Note 3: If you want to interfere with the output, use the provided methods or overwrite this one.
     * @see AlexSubCommand#setCmdParamLine(BaseComponent)
     * @param label the label
     * @return the helpLine
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    @NotNull
    public BaseComponent getHelpLine(@NotNull String label) {
        return this.getPrefixMessage(new ComponentBuilder("/" + label + " ").color(ChatColor.GOLD).append(this.helpLine).create());
    }

    /**
     * Gets the prefix.
     * @return the prefix
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    @NotNull
    public BaseComponent getPrefix() {
        return prefix.duplicate();
    }

    /**
     * Gets the usage line (changes upon finalization!)
     * <p>Note: This line is sent to indicate wrong usage. It changes up finalization.
     * <p>Note 2: The default usageLine will be build/finalized using the usagePrefix, the label, the cmdChain, the internal parameters and the cmdParamLine.
     * <p>Note 3: If you want to interfere with the output, use the provided methods or overwrite this one. Only use usagePrefix, it will be prefixed after finalization.
     * @see AlexSubCommand#setUsagePrefix(BaseComponent)
     * @see AlexSubCommand#setCmdParamLine(BaseComponent)
     * @see AlexSubCommand#makeFinal()
     * @return the usage line
     * @throws IllegalStateException if this subCmd is not final.
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    @NotNull
    public BaseComponent getUsageLine(@NotNull String label) throws IllegalStateException {
        if (!this.isFinal)
            throw new IllegalStateException("usageLine can only be requested after finalization.");
        BaseComponent line = usagePrefix.duplicate();
        line.addExtra(" /" + label + " ");
        assert usageLine != null; // gets finalized
        line.addExtra(usageLine);
        return line;
    }

    /**
     * Gets the usage prefix (changes upon finalization!).
     * <p>Note: This value should not include the basic prefix and is used to build the usage line upon finalization.
     * @see AlexSubCommand#getUsageLine(String)
     * @see AlexSubCommand#makeFinal()
     * @return the usage prefix.
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    @NotNull
    public BaseComponent getUsagePrefix() {
        return this.usagePrefix.duplicate();
    }

    /**
     * Gets the permission.
     * @return the permission
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    @Nullable
    public String getPermission() {
        return this.permission;
    }

    /**
     * Gets the no permission line (changes upon finalization!).
     * <p>Note: This line is sent to indicate missing permissions. It changes upon finalization.
     * @see AlexSubCommand#makeFinal()
     * @return the no permission line
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    @Nullable
    public BaseComponent getNoPermissionLine() {
        if (this.noPermissionLine != null)
            return this.noPermissionLine.duplicate();
        return null;
    }

    /**
     * Gets the final state.
     * @see AlexSubCommand#makeFinal()
     * @return true if this subCmd is final, false otherwise
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public boolean isFinal() {
        return this.isFinal;
    }

    // ================================================================================================================================================
    //  SETTER
    // ================================================================================================================================================

    /**
     * Adds subCmds to this subCmd and makes it final.
     * <p>Note: Every added child must have an unique (lowercase) name. If this method does not throw an IAE it will make the parent final.
     * @see AlexSubCommand#makeFinal()
     * @param children the final children
     * @throws IllegalArgumentException if at least one child is not final, is named "help" or has a name which was already added.
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void addSubCmds(@NotNull Collection<? extends AlexSubCommand> children) throws IllegalArgumentException {
        for (AlexSubCommand child : children) {
            if (child.isFinal && !child.getName().equalsIgnoreCase("help") &&!subCommands.containsKey(child.name.toLowerCase())) {
                subCommands.put(child.name.toLowerCase(), child);
            } else {
                subCommands.clear();
                throw new IllegalArgumentException("added children must be final, not named \"help\" and unique (except lower-/uppercase) by name.");
            }
        }
        this.makeFinal();
    }

    /**
     * Adds subCmds to this subCmd and makes it final.
     * <p>Note: Every added child must have an unique (lowercase) name. If this method does not throw an IAE it will make the parent final.
     * @see AlexSubCommand#makeFinal()
     * @param child the final child
     * @throws IllegalArgumentException if at least one child is not final, is named "help" or has a name which was already added.
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void addSubCmds(@NotNull AlexSubCommand child) throws IllegalArgumentException {
        if (child.isFinal && !child.getName().equalsIgnoreCase("help") &&!subCommands.containsKey(child.name.toLowerCase())) {
            subCommands.put(child.name.toLowerCase(), child);
        } else {
            subCommands.clear();
            throw new IllegalArgumentException("added child must be final, not named \"help\" and unique (except lower-/uppercase) by name.");
        }
        this.makeFinal();
    }

    /**
     * Makes the subCmd final.
     * <p>Note: This method finalizes all messages meaning it applies all needed formats (i.e. prefix etc.). After finalization critical (custom) set methods should usually be blocked using {@link AlexSubCommand#isFinal()}.
     * <p><b>IMPORTANT: The default implementation just forwards to {@link AlexSubCommand#internalMakeFinal()}. Make sure to call this method when overwriting! This method is also expected to be called multiple times so use {@link AlexSubCommand#isFinal()} in order to not do formatting etc. twice.</b>
     * @see AlexSubCommand#internalMakeFinal()
     * @see AlexSubCommand#isFinal()
     * @throws IllegalStateException if mandatory values are not set
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void makeFinal() throws IllegalStateException {
        this.internalMakeFinal();
    }

    /**
     * Technical method to finalize this subCmd.
     * <p>Note: This method is expected to only be called by {@link AlexSubCommand#makeFinal()}. It finalizes all preset fields and blocks all critical set methods.
     * <p><b>IMPORTANT: Do not overwrite this method!</b>
     * @see AlexSubCommand#makeFinal()
     * @throws IllegalStateException if mandatory values are not set
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    protected void internalMakeFinal() throws IllegalStateException {
        if (!isFinal) {
            this.isFinal = true;

            if (this.cmdParamLine == null && !this.subCommands.isEmpty()) {
                this.cmdParamLine = new TextComponent("<subCmd>");
            }

            // creating helpCmdHeader...
            List<BaseComponent> header = new ArrayList<>();
            for (BaseComponent line : this.helpCmdHeader) {
                header.add(this.getPrefixMessage(line));
            }
            this.helpCmdHeader = header;

            // creating help & usage line...
            ComponentBuilder helpLineBuilder = null;
            ComponentBuilder usageLineBuilder = null;
            if (this.cmdChain != null) {
                helpLineBuilder = new ComponentBuilder(cmdChain.duplicate());
                usageLineBuilder = new ComponentBuilder(cmdChain.duplicate());
            }

            if (this.cmdParamLine != null) {
                if (helpLineBuilder != null) {
                    helpLineBuilder.append(" ").append(this.cmdParamLine.duplicate());
                } else {
                    helpLineBuilder = new ComponentBuilder(this.cmdParamLine.duplicate());
                }

                if (usageLineBuilder != null) {
                    usageLineBuilder.append(" ").append(this.cmdParamLine.duplicate());
                } else {
                    usageLineBuilder = new ComponentBuilder(this.cmdParamLine.duplicate());
                }
            }

            if (helpLineBuilder != null) {
                this.helpLine = new TextComponent(helpLineBuilder.append(": ").append(this.helpLine).color(prefix.getColor()).create());
            } else {
                this.helpLine = new TextComponent(new ComponentBuilder().append(this.helpLine).color(prefix.getColor()).create());
            }
            BaseComponent[] usagePrefix = new ComponentBuilder(this.getPrefix()).append(" ").append(this.usagePrefix).create();
            this.usagePrefix = new TextComponent(usagePrefix);
            this.usagePrefix.setColor(usagePrefix[usagePrefix.length - 1].getColor());
            if (usageLineBuilder != null) {
                this.usageLine = new TextComponent(usageLineBuilder.create());
            }

            // permission stuff...
            if (this.noPermissionLine == null)
                throw new IllegalStateException("noPermissionLine must be set.");
            this.noPermissionLine = this.getPrefixMessage(this.noPermissionLine);
        }
    }

    private static IllegalStateException getFinalException(@NotNull String value) {
        return new IllegalStateException(value + " cannot be changed after finalization/adding a child.");
    }

    /**
     * Adds an extra argument.
     * <p>Note: extra arguments claim the next available argument of the executed command successively. They have to be one of the set options or the usage line gets send.
     * @see AlexSubCommand#execute(CommandSender, String, List, List, String[], int)
     * @param helpLineParameter the parameter added to the command line chain.
     * @param options the available options
     * @throws IllegalStateException if the subCmd is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void addExtraArgument(@NotNull BaseComponent helpLineParameter, @NotNull Collection<String> options) throws IllegalStateException {
        if (isFinal)
            throw getFinalException("extraArgument");

        Objects.requireNonNull(helpLineParameter, "helpLineParameter must not be null.");
        Objects.requireNonNull(options, "options must not be null");

        if (cmdChain == null) {
            cmdChain = new TextComponent("<");
        } else {
            cmdChain.addExtra(" <");
        }
        cmdChain.addExtra(helpLineParameter);
        cmdChain.addExtra(">");


        this.extraArgumentOptions.add(new HashSet<>(options));
    }

    /**
     * Adds an extra argument.
     * <p>Note: extra arguments claim the next available argument of the executed command successively. They have to be one of the set options or the usage line gets send.
     * @see AlexSubCommand#execute(CommandSender, String, List, List, String[], int)
     * @param helpLineParameter the parameter added to the command line chain.
     * @param options the available options
     * @throws IllegalStateException if the subCmd is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void addExtraArgument(@NotNull String helpLineParameter, @NotNull Collection<String> options) throws IllegalStateException {
        this.addExtraArgument(new TextComponent(helpLineParameter), options);
    }

    /**
     * Edits an extraArgument's options.
     * <p>Note: The index just refers to the extraArguments, so in order to edit the first extraArgument's option it would be index 0 and so on.
     * <p><b>IMPORTANT: There must already be an extraArgument in order to edit its options!</b>
     * @see AlexSubCommand#addExtraArgument(BaseComponent, Collection)
     * @param index the index of the extraArgument
     * @param options the new options
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void editExtraArgumentOption(int index, @NotNull Collection<String> options) {
        this.extraArgumentOptions.set(index, new HashSet<>(options));
    }

    /**
     * Sets the prefix.
     * @param prefix the prefix
     * @throws IllegalStateException if the subCmd is already final
     */
    @API(status = API.Status.STABLE, since="1.8.0")
    public void setPrefix(@NotNull BaseComponent prefix) throws IllegalStateException {
        if (isFinal)
            throw getFinalException("prefix");

        this.prefix = prefix.duplicate();
    }

    /**
     * Sets the prefix.
     * <p>Note: The color will be set to the last baseComponent's color. Therefore not explicitly set colors in the array might change.
     * @param prefix the prefix
     * @throws IllegalStateException if the subCmd is already final
     * @throws IllegalArgumentException if prefix is empty
     */
    @API(status = API.Status.STABLE, since="1.8.0")
    public void setPrefix(@NotNull BaseComponent[] prefix) throws IllegalStateException, IllegalArgumentException {
        if (prefix.length == 0)
            throw new IllegalArgumentException("prefix is empty");

        TextComponent newPrefix = new TextComponent(prefix);
        newPrefix.setColor(prefix[prefix.length - 1].getColor());
        this.setPrefix(newPrefix);
    }

    /**
     * Sets the permission.
     * @see AlexSubCommand#execute(CommandSender, String, List, List, String[], int)
     * @see AlexSubCommand#canExecute(CommandSender)
     * @param permission the permission to execute this subCmd.
     * @throws IllegalStateException if the subCmd is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void setPermission(@Nullable String permission) throws IllegalStateException {
        if (isFinal)
            throw getFinalException("permission");
        this.permission = permission;
    }

    /**
     * Sets if this (and all following) subCmd should be executable by players.
     * @param value the value
     * @throws IllegalStateException if the subCmd is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void setIsPlayerCmd(boolean value) throws IllegalStateException {
        if (isFinal)
            throw getFinalException("isPlayerCmd");
        this.isPlayerCmd = value;
    }

    /**
     * Sets if this (and all following) subCmd should be executable via console.
     * @param value the value
     * @throws IllegalStateException if the subCmd is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void setIsConsoleCmd(boolean value) throws IllegalStateException {
        if (isFinal)
            throw getFinalException("isConsoleCmd");
        this.isConsoleCmd = value;
    }

    /**
     * Sets the line to indicate missing permissions.
     * <p>Note: Set lines should not include prefixes or similar. All default formatting is done by finalizing.
     * @see AlexSubCommand#makeFinal()
     * @param line the no permission line
     * @throws IllegalStateException if the subCmd is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void setNoPermissionLine(@NotNull BaseComponent line) throws IllegalStateException {
        if (isFinal)
            throw getFinalException("noPermissionLine");
        this.noPermissionLine = line.duplicate();
    }

    /**
     * Sets the line to indicate missing permissions.
     * <p>Note: Set lines should not include prefixes or similar. All default formatting is done by finalizing.
     * @see AlexSubCommand#makeFinal()
     * @param line the no permission line
     * @throws IllegalStateException if the subCmd is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void setNoPermissionLine(@NotNull BaseComponent[] line) throws IllegalStateException {
        this.setNoPermissionLine(new TextComponent(line));
    }

    /**
     * Adds a header line above the integrated help subCmd.
     * <p>Note: Added lines should not include prefixes or similar. All default formatting is done by finalizing.
     * @see AlexSubCommand#makeFinal()
     * @param line the line
     * @throws IllegalStateException if the subCmd is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void addHelpCmdHeaderLine(@NotNull BaseComponent line) throws IllegalStateException {
        if (isFinal)
            throw getFinalException("noPermissionLine");
        this.helpCmdHeader.add(line.duplicate());
    }

    /**
     * Adds a header line above the integrated help subCmd.
     * <p>Note: Added lines should not include prefixes or similar. All default formatting is done by finalizing.
     * @see AlexSubCommand#makeFinal()
     * @param line the line
     * @throws IllegalStateException if the subCmd is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void addHelpCmdHeaderLine(@NotNull BaseComponent[] line) throws IllegalStateException {
        this.helpCmdHeader.add(new TextComponent(line));
    }

    /**
     * Sets the command parameter line used in the integrated help subCmd.
     * <p>This line should add all parameters that are not added by any other method, i. e. all parameter beyond extraArguments.
     * <p>Note: Set lines should not include prefixes or similar. All default formatting is done by finalizing.
     * @see AlexSubCommand#makeFinal()
     * @param line the line
     * @throws IllegalStateException if the subCmd is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void setCmdParamLine(@NotNull BaseComponent line) throws IllegalStateException {
        if (isFinal)
            throw getFinalException("CmdParamLine");
        this.cmdParamLine = line.duplicate();
    }

    /**
     * Sets the usage prefix used in the usage line.
     * <p>Note: The prefix should not include the main subCmd prefix. All default formatting is done by finalizing.
     * @param line the prefix
     * @throws IllegalStateException if the subCmd is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void setUsagePrefix(@NotNull final BaseComponent line) throws IllegalStateException {
        if (isFinal)
            throw getFinalException("UsagePrefix");
        this.usagePrefix = line.duplicate();
    }

    /**
     * Sets the usage prefix used in the usage line.
     * <p>Note: The prefix should not include the main subCmd prefix. All default formatting is done by finalizing.
     * <p>Note 2: The color will be set to the last baseComponent's color. Therefore not explicitly set colors in the array might change.
     * @param line the prefix
     * @throws IllegalStateException if the subCmd is already final
     * @throws IllegalArgumentException if line is empty
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void setUsagePrefix(@NotNull final BaseComponent[] line) throws IllegalStateException, IllegalArgumentException {
        if (isFinal)
            throw getFinalException("UsagePrefix");
        if (line.length == 0)
            throw new IllegalArgumentException("line is empty");

        TextComponent newUsagePrefix = new TextComponent(line);
        newUsagePrefix.setColor(line[line.length - 1].getColor());
        this.setUsagePrefix(newUsagePrefix);
    }

    // ================================================================================================================================================
    //  HELP COMMAND
    // ================================================================================================================================================

    @API(status = API.Status.INTERNAL, since ="1.8.0")
    private void help(@NotNull CommandSender sender, @NotNull String label, @NotNull List<AlexSubCommand> previousCmds, @NotNull List<String> previousExtraArguments, @NotNull String[] args, final int startIndex) {
        for (BaseComponent baseComponent : helpCmdHeader) {
            sendMessage(sender, baseComponent);
        }

        if (this.subCommands.keySet().isEmpty()) {
            sendMessage(sender, this.getHelpLine(label));
        } else {
            for (String subCmdName : this.subCommands.keySet()) {
                AlexSubCommand subCmd = this.subCommands.get(subCmdName);
                if (subCmd.internalCanExecute(sender))
                    sendMessage(sender, subCmd.getHelpLine(label));
            }
        }
    }

    // ================================================================================================================================================
    //  EXECUTION
    // ================================================================================================================================================

    @Nullable // returns null if not enough arguments exist or some set argument is not set correctly.
    private List<String> getNewExtraArguments(@NotNull List<String> previousExtraArguments, @NotNull String[] args, int startIndex) {
        List<String> newExtraArguments = new ArrayList<>(previousExtraArguments);
        int startIndexAfterExtraArguments = startIndex + extraArgumentOptions.size();

        if (args.length <= startIndexAfterExtraArguments - 1) {
            return null;
        }

        for (int i = startIndex, j = 0; i < startIndexAfterExtraArguments; i++, j++) {
            if (extraArgumentOptions.get(j).stream().anyMatch(args[i]::equalsIgnoreCase)) {
                newExtraArguments.add(args[i]);
            } else {
                return null;
            }
        }
        return newExtraArguments;
    }

    /**
     * Gets if a sender can internally executes this command.
     * <p><b>IMPORTANT: This method is strictly internal. DO NOT TOUCH!</b> Please use the already provided methods to interfere with the return value.
     * @see AlexSubCommand#getPermission()
     * @see AlexSubCommand#setPermission(String)
     * @see AlexSubCommand#setIsConsoleCmd(boolean)
     * @see AlexSubCommand#setIsPlayerCmd(boolean)
     * @see AlexSubCommand#canExecute(CommandSender)
     * @param sender the sender
     * @return value of {@link AlexSubCommand#canExecute(CommandSender)} if the sender passes all conditions (permission, player/console instance check), false otherwise
     */
    @API(status = API.Status.INTERNAL, since ="1.8.0")
    protected boolean internalCanExecute(@NotNull CommandSender sender) {
        if (((sender instanceof Player) && this.isPlayerCmd) || ((sender instanceof ConsoleCommandSender) && this.isConsoleCmd)) {
            if (permission == null || sender.hasPermission(this.permission)) {
                ConsoleMessage.debug(this.getClass(), debugable, "sender passed internalCanExecute");
                return this.canExecute(sender);
            }
        }
        return false;
    }

    /**
     * Gets if a commandSender can execute this subCmd.
     * <p>This method gets called after a commandSender has passed {@link AlexSubCommand#internalCanExecute(CommandSender)} and will always return true by default. Overwrite this method if you want to add special conditions.
     * @see AlexSubCommand#internalCanExecute(CommandSender)
     * @param sender the sender
     * @return true if the sender can execute this command, false otherwise
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    protected boolean canExecute(@NotNull CommandSender sender) {
        return true;
    }

    /**
     * Internally executes this command.
     * <p>This method gets chain called by all subCmds. If all extraArguments are satisfied and no subCmd is found, {@link AlexSubCommand#execute(CommandSender, String, List, List, String[], int)} gets called.
     * <p>Note: For more precise information please see {@link AlexSubCommand#execute(CommandSender, String, List, List, String[], int)}
     * <p><b>IMPORTANT: This method is strictly internal. DO NOT TOUCH!</b>
     * @see AlexSubCommand#execute(CommandSender, String, List, List, String[], int)
     * @see AlexSubCommand#canExecute(CommandSender)
     * @param sender the sender
     * @param label the label
     * @param previousCmds the previousCmds
     * @param previousExtraArguments the previousExtraArguments added by previousCmds
     * @param args the args
     * @param startIndex the index of the first argument of this subCmd
     */
    @API(status = API.Status.INTERNAL, since ="1.8.0")
    protected void internalExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<AlexSubCommand> previousCmds, @NotNull List<String> previousExtraArguments, @NotNull final String[] args, int startIndex) {
        if (!this.internalCanExecute(sender)) {
            ConsoleMessage.debug(this.getClass(), debugable, "EXECUTION: sender did not pass internalCanExecute");
            assert noPermissionLine != null; // is mandatory
            sendMessage(sender, noPermissionLine);
            return;
        }

        if (args.length > startIndex && args[startIndex].equalsIgnoreCase("help")) {
            ConsoleMessage.debug(this.getClass(), debugable, "EXECUTION: args[" + startIndex + "] is help");
            this.help(sender, label, previousCmds, previousExtraArguments, args, startIndex);
            return;
        }

        List<String> newExtraArguments = this.getNewExtraArguments(previousExtraArguments, args, startIndex);
        if (newExtraArguments == null) { // too little arguments, an extraArgument is set wrong
            ConsoleMessage.debug(this.getClass(), debugable, "EXECUTION: newExtraArguments is null");
            sendMessage(sender, this.getUsageLine(label));
            return;
        }

        int startIndexAfterExtraArguments = startIndex + extraArgumentOptions.size();

        // check for subCmds....
        if (args.length > startIndexAfterExtraArguments) {
            ConsoleMessage.debug(this.getClass(), debugable, "EXECUTION: There is another argument after extraArguments, searching for subCmd...");
            AlexSubCommand subCmd = this.getChild(args[startIndexAfterExtraArguments]);
            if (subCmd != null) {
                ConsoleMessage.debug(this.getClass(), debugable, "EXECUTION: found subCmd " + subCmd.getName());
                for (int i = 0; i <= extraArgumentOptions.size(); i++) {
                    previousCmds.add(this);
                }
                subCmd.internalExecute(sender, label, previousCmds, newExtraArguments, args, startIndexAfterExtraArguments + 1);
                return;
            }
            ConsoleMessage.debug(this.getClass(), debugable, "EXECUTION: Did not find any subCmd named " + args[startIndexAfterExtraArguments]);
        }

        ConsoleMessage.debug(this.getClass(), debugable, "EXECUTION: calling execute method...");
        if (!this.execute(sender, label, previousCmds, newExtraArguments, args, startIndexAfterExtraArguments)) {
            sendMessage(sender, this.getUsageLine(label));
        }

    }

    /**
     * Executes this command.
     * <p>Gets called by {@link AlexSubCommand#internalExecute(CommandSender, String, List, List, String[], int) } when {@link AlexSubCommand#internalCanExecute(CommandSender)} is true (i. e. sender meets all criteria and {@link AlexSubCommand#canExecute(CommandSender)} is true), all extraArguments are satisfied and no subCmd is found.
     * <p>Note: The startIndex will point to the first argument which was not already processed (i.e. the first arg this execute method should process/after extraArguments). The previousCmds and previousExtraArguments will have been updated by the {@link AlexSubCommand#internalExecute(CommandSender, String, List, List, String[], int)} respectively.
     * @see AlexSubCommand#internalExecute(CommandSender, String, List, List, String[], int)
     * @see AlexSubCommand#canExecute(CommandSender)
     * @see AlexSubCommand#internalCanExecute(CommandSender)
     * @param sender the sender
     * @param label the label
     * @param previousCmds the previousCmds
     * @param previousExtraArguments the previousExtraArguments added by previousCmds
     * @param args the args
     * @param startIndex the index of the first argument of this subCmd
     * @return true if the command was used correctly, false otherwise
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    protected boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull List<AlexSubCommand> previousCmds, @NotNull List<String> previousExtraArguments, @NotNull final String[] args, int startIndex) {
        return false;
    }

    // ================================================================================================================================================
    //  TAB COMPLETION
    // ================================================================================================================================================

    /**
     * Adds additional tabCompleterOptions to this subCmd.
     * <p>This method is supposed to add new options alongside the children's names and gets called by {@link AlexSubCommand#getInternalTabCompletion(CommandSender, String, List, List, String[], int)} only if the tabCompletion is not beyond this index. The list is supposed to contain every option for the specific sender and will be sorted etc. internally.
     * <p>Note: For tabCompletions after the children's names (i. e. the first argument which is used within {@link AlexSubCommand#execute(CommandSender, String, List, List, String[], int)}) use/overwrite {@link AlexSubCommand#getTabCompletion(CommandSender, String, List, List, String[], int)}
     * @see AlexSubCommand#getInternalTabCompletion(CommandSender, String, List, List, String[], int)
     * @see AlexSubCommand#getTabCompletion(CommandSender, String, List, List, String[], int)
     * @param sender the sender
     * @param label the label
     * @param previousCmds the previousCmds
     * @param previousExtraArguments the previousExtraArguments added by previousCmds
     * @param args the args
     * @param startIndex the index of the first argument of this subCmd
     * @return a list of additional options
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    @NotNull
    protected List<String> additionalTabCompleterOptions(@NotNull CommandSender sender, @NotNull String label, @NotNull List<AlexSubCommand> previousCmds, @NotNull List<String> previousExtraArguments, @NotNull final String[] args, int startIndex) {
        return new ArrayList<>();
    }

    /**
     * Manages the tabCompletion.
     * <p>This method gets chain called by all subCmds and manages the tabCompletion.
     * <p>Note: TabCompletion will be interrupted if {@link AlexSubCommand#internalCanExecute(CommandSender)} is false or already set extraArguments are not set correctly.
     * <p><b>IMPORTANT: This method is strictly internal. DO NOT TOUCH!</b> Please use other provided methods in order to interfere with tabCompletion.
     * @see AlexSubCommand#additionalTabCompleterOptions(CommandSender, String, List, List, String[], int)
     * @see AlexSubCommand#getTabCompletion(CommandSender, String, List, List, String[], int)
     * @param sender the sender
     * @param label the label
     * @param previousCmds the previousCmds
     * @param previousExtraArguments the previousExtraArguments added by previousCmds
     * @param args the args
     * @param startIndex the index of the first argument of this subCmd
     * @return a list of tabCompletions
     */
    @API(status = API.Status.INTERNAL, since ="1.8.0")
    @NotNull
    protected List<String> getInternalTabCompletion(@NotNull CommandSender sender, @NotNull String label, @NotNull List<AlexSubCommand> previousCmds, @NotNull List<String> previousExtraArguments, @NotNull final String[] args, int startIndex) {
        List<String> completions = new ArrayList<>();

        if (!this.internalCanExecute(sender)) {
            return new ArrayList<>();
        }

        int startIndexAfterExtraArguments = startIndex + extraArgumentOptions.size();

        if (args.length > startIndexAfterExtraArguments) {
            ConsoleMessage.debug(this.getClass(), debugable, "TAB-COMPLETION: args.length > indexAfterExtraArguments");

            List<String> newExtraArguments = this.getNewExtraArguments(previousExtraArguments, args, startIndex);
            if (newExtraArguments == null) { // only null if some extraArgument was set wrong.
                ConsoleMessage.debug(this.getClass(), debugable, "TAB-COMPLETION: extraArgument is set wrong");
                return new ArrayList<>();
            }

            // updating previous commands for every extraArgument...
            for (int i = 0; i < extraArgumentOptions.size(); i++) {
                previousCmds.add(this);
            }

            // search for subCmd...
            AlexSubCommand subCmd = this.getChild(args[startIndexAfterExtraArguments]);
            if (subCmd != null) {
                ConsoleMessage.debug(this.getClass(), debugable, "TAB-COMPLETION: found subCmd " + subCmd.getName());
                return subCmd.getInternalTabCompletion(sender, label, previousCmds, newExtraArguments, args, startIndexAfterExtraArguments + 1);
            }

            // is it beyond the subCmd names index?
            if (args.length > startIndexAfterExtraArguments + 1) {
                ConsoleMessage.debug(this.getClass(), debugable, "TAB-COMPLETION: beyond subCmd names Index, returning getTabCompletion...");
                return this.getTabCompletion(sender, label, previousCmds, previousExtraArguments, args, startIndexAfterExtraArguments + 1);
            }

            Set<String> subCmdNames = new HashSet<>();
            for (AlexSubCommand cmd : subCommands.values()) {
                subCmdNames.add(cmd.getName());
            }

            if (extraArgumentOptions.size() == 0)
                subCmdNames.add("help");

            StringUtil.copyPartialMatches(args[startIndexAfterExtraArguments], subCmdNames, completions);
            StringUtil.copyPartialMatches(args[startIndexAfterExtraArguments], this.additionalTabCompleterOptions(sender, label, previousCmds, newExtraArguments, args, startIndexAfterExtraArguments), completions);

        } else if (args.length > startIndex) { // extraArgumentIndex
            Set<String> options = new HashSet<>();
            if (startIndex == args.length - 1)
                options.add("help");

            for (int i = startIndex; i < startIndexAfterExtraArguments; i++) {
                if (i == args.length - 1) {
                    options.addAll(extraArgumentOptions.get(i - startIndex));
                    StringUtil.copyPartialMatches(args[i], options, completions);
                    ConsoleMessage.debug(this.getClass(), debugable, "TAB-COMPLETION: we are at extraArgumentOptions list index " + (i - startIndex));
                    break;
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }

    /**
     * Gets the tabCompletion for this command.
     * <p>This method gets called by {@link AlexSubCommand#getInternalTabCompletion(CommandSender, String, List, List, String[], int)} when the sender can execute the command, all already set extraArguments are set correctly and no SubCmd was found while args.length is beyond this index.
     * <p>Note: The startIndex will point to the first argument which was not already processed (i.e. the first arg after the children's names/additional tabCompletions). The previousCmds and previousExtraArguments will have been updated by the {@link AlexSubCommand#getInternalTabCompletion(CommandSender, String, List, List, String[], int)} respectively.
     * <p>Note2: The returned list is expected to be filled using <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/util/StringUtil.html#copyPartialMatches-java.lang.String-java.lang.Iterable-T-">Spigot#StringUtil.copyPartialMatches</a>and sorted via <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#sort-java.util.List-">Collections.sort</a>
     * @see <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/util/StringUtil.html">Spigot#StringUtil</a>
     * @see AlexSubCommand#additionalTabCompleterOptions(CommandSender, String, List, List, String[], int)
     * @see AlexSubCommand#getInternalTabCompletion(CommandSender, String, List, List, String[], int)
     * @param sender the sender
     * @param label the label
     * @param previousCmds the previousCmds
     * @param previousExtraArguments the previousExtraArguments added by previousCmds
     * @param args the args
     * @param startIndex the index of the first argument of this subCmd
     * @return a list of tabCompletions
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    @NotNull
    protected List<String> getTabCompletion(@NotNull CommandSender sender, @NotNull String label, @NotNull List<AlexSubCommand> previousCmds, @NotNull List<String> previousExtraArguments, @NotNull String[] args, int startIndex) {
        return new ArrayList<>();
    }
}
