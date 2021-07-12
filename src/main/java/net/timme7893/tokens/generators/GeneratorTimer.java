package net.timme7893.tokens.generators;

import net.timme7893.tokens.Tokens;
import net.timme7893.tokens.utils.text.PKeys;
import net.timme7893.tokens.utils.text.PValues;
import net.timme7893.tokens.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class GeneratorTimer {

    private Tokens tokens;
    private GeneratorManager generatorManager;

    private int timer;

    public GeneratorTimer(Tokens tokens, GeneratorManager generatorManager) {
        this.tokens = tokens;
        this.generatorManager = generatorManager;
        init();
    }

    private void init() {
        BukkitScheduler scheduler = tokens.getServer().getScheduler();
        timer = scheduler.scheduleSyncRepeatingTask(tokens, new Runnable() {
            @Override
            public void run() {

                generatorManager.generators.values().stream().forEach(generator -> {
                    if (generator.ready()) {
                        int tokensToAdd = generator.getTokensPerGenerate();
                        generator.setTokens(generator.getTokens() + tokensToAdd);
                        generator.getArmorStand().setCustomName(Text.format(generatorManager.getArmorStandName(), PKeys.set("tokens"), PValues.set(generator.getTokens(),1)));
                    }
                });

            }
        }, 0L, 20L);
    }

    public void stopTimer() {
        Bukkit.getScheduler().cancelTask(timer);
    }
}
