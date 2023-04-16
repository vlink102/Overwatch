package me.vlink102.personal.overwatch.minecraftlink;

import me.vlink102.personal.overwatch.EntityStats;
import me.vlink102.personal.overwatch.Overwatch;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionbarManager {
    public ActionbarManager(Overwatch overwatch) {}

    public static final ChatColor health = ChatColor.of("#FFFFFF");
    public static final ChatColor shields = ChatColor.of("#28BEFF");
    public static final ChatColor overhealth = ChatColor.of("#3DF83E");
    public static final ChatColor armor = ChatColor.of("#FFAD00");
    public static final ChatColor health_boost = ChatColor.of("#FFFC84");
    public static final ChatColor anti = ChatColor.of("#F204F2");
    public static final ChatColor no_health = ChatColor.of("#636169");

    public static String createHealthString(int barCount, me.vlink102.personal.overwatch.minecraftlink.Player player, boolean showWords) {
        EntityStats stats = player.getHero().entityStats();
        double maxHealth = stats.getHealth();
        double maxArmor = stats.getArmor();
        double maxShields = stats.getShields();
        double maxTotal = maxHealth + maxArmor + maxShields + player.getOverhealth();

        double healthBars = scaleRange(player.getHealth(), 0, barCount, 0, maxTotal);
        double armorBars = scaleRange(player.getArmor(), 0, barCount, 0, maxTotal);
        double shieldBars = scaleRange(player.getShields(), 0, barCount, 0, maxTotal);
        double overhealthBars = scaleRange(player.getOverhealth(), 0, barCount, 0, maxTotal);

        return (player.isOverallAntied() ?  anti + (showWords ? "§lANTI§r  " : "§l⃠§r  ") + anti + "[§r" : (player.getOverallHealingBoostPercentage() > 0 ? health_boost + (showWords ? "§lBOOSTED§r  " : "§l✚§r  ") + health_boost + "[§r" : "")) +
                health + "|".repeat((int) Math.round(healthBars)) +
                armor + "|".repeat((int) Math.round(armorBars)) +
                shields + "|".repeat((int) Math.round(shieldBars)) +
                overhealth + "|".repeat((int) Math.round(overhealthBars)) +
                no_health + "|".repeat((int) (barCount - (healthBars + armorBars + shieldBars + overhealthBars))) +
                (player.isOverallAntied() ? anti + "]" : (player.getOverallHealingBoostPercentage() > 0 ? health_boost + "]§r" : "")); // + "    §7Health: §f" + player.getHealth() + "§8,  §7Armor: §6" + player.getArmor() + "§8, §7Shields: §b" + player.getShields() + "§8, §7Overhealth: §a" + player.getOverhealth());
    }

    public static String getDisplay(int barCount, double x, double max) {
        double healthBars = scaleRange(x, 0, barCount, 0, max);

        return health + "|".repeat((int) Math.round(healthBars)) +
                no_health + "|".repeat((int) (barCount - healthBars));
    }

    public static String getHealthDisplay(int barCount, me.vlink102.personal.overwatch.minecraftlink.Player player) {
        return getDisplay(barCount, player.getHealth(), player.getHero().entityStats().getHealth());
    }

    public static String getArmorDisplay(int barCount, me.vlink102.personal.overwatch.minecraftlink.Player player) {
        return getDisplay(barCount, player.getArmor(), player.getHero().entityStats().getArmor());
    }

    public static String getShieldDisplay(int barCount, me.vlink102.personal.overwatch.minecraftlink.Player player) {
        return getDisplay(barCount, player.getShields(), player.getHero().entityStats().getShields());
    }

    public static String getOverHealthDisplay(int barCount, me.vlink102.personal.overwatch.minecraftlink.Player player) {
        return getDisplay(barCount, player.getOverhealth(), player.getOverhealth());
    }

    public static double scaleRange(double x, double a, double b, double min, double max) {
        if (max == min) {
            return 0;
        }
        return ((b - a) * (x - min)) / (max - min);
    }

    public void updateActionBar(Player player) {
        me.vlink102.personal.overwatch.minecraftlink.Player player1 = Overwatch.players.get(player.getUniqueId());
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(createHealthString(75, player1, false)));
    }
}
