package net.timme7893.tokens;

import lombok.Getter;
import net.timme7893.tokens.commands.TokensCommand;
import net.timme7893.tokens.generators.GeneratorManager;
import net.timme7893.tokens.listeners.BlockListener;
import net.timme7893.tokens.listeners.GeneratorListener;
import net.timme7893.tokens.utils.file.Archive;
import net.timme7893.tokens.utils.menus.MenuListener;
import net.timme7893.tokens.utils.messages.MessageAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Tokens extends JavaPlugin {

    @Getter
    private static Tokens instance = null;
    @Getter
    private Archive archive = null;
    @Getter
    public GeneratorManager generatorManager = null;
    @Getter
    public MessageAPI messageAPI = null;

    public void onEnable() {
        // Instance
        instance = this;

        // Archive
        archive = new Archive(this);
        archive.insertDefaultConfig();
        archive.insertWrapperFiles("config.yml", "generators.yml", "tokensData.yml", "messages.yml", "generator-menu.yml");

        // Other instances
        generatorManager = new GeneratorManager(this);
        messageAPI = new MessageAPI();

        // Listeners
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockListener(this), this);
        pm.registerEvents(new GeneratorListener(this), this);
        pm.registerEvents(new MenuListener(),this);

        // Commands
        getCommand("tokens").setExecutor(new TokensCommand());
    }

    public void onDisable() {
        generatorManager.disableGenerators();
        instance = null;
    }

    public static Tokens getInstance() {
        return instance;
    }
}
