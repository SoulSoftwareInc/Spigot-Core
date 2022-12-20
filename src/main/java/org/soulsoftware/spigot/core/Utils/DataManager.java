/*
 * Copyright (c) SoulSoftware 2022.
 */

package org.soulsoftware.spigot.core.Utils;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

public class DataManager {
    @SneakyThrows
    public static ItemStack setCustomSkullPlayer(ItemStack head, Player player) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        Class<?> strClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
        GameProfile profile = (GameProfile) strClass.cast(player).getClass().getMethod("getProfile")
                .invoke(strClass.cast(player));
        Property property = profile.getProperties().get("textures").iterator().next();
        JSONParser parser = new JSONParser();
        String id = ((String) ((JSONObject) ((JSONObject) ((JSONObject) parser.parse(new String(Base64.getDecoder().decode(property.getValue().getBytes())))).get("textures")).get("SKIN")).get(
                "url"));
        id = id.substring(38);
        return setCustomSkullTexture(head, id);
    }

    public static ItemStack setCustomSkullTexture(ItemStack head, String id) {
        if (VersionManager.isAir(head)) {
            if (hasNBT(head, "SkullOwner")) head = removeNBT(head, "SkullOwner");
        }
        id = id.replace(":", "");
        String base64 = new String(Base64.getEncoder().encode(String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s" +
                        "\"}}}",
                "http://textures.minecraft.net/texture/" + id).getBytes()));
        Material material = Material.getMaterial("SKULL_ITEM");
        if (material == null) {
            head.setType(Material.getMaterial("PLAYER_HEAD"));
        } else {
            head.setType(material);
            head.setDurability((short) SkullType.PLAYER.ordinal());
            head.setData(material.getNewData((byte) SkullType.PLAYER.ordinal()));
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", base64));
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack setNBT(ItemStack item, String key, String value) {
        NBTItem nbti = new NBTItem(item);
        nbti.setString(key, value);
        return nbti.getItem();
    }

    public static Boolean hasNBT(ItemStack item, String key) {
        NBTItem nbti = new NBTItem(item);
        return nbti.hasKey(key);
    }

    public static HashMap<String, String> getAll(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        HashMap<String, String> map = new HashMap<>();
        for (String key : nbti.getKeys()) {
            map.put(key, nbti.getString(key));
        }
        return map;
    }

    public static ItemStack removeNBT(ItemStack item, String key) {
        NBTItem nbti = new NBTItem(item);
        nbti.removeKey(key);
        return nbti.getItem();
    }

    public static String getNBT(ItemStack item, String key) {
        NBTItem nbti = new NBTItem(item);
        return nbti.getString(key);
    }
}
