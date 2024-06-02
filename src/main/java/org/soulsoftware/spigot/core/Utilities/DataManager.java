/*
 * Copyright (c) SoulSoftware 2022.
 */

package org.soulsoftware.spigot.core.Utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class DataManager {
    private static int PERSISTANT_VERSION_LIMIT = 16;

    public static ItemStack setUnbreakable(ItemStack itemStack, boolean unbreaking) {
        NBTItem nbtItem = new NBTItem(itemStack);
        if (unbreaking) nbtItem.setByte("Unbreakable", (byte) 1);
        else nbtItem.setByte("Unbreakable", (byte) 0);
        return nbtItem.getItem();
    }

    public static ItemStack setCustomSkullPlayer(ItemStack head, OfflinePlayer player) {
        return setCustomSkullTexture(head, getPlayerSkullId(player));
    }

    public static String getPlayerSkullId(OfflinePlayer player) {
        try {
            URL url = null;
            try {
                url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + player.getUniqueId().toString().replace("-", ""));
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
            return texture;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return "a4e1da882e434829b96ec8ef242a384a53d89018fa65fee5b37deb04eccbf10e";
        }
    }

    public static ItemStack setCustomSkullTexture(ItemStack head, String id) {
        try {
            String base64;
            if (VersionUtility.isAir(head)) {
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

    public static ItemStack setRawNBT(ItemStack item, String key, Object value) {
        NBTItem nbti = new NBTItem(item);
        if (value instanceof String) nbti.setString(key, (String) value);
        else if (value instanceof Integer) nbti.setInteger(key, (Integer) value);
        else if (value instanceof Byte) nbti.setByte(key, (Byte) value);
        else if (value instanceof Boolean) nbti.setBoolean(key, (Boolean) value);
        else if (value instanceof byte[]) nbti.setByteArray(key, (byte[]) value);
        else if (value instanceof int[]) nbti.setIntArray(key, (int[]) value);
        else if (value instanceof Double) nbti.setDouble(key, (Double) value);
        else if (value instanceof Float) nbti.setFloat(key, (Float) value);
        else if (value instanceof Enum) nbti.setEnum(key, (Enum<?>) value);
        else if (value instanceof UUID) nbti.setUUID(key, (UUID) value);
        else if (value instanceof ItemStack) nbti.setItemStack(key, (ItemStack) value);
        else if (value instanceof ItemStack[]) nbti.setItemStackArray(key, (ItemStack[]) value);
        else if (value instanceof Long) nbti.setLong(key, (Long) value);
        else if (value instanceof Short) nbti.setShort(key, (Short) value);

        return nbti.getItem();
    }

    public static ItemStack setNBT(ItemStack item, String key, String value) {
        if (VersionUtility.getServerVersion() < PERSISTANT_VERSION_LIMIT) {
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
        if (VersionUtility.getServerVersion() < PERSISTANT_VERSION_LIMIT) {
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
        if (VersionUtility.getServerVersion() < PERSISTANT_VERSION_LIMIT) {
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

            for (NamespacedKey key : keys) {
                if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    map.put(key.getKey(), meta.getPersistentDataContainer().get(key, PersistentDataType.STRING));
                }
            }
            return map;
        }
    }

    public static HashMap<String, Object> getRawAll(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        HashMap<String, Object> map = new HashMap<>();
        for (String key : nbti.getKeys()) {
            map.put(key, nbti.getCompound());
        }
        return map;
    }

    public static ItemStack removeNBT(ItemStack item, String key) {
        if (VersionUtility.getServerVersion() < PERSISTANT_VERSION_LIMIT) {
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
        if (VersionUtility.getServerVersion() < PERSISTANT_VERSION_LIMIT) {
            NBTItem nbti = new NBTItem(item);
            return nbti.getString(key);
        } else {
            ItemMeta meta = item.getItemMeta();
            return meta.getPersistentDataContainer().get(new NamespacedKey("soulsoftware", key), PersistentDataType.STRING);
        }
    }
}
