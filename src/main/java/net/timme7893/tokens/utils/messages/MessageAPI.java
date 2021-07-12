package net.timme7893.tokens.utils.messages;

import net.timme7893.tokens.utils.file.Archive;
import net.timme7893.tokens.utils.file.File;
import net.timme7893.tokens.utils.text.PKeys;
import net.timme7893.tokens.utils.text.PValues;
import net.timme7893.tokens.utils.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageAPI {

    private File messageFile;

    public MessageAPI() {
        messageFile = Archive.get("messages");
    }

    public String getMessage(String path) {
        if (messageFile.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', messageFile.asString("prefix") + messageFile.asString(path + ".message"));
        } else {
            return "path is invalid";
        }
    }

    public String getMessage(String path, PKeys pKeys, PValues pValues) {
        String message = getMessage(path);
        return Text.format(message,pKeys,pValues);
    }

    public void sendMessage(Player player, String path, PKeys pKeys, PValues pValues) {
        String message = getMessage(path,pKeys,pValues);
        player.sendMessage(message);
    }

    public void sendMessage(Player player, String path) {
        player.sendMessage(getMessage(path));
    }

}
