package net.timme7893.tokens.utils.text;


import net.timme7893.tokens.utils.file.File;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Text {

    public static void fileToPlayer(Player player, String section, File file, PKeys pKeys, PValues pValues) {
        player.sendMessage(format(file.asString(section), pKeys, pValues));
    }

    public static void fileToPlayer(Player player, String section, File file) {
        fileToPlayer(player, section, file, null, null);
    }

    public static void player(Player player, String string) {
        player.sendMessage(format(string));
    }

    public static void sender(CommandSender sender, String string) {
        sender.sendMessage(format(string));
    }

    public static String format(String string, PKeys pKeys, PValues pValues) {
        return ChatColor.translateAlternateColorCodes('&', replacePlaceholders(string, pKeys, pValues));
    }

    public static String format(String string) {
        return format(string, null, null);
    }

    public static List<String> format(List<String> list, PKeys pKeys, PValues pValues) {
        return list.stream().map(string -> Text.format(string, pKeys, pValues)).collect(Collectors.toList());
    }

    public static List<String> format(List<String> list) {
        return format(list, null, null);
    }

    public static String replacePlaceholders(String text, PKeys pKeys, PValues pValues) {
        if (pKeys == null || pValues == null)
            return text;

        for (int i = 0; i < pKeys.getKeys().size(); i++)
            text = text.replace("%" + pKeys.getKeys().get(i) + "%", Objects.toString(pValues.getValues().get(i)));

        return text;
    }
}
