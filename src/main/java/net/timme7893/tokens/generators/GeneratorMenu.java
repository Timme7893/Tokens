package net.timme7893.tokens.generators;

import net.timme7893.tokens.Tokens;
import net.timme7893.tokens.tokens.TPlayer;
import net.timme7893.tokens.utils.file.Archive;
import net.timme7893.tokens.utils.menus.AbstractMenu;
import net.timme7893.tokens.utils.messages.MessageAPI;
import net.timme7893.tokens.utils.text.PKeys;
import net.timme7893.tokens.utils.text.PValues;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class GeneratorMenu extends AbstractMenu {

    private Tokens tokens;
    private MessageAPI api;

    private TPlayer tPlayer;
    private Generator generator;

    public GeneratorMenu(Tokens tokens, Player player, Generator generator) {
        super(Tokens.getInstance(),player, Archive.get("generator-menu"), Archive.get("generator-menu").asString("title"),Archive.get("generator-menu").asInt("rows"));
        this.tokens = tokens;
        this.api = tokens.getMessageAPI();
        this.tPlayer = new TPlayer(player);
        this.generator = generator;
        this.setup();
        new BukkitRunnable() {
            @Override
            public void run() {
                show();
                loadTimer();
            }

        }.runTaskLater(tokens, 3);
    }

    @Override
    protected void configure() {
        this.addItem("tier", PKeys.set("tier", "tokens","delay","next-tier","cost"), PValues.set(generator.getTier(), generator.getTokensPerGenerate(), generator.getGeneratingTime(),generator.getNextTier(),generator.getCostToUpgrade(), 1,2,3,4,5))
                .setAction((menuItem, clickType) -> {
                    int tokensRequired = generator.getCostToUpgrade();
                    if (!tPlayer.hasTokens(tokensRequired)) {
                        api.sendMessage(player,"not-enough-tokens-for-upgrade",PKeys.set("tokens"),PValues.set(generator.getCostToUpgrade(),1));
                        closeGUI();
                        return;
                    }
                    String nextTier = generator.getNextTier();
                    tPlayer.removeTokens(tokensRequired);
                    if (generator.upgradeGenerator(player)) {
                        api.sendMessage(player, "generator-upgraded", PKeys.set("next-tier", "tokens"), PValues.set(nextTier, tokensRequired, 1, 2));
                    }
                    closeGUI();
                });

         this.addItem("tokens", PKeys.set("tokens","new-tokens"), PValues.set(generator.getTokens(), generator.getTokensPerGenerate(),1,2))
                .setAction((menuItem, clickType) -> {
                    int tokens = generator.getTokens();
                    generator.setTokens(0);
                    tPlayer.addTokens(tokens);
                    closeGUI();
                    api.sendMessage(player,"claimed-tokens",PKeys.set("tokens"),PValues.set(tokens,1));
                });

        this.addItem(this.file, "exit")
                .setAction((menuItem, clickType) -> {
                    closeGUI();
                });
    }

    public void closeGUI() {
        this.close();
        Bukkit.getScheduler().cancelTask(timer);
    }

    private int timer;

    public void loadTimer() {
        BukkitScheduler scheduler = tokens.getServer().getScheduler();
        timer = scheduler.scheduleSyncRepeatingTask(tokens, new Runnable() {
            @Override
            public void run() {
                injectPlaceholders(player,"tokens", PKeys.set("tokens","new-tokens"),PValues.set(generator.getTokens(), generator.getTokensPerGenerate(), 1,2),"");
            }
        }, 0L, 20L);
    }
}
