package org.soulsoftware.spigot.core.Utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VersionManager {
    @Getter
    private final static Double serverVersion;

    static {
        Double sV = 0d;
        String[] scoreSplit = Bukkit.getServer().getClass().getName().split("\\.")[3].split("_");
        sV += Integer.parseInt(scoreSplit[1]);
        if(scoreSplit.length==3) sV += Integer.parseInt(scoreSplit[2].substring(1))/10d;

        serverVersion = sV;

    }
    public static boolean isAir(ItemStack stack) {
        if (stack == null) return true;
        return stack.getType().equals(Material.AIR);
    }

}
