package net.timme7893.tokens.generators;

import net.timme7893.tokens.Tokens;
import net.timme7893.tokens.utils.file.Archive;
import net.timme7893.tokens.utils.file.File;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;

public class GeneratorManager {

    public HashMap<Location, Generator> generators = new HashMap<Location,Generator>();
    private Tokens tokens;
    private File configFile;
    private GeneratorTimer generatorTimer;
    private String armorStandName = "";

    private boolean listEmpty = false;

    public GeneratorManager(Tokens tokens) {
        this.tokens = tokens;
        configFile = Archive.get("generators");

        List<String> generatorsList = configFile.asList("generators-list");
        for (String generatorString : generatorsList) {
            System.out.println("loading: " + generatorString);
            if (generatorString.equals("")) {
                if (generatorsList.size() == 1) {
                    listEmpty = true;
                }
            } else {
                Generator generator = new Generator(generatorString);
                generators.put(generator.getLocation(), generator);
            }
        }
        generatorTimer = new GeneratorTimer(tokens,this);
        armorStandName = Archive.get("config").asString("armorstand-text");
    }

    public void newGenerator(Location location) {
        int ID;
        if (!listEmpty) {
            ID = new Integer(getGeneratorsList().get(getGeneratorsList().size() - 1).replaceAll("generator", "")) + 1;
        } else {
            ID = 1;
            listEmpty = false;
        }
        String stringID = "generator" + ID;

        List<String> generatorsList = getGeneratorsList();
        generatorsList.add(stringID);
        setGeneratorsList(generatorsList);

        // Different aproach of saving data because the FileWrapper doesn't always processes all FileChanges in one.
        FileConfiguration fileConfiguration = configFile.getFileConfiguration();
        fileConfiguration.set("generators." + stringID, "");
        fileConfiguration.set("generators." + stringID + ".world", location.getWorld().getName());
        fileConfiguration.set("generators." + stringID + ".x", location.getX());
        fileConfiguration.set("generators." + stringID + ".y", location.getY());
        fileConfiguration.set("generators." + stringID + ".z", location.getZ());
        fileConfiguration.set("generators." + stringID + ".tier", getFirstTier());
        fileConfiguration.set("generators." + stringID + ".tokens", 0);
        configFile.getFileWrapper().saveConfig();
        configFile.recache();

        generators.put(location, new Generator(stringID));
    }

    public void deleteGenerator(Location location) {
        Generator generator = getGeneratorWithLocation(location);
        if (generator == null)
            return;

        List<String> generatorsList = getGeneratorsList();
        generatorsList.remove(generator.getID());
        setGeneratorsList(generatorsList);

        if (generatorsList.size() == 1 && generatorsList.get(0).equals("") || generatorsList.isEmpty()) {
            listEmpty = true;
        }

        String stringID = generator.getID();
        configFile.addFileChange("generators." + stringID + ".world", File.ChangeType.DELETE, null);
        configFile.addFileChange("generators." + stringID + ".x", File.ChangeType.DELETE, null);
        configFile.addFileChange("generators." + stringID + ".y", File.ChangeType.DELETE, null);
        configFile.addFileChange("generators." + stringID + ".z", File.ChangeType.DELETE, null);
        configFile.addFileChange("generators." + stringID + ".tier", File.ChangeType.DELETE, null);
        configFile.addFileChange("generators." + stringID + ".tokens", File.ChangeType.DELETE, null);
        configFile.addFileChange("generators." + stringID, File.ChangeType.DELETE, null);
        configFile.saveChanges();

        generator.removeArmorStand();
        generators.remove(generator.getLocation());
    }

    public Generator getGeneratorWithLocation(Location location) {
        return generators.values().stream().filter(generator -> {
            if (generator.getLocation().equals(location)) {
                return true;
            }
            return false;
        }).findFirst().orElse(null);
    }

    public List<String> getGeneratorsList() {
        return (List<String>) configFile.asList("generators-list");
    }

    public void setGeneratorsList(List<String> list) {
        configFile.addFileChange("generators-list", File.ChangeType.SET, list);
        configFile.saveChanges();
    }

    public void disableGenerators() {
        generators.values().stream().forEach(generator -> generator.removeArmorStand());
    }

    public String getFirstTier() {
        return Archive.get("config").asList("tiers-list").get(0);
    }

    public GeneratorTimer getGeneratorTimer() {
        return generatorTimer;
    }

    public String getArmorStandName() {
        return armorStandName;
    }
}
