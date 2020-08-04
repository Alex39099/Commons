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
