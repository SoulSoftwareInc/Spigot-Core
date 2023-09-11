package org.soulsoftware.spigot.core.Utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VersionManager {
    @Getter
    private final static Integer serverVersion = Integer.parseInt(Bukkit.getServer().getClass().getName().split("\\.")[3].split("_")[1]);
    public static boolean isAir(ItemStack stack) {
        if (stack == null) return true;
        return stack.getType().equals(Material.AIR);
    }

}
