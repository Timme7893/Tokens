package net.timme7893.tokens.commands;

import net.timme7893.tokens.Tokens;
import net.timme7893.tokens.tokens.TPlayer;
import net.timme7893.tokens.utils.file.Archive;
import net.timme7893.tokens.utils.messages.MessageAPI;
import net.timme7893.tokens.utils.text.PKeys;
import net.timme7893.tokens.utils.text.PValues;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TokensCommand implements CommandExecutor {

    private MessageAPI api = Tokens.getInstance().messageAPI;
    private String permission = Archive.get("messages").asString("admin-permission");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        TPlayer tplayer = new TPlayer(player);

        if (label.equalsIgnoreCase("tokens")) {

        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            if (!checkPermission(player)) {
                api.sendMessage(player,"no-permission", PKeys.set("admin-permission"), PValues.set(permission,1));
                return true;
            }

            String user = args[1];
            if (getPlayer(user) == null) {
                api.sendMessage(player,"user-not-found", PKeys.set("user"), PValues.set(user,1));
                return true;
            }
            TPlayer target = new TPlayer(getPlayer(user));

            String number = args[2];
            if (!validInteger(number)) {
                player.sendMessage(api.getMessage("not-valid-number"));
                return true;
            }
            int tokens = Integer.parseInt(number);

            target.setTokens(tokens);
            api.sendMessage(player,"set-tokens", PKeys.set("user","tokens"), PValues.set(target.getBukkitPlayer().getName(),tokens,1,2));
            return true;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            if (!checkPermission(player)) {
                api.sendMessage(player,"no-permission", PKeys.set("admin-permission"), PValues.set(permission,1));
                return true;
            }

            String user = args[1];
            if (getPlayer(user) == null) {
                api.sendMessage(player,"user-not-found", PKeys.set("user"), PValues.set(user,1));
                return true;
            }
            TPlayer target = new TPlayer(getPlayer(user));

            String number = args[2];
            if (!validInteger(number)) {
                player.sendMessage(api.getMessage("not-valid-number"));
                return true;
            }
            int tokensAdded = Integer.parseInt(number);

            target.addTokens(tokensAdded);
            api.sendMessage(player,"add-tokens", PKeys.set("tokens","user"), PValues.set(tokensAdded,target.getBukkitPlayer().getName(),1,2));
            return true;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            if (!checkPermission(player)) {
                api.sendMessage(player,"no-permission", PKeys.set("admin-permission"), PValues.set(permission,1));
                return true;
            }

            String user = args[1];
            if (getPlayer(user) == null) {
                api.sendMessage(player,"user-not-found", PKeys.set("user"), PValues.set(user,1));
                return true;
            }
            TPlayer target = new TPlayer(getPlayer(user));

            String number = args[2];
            if (!validInteger(number)) {
                player.sendMessage(api.getMessage("not-valid-number"));
                return true;
            }
            int tokensRemoved = Integer.parseInt(number);

            target.removeTokens(tokensRemoved);
            api.sendMessage(player,"remove-tokens", PKeys.set("tokens","user"), PValues.set(tokensRemoved,target.getBukkitPlayer().getName(),1,2));
            return true;
        }


        if (args.length == 0) {
            api.sendMessage(player,"own-tokens", PKeys.set("tokens"), PValues.set(tplayer.getTokens(),0));
            return true;
        } else if (args.length == 1) {
            String user = args[0];
            if (getPlayer(user) == null) {
                api.sendMessage(player,"user-not-found", PKeys.set("user"), PValues.set(user,1));
                return true;
            }
            TPlayer target = new TPlayer(getPlayer(user));
            api.sendMessage(player,"user-tokens", PKeys.set("user", "tokens"), PValues.set(user,target.getTokens(),1,2));
            return true;
        }
        }

        return true;
    }

    public boolean checkPermission(Player player) {
        return player.hasPermission(permission);
    }

    public Player getPlayer(String username) {
        if (Bukkit.getPlayer(username) != null) {
            return Bukkit.getPlayer(username);
        } else {
            return null;
        }
    }

    public boolean validInteger(String integer) {
        try {
            int number = Integer.parseInt(integer);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
