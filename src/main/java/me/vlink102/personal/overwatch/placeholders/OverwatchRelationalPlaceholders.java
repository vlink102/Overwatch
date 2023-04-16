package me.vlink102.personal.overwatch.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import me.vlink102.personal.overwatch.Overwatch;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OverwatchRelationalPlaceholders extends PlaceholderExpansion implements Relational {
    private final Overwatch plugin;

    public OverwatchRelationalPlaceholders(Overwatch plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "V_Link";
    }

    @Override
    public String getIdentifier() {
        return "overwatch-relational";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    public static final ChatColor ally = ChatColor.of("#26D4FD");
    public static final ChatColor enemy = ChatColor.of("#EE3052");

    @Override
    public String onPlaceholderRequest(Player one, Player two, String identifier) {
        if(one == null || two == null)
            return null; // We require both Players to be online

        if (identifier.equalsIgnoreCase("name")) {
            me.vlink102.personal.overwatch.minecraftlink.Player p1 = Overwatch.players.get(one.getUniqueId());
            me.vlink102.personal.overwatch.minecraftlink.Player p2 = Overwatch.players.get(two.getUniqueId());

            if (p1.isAlly(p2)) {
                return ally + one.getName();
            } else {
                return enemy + one.getName();
            }
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
