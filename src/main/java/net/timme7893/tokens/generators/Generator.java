package net.timme7893.tokens.generators;

import net.timme7893.tokens.Tokens;
import net.timme7893.tokens.utils.file.Archive;
import net.timme7893.tokens.utils.file.File;
import net.timme7893.tokens.utils.text.PKeys;
import net.timme7893.tokens.utils.text.PValues;
import net.timme7893.tokens.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class Generator {

    private String ID;
    private File generatorsFile;
    private File configFile;

    private String tier;
    private int generatingTime, tokensPerGenerate, costToUpgrade;
    private String nextTier;
    private int tokens = 0;

    private String world = "";
    private double x = 0,y = 0,z = 0;

    private ArmorStand armorStand;

    public Generator(String generatorID) {
        this.ID = generatorID;
        this.generatorsFile = Archive.get("generators");
        this.configFile = Archive.get("config");

        String section = "generators." + ID + ".";
        this.tier = generatorsFile.asString(section + "tier");
        this.tokens = generatorsFile.asInt(section + "tokens");

        this.world = generatorsFile.asString(section + "world");
        this.x = generatorsFile.asDouble(section + "x");
        this.y = generatorsFile.asDouble(section + "y");
        this.z = generatorsFile.asDouble(section + "z");

        loadGenerator(tier);
        loadAmorStand();
    }

    public void loadGenerator(String tier) {
        this.generatingTime = configFile.asInt("tiers." + tier + ".generatingTime");
        this.tokensPerGenerate = configFile.asInt("tiers." + tier + ".tokensPerGenerate");

        List<String> tiersList = configFile.asList("tiers-list");
        int tierIndex = tiersList.indexOf(tier);
        int nextTierIndex = tierIndex + 1;
        if ((nextTierIndex + 1) > tiersList.size()) {
            this.nextTier = "-";
        } else {
            this.nextTier = tiersList.get(nextTierIndex);
        }

        if (nextTier.equals("-")) {
            this.costToUpgrade = 0;
        } else {
            this.costToUpgrade = configFile.asInt("tiers." + nextTier + ".costToUpgrade");
        }

    }

    public void loadAmorStand() {
        String customName = Text.format(configFile.asString("armorstand-text"), PKeys.set("tokens"), PValues.set(tokens,1));
        Location location = getLocation();
        Location entityLocation = new Location(location.getWorld(),location.getX(),location.getY() + 0.5,location.getZ() + 0.4);

        if (!entityLocation.getWorld().getEntities().contains(EntityType.ARMOR_STAND)) {
            createAmorStand(entityLocation,customName);
        } else {
            List<Entity> entitys = entityLocation.getWorld().getEntities();
            armorStand = (ArmorStand) entitys.stream().filter(entity -> entity.getType().equals(EntityType.ARMOR_STAND)).findFirst().orElse(null);
            if (armorStand == null) {
                createAmorStand(entityLocation,customName);
            }
        }
    }

    public void createAmorStand(Location entityLocation, String name) {
        armorStand = (ArmorStand) entityLocation.getWorld().spawnEntity(entityLocation, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setCustomName(name);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setMaxHealth(2047.0);
        armorStand.setHealth(2047.0);
    }

    public void removeArmorStand() {
       armorStand.remove();
    }

    public boolean upgradeGenerator(Player player) {
        if (nextTier.equals("-")) {
            Tokens.getInstance().getMessageAPI().sendMessage(player,"generator-max-tier");
            return false;
        }
        generatorsFile.addFileChange("generators." + ID + ".tier", File.ChangeType.SET, nextTier);
        generatorsFile.saveChanges();
        this.tier = nextTier;
        loadGenerator(nextTier);
        ticks = 0;
        return true;
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(this.world);
        Location location = new Location(world,x,y,z);
        return location;
    }

    public String getID() {
        return ID;
    }

    public File getGeneratorsFile() {
        return generatorsFile;
    }

    public File getConfigFile() {
        return configFile;
    }

    public String getTier() {
        return tier;
    }

    public int getGeneratingTime() {
        return generatingTime;
    }

    public int getTokensPerGenerate() {
        return tokensPerGenerate;
    }

    public int getCostToUpgrade() {
        return costToUpgrade;
    }

    public String getNextTier() {
        return nextTier;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
        String customName = Text.format(configFile.asString("armorstand-text"), PKeys.set("tokens"), PValues.set(tokens,1));
        armorStand.setCustomName(customName);
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }
    // Timer

    private int ticks = 0;

    public boolean ready() {
        if (ticks == generatingTime) {
            ticks = 0;
            return true;
        } else {
            ticks++;
            return false;
        }
    }
}
