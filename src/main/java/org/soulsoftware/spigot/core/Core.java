package org.soulsoftware.spigot.core;

import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.soulsoftware.spigot.core.Events.ServerTickEvent;

public final class Core extends JavaPlugin {
    @Override
    public void onEnable() {
        MinecraftVersion.disableUpdateCheck();
        MinecraftVersion.disableBStats();
        MinecraftVersion.disablePackageWarning();
        Bukkit.getScheduler().runTaskTimer(this, () ->
                Bukkit.getServer().getPluginManager().callEvent(new ServerTickEvent()), 0, 0);
    }
}
