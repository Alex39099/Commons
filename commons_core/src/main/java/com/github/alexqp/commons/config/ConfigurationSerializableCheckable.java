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

package com.github.alexqp.commons.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * This interface is used in {@link ConfigChecker}.
 * @see ConfigChecker#checkSerializable(ConfigurationSection, String, ConsoleErrorType, ConfigurationSerializableCheckable, boolean)
 * @see ConfigChecker#checkSerializable(ConfigurationSection, String, ConsoleErrorType, Class, boolean)
 */

public interface ConfigurationSerializableCheckable extends ConfigurationSerializable {

    /**
     * Gets executed by ConfigChecker to check an instance.
     * @param checker the configChecker
     * @param section the section
     * @param path the path within the section (i. e. the name)
     * @param errorType the ConsoleErrorType (controls console messages)
     * @param overwrite should values get overwritten?
     * @return true if all values are set correctly, false otherwise. This has no impact on ConfigChecker if overwrite is false (i. e. no extra msg)
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean checkValues(ConfigChecker checker, ConfigurationSection section, String path, ConsoleErrorType errorType, boolean overwrite);
}
