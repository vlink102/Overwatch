package me.vlink102.personal.overwatch.minecraftlink;

import me.vlink102.personal.overwatch.EntityStats;
import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.Team;
import me.vlink102.personal.overwatch.abilities.Ability;
import me.vlink102.personal.overwatch.abilities.types.DualWeaponPrimary;
import me.vlink102.personal.overwatch.abilities.types.Reloadable;
import me.vlink102.personal.overwatch.heroes.Hero;
import me.vlink102.personal.overwatch.heroes.ana.Ana;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player {
    private final Overwatch overwatch;

    private boolean hasAcceptedResourcePack;
    private boolean isRecovering;
    private boolean isCasting;

    private boolean isScoped;

    public boolean isScoped() {
        return isScoped;
    }

    public void setScoped(boolean scoped) {
        isScoped = scoped;
    }

    public boolean isRecovering() {
        return isRecovering;
    }

    public boolean isCasting() {
        return isCasting;
    }

    public void setRecovering(boolean recovering) {
        isRecovering = recovering;
    }

    public void setCasting(boolean casting) {
        isCasting = casting;
    }

    public enum NPCStatus {
        ALLY_ALL,
        ENEMY_ALL
    }

    private NPCStatus status;

    public NPCStatus getStatus() {
        return status;
    }

    private Team team;
    private final org.bukkit.entity.Player playerBind;
    private Hero hero;
    private float health;
    private float shields;
    private float armor;
    private float overhealth;

    public boolean isAlly(Player player) {
        if (overwatch.getRegistry().isNPC(player.getPlayerBind())) {
            if (overwatch.getRegistry().isNPC(playerBind)) {
                return status == player.getStatus();
            } else {
                return player.getStatus() == NPCStatus.ALLY_ALL;
            }
        } else {
            if (overwatch.getRegistry().isNPC(playerBind)) {
                return status == NPCStatus.ALLY_ALL;
            } else {
                return player.getTeam().getTeamID() == team.getTeamID();
            }
        }
    }

    private final HashMap<Player, Recent> recentAffectors;

    private long lastDamaged = 0;

    public long getLastDamaged() {
        return lastDamaged;
    }

    public long getMSSinceLastDamaged() {
        return System.currentTimeMillis() - lastDamaged;
    }

    public void setLastDamaged(long lastDamaged) {
        this.lastDamaged = lastDamaged;
    }

    public record Debuff(TimeUnit expires, Player bind) {}
    public record ValuedDebuff(TimeUnit expires, Player bind, float value) {}

    private final List<Debuff> isAntied;
    private boolean onFire;

    public boolean isOnFire() {
        return onFire;
    }

    public boolean isOverallAntied() {
        return !isAntied.isEmpty();
    }

    public List<Debuff> getIsAntied() {
        return isAntied;
    }

    public void setAntied(Player anti, TimeUnit duration) {
        isAntied.add(new Debuff(duration.addSysTime(), anti));
        if (anti != null) {
            addDebuffer(anti);
        }
    }

    private HashMap<Ability, Integer> ammoMap;

    public HashMap<Ability, Integer> getAmmoMap() {
        return ammoMap;
    }

    public void useRound() {
        if (selected instanceof Reloadable reloadable) {
            ammoMap.put((Ability) reloadable, ammoMap.get(reloadable) - 1);
        }
    }

    public void reload() {
        if (selected instanceof Reloadable ammoAbility) {
            if (ammoMap.get(selected) < ammoAbility.magazineSize()) {
                playerBind.getInventory().setHeldItemSlot(7);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        //TODO interrupt reloading
                        ammoMap.put(selected, ammoAbility.magazineSize());
                        if (playerBind.getInventory().getHeldItemSlot() == 7) {
                            resetHeldItemSlot();
                        }
                    }
                }.runTaskLater(overwatch, ammoAbility.reloadTime().getTicks());
            }
        }
        resetHeldItemSlot();
    }

    public void resetHeldItemSlot() {
        playerBind.getInventory().setHeldItemSlot(selected instanceof DualWeaponPrimary ? 1 : 8);
    }

    private final List<ValuedDebuff> damageDealtAmplification; // This player's damage boost (e.g. cassidy nano 50%)

    private final List<ValuedDebuff> damageReduction; // This player's damage resistance (e.g. orisa fortify 40%) MINIMUM 50%
    private final List<ValuedDebuff> damageTakenAmplification; // This player's damage taken amplification (e.g. zenyatta orb 25%)
    private final List<ValuedDebuff> healingBoostPercentage; // This player's healing boost (e.g. ana grenade 50%)

    public List<ValuedDebuff> getDamageTakenAmplification() {
        return damageTakenAmplification;
    }

    public float getOverallDamageReduction() {
        float total = 0;
        for (ValuedDebuff debuff : damageReduction) {
            total += debuff.value;
        }
        return total;
    }

    public float getOverallDamageTakenAmplification() {
        float total = 0;
        for (ValuedDebuff valuedDebuff : damageTakenAmplification) {
            total += valuedDebuff.value;
        }
        return total;
    }

    public float getOverallDamageDealtAmplification() {
        float total = 0;
        for (ValuedDebuff valuedDebuff : damageDealtAmplification) {
            total += valuedDebuff.value;
        }
        return total;
    }

    public float getOverallHealingBoostPercentage() {
        float total = 0;
        for (ValuedDebuff valuedDebuff : healingBoostPercentage) {
            total += valuedDebuff.value;
        }
        return total;
    }

    public class ModifierSource {
        private final Player player;
        private final float modifier;
        private final long duration;

        public ModifierSource(Player player, float modifier, long duration) {
            this.player = player;
            this.modifier = modifier;
            this.duration = duration;
        }

        public float getModifier() {
            return modifier;
        }

        public Player getPlayer() {
            return player;
        }

        public long getDuration() {
            return duration;
        }
    }

    private float healingDone;
    private float damageDealt;
    private float damageMitigated;
    private int eliminations;
    private int deaths;
    private int assists;

    private Ability selected; // for duel wielders

    public void addHealingDone(float healing) {
        healingDone += healing;
    }

    private void addDamageDealt(float damage) {
        damageDealt += damage;
    }

    private void addDamageMitigated(float damage) {
        damageMitigated += damage;
    }

    public void incrementEliminations() {
        eliminations ++;
    }

    public void incrementDeaths() {
        deaths ++;
    }

    public void incrementAssists() {
        assists ++;
    }

    public Team getTeam() {
        return team;
    }

    public Team switchTeam() {
        if (team == null) return team;
        Team newTeam;
        if (status != null) {
            newTeam = (this.team.getTeamID() == 3 ? Overwatch.enemyNPC : Overwatch.allyNPC);
            if (newTeam.isFull()) {
                return team;
            }
            team.removePlayer(this);
            this.team = newTeam;
            newTeam.addPlayer(this);
            this.status = this.status == NPCStatus.ALLY_ALL ? NPCStatus.ENEMY_ALL : NPCStatus.ALLY_ALL;
        } else {
            newTeam = (this.team.getTeamID() == 1 ? Overwatch.team2 : Overwatch.team1);
            if (newTeam.isFull()) {
                return team;
            }
            team.removePlayer(this);
            this.team = newTeam;
            newTeam.addPlayer(this);
        }
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void filterIntoTeam(boolean npc) {
        if (npc) {
            if (Overwatch.allyNPC.isFull()) {
                Overwatch.enemyNPC.addPlayer(this);
                this.status = NPCStatus.ENEMY_ALL;
            } else {
                Overwatch.allyNPC.addPlayer(this);
                this.status = NPCStatus.ALLY_ALL;
            }
        } else {
            if (Overwatch.team1.isFull()) {
                Overwatch.team2.addPlayer(this);
            } else {
                Overwatch.team1.addPlayer(this);
            }
            this.status = null;
        }
    }

    public Player(org.bukkit.entity.Player player, Overwatch overwatch, boolean npc) {
        this.hasAcceptedResourcePack = false;
        this.overwatch = overwatch;
        this.playerBind = player;
        this.isRecovering = false;
        this.isCasting = false;
        this.isScoped = false;
        this.hero = new Ana(overwatch);
        this.ammoMap = new HashMap<>();
        for (Ability ability : hero.abilities().getAbilities()) {
            ammoMap.put(ability, ability instanceof Reloadable reloadable ? reloadable.magazineSize() : null);
        }
        EntityStats stats = hero.entityStats();
        this.health = stats.getHealth();
        this.armor = stats.getArmor() == null ? 0 : stats.getArmor();
        this.shields = stats.getShields() == null ? 0 : stats.getShields();
        this.overhealth = 0;
        this.damageDealtAmplification = new ArrayList<>();
        this.damageReduction = new ArrayList<>();
        this.healingBoostPercentage = new ArrayList<>();
        this.damageTakenAmplification = new ArrayList<>();
        this.healingDone = 0;
        this.damageDealt = 0;
        this.damageMitigated = 0;
        this.eliminations = 0;
        this.deaths = 0;
        this.assists = 0;
        this.isAntied = new ArrayList<>();
        this.selected = hero.abilities().primaryAbility();
        this.recentAffectors = new HashMap<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                filterIntoTeam(npc);
            }
        }.runTaskLater(overwatch, 1);
    }

    public org.bukkit.entity.Player getPlayerBind() {
        return playerBind;
    }

    public float getHealth() {
        return health;
    }

    public float getTotalHealth() {
        return health + armor + shields + overhealth;
    }

    public float getTotalEhp() {
        return ((health + getArmorEhp() + shields + overhealth) / (1 - getOverallDamageReduction() + getOverallDamageTakenAmplification()));
    }

    public float getOverhealth() {
        return overhealth;
    }

    public List<ValuedDebuff> getDamageDealtAmplification() {
        return damageDealtAmplification;
    }

    public List<ValuedDebuff> getDamageReduction() {
        return damageReduction;
    }

    public List<ValuedDebuff> getHealingBoostPercentage() {
        return healingBoostPercentage;
    }

    public void addDamageDealtAmplification(Player buffer, TimeUnit duration, float damageDealtAmplification) {
        this.damageDealtAmplification.add(new ValuedDebuff(duration.addSysTime(), buffer, damageDealtAmplification));
        if (buffer != null) {
            addBuffer(buffer);
        }
    }

    public void addDamageReduction(Player reducer, TimeUnit duration, float damageReduction) {
        this.damageReduction.add(new ValuedDebuff(duration.addSysTime(), reducer, damageReduction));
        if (reducer != null) {
            addDebuffer(reducer);
        }
    }

    public void addDamageTakenAmplification(Player debuffer, TimeUnit duration, float damageTakenAmplification) {
        this.damageTakenAmplification.add(new ValuedDebuff(duration.addSysTime(), debuffer, damageTakenAmplification));
        if (debuffer != null) {
            addDebuffer(debuffer);
        }
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public Hero getHero() {
        return hero;
    }

    public void setOverhealth(float overhealth) {
        this.overhealth = overhealth;
    }

    public void damage(Player inflictor, float damage) {
        float initialDamage = damage;
        lastDamaged = System.currentTimeMillis();
        damage *= (1 + inflictor.getOverallDamageDealtAmplification());
        damage *= (1 - getOverallDamageReduction() + getOverallDamageTakenAmplification());
        float damageDealt = 0;
        float unmodifiedDamageDealt = 0;
        float remainingDamage = damage;
        float unmodifiedRemainingDamage = initialDamage;
        if (remainingDamage >= overhealth) {
            remainingDamage -= overhealth;
            unmodifiedRemainingDamage -= overhealth;
            damageDealt += overhealth;
            unmodifiedDamageDealt += overhealth;
            overhealth = 0;

            if (remainingDamage >= shields) {
                remainingDamage -= shields;
                unmodifiedRemainingDamage -= shields;
                damageDealt += shields;
                unmodifiedDamageDealt += shields;
                shields = 0;

                if (remainingDamage >= armor) {
                    float temp = remainingDamage;
                    remainingDamage -= ((armor / 100) * 43) - armor;
                    unmodifiedRemainingDamage -= getArmorEhp(armor);
                    unmodifiedDamageDealt += getArmorEhp(armor);
                    armor = 0;
                    damageDealt += (temp - remainingDamage);

                    if (remainingDamage >= health) {
                        remainingDamage -= health;
                        unmodifiedRemainingDamage -= health;
                        damageDealt += health;
                        unmodifiedDamageDealt += health;
                        health = 0;
                    } else {
                        health -= remainingDamage;
                        damageDealt += remainingDamage;
                        unmodifiedDamageDealt += remainingDamage;
                    }
                } else {
                    remainingDamage *= 0.7;
                    armor -= remainingDamage;
                    damageDealt += remainingDamage;
                    unmodifiedDamageDealt += unmodifiedRemainingDamage;
                }
            } else {
                shields -= remainingDamage;
                damageDealt += remainingDamage;
                unmodifiedDamageDealt += unmodifiedRemainingDamage;
            }
        } else {
            overhealth -= remainingDamage;
            damageDealt += remainingDamage;
            unmodifiedDamageDealt += unmodifiedRemainingDamage;
        }

        addDamageMitigated(unmodifiedDamageDealt - damageDealt);
        inflictor.addDamageDealt(damageDealt);

        if (health == 0) {
            incrementDeaths();
            recentAffectors.remove(inflictor);
            inflictor.incrementEliminations();
            for (Player recentAffector : recentAffectors.keySet()) {
                switch (recentAffectors.get(recentAffector).getRecentAssistType()) {
                    case DAMAGED_PLAYER -> recentAffector.incrementEliminations();
                    case DEBUFFED_PLAYER -> recentAffector.incrementAssists();
                }
            }
            recentAffectors.clear();
            for (Player player : inflictor.recentAffectors.keySet()) {
                if (inflictor.recentAffectors.get(player).getRecentAssistType() == RecentAssistType.BUFFED_PLAYER) {
                    player.incrementAssists();
                }
            }
        } else {
            addDamager(inflictor);
        }

        Overwatch.actionbarManager.updateActionBar(playerBind);
    }

    public void addBuffer(Player player) {
        if (!player.equals(this)) {
            recentAffectors.put(player, new Recent(RecentAssistType.BUFFED_PLAYER, System.currentTimeMillis() + 5000L));
        }
    }

    public void addDebuffer(Player player) {
        if (!player.equals(this)) {
            if (recentAffectors.containsKey(player)) {
                if (!recentAffectors.get(player).getRecentAssistType().equals(RecentAssistType.DAMAGED_PLAYER)) {
                    recentAffectors.put(player, new Recent(RecentAssistType.DEBUFFED_PLAYER, System.currentTimeMillis() + 3000L));
                }
            }
        }
    }

    public void addDamager(Player player) {
        if (!player.equals(this)) {
            recentAffectors.put(player, new Recent(RecentAssistType.DAMAGED_PLAYER, -1L));
            for (Player player1 : recentAffectors.keySet()) {
                if (recentAffectors.get(player1).getRecentAssistType().equals(RecentAssistType.DEBUFFED_PLAYER)) {
                    recentAffectors.put(player1, new Recent(RecentAssistType.DEBUFFED_PLAYER, System.currentTimeMillis() + 5000L));
                }
            }
        }
    }

    public void cleanse(Player cleanser) {
        isAntied.clear();
        onFire = false;
        damageReduction.clear();
        damageTakenAmplification.clear();
        addBuffer(cleanser);
    }


    public void healShields(float shields) {
        if (!isOverallAntied()) {
            this.shields += shields * (1 + getOverallHealingBoostPercentage());
            this.shields = Math.min(hero.entityStats().getShields(), shields);
        }
    }

    public void heal(Player healer, float healing) {
        if (!isOverallAntied()) {
            healing *= (1 + getOverallHealingBoostPercentage());
            float healingDone = 0;
            float newHealing = healing;
            EntityStats stats = hero.entityStats();
            float healthToHeal = stats.getHealth() - health;
            if (newHealing >= healthToHeal) {
                newHealing -= healthToHeal;
                healingDone += healthToHeal;
                health = stats.getHealth();
                float armorToHeal = stats.getArmor() - armor;
                if (newHealing >= armorToHeal) {
                    newHealing -= armorToHeal;
                    healingDone += armorToHeal;
                    armor = stats.getArmor();
                    float shieldsToHeal = stats.getShields() - shields;
                    if (newHealing >= shieldsToHeal) {
                        shields = stats.getShields();
                    } else {
                        shields += newHealing;
                    }

                } else {
                    armor += newHealing;
                    healingDone += newHealing;
                }

            } else {
                health += newHealing;
                healingDone += newHealing;
            }

            if (healer != null) healer.addHealingDone(healingDone);
        }
    }

    /**
     * @param percentage A value greater than 0
     */
    public void boostHealing(Player booster, TimeUnit duration, float percentage) {
        if (!isOverallAntied()) {
            healingBoostPercentage.add(new ValuedDebuff(duration.addSysTime(), booster, percentage));
            addBuffer(booster);
        }
    }

    public float getShields() {
        return shields;
    }

    public float getArmor() {
        return armor;
    }

    public float getArmorEhp() {
        return armor / 0.7f;
    }
    public float getArmorEhp(float armor) {
        return armor / 0.7f;
    }

    public void setSelected(Ability selected) {
        this.selected = selected;
    }

    public Ability getSelected() {
        return selected;
    }

    public int getEliminations() {
        return eliminations;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAssists() {
        return assists;
    }

    public float getDamageDealt() {
        return damageDealt;
    }

    public float getDamageMitigated() {
        return damageMitigated;
    }

    public float getHealingDone() {
        return healingDone;
    }

    public void setEliminations(int eliminations) {
        this.eliminations = eliminations;
    }

    public void setAmmo(int ammo) {
        if (selected instanceof Reloadable reloadable) {
            ammoMap.put((Ability) reloadable, Math.min(reloadable.magazineSize(), ammo));
        }
    }

    public void setOverAmmo(int ammo) {
        if (selected instanceof Reloadable reloadable) {
            ammoMap.put((Ability) reloadable, ammo);
        }
    }

    public void setArmor(float armor) {
        this.armor = armor;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public void setDamageDealt(float damageDealt) {
        this.damageDealt = damageDealt;
    }

    public void setDamageMitigated(float damageMitigated) {
        this.damageMitigated = damageMitigated;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setHealingDone(float healingDone) {
        this.healingDone = healingDone;
    }

    public void setShields(float shields) {
        this.shields = shields;
    }

    public void decreaseShields(float shields) {
        this.shields -= shields;
        this.shields = Math.max(this.shields, 0);
    }

    public void addShields(float shields) {
        if (!isOverallAntied()) {
            this.shields += shields * (1 + getOverallHealingBoostPercentage());
            this.shields = Math.min(this.shields, hero.entityStats().getShields());
        }
    }

    public void setOnFire(boolean onFire) {
        this.onFire = onFire;
    }

    public HashMap<Player, Recent> getRecentAffectors() {
        return recentAffectors;
    }

    public enum RecentAssistType {
        DAMAGED_PLAYER,
        BUFFED_PLAYER,
        DEBUFFED_PLAYER
    }

    public static class Recent {
        private RecentAssistType recentAssistType;
        private Long timestamp;

        public Recent(RecentAssistType recentAssistType, Long timestamp) {
            this.recentAssistType = recentAssistType;
            this.timestamp = timestamp;
        }

        public RecentAssistType getRecentAssistType() {
            return recentAssistType;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setRecentAssistType(RecentAssistType recentAssistType) {
            this.recentAssistType = recentAssistType;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public boolean hasAcceptedResourcePack() {
        return hasAcceptedResourcePack;
    }

    public void setHasAcceptedResourcePack(boolean hasAcceptedResourcePack) {
        this.hasAcceptedResourcePack = hasAcceptedResourcePack;
    }
}
