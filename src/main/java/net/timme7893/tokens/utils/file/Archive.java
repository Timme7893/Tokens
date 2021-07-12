package net.timme7893.tokens.utils.file;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.Arrays;
import java.util.Map;

public class Archive {

    @Getter
    private static Map<String, File> fileMap = Maps.newHashMap();
    private final JavaPlugin plugin;

    public Archive(JavaPlugin plugin) {
        this.plugin = plugin;

        fileMap.put("empty", new File(plugin.getConfig(), "empty"));
    }

    public static File get(String name) {
        return fileMap.get(name == null ? "empty" : name);
    }

    public void rearchive() {
        fileMap.values().forEach(File::recache);
    }

    public void insertDefaultConfig() {
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        fileMap.put("config", new File(plugin.getConfig(), "config"));
    }

    public void insertWrapperFiles(String... fileNames) {
        Arrays.stream(fileNames)
                .map(fileName -> new File(new FileWrapper(plugin, fileName.split("/").length > 1 ?
                        fileName.split("/")[0] : "", fileName),
                        (fileName.split("/").length > 1 ?
                                fileName.split("/")[1] : fileName).replace(".yml", "")))
                .forEach(file -> fileMap.put(file.getName(), file));
    }
}
