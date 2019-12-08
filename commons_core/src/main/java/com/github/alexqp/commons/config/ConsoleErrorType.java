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
