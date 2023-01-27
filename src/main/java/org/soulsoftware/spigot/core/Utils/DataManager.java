/*
 * Copyright (c) SoulSoftware 2022.
 */

package org.soulsoftware.spigot.core.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.changeme.nbtapi.NBTItem;
import junit.runner.Version;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import sun.jvm.hotspot.debugger.dummy.DummyDebugger;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class DataManager {
    public static ItemStack setCustomSkullPlayer(ItemStack head, OfflinePlayer player) {
        URL url = null;
        try {
            url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + player.getUniqueId().toString().replace("-",""));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        InputStreamReader read = null;
        try {
            read = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonObject textureProperty = JsonParser.parseReader(read).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        try {
            read.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String texture = new String(Base64.getDecoder().decode(textureProperty.get("value").getAsString()));
        texture = JsonParser.parseString(texture).getAsJsonObject().get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString().substring(38);
        return setCustomSkullTexture(head, texture);
    }
    public static ItemStack setCustomSkullTexture(ItemStack head, String id) {
        try {
            String base64;
            if (VersionManager.isAir(head)) {
                if (hasRawNBT(head, "SkullOwner")) head = removeRawNBT(head, "SkullOwner");
            }
            try {
                JsonObject textureProperty = JsonParser.parseString(
                        new String(Base64.getDecoder().decode(id))
                ).getAsJsonObject().get("textures").getAsJsonObject().get("SKIN").getAsJsonObject();
                base64 = id;
            } catch (Throwable ex) {
                id = id.replace(":", "");
                base64 = new String(Base64.getEncoder().encode(String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s" +
                                "\"}}}",
                        "http://textures.minecraft.net/texture/" + id).getBytes()));
            }
            Material material = Material.getMaterial("SKULL_ITEM");
            if (material == null) {
                head.setType(Material.getMaterial("PLAYER_HEAD"));
            } else {
                head.setType(material);
                head.setDurability((short) SkullType.PLAYER.ordinal());
                head.setData(material.getNewData((byte) SkullType.PLAYER.ordinal()));
            }

            GameProfile profile = new GameProfile(UUID.randomUUID(), UUID.randomUUID().toString());
            profile.getProperties().put("textures", new Property("textures", base64));
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            try {
                Method setProfile = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                setProfile.setAccessible(true);
                setProfile.invoke(meta, profile);
            } catch (NoSuchMethodException nsm) {
                try {
                    Field profileField = meta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(meta, profile);
                } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
            head.setItemMeta(meta);
            return head;
        } catch (Throwable io) {
            io.printStackTrace();
        }
        return head;
    }

    public static ItemStack setNBT(ItemStack item, String key, String value) {
        if(VersionManager.getServerVersion()<14) {
            NBTItem nbti = new NBTItem(item);
            nbti.setString(key, value);
            return nbti.getItem();
        } else {
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(
                    new NamespacedKey("soulsoftware", key), PersistentDataType.STRING, value
            );
            item.setItemMeta(meta);
            return item;
        }
    }

    public static Boolean hasNBT(ItemStack item, String key) {
        if(VersionManager.getServerVersion()<14) {
            NBTItem nbti = new NBTItem(item);
            return nbti.hasKey(key);
        } else {
            ItemMeta meta = item.getItemMeta();
            return meta.getPersistentDataContainer().has(
                    new NamespacedKey("soulsoftware", key), PersistentDataType.STRING);
        }
    }

    public static Boolean hasRawNBT(ItemStack item, String key) {
        NBTItem nbti = new NBTItem(item);
        return nbti.hasKey(key);
    }

    public static HashMap<String, String> getAll(ItemStack item) {
        if(VersionManager.getServerVersion()<14) {
            NBTItem nbti = new NBTItem(item);
            HashMap<String, String> map = new HashMap<>();
            for (String key : nbti.getKeys()) {
                map.put(key, nbti.getString(key));
            }
            return map;
        } else {
            ItemMeta meta = item.getItemMeta();
            Set<NamespacedKey> keys = meta.getPersistentDataContainer().getKeys();
            HashMap<String, String> map = new HashMap<>();

            for(NamespacedKey key : keys) {
                if(meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    map.put(key.getKey(), meta.getPersistentDataContainer().get(key, PersistentDataType.STRING));
                }
            }
            return map;
        }
    }

    public static ItemStack removeNBT(ItemStack item, String key) {
        if(VersionManager.getServerVersion()<14) {
            NBTItem nbti = new NBTItem(item);
            nbti.removeKey(key);
            return nbti.getItem();
        } else {
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().remove(new NamespacedKey("soulsoftware", key));
            item.setItemMeta(meta);
            return item;
        }
    }

    public static ItemStack removeRawNBT(ItemStack item, String key) {
        NBTItem nbti = new NBTItem(item);
        nbti.removeKey(key);
        return nbti.getItem();
    }

    public static String getNBT(ItemStack item, String key) {
        if(VersionManager.getServerVersion()<14) {
            NBTItem nbti = new NBTItem(item);
            return nbti.getString(key);
        } else {
            ItemMeta meta = item.getItemMeta();
            return meta.getPersistentDataContainer().get(new NamespacedKey("soulsoftware", key), PersistentDataType.STRING);
        }
    }
}
