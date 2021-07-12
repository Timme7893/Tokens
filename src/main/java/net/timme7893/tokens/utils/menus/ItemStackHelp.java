package net.timme7893.tokens.utils.menus;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class ItemStackHelp {

    public static ItemStack newItemStack(String item, String itemName, List<String> lore) {
        return ItemStackHelp.newItemStack(null, item, itemName, lore, "");
    }

    public static ItemStack newItemStack(Player player, String item, String itemName, List<String> lore, String itemPlaceholder) {
        String[] itemInfo = item.split(":");

        if (itemInfo[0].equalsIgnoreCase("head"))
            return ItemStackHelp.newHeadItem(player, itemInfo[1], itemName, lore);

        String materialString = itemInfo[0].toUpperCase().replace("%MISSION-ITEM%",itemPlaceholder);
        Material material = Material.valueOf(materialString);

        byte data = itemInfo.length > 1 ? Byte.valueOf(itemInfo[1].replace(";glow", "")) : 0;
        boolean glow = itemInfo.length > 1 && (itemInfo[1].split(";").length > 1
                && itemInfo[1].split(";")[1].equalsIgnoreCase("glow"));

        ItemStack itemStack = new ItemStack(material, 1, data);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;
        if (itemName != null)
            itemMeta.setDisplayName(itemName);
        if (lore != null && !lore.isEmpty())
            itemMeta.setLore(lore);
        if (glow) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 0, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack newHeadItem(Player player, String owner, String itemName, List<String> lore) {
        ItemStack itemStack = new ItemStack(Material.SKULL, 1, (byte) 3);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner(owner.equalsIgnoreCase("own") && player != null ? player.getName() : owner);
        if (itemName != null)
            skullMeta.setDisplayName(itemName);
        if (lore != null && !lore.isEmpty())
            skullMeta.setLore(lore);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
}
