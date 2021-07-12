package net.timme7893.tokens.utils.menus;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;


import net.timme7893.tokens.utils.file.File;
import net.timme7893.tokens.utils.text.PKeys;
import net.timme7893.tokens.utils.text.PValues;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;

public abstract class AbstractMenu implements InventoryHolder {

    protected final Plugin plugin;
    protected final Player player;
    protected final File file;
    private final BinaryOperator<Integer> slotFormat = (left, right) -> 9 * (left - 1) + (right - 1);
    @Setter
    private ItemStack borderItem;
    @Setter
    private ItemStack fillerItem;
    private Inventory inventory;
    @Setter
    private String title;
    @Setter
    private int rows;
    private Map<MenuItem, Integer> preMenuItems = Maps.newHashMap();
    private Map<Integer, MenuItem> menuItems = Maps.newHashMap();
    @Setter
    @Getter
    private CloseAction closeAction;

    public AbstractMenu(Plugin plugin, Player player, File file, String title, int rows) {
        this.plugin = plugin;
        this.player = player;
        this.file = file;
        this.title = title;
        this.rows = rows;
    }

    /**
     * Configure menu
     */
    protected abstract void configure();



    protected MenuItem addItem(File file, String section, PKeys pKeys, PValues pValues, int row, int slot, String itemPlaceholder) {
        MenuItem menuItem = new MenuItem(this.player, file, section, pKeys, pValues,itemPlaceholder);
        this.preMenuItems.put(menuItem, this.slotFormat.apply(row, slot));
        return menuItem;
    }

    protected MenuItem addItem(File file, String section, PKeys pKeys, PValues pValues, int row, int slot) {
        return this.addItem(file, section, pKeys, pValues, row, slot, "");
    }

    protected MenuItem addItem(File file, String section, int row, int slot) {
        return this.addItem(file, "items." + section, null, null, row, slot, "");
    }


    protected MenuItem addItem(File file, String section, PKeys pKeys, PValues pValues, String itemPlaceholder) {
        return this.addItem(file, "items." + section, pKeys, pValues,
                file.asInt(String.format("items.%s.row", section)),
                file.asInt(String.format("items.%s.slot", section)), itemPlaceholder);
    }

    protected MenuItem addItem(File file, String section) {
        return this.addItem(file, section, null, null, "");
    }

    protected MenuItem addItem(Player player, File file, String section, PKeys pKeys, PValues pValues, int row, int slot) {
        return this.addItem(this.file, section);
    }

    protected MenuItem addItem(String section, PKeys pKeys, PValues pValues) {
        return this.addItem(this.file, section, pKeys, pValues, "");
    }

    protected MenuItem addItem(ItemHolder.ItemHolderBuilder itemHolderBuilder, int row, int slot) {
        return this.addItem(itemHolderBuilder.build().getItem(), row, slot);
    }

    protected MenuItem addItem(ItemStack itemStack, int row, int slot) {
        MenuItem menuItem = new MenuItem(itemStack);
        this.preMenuItems.put(menuItem, this.slotFormat.apply(row, slot));
        return menuItem;
    }

    /**
     * Get cached {@link Inventory}, and create new inventory if null
     *
     * @return the cached {@link Inventory}
     */
    public Inventory getInventory() {
        if (this.inventory == null)
                this.inventory = Bukkit.createInventory(this, rows * 9, title);
        return this.inventory;
    }

    /**
     * Show cached {@link Inventory} to {@link Player}
     */
    public void show() {
        this.player.openInventory(this.getInventory());
    }

    /**
     * Close the current {@link Inventory} of the {@link Player}
     */
    public void close() {
        this.player.closeInventory();
        if (Objects.nonNull(this.closeAction))
            this.closeAction.onClose();
    }

    /**
     * This will open a new {@link AbstractMenu} while in another one
     *
     * @param abstractMenu the {@link AbstractMenu} that will be opened after a delay of 1 ms
     */
    protected void openNewMenu(AbstractMenu abstractMenu) {
        Bukkit.getScheduler().runTaskLater(this.plugin, abstractMenu::show, 1);
    }

    /**
     * Simplified run task method
     *
     * @param runnable the {@link Runnable}
     */
    protected void run(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

    /**
     * Get a {@link MenuItem} based on a slot
     *
     * @param slot the slot that we wil get it from
     * @return the {@link MenuItem} based on the slot
     */
    public MenuItem getMenuItem(int slot) {
        return this.menuItems.getOrDefault(slot, null);
    }

    /**
     * Set a {@link MenuItem} to a slot
     *
     * @param menuItem the {@link MenuItem} that will be set
     * @param slot     the slot we will be setting it to
     */
    public void setItem(MenuItem menuItem, int slot) {
        this.clearSlot(slot);
        this.getInventory().setItem(slot, menuItem.getItemStack());
        this.menuItems.put(slot, menuItem);
    }

    /**
     * Clear a slots data
     *
     * @param slot the slot that we will clear
     */
    public void clearSlot(int slot) {
        this.getInventory().clear(slot);
        if (Objects.nonNull(this.getMenuItem(slot)))
            this.menuItems.remove(slot);
    }

    /**
     * Execute the onClick method in the {@link MenuItem.ClickAction} interface
     *
     * @param slot      the slot that will be activated
     * @param clickType the type of click that will be passed through the onClick method
     */
    public void activateSlot(int slot, ClickType clickType) {
        MenuItem menuItem = this.getMenuItem(slot);

        if (Objects.isNull(menuItem) || Objects.isNull(menuItem.getAction()))
            return;

        menuItem.getAction().onClick(menuItem, clickType);
    }

    /**
     * Update the placeholders of a {@link MenuItem}
     *
     * @param section the item we will be updating the placeholders of
     * @param pKeys   the key placeholders
     * @param pValues the values of the keys
     */
    protected void injectPlaceholders(Player player, String section, PKeys pKeys, PValues pValues, String itemPlaceholder) {
        int slot = this.slotFormat.apply(file.asInt(String.format("items.%s.row", section)), file.asInt(String.format("items.%s.slot", section)));
        MenuItem menuItem = this.getMenuItem(slot);

        if (Objects.isNull(menuItem))
            return;

        this.setItem(menuItem.injectPlaceholders(player, this.file, "items." + section, pKeys, pValues, itemPlaceholder), slot);
    }

    protected void setup() {
        this.configure();
        if (Objects.nonNull(this.borderItem)) {
            for (int n = 0; n < 18; n++) {
                this.setItem(new MenuItem(this.borderItem), n > 8 ? (n - 9) + 9 * (this.rows - 1) : n);
            }
            for (int n = 1; n < (this.rows - 2) * 2 + 1; n++) {
                int slot = 9 * (n - 1);
                if (slot >= inventory.getSize())
                    break;

                this.setItem(new MenuItem(this.borderItem), slot);
                this.setItem(new MenuItem(this.borderItem), slot + 8);
            }
        }
        this.preMenuItems.forEach(this::setItem);
        if (Objects.nonNull(this.fillerItem)) {
            for (int i = 0; i < this.inventory.getSize(); i++) {
                if (Objects.nonNull(this.inventory.getItem(i)))
                    continue;
                this.setItem(new MenuItem(this.fillerItem), i);
            }
        }
    }



    public interface CloseAction {

        void onClose();
    }
}
