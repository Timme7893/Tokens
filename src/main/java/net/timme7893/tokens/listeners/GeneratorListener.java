package net.timme7893.tokens.listeners;

import net.timme7893.tokens.Tokens;
import net.timme7893.tokens.generators.Generator;
import net.timme7893.tokens.generators.GeneratorMenu;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;


public class GeneratorListener implements Listener {

    private Tokens tokens;
    private HashMap<Player, GeneratorMenu> activeMenus = new HashMap<Player, GeneratorMenu>();

    public GeneratorListener(Tokens tokens) {
        this.tokens = tokens;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void openGenerator(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Location blockLocation = event.getClickedBlock().getLocation();
            Location loc = new Location(blockLocation.getWorld(),blockLocation.getX(),blockLocation.getY(),blockLocation.getZ());
            if (tokens.getGeneratorManager().getGeneratorWithLocation(loc) != null) {
                Generator generator = tokens.getGeneratorManager().getGeneratorWithLocation(loc);
                GeneratorMenu menu = new GeneratorMenu(tokens,event.getPlayer(),generator);
                activeMenus.put(event.getPlayer(),menu);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void openInventory(InventoryOpenEvent event) {
        if (activeMenus.containsKey(event.getPlayer())) {
            GeneratorMenu menu = activeMenus.get(event.getPlayer());
            if (!event.getInventory().equals(menu.getInventory())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void closeInventory(InventoryCloseEvent event) {
        if (activeMenus.containsKey(event.getPlayer())) {
            if (activeMenus.get(event.getPlayer()).getInventory().equals(event.getInventory())) {
                activeMenus.remove(event.getPlayer());
            }
        }
    }
}
