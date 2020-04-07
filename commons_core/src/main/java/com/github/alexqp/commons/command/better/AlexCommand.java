package com.github.alexqp.commons.command.better;

import com.github.alexqp.commons.messages.ConsoleMessage;
import com.github.alexqp.commons.messages.SetDebugable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.apiguardian.api.API;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is a part of the AlexCommand API. Instances of this class are supposed to be the <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/command/TabExecutor.html">TabExecutor</a> of a PluginCommand specified in the plugin.yml. In order to set an instance as TabExecutor, please use {@link AlexCommand#register()}.
 * <p>AlexSubCommands are supposed to be constructed with an instance <b>after</b> setting the usagePrefix (and permission dummy) but <b>before</b> finalization.
 */
public class AlexCommand extends AlexSubCommand implements TabExecutor {

    /**
     * Controls debug messages for this API.
     * <p>The debug status is inherited by all children.
     */
    @API(status = API.Status.STABLE, since = "1.8.0")
    public SetDebugable debugable = new SetDebugable() {
        private boolean debug = false;

        @Override
        public void setDebug(boolean debug) {
            this.debug = debug;
        }

        @Override
        public String getName() {
            return "ALEXCOMMAND-API";
        }

        @Override
        public boolean getDebug() {
            return debug;
        }
    };

    private @NotNull JavaPlugin plugin;
    private @NotNull List<BaseComponent[]> creditLines = new ArrayList<>();

    private AlexCommand(@NotNull String name, @NotNull JavaPlugin plugin) {
        super(name, new TextComponent("All Commands."));
        this.plugin = plugin;
        this.addCreditLine(this.getBasicCreditLine(plugin));
    }

    /**
     * Creates an AlexCommand
     * @param name the name
     * @param plugin the plugin
     * @param pluginPrefixColor the prefixColor for the plugin's name
     */
    @API(status = API.Status.STABLE, since = "1.8.0")
    public AlexCommand(@NotNull String name, @NotNull JavaPlugin plugin, @NotNull ChatColor pluginPrefixColor) {
        this(name, plugin);
        ComponentBuilder prefix = new ComponentBuilder("[").append(plugin.getName()).color(pluginPrefixColor).append("]", ComponentBuilder.FormatRetention.NONE);
        this.setPrefix(new TextComponent(prefix.create()));
    }

    /**
     * Creates an AlexCommand
     * @param name the name
     * @param plugin the plugin
     * @param pluginPrefixColor the prefixColor for the plugin's name
     * @param defTextColor the default color to display text messages
     */
    @API(status = API.Status.STABLE, since = "1.8.0")
    public AlexCommand(@NotNull String name, @NotNull JavaPlugin plugin, @NotNull ChatColor pluginPrefixColor, @NotNull ChatColor defTextColor) {
        this(name, plugin);
        ComponentBuilder prefix = new ComponentBuilder("[").color(defTextColor).append(plugin.getName()).color(pluginPrefixColor).append("]", ComponentBuilder.FormatRetention.NONE).color(defTextColor);
        this.setPrefix(new TextComponent(prefix.create()));
    }

    /**
     * Sets this AlexCommand as TabExecutor.
     * @see AlexSubCommand#makeFinal()
     * @throws IllegalStateException if this instance is not final
     * @return true if it was successful, false otherwise
     */
    @API(status = API.Status.STABLE, since = "1.8.0")
    public boolean register() throws IllegalStateException {
        if (!this.isFinal())
            throw new IllegalStateException("instance must be final before registering");

        PluginCommand command = plugin.getCommand(this.getName());
        if (command != null) {
            command.setTabCompleter(this);
            command.setExecutor(this);
            ConsoleMessage.debug(this.getClass(), debugable, "Registered command.");
            return true;
        } else {
            ConsoleMessage.debug(this.getClass(), debugable, "Could not register command because PluginCommand was null for " + this.getName() + ". Please register it in the plugin.yml");
            return false;
        }
    }

    // ================================================================================================================================================
    //  CREDIT LINE STUFF
    // ================================================================================================================================================

    private BaseComponent[] getBasicCreditLine(@NotNull JavaPlugin plugin) {
        ComponentBuilder builder = new ComponentBuilder("version " + plugin.getDescription().getVersion()).append(new TextComponent(", author alex_qp"));
        builder.event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/members/alex_qp.306806/"));
        return builder.create();
    }

    /**
     * Adds a creditLine.
     * <p>Note: Added lines should not include prefixes or similar. All default formatting is done by finalizing.
     * @see AlexSubCommand#makeFinal()
     * @see AlexCommand#onCommand(CommandSender, Command, String, String[])
     * @param line the line
     * @throws IllegalStateException if the cmd is already final
     */
    @API(status = API.Status.STABLE, since ="1.8.0")
    public void addCreditLine(@NotNull BaseComponent[] line) throws IllegalStateException {
        if (isFinal())
            throw new IllegalStateException("creditLines cannot be edited ");
        Objects.requireNonNull(line, "line must not be null");
        creditLines.add(line);
    }

    /**
     * Removes all creditLines.
     * @see AlexCommand#onCommand(CommandSender, Command, String, String[])
     * @throws IllegalStateException if the cmd is already final
     */
    @API(status = API.Status.EXPERIMENTAL, since ="1.8.0")
    public void clearCreditLines() throws IllegalStateException {
        if (isFinal())
            throw new IllegalStateException("creditLine");
        creditLines.clear();
    }

    private void credits(@NotNull CommandSender sender) {
        for (BaseComponent[] creditLine : creditLines) {
            sendMessage(sender, creditLine);
        }
    }

    // ================================================================================================================================================
    //  INTERNAL OVERRIDES
    // ================================================================================================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public void internalMakeFinal() {
        List<BaseComponent[]> newCreditLines = new ArrayList<>();
        for (BaseComponent[] creditLine : creditLines) {
            newCreditLines.add(this.getPrefixMessage(creditLine));
        }
        this.creditLines = newCreditLines;
        ((AlexSubCommand) this).internalMakeFinal();
    }

    /**
     * Bukkit interface method.
     * <p>If args is empty, the creditLines will be sent to the sender. Otherwise this method redirects the arguments to {@link AlexSubCommand#internalExecute(CommandSender, String, List, List, String[], int)}.
     * <p><b>IMPORTANT: This method is strictly internal. DO NOT TOUCH!</b>
     * @see AlexSubCommand#internalExecute(CommandSender, String, List, List, String[], int)
     * @param sender the sender
     * @param command the command
     * @param label the label
     * @param args the args
     * @return true
     */
    @API(status = API.Status.INTERNAL, since = "1.8.0")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            this.credits(sender);
            return true;
        }

        this.internalExecute(sender, label, new ArrayList<>(), new ArrayList<>(), args, 0);
        return true;
    }

    /**
     * Bukkit interface method.
     * <p>This method redirects the arguments to the internal tabCompletion.
     * <p><b>IMPORTANT: This method is strictly internal. DO NOT TOUCH!</b>
     * @see AlexSubCommand#getInternalTabCompletion(CommandSender, String, List, List, String[], int)
     * @param sender the sender
     * @param command the command
     * @param label the label
     * @param args the args
     * @return a list of tabCompletions
     */
    @API(status = API.Status.INTERNAL, since = "1.8.0")
    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return this.getInternalTabCompletion(sender, label, new ArrayList<>(), new ArrayList<>(), args, 0);
    }
}
