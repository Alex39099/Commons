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

package com.github.alexqp.commons.config;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public enum ConsoleErrorType {

    /**
     * Represents a warning message.
     */
    WARN(ChatColor.GOLD),
    /**
     * Represents an error/severe message.
     */
    ERROR(ChatColor.RED),
    /**
     * Dummy to represent no message.
     */
    NONE;

    private ChatColor color = ChatColor.WHITE;

    ConsoleErrorType() {}

    ConsoleErrorType(ChatColor chatColor) {
        color = chatColor;
    }

    /**
     * Returns the ChatColor.
     * @return the chatColor
     */
    @NotNull
    public ChatColor getColor() {
        return this.color;
    }
}
