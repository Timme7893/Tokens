package net.timme7893.tokens.utils.file;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;

import net.timme7893.tokens.utils.menus.ItemStackHelp;
import net.timme7893.tokens.utils.text.PKeys;
import net.timme7893.tokens.utils.text.PValues;
import net.timme7893.tokens.utils.text.Text;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class File {

    @Getter
    private final FileWrapper fileWrapper;
    @Getter
    private final FileConfiguration fileConfiguration;
    @Getter
    private final String name;
    private final Map<String, Map.Entry<Object, ChangeType>> fileChanges = new HashMap<>();
    @Getter
    private Map<String, Object> fileMap = Maps.newHashMap();

    public File(FileConfiguration fileConfiguration, String name) {
        this.fileWrapper = null;
        this.fileConfiguration = fileConfiguration;
        this.name = name;
        this.cache();
    }

    public File(FileWrapper fileWrapper, String name) {
        this.fileWrapper = fileWrapper;
        this.fileConfiguration = fileWrapper.getConfig();
        this.name = name;
        this.cache();
    }

    public void saveChanges() {
        this.fileChanges.forEach((section, entry) -> {
            if (entry.getValue().equals(ChangeType.DELETE)) {
                this.fileConfiguration.set(section, null);
            } else if (entry.getValue().equals(ChangeType.SET)) {
                this.fileConfiguration.set(section, entry.getKey());
            }
        });
        this.fileWrapper.saveConfig();
    }

    public void addFileChange(String section, ChangeType fileChangeType, Object object) {
        this.fileChanges.put(section, new AbstractMap.SimpleEntry<>(object, fileChangeType));
        this.fileMap.put(section, object);
    }

    public ItemStack getItem(Player player, String section) {
        return this.getItem(player, section, null, null, null);
    }

    public ItemStack getItem(Player player, String section, PKeys pKeys, PValues pValues, String itemPlaceholder) {
        return ItemStackHelp.newItemStack(player, this.asString(section + ".item"),
                this.asString(section + ".name", pKeys, pValues), this.asList(section + ".lore", pKeys, pValues), itemPlaceholder);
    }

    public ItemStack getHeadItem(Player player, String owner, String section, PKeys pKeys, PValues pValues) {
        return ItemStackHelp.newHeadItem(player, owner, this.asString(section + ".name", pKeys, pValues),
                this.asList(section + ".lore", pKeys, pValues));
    }

    public Set<String> asSectionKeys(String section, boolean deep) {
        if (!this.fileConfiguration.contains(section))
            return Sets.newHashSet();

        return Objects.requireNonNull(this.fileConfiguration.getConfigurationSection(section)).getKeys(deep);
    }

    public String asString(String section, PKeys pKeys, PValues pValues) {
        String replacer = "error";
        if (this.throwErrorCheck(section, replacer) || this.valueErrorCheck(section, "asString", replacer))
            return Text.format(replacer);
        return Text.format((String) this.getCached(section), pKeys, pValues);
    }

    public String asString(String section) {
        return this.asString(section, null, null);
    }

    public Integer asInt(String section) {
        String replacer = "0";
        if (this.throwErrorCheck(section, replacer) || this.valueErrorCheck(section, "asInt", replacer))
            return 0;
        return (Integer) this.getCached(section);
    }

    public Boolean asBool(String section) {
        String replacer = "false";
        if (this.throwErrorCheck(section, replacer) || this.valueErrorCheck(section, "asBool", replacer))
            return false;
        return (Boolean) this.getCached(section);
    }

    public List<String> asList(String section, PKeys pKeys, PValues pValues) {
        String replacer = "an empty stringlist";
        if (this.throwErrorCheck(section, replacer) || this.valueErrorCheck(section, "asList", replacer))
            return Collections.emptyList();
        List<String> coloredList = new ArrayList<>();
        ((List<String>) this.getCached(section)).forEach(string -> coloredList.add(Text.format(string, pKeys, pValues)));
        return coloredList;
    }

    public List<String> asList(String section) {
        return this.asList(section, null, null);
    }

    public Set<String> asSet(String section) {
        String replacer = "an empty set";
        if (this.throwErrorCheck(section, replacer) || this.valueErrorCheck(section, "asSet", replacer))
            return Collections.emptySet();
        return (Set<String>) this.getCached(section);
    }

    public Object asObject(String section) {
        String replacer = "an empty object";
        if (this.throwErrorCheck(section, replacer) || this.valueErrorCheck(section, "asObject", replacer))
            return new Object();
        return this.getCached(section);
    }

    public Double asDouble(String section) {
        String replacer = "0.0";
        if (this.throwErrorCheck(section, replacer) || this.valueErrorCheck(section, "asDouble", replacer))
            return 0.0;
        return Double.valueOf(this.getCached(section).toString());
    }

    public boolean contains(String section) {
        return this.getCached(section) != null;
    }

    private boolean valueErrorCheck(String section, String value, String replacer) {
        switch (value) {
            case "asInt":
                if (!this.getCached(section).getClass().getName().equals("java.lang.Integer")) {
                    this.printValueErrorCheck(section, value, replacer);
                    return true;
                }
                break;
            case "asString":
                if (!this.getCached(section).getClass().getName().equals("java.lang.String")) {
                    this.printValueErrorCheck(section, value, replacer);
                    return true;
                }
                break;
            case "asBool":
                if (!this.getCached(section).getClass().getName().equals("java.lang.Boolean")) {
                    this.printValueErrorCheck(section, value, replacer);
                    return true;
                }
                break;
            case "asDouble":
                if (this.getCached(section).getClass().getName().equals("java.lang.Integer")) return false;
                if (!this.getCached(section).getClass().getName().equals("java.lang.Double")) {
                    this.printValueErrorCheck(section, value, replacer);
                    return true;
                }
                break;
            case "asList":
                if (!this.getCached(section).getClass().getName().equals("java.util.ArrayList")) {
                    this.printValueErrorCheck(section, value, replacer);
                    return true;
                }
                break;
            case "asSet":
                if (!this.getCached(section).getClass().getName().equals("java.util.HashSet")) {
                    this.printValueErrorCheck(section, value, replacer);
                    return true;
                }
                break;
        }
        return false;
    }

    private void printValueErrorCheck(String section, String value, String replacer) {
        System.out.println("==================================================================================");
        System.out.println("The configure section " + section + " has to be of the type " + value.toLowerCase().replace("as", "") + "!");
        System.out.println("To avoid further errors we will replace this value with " + replacer);
        System.out.println("==================================================================================");
    }

    private boolean throwErrorCheck(String section, String replacer) {
        if (this.getCached(section) == null) {
            System.out.println("==================================================================================");
            System.out.println("The configure section " + section + " does not exist in " + this.name + ".yml!");
            System.out.println("To avoid further errors we will replace this value with " + replacer);
            System.out.println("==================================================================================");
            return true;
        }
        return false;
    }

    private Object getCached(String section) {
        return this.fileMap.get(section);
    }

    public void recache() {
        if (this.fileWrapper != null)
            this.fileWrapper.reloadConfig();

        this.cache();
    }

    private void cache() {
        this.fileMap = this.fileConfiguration.getKeys(true)
                .stream()
                .collect(Collectors.toMap(key -> key, this.fileConfiguration::get));
    }

    public String getName() {
        return name;
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public FileWrapper getFileWrapper() {
        return fileWrapper;
    }

    public enum ChangeType {
        DELETE, SET
    }
}
