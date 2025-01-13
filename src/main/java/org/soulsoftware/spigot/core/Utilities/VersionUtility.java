package org.soulsoftware.spigot.core.Utilities;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VersionUtility {
    @Getter
    private final static Double serverVersion;

    static {
        double sV = 0d;

        Exception exception = null;
        try {
            String serverVersionString = Bukkit.getBukkitVersion();
            String versionPart = serverVersionString.split("-")[0];

            String[] versionNumbers = versionPart.split("\\.");

            if (versionNumbers.length >= 2) {
                int minor = Integer.parseInt(versionNumbers[1]);
                sV += minor;
            }
        } catch (Exception e) {
            exception = e;
        }

        if(exception == null) {
            try {
                String serverClassName = Bukkit.getServer().getClass().getName();
                String[] scoreSplit = serverClassName.split("\\.")[3].split("_");

                if (scoreSplit.length >= 2) {
                    sV += Integer.parseInt(scoreSplit[1]);
                    if (scoreSplit.length == 3) {
                        String minorVersion = scoreSplit[2].substring(1);
                        sV += Integer.parseInt(minorVersion) / 10d;
                    }
                }
            } catch (Exception e) {
                exception = e;
            }
        }

        if (exception != null) {
            Bukkit.getLogger().severe("Failed to parse server version, defaulting to 0. Error: " + exception.getMessage());
        }

        serverVersion = sV;
    }

    /**
     * Adjusts AIR check for pre 1.9 standard
     */
    public static boolean isAir(ItemStack stack) {
        if (stack == null) return true;
        return stack.getType().equals(Material.AIR);
    }

}
