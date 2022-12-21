package org.soulsoftware.spigot.core.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VersionManager {
    public static boolean isAir(ItemStack stack) {
        if (stack == null) return true;
        return stack.getType().equals(Material.AIR);
    }

    public static Integer getServerVersion() {
        return Integer.parseInt(Bukkit.getServer().getClass().getName().split("\\.")[3].split("_")[1]);
    }
}
