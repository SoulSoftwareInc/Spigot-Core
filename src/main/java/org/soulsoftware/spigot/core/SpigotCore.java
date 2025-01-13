package org.soulsoftware.spigot.core;

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.soulsoftware.spigot.core.Events.ServerTickEvent;
import org.soulsoftware.spigot.core.Utilities.VersionUtility;

public class SpigotCore {

    /**
     * Call in your plugins {@code onEnable}
     */
    public void initialize(Plugin plugin) {
        MinecraftVersion.disableUpdateCheck();
        MinecraftVersion.disableBStats();
        MinecraftVersion.disablePackageWarning();
        Bukkit.getScheduler().runTaskTimer(plugin, () ->
                Bukkit.getServer().getPluginManager().callEvent(new ServerTickEvent()), 0, 0);
    }

}
