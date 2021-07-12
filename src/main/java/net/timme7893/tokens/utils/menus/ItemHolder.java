package net.timme7893.tokens.utils.menus;

import lombok.Builder;
import lombok.Singular;

import net.timme7893.tokens.utils.text.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public class ItemHolder {

    private final Material type = null;
    private final byte data = 0;
    private final int amount = 0;
    public static String name = "";
    public static final List<String> lore = null;
    @Singular
    private final Set<ItemFlag> itemFlags = null;
    private ItemStack itemStack;

    /**
     * Turn all variables into an {@link ItemStack}
     *
     * @return the {@link ItemStack} that has been generated from variables
     */
    public ItemStack getItem() {
        if (this.itemStack == null) {
            ItemStack itemStack = new ItemStack(this.type, this.amount, this.data);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (Objects.isNull(itemMeta))
                return this.itemStack = itemStack;
            if (Objects.nonNull(this.name))
                itemMeta.setDisplayName(Text.format(this.name));
            if (!this.lore.isEmpty())
                itemMeta.setLore(Text.format(lore));
            itemStack.setItemMeta(itemMeta);
            this.itemStack = itemStack;
        }
        return this.itemStack;
    }

    /**
     * Re-create the {@link ItemStack} from variables
     *
     * @return
     */
    public ItemStack reSetGetItem() {
        this.itemStack = null;
        return this.getItem();
    }

    public static class ItemHolderBuilder {

        public ItemHolderBuilder lore(String... strings) {
            ItemHolder.lore.addAll(Arrays.asList(strings));
            return this;
        }

        public ItemHolderBuilder replace(String string, Object replacer) {
            ItemHolder.name = ItemHolder.name.replace(string, Text.format(Objects.toString(replacer)));
            ItemHolder.lore.addAll(ItemHolder.lore.stream().map(line -> line.replace(string, Text.format(Objects.toString(replacer)))).collect(Collectors.toList()));
            return this;
        }
    }
}
