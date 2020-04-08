package com.github.alexqp.commons.messages;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apiguardian.api.API;
import org.jetbrains.annotations.NotNull;

/**
 * A utility class to translate messages.
 */
public class MessageTranslator {

    private MessageTranslator() {}

    /**
     * Translates a (legacy) Bukkit message into the ChatComponentAPI.
     * <p>Note: The color codes should be prefixed with {@literal &}
     * @see <a href="https://ci.md-5.net/job/BungeeCord/ws/chat/target/apidocs/net/md_5/bungee/api/chat/package-summary.html">Chat Component API of Bungee</a>
     * @param msg the msg
     * @return the translated msg
     */
    @API(status = API.Status.STABLE, since = "1.8.0")
    @NotNull
    public static BaseComponent[] translateBukkitColorCodes(@NotNull String msg) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg));
    }

    /**
     * Translates a (legacy) Bukkit message into the ChatComponentAPI.
     * <p>Note: The color codes should be prefixed with {@literal &}
     * @see <a href="https://ci.md-5.net/job/BungeeCord/ws/chat/target/apidocs/net/md_5/bungee/api/chat/package-summary.html">Chat Component API of Bungee</a>
     * @param msg the msg
     * @param defTextColor the default text color
     * @return the translated msg
     */
    @API(status = API.Status.STABLE, since = "1.8.0")
    @NotNull
    public static BaseComponent[] translateBukkitColorCodes(@NotNull String msg, @NotNull ChatColor defTextColor) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg), defTextColor);
    }
}
