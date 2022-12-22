/*
 * Copyright (c) SoulSoftware 2022.
 */

package org.soulsoftware.spigot.core.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

public class DataManager {
    @SneakyThrows
    public static ItemStack setCustomSkullPlayer(ItemStack head, OfflinePlayer player) {
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + player.getUniqueId().toString().replace("-",""));
        InputStreamReader read = new InputStreamReader(url.openStream());
        JsonObject textureProperty = JsonParser.parseReader(read).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        read.close();
        String texture = new String(Base64.getDecoder().decode(textureProperty.get("value").getAsString()));
        texture = JsonParser.parseString(texture).getAsJsonObject().get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString().substring(38);
        return setCustomSkullTexture(head, texture);
    }

    public static ItemStack setCustomSkullTexture(ItemStack head, String id) {
        try {
            String base64;
            try {
                JsonReader reader = new JsonReader(new StringReader(new String(Base64.getDecoder().decode(id))));
                JsonObject textureProperty = JsonParser.parseReader(reader).getAsJsonObject().get("textures").getAsJsonObject().get("SKIN").getAsJsonObject();
                base64 = id;
            } catch (IllegalStateException ex) {
                if (VersionManager.isAir(head)) {
                    if (hasNBT(head, "SkullOwner")) head = removeNBT(head, "SkullOwner");
                }
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
        } catch (Throwable io) {
            io.printStackTrace();
            return head;
        }
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
