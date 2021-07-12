package net.timme7893.tokens.utils.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public class MenuListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        InventoryAction inventoryAction = event.getAction();

        if (!(inventory.getHolder() instanceof AbstractMenu))
            return;

        AbstractMenu abstractMenu = (AbstractMenu) inventory.getHolder();

        event.setCancelled(true);
        player.updateInventory();

        int rawSlot = event.getRawSlot();
        if (rawSlot < inventory.getSize() && !inventoryAction.equals(InventoryAction.NOTHING))
            abstractMenu.activateSlot(rawSlot, ClickType.fromInventoryAction(inventoryAction));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMenuClosed(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof AbstractMenu) || !(event.getPlayer() instanceof Player))
            return;

        AbstractMenu.CloseAction closeAction = ((AbstractMenu) inventory.getHolder()).getCloseAction();

        if (Objects.nonNull(closeAction))
            closeAction.onClose();
    }
}
