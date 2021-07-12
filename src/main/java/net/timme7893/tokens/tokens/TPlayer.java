package net.timme7893.tokens.tokens;

import net.timme7893.tokens.utils.file.Archive;
import net.timme7893.tokens.utils.file.File;
import org.bukkit.entity.Player;

public class TPlayer {

    private Player player;
    private File tokensFile;
    private String UUID;

    private int tokens;

    public TPlayer(Player player) {
        this.player = player;
        this.tokensFile = Archive.get("tokensData");
        this.UUID = player.getUniqueId().toString();

        if (!tokensFile.contains(UUID + ".tokens")) {
            tokensFile.addFileChange(UUID, File.ChangeType.SET, "");
            tokensFile.addFileChange(UUID + ".tokens", File.ChangeType.SET, 0);
            tokensFile.saveChanges();
        }

        tokens = tokensFile.asInt(UUID + ".tokens");
    }

    public Player getBukkitPlayer() {
        return player;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
        tokensFile.addFileChange(UUID + ".tokens", File.ChangeType.SET, tokens);
        tokensFile.saveChanges();
    }

    public void addTokens(int tokens) {
        setTokens(getTokens() + tokens);
    }

    public void removeTokens(int tokens) {
        setTokens(getTokens() - tokens);
    }

    public boolean hasTokens(int tokens) {
        return getTokens() >= tokens;
    }
}
