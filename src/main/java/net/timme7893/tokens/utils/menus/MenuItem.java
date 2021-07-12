package net.timme7893.tokens.utils.menus;

import lombok.Getter;
import lombok.Setter;

import net.timme7893.tokens.utils.file.File;
import net.timme7893.tokens.utils.text.PKeys;
import net.timme7893.tokens.utils.text.PValues;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class MenuItem {

    private ItemStack itemStack;
    @Setter
    private ClickAction action = null;

    MenuItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    MenuItem(Player player, File file, String section, PKeys pKeys, PValues pValues, String itemPlaceholder) {
        this.itemStack = file.getItem(player, section, pKeys, pValues, itemPlaceholder);
    }

    MenuItem(ItemHolder.ItemHolderBuilder itemHolderBuilder) {
        this.itemStack = itemHolderBuilder.build().getItem();
    }

    MenuItem injectPlaceholders(Player player, File file, String section, PKeys pKeys, PValues pValues, String itemPlaceholder) {
        this.itemStack = file.getItem(player, section, pKeys, pValues, itemPlaceholder);
        return this;
    }

    public interface ClickAction {

        void onClick(MenuItem menuItem, ClickType clickType);
    }
}
