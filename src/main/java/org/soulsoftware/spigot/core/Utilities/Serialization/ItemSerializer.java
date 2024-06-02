package org.soulsoftware.spigot.core.Utilities.Serialization;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.soulsoftware.spigot.core.Utilities.MapUtility;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;

public class ItemSerializer {
    public static String toBase64(String title, Inventory inventory) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("type", inventory.getType());
        data.put("size", inventory.getSize());
        data.put("items", inventory.getContents());
        return new String(Base64.getEncoder().encode(MapUtility.mapToBytes(data)));
    }

    public static Inventory fromBase64(String data) {
        HashMap<String, Object> map = (HashMap<String, Object>) MapUtility.bytesToMap(Base64.getDecoder().decode(data.getBytes()));
        Inventory inventory;
        String title = (String) map.get("title");
        InventoryType type = (InventoryType) map.get("type");
        Integer size = (Integer) map.get("size");
        ItemStack[] contents = (ItemStack[]) map.get("items");
        if (size != type.getDefaultSize())
            inventory = Bukkit.createInventory(null, size, title);
        else
            inventory = Bukkit.createInventory(null, type, title);
        inventory.setContents(contents);
        return inventory;
    }

    public static String writeRaw(ItemStack... items) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeInt(items.length);

            for (ItemStack item : items)
                dataOutput.writeObject(item);

            return Base64Coder.encodeLines(outputStream.toByteArray());

        } catch (Exception ignored) {
            ignored.printStackTrace();
            return "";
        }
    }

    public static ItemStack[] readRaw(String source) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(source));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++)
                items[i] = (ItemStack) dataInput.readObject();

            return items;
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return new ItemStack[0];
        }
    }
}
