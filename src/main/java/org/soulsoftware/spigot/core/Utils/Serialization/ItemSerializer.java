package org.soulsoftware.spigot.core.Utils.Serialization;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.soulsoftware.spigot.core.Utils.Utils;

import java.util.Base64;
import java.util.HashMap;

public class ItemSerializer {
    public static String toBase64(String title, Inventory inventory) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("type", inventory.getType());
        data.put("size", inventory.getSize());
        data.put("items", inventory.getContents());
        return new String(Base64.getEncoder().encode(Utils.mapToBytes(data)));
    }

    public static Inventory fromBase64(String data) {
        HashMap<String, Object> map = (HashMap<String, Object>) Utils.bytesToMap(Base64.getDecoder().decode(data.getBytes()));
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
}
