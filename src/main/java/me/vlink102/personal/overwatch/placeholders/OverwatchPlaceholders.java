package me.vlink102.personal.overwatch.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.Team;
import me.vlink102.personal.overwatch.abilities.types.Reloadable;
import me.vlink102.personal.overwatch.heroes.Hero;
import me.vlink102.personal.overwatch.heroes.roles.Damage;
import me.vlink102.personal.overwatch.heroes.roles.Support;
import me.vlink102.personal.overwatch.heroes.roles.Tank;
import me.vlink102.personal.overwatch.minecraftlink.ActionbarManager;
import me.vlink102.personal.overwatch.minecraftlink.TableGenerator;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.world.inventory.Slot;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class OverwatchPlaceholders extends PlaceholderExpansion {
    private final Overwatch plugin;

    public OverwatchPlaceholders(Overwatch plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "V_Link";
    }

    @Override
    public String getIdentifier() {
        return "overwatch";
    }

    @Override
    public String getVersion() {
        return "1.0.3";
    }

    @Override
    public boolean persist() {
        return true;
    }

    public static String getNPCDisplay(Overwatch plugin, int npcID) {
        Player npc = (Player) plugin.getRegistry().getById(npcID).getEntity();
        return Overwatch.players.get(npc.getUniqueId()).getStatus() == me.vlink102.personal.overwatch.minecraftlink.Player.NPCStatus.ALLY_ALL ? "&e" + npc.getName() + " &7(Ally)" : "&d" + npc.getName() + " &7(Enemy)";
    }

    public static String getNPCStatus(Overwatch plugin, int npcID) {
        Player npc = (Player) plugin.getRegistry().getById(npcID).getEntity();
        return Overwatch.players.get(npc.getUniqueId()).getStatus() == me.vlink102.personal.overwatch.minecraftlink.Player.NPCStatus.ALLY_ALL ? "Ally" : "Enemy";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        String[] paramList = params.split("_");
        if (paramList.length > 0) {
            if (player == null) {
                if (paramList[0].equalsIgnoreCase("npc-status")) {
                    return getNPCStatus(plugin, Integer.parseInt(paramList[1]));
                } else if (paramList[0].equalsIgnoreCase("npc-display")) {
                    return getNPCDisplay(plugin, Integer.parseInt(paramList[1]));
                }
                return getForPlayer(paramList, (Player) plugin.getRegistry().getById(Integer.parseInt(paramList[0])).getEntity());
            } else {
                Player bukkitPlayer = player.getPlayer();
                if (bukkitPlayer == null) return null;
                if (paramList[0].equalsIgnoreCase("self")) {
                    return getForPlayer(paramList, bukkitPlayer);
                } else if (paramList[0].equalsIgnoreCase("scoreboard")) {
                    if (paramList[1].equalsIgnoreCase("header")) {
                        return generateScoreboardTable(Overwatch.players.get(player.getUniqueId())).get(0);
                    }
                    if (paramList.length > 2) {
                        int team = Integer.parseInt(paramList[1]);
                        int teamMember = Integer.parseInt(paramList[2]);
                        switch (team) {
                            case 1 -> {
                                return getScoreboardPlayer(paramList, Overwatch.team1.getPlayers().get(teamMember).getPlayer(), teamMember);
                            }
                            case 2 -> {
                                return getScoreboardPlayer(paramList, Overwatch.team2.getPlayers().get(teamMember).getPlayer(), teamMember);
                            }
                        }
                    }
                } else {
                    for (me.vlink102.personal.overwatch.minecraftlink.Player onlinePlayer : Overwatch.players.values()) {
                        if (onlinePlayer.getPlayerBind().getName().equalsIgnoreCase(paramList[0])) {
                            return getForPlayer(paramList, onlinePlayer.getPlayerBind());
                        }
                    }
                }
            }
        }

        return null;
    }

    private String getScoreboardPlayer(String[] params, me.vlink102.personal.overwatch.minecraftlink.Player player, int index) {
        if (params.length > 3) {
            switch (params[3]) {
                case "role" -> {
                    Hero hero = player.getHero();
                    if (hero instanceof Support) {
                        return "Support";
                    }
                    if (hero instanceof Tank) {
                        return "Tank";
                    }
                    if (hero instanceof Damage) {
                        return "Damage";
                    }
                }
                case "role-icon" -> {
                    Hero hero = player.getHero();
                    if (hero instanceof Support) {
                        return "§l✚";
                    }
                    if (hero instanceof Tank) {
                        return "§l⛨";
                    }
                    if (hero instanceof Damage) {
                        return "§l!!!";
                    }
                }
                case "name" -> {
                    return player.getPlayerBind().getName();
                }
            }
        }
        return null;
    }

    private static String getRoleFromIndex(int index) {
        return switch (index) {
            case 3, 4 -> "            §l✚§r";
            case 1, 2 -> "            §l!!!§r";
            case 0 -> "            §l⛨§r";
            default -> null;
        };
    }

    private static final ChatColor ENEMY_BAR = ChatColor.of("#8E1E2F");
    private static final ChatColor ENEMY_ROLE = ChatColor.of("#E7304D");
    private static final ChatColor ENEMY_DARK = ChatColor.of("#821426");

    private static final ChatColor ALLY_BAR = ChatColor.of("#227C96");
    private static final ChatColor ALLY_ROLE = ChatColor.of("#23CFF6");
    private static final ChatColor ALLY_DARK = ChatColor.of("#12677C");

    private static final ChatColor SELF_BAR = ChatColor.of("#23ABCB");
    private static final ChatColor SELF_DARK = ChatColor.of("#1992AE");

    public static List<String> generateScoreboardTable(me.vlink102.personal.overwatch.minecraftlink.Player player) {
        Team allies = player.getTeam();
        Team enemies = allies.getTeamID() == Overwatch.team1.getTeamID() ? Overwatch.team2 : Overwatch.team1;

        List<Team.Slot> allyPlayers = allies.getPlayers();
        List<Team.Slot> enemyPlayers = enemies.getPlayers();
        List<Team.Slot> allyNpcs = Overwatch.allyNPC.getPlayers();
        List<Team.Slot> enemyNpcs = Overwatch.enemyNPC.getPlayers();

        TableGenerator generator = new TableGenerator(
                TableGenerator.Alignment.LEFT,
                TableGenerator.Alignment.LEFT,
                TableGenerator.Alignment.CENTER,
                TableGenerator.Alignment.CENTER,
                TableGenerator.Alignment.CENTER,
                TableGenerator.Alignment.LEFT,
                TableGenerator.Alignment.CENTER,
                TableGenerator.Alignment.CENTER,
                TableGenerator.Alignment.CENTER
        );

        generator.addRow("§l§r", "§l§r", "§7E", "§7A", "§7D", "§8", "§7DMG", "§7H", "§7MIT");

        int i = 0;
        for (Team.Slot slot : allyPlayers) {
            me.vlink102.personal.overwatch.minecraftlink.Player team1Player = slot.getPlayer();
            if (team1Player == null) {
                generator.addRow(
                        "§f" + getRoleFromIndex(i),
                        "§7Waiting for players...",
                        "§8" +"0", "§8" +"0", "§8" +"0", "", "§7" +"0", "§7" +"0", "§7" +"0"
                );
                i++;
                continue;
            }
            i++;


            Hero hero = team1Player.getHero();
            String icon = "";
            if (hero instanceof Support) {
                icon = "            §l✚§r"; // 47
            }
            if (hero instanceof Damage) {
                icon = "            §l!!!§r"; // 49
            }
            if (hero instanceof Tank) {
                icon = "            §l⛨§r"; // 47
            }
            String playerName = team1Player.getPlayerBind().getName();
            String kills = String.valueOf(team1Player.getEliminations());
            String assists = String.valueOf(team1Player.getAssists());
            String deaths = String.valueOf(team1Player.getDeaths());
            String dmg = String.valueOf((int) team1Player.getDamageDealt());
            String h = String.valueOf((int) team1Player.getHealingDone());
            String mit = String.valueOf((int) team1Player.getDamageMitigated());

            if (player.getPlayerBind().getUniqueId().equals(team1Player.getPlayerBind().getUniqueId())) {
                generator.addRow(
                        ALLY_ROLE + icon + "§8",
                        SELF_BAR + playerName + "§8",
                        SELF_DARK +kills + "§8",
                        SELF_DARK +assists + "§8",
                        SELF_DARK +deaths + "§8",
                        "" + "§8",
                        SELF_BAR +dmg + "§8",
                        SELF_BAR +h + "§8",
                        SELF_BAR +mit + "§8"
                );
            } else {
                generator.addRow(
                        ALLY_ROLE +icon + "§8",
                        ALLY_BAR +playerName + "§8",
                        ALLY_DARK +kills + "§8",
                        ALLY_DARK +assists + "§8",
                        ALLY_DARK +deaths + "§8",
                        "" + "§8",
                        ALLY_BAR +dmg + "§8",
                        ALLY_BAR +h + "§8",
                        ALLY_BAR +mit + "§8"
                );
            }
        }

        i = 0;
        for (Team.Slot slot : enemyPlayers) {
            me.vlink102.personal.overwatch.minecraftlink.Player enemyPlayer = slot.getPlayer();
            if (enemyPlayer == null) {
                generator.addRow(
                        "§f" + getRoleFromIndex(i),
                        "§7" +"Waiting for players...",
                        "§8" +"0", "§8" +"0", "§8" +"0", "", "§7" +"0", "§7" +"0", "§7" +"0"
                );
                i++;
                continue;
            }
            i++;

            Hero hero = enemyPlayer.getHero();
            String icon = "";
            if (hero instanceof Support) {
                icon = "            §l✚§r"; // 47
            }
            if (hero instanceof Damage) {
                icon = "            §l!!!§r"; // 49
            }
            if (hero instanceof Tank) {
                icon = "            §l⛨§r"; // 47
            }
            String playerName = enemyPlayer.getPlayerBind().getName();
            String kills = String.valueOf(enemyPlayer.getEliminations());
            String assists = String.valueOf(enemyPlayer.getAssists());
            String deaths = String.valueOf(enemyPlayer.getDeaths());
            String dmg = String.valueOf((int) enemyPlayer.getDamageDealt());
            String h = String.valueOf((int) enemyPlayer.getHealingDone());
            String mit = String.valueOf((int) enemyPlayer.getDamageMitigated());

            generator.addRow(
                    ENEMY_ROLE + icon + "§8",
                    ENEMY_BAR +playerName + "§8",
                    ENEMY_DARK +kills + "§8",
                    ENEMY_DARK +assists + "§8",
                    ENEMY_DARK +deaths + "§8",
                    "" + "§8",
                    ENEMY_BAR +dmg + "§8",
                    ENEMY_BAR +h + "§8",
                    ENEMY_BAR +mit + "§8"
            );
        }
        i = 0;
        for (Team.Slot slot : allyNpcs) {
            me.vlink102.personal.overwatch.minecraftlink.Player allyNpc = slot.getPlayer();
            if (allyNpc == null) {
                generator.addRow(
                        "§f" + "            §l⁇§r",
                        "§7" + "Empty NPC slot §8(Ally)",
                        "§8" +"0", "§8" +"0", "§8" +"0", "", "§7" +"0", "§7" +"0", "§7" +"0"
                );
                i++;
                continue;
            }
            i++;

            Hero hero = allyNpc.getHero();
            String icon = "";
            if (hero instanceof Support) {
                icon = "            §l✚§r"; // 47
            }
            if (hero instanceof Damage) {
                icon = "            §l!!!§r"; // 49
            }
            if (hero instanceof Tank) {
                icon = "            §l⛨§r"; // 47
            }
            String playerName = allyNpc.getPlayerBind().getName();
            String kills = String.valueOf(allyNpc.getEliminations());
            String assists = String.valueOf(allyNpc.getAssists());
            String deaths = String.valueOf(allyNpc.getDeaths());
            String dmg = String.valueOf((int) allyNpc.getDamageDealt());
            String h = String.valueOf((int) allyNpc.getHealingDone());
            String mit = String.valueOf((int) allyNpc.getDamageMitigated());

            generator.addRow(
                    "§e" + icon + "§8",
                    "§6" +playerName + "§8",
                    "§6" +kills + "§8",
                    "§6" +assists + "§8",
                    "§6" +deaths + "§8",
                    "" + "§8",
                    "§6" +dmg + "§8",
                    "§6" +h + "§8",
                    "§6" +mit + "§8"
            );
        }

        i = 0;
        for (Team.Slot slot : enemyNpcs) {
            me.vlink102.personal.overwatch.minecraftlink.Player enemyNpc = slot.getPlayer();
            if (enemyNpc == null) {
                generator.addRow(
                        "§f" + "            §l⁇§r",
                        "§7" + "Empty NPC slot §8(Enemy)",
                        "§8" +"0", "§8" +"0", "§8" +"0", "", "§7" +"0", "§7" +"0", "§7" +"0"
                );
                i++;
                continue;
            }
            i++;

            Hero hero = enemyNpc.getHero();
            String icon = "";
            if (hero instanceof Support) {
                icon = "            §l✚§r"; // 47
            }
            if (hero instanceof Damage) {
                icon = "            §l!!!§r"; // 49
            }
            if (hero instanceof Tank) {
                icon = "            §l⛨§r"; // 47
            }
            String playerName = enemyNpc.getPlayerBind().getName();
            String kills = String.valueOf(enemyNpc.getEliminations());
            String assists = String.valueOf(enemyNpc.getAssists());
            String deaths = String.valueOf(enemyNpc.getDeaths());
            String dmg = String.valueOf((int) enemyNpc.getDamageDealt());
            String h = String.valueOf((int) enemyNpc.getHealingDone());
            String mit = String.valueOf((int) enemyNpc.getDamageMitigated());

            generator.addRow(
                    "§d" + icon + "§8",
                    "§5" +playerName + "§8",
                    "§5" +kills + "§8",
                    "§5" +assists + "§8",
                    "§5" +deaths + "§8",
                    "" + "§8",
                    "§5" +dmg + "§8",
                    "§5" +h + "§8",
                    "§5" +mit + "§8"
            );
        }

        return generator.generate(TableGenerator.Receiver.CLIENT, true, true);
    }

    public static String getPaddedString(String str, int maxPadding) {
        if (str == null) {
            throw new NullPointerException("Can not add padding in null String!");
        }

        int length = str.length();
        int padding = (maxPadding - length) / 2;//decide left and right padding
        if (padding <= 0) {
            return str;// return actual String if padding is less than or equal to 0
        }

        String empty = "", hash = "#";//hash is used as a place holder

        // extra character in case of String with even length
        int extra = (length % 2 == 0) ? 1 : 0;

        String leftPadding = "%" + padding + "s";
        String rightPadding = "%" + (padding - extra) + "s";

        String strFormat = leftPadding + "%s" + rightPadding;
        String formattedString = String.format(strFormat, empty, hash, empty);

        //Replace space with * and hash with provided String
        return formattedString.replace(hash, str);
    }

    private String getForPlayer(String[] paramList, Player player) {
        if (player != null) {
            if (paramList.length > 1) {
                me.vlink102.personal.overwatch.minecraftlink.Player overwatchPlayer = Overwatch.players.get(player.getUniqueId());
                switch (paramList[1]) {
                    case "display" -> {
                        if (paramList.length > 2) {
                            int barCount = 75;
                            if (paramList.length > 3) {
                                barCount = Integer.parseInt(paramList[3]);
                            }
                            switch (paramList[2]) {
                                case "health" -> {
                                    return ActionbarManager.getHealthDisplay(barCount, overwatchPlayer);
                                }
                                case "armor" -> {
                                    return ActionbarManager.getArmorDisplay(barCount, overwatchPlayer);
                                }
                                case "shield" -> {
                                    return ActionbarManager.getShieldDisplay(barCount, overwatchPlayer);
                                }
                                case "overhealth" -> {
                                    return ActionbarManager.getOverHealthDisplay(barCount, overwatchPlayer);
                                }
                                case "total" -> {
                                    return ActionbarManager.createHealthString(barCount, overwatchPlayer, false);
                                }
                            }
                        }
                    }
                    case "health" -> {
                        return String.valueOf(overwatchPlayer.getHealth());
                    }
                    case "armor" -> {
                        return String.valueOf(overwatchPlayer.getArmor());
                    }
                    case "armor-ehp" -> {
                        return String.valueOf(overwatchPlayer.getArmorEhp());
                    }
                    case "shields" -> {
                        return String.valueOf(overwatchPlayer.getShields());
                    }
                    case "overhealth" -> {
                        return String.valueOf(overwatchPlayer.getOverhealth());
                    }
                    case "is-antied" -> {
                        return String.valueOf(overwatchPlayer.isOverallAntied());
                    }
                    case "total" -> {
                        return String.valueOf(overwatchPlayer.getTotalHealth());
                    }
                    case "total-ehp" -> {
                        return String.valueOf(overwatchPlayer.getTotalEhp());
                    }
                    case "remaining-ammo" -> {
                        return String.valueOf(overwatchPlayer.getAmmoMap().get(overwatchPlayer.getSelected()));
                    }
                    case "max-ammo" -> {
                        if (overwatchPlayer.getSelected() instanceof Reloadable reloadable) {
                            return String.valueOf(reloadable.magazineSize());
                        }
                    }
                    case "hero" -> {
                        return overwatchPlayer.getHero().name();
                    }
                    case "game" -> {
                        if (paramList.length > 2) {
                            switch (paramList[2]) {
                                case "kills" -> {
                                    return String.valueOf(overwatchPlayer.getEliminations());
                                }
                                case "deaths" -> {
                                    return String.valueOf(overwatchPlayer.getDeaths());
                                }
                                case "assists" -> {
                                    return String.valueOf(overwatchPlayer.getAssists());
                                }
                                case "damage-dealt" -> {
                                    return String.valueOf(overwatchPlayer.getDamageDealt());
                                }
                                case "damage-mitigated" -> {
                                    return String.valueOf(overwatchPlayer.getDamageMitigated());
                                }
                                case "healing-done" -> {
                                    return String.valueOf(overwatchPlayer.getHealingDone());
                                }
                            }
                        }
                    }
                    case "modifiers" -> {
                        if (paramList.length > 2) {
                            switch (paramList[2]) {
                                case "healing-boost" -> {
                                    return (overwatchPlayer.getOverallHealingBoostPercentage() * 100) + "%";
                                }
                                case "damage-boost" -> {
                                    return (overwatchPlayer.getOverallDamageDealtAmplification() * 100) + "%";
                                }
                                case "damage-reduction" -> {
                                    return (overwatchPlayer.getOverallDamageReduction() * 100) + "%";
                                }
                                case "damage-taken-amplification" -> {
                                    return (overwatchPlayer.getOverallDamageTakenAmplification() * 100) + "%";
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
