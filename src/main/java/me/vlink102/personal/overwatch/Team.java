package me.vlink102.personal.overwatch;

import me.vlink102.personal.overwatch.heroes.Hero;
import me.vlink102.personal.overwatch.heroes.roles.Damage;
import me.vlink102.personal.overwatch.heroes.roles.Support;
import me.vlink102.personal.overwatch.heroes.roles.Tank;
import me.vlink102.personal.overwatch.minecraftlink.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Team {
    private final org.bukkit.scoreboard.Team team;
    private final int teamID;
    private final List<Slot> players;
    private final boolean isNPCTeam;

    private List<Player> savedPlayers;
    private List<org.bukkit.entity.Player> savedBukkitPlayers;

    public Team(int teamID, boolean isNPCTeam) {
        this.teamID = teamID;
        this.players = new ArrayList<>();
        this.savedPlayers = new ArrayList<>();
        this.savedBukkitPlayers = new ArrayList<>();
        this.isNPCTeam = isNPCTeam;
        if (!isNPCTeam) {
            players.add(new Slot(SlotRole.TANK));
            players.add(new Slot(SlotRole.DAMAGE));
            players.add(new Slot(SlotRole.DAMAGE));
            players.add(new Slot(SlotRole.SUPPORT));
            players.add(new Slot(SlotRole.SUPPORT));
        } else {
            players.add(new Slot(SlotRole.UNKNOWN));
            players.add(new Slot(SlotRole.UNKNOWN));
        }
        this.team = Overwatch.scoreboard.registerNewTeam(String.valueOf(teamID));
        team.setAllowFriendlyFire(false);
        team.setCanSeeFriendlyInvisibles(true);
        team.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
        team.setOption(org.bukkit.scoreboard.Team.Option.DEATH_MESSAGE_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
    }

    public int getTeamID() {
        return teamID;
    }

    public List<Slot> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        Hero hero = player.getHero();
        if (hero instanceof Support) {
            for (Slot slot : players) {
                if (slot.role.equals(SlotRole.SUPPORT) && slot.isAvailable()) {
                    slot.setPlayer(player);
                    team.addEntry(player.getPlayerBind().getName());
                    player.setTeam(this);
                    organiseConnectives();
                    return;
                }
            }
        }
        if (hero instanceof Damage) {
            for (Slot slot : players) {
                if (slot.role.equals(SlotRole.DAMAGE) && slot.isAvailable()) {
                    slot.setPlayer(player);
                    team.addEntry(player.getPlayerBind().getName());
                    player.setTeam(this);
                    organiseConnectives();
                    return;
                }
            }
        }
        if (hero instanceof Tank) {
            for (Slot slot : players) {
                if (slot.role.equals(SlotRole.TANK) && slot.isAvailable()) {
                    slot.setPlayer(player);
                    team.addEntry(player.getPlayerBind().getName());
                    player.setTeam(this);
                    organiseConnectives();
                    return;
                }
            }
        }

        for (Slot slot : players) {
            if (slot.isAvailable()) {
                slot.setPlayer(player);
                team.addEntry(player.getPlayerBind().getName());
                player.setTeam(this);
                organiseConnectives();
                return;
            }
        }

        throw new IndexOutOfBoundsException("Team " + teamID + " could not fit player (full)");
    }

    public boolean isFull() {
        for (Slot slot : players) {
            if (slot.isAvailable()) {
                return false;
            }
        }
        return true;
    }

    public void removePlayer(Player player) {
        for (Slot slot : players) {
            if (player.equals(slot.getPlayer())) {
                slot.setPlayer(null);
                team.removeEntry(player.getPlayerBind().getName());
                player.setTeam(null);
                organiseConnectives();
                return;
            }
        }
    }

    public void organiseConnectives() {
        players.sort(new SlotComparator());
        savedPlayers = getPlayers(this);
        savedBukkitPlayers = getBukkitPlayers(this);
    }

    public boolean isNPCTeam() {
        return isNPCTeam;
    }

    public static class SlotComparator implements Comparator<Slot> {
        @Override
        public int compare(Slot o1, Slot o2) {
            return o1.compareTo(o2);
        }
    }

    public static class Slot implements Comparable<Slot> {
        private Player player;
        private final SlotRole defaultRole;
        private SlotRole role;

        public Slot(Player player, SlotRole role, SlotRole defaultRole) {
            this.player = player;
            this.role = role;
            this.defaultRole = defaultRole;
        }

        public Slot() {
            this.player = null;
            this.role = SlotRole.UNKNOWN;
            this.defaultRole = SlotRole.UNKNOWN;
        }

        public Slot(Player player) {
            this.player = player;
            this.role = SlotRole.UNKNOWN;
            this.defaultRole = SlotRole.UNKNOWN;
        }

        public Slot(SlotRole role, SlotRole defaultRole) {
            this.player = null;
            this.role = role;
            this.defaultRole = defaultRole;
        }

        public Slot(SlotRole role) {
            this.player = null;
            this.role = role;
            this.defaultRole = role;
        }

        public Slot(Player player, SlotRole role) {
            this.player = player;
            this.role = role;
            this.defaultRole = role;
        }

        public SlotRole getRole() {
            return role;
        }

        public void setRole(SlotRole role) {
            this.role = role;
        }

        public SlotRole getDefaultRole() {
            return defaultRole;
        }

        public void setPlayer(Player value) {
            this.player = value;
        }

        public void removePlayer() {
            this.player = null;
        }

        public Player getPlayer() {
            return player;
        }

        public boolean isAvailable() {
            return player == null;
        }

        @Override
        public int compareTo(@NotNull Team.Slot o) {
            if (role.equals(o.getRole())) {
                if (isAvailable()) {
                    if (o.isAvailable()) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {
                    if (o.isAvailable()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            } else {
                return 0;
            }
        }
    }

    private List<Player> getPlayers(Team team) {
        List<Player> p1 = new ArrayList<>();
        for (Slot slot : team.players) {
            if (slot.player == null) continue;
            p1.add(slot.player);
        }
        return p1;
    }

    private List<org.bukkit.entity.Player> getBukkitPlayers(Team team) {
        List<org.bukkit.entity.Player> p1 = new ArrayList<>();
        for (Slot slot : team.players) {
            if (slot.player == null) continue;
            p1.add(slot.player.getPlayerBind());
        }
        return p1;
    }

    private List<Player> getEnemyPlayers(Team team) {
        return getPlayers(getEnemyTeam(team));
    }

    private List<org.bukkit.entity.Player> getEnemyBukkitPlayers(Team team) {
        return getBukkitPlayers(getEnemyTeam(team));
    }

    public List<org.bukkit.entity.Player> getSavedBukkitPlayers() {
        return savedBukkitPlayers;
    }

    public List<Player> getSavedPlayers() {
        return savedPlayers;
    }

    public static Team getEnemyTeam(Team team) {
        return switch (team.teamID) {
            case 1 -> Overwatch.team2;
            case 2 -> Overwatch.team1;
            case 3 -> Overwatch.enemyNPC;
            case 4 -> Overwatch.allyNPC;
            default -> null;
        };
    }

    public enum SlotRole {
        DAMAGE,
        SUPPORT,
        TANK,
        UNKNOWN
    }

}
