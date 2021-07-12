package net.timme7893.tokens.listeners;

import net.timme7893.tokens.Tokens;
import net.timme7893.tokens.utils.file.Archive;
import net.timme7893.tokens.utils.file.File;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.logging.Level;

public class BlockListener implements Listener {

    private Tokens tokens;
    private Material generatorBlock = null;

    public BlockListener(Tokens tokens) {
        this.tokens = tokens;
        File configFile = Archive.get("config");
        String materialString = configFile.asString("generator-block");
        if (Material.getMaterial(materialString) == null) {
            Tokens.getInstance().getLogger().log(Level.WARNING, "Configured generator-block isn't a valid block! Generator block is now set to STONE.");
            generatorBlock = Material.STONE;
            return;
        }
        generatorBlock = Material.getMaterial(materialString);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void generatorPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        if (placedBlock.getType() != generatorBlock) {
            return;
        }

        tokens.generatorManager.newGenerator(event.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void generatorDestroy(BlockBreakEvent event) {
        Block removedBlock = event.getBlock();
        if (removedBlock.getType() != generatorBlock && tokens.generatorManager.getGeneratorWithLocation(removedBlock.getLocation()) == null) {
            return;
        }

        tokens.generatorManager.deleteGenerator(removedBlock.getLocation());
    }
}
