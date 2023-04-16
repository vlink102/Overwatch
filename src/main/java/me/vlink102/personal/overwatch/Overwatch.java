package me.vlink102.personal.overwatch;

import me.neznamy.tab.api.TabAPI;
import me.vlink102.personal.overwatch.abilities.cooldowns.Cooldown;
import me.vlink102.personal.overwatch.heroes.properties.RegenerativeEntity;
import me.vlink102.personal.overwatch.minecraftlink.*;
import me.vlink102.personal.overwatch.placeholders.OverwatchPlaceholders;
import me.vlink102.personal.overwatch.placeholders.OverwatchRelationalPlaceholders;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.math.BigInteger;
import java.util.*;

public final class Overwatch extends JavaPlugin implements Listener, CommandExecutor {
    private static final String packURL = "https://download.mc-packs.net/pack/4877daaf80fa92d6b7652da2955ec409f11428e8.zip";
    private static final String SHA_1_HASH = "4877daaf80fa92d6b7652da2955ec409f11428e8";

    public static final HashMap<UUID, Player> players = new HashMap<>();
    public static Team team1;
    public static Team team2;
    public static Team enemyNPC;
    public static Team allyNPC;
    public static ActionbarManager actionbarManager;

    public static NamespacedKey abilityOwnerKey;
    public static NamespacedKey abilityTypeKey;

    public static Scoreboard scoreboard;

    private NPCRegistry registry;

    public NPCRegistry getRegistry() {
        return registry;
    }

    @Override
    public void onEnable() {
        System.out.println("Enabled Overwatch [V_Link]");
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        scoreboard = scoreboardManager.getNewScoreboard();
        registry = CitizensAPI.createNamedNPCRegistry("overwatch", new MemoryNPCDataStore());
        abilityOwnerKey = new NamespacedKey(this, "overwatch_ability_owner");
        abilityTypeKey = new NamespacedKey(this, "overwatch_ability_type");
        Bukkit.getServer().getPluginManager().registerEvents(new AbilityListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new HeadshotListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new HitscanListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new SoundListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ProjectileHitListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        actionbarManager = new ActionbarManager(this);
        Objects.requireNonNull(Bukkit.getServer().getPluginCommand("overwatch")).setExecutor(this);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, Cooldown::handleCooldowns, 1L, 1L);
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new OverwatchPlaceholders(this).register();
            new OverwatchRelationalPlaceholders(this).register();
        }
        team1 = new Team(1, false);
        team2 = new Team(2, false);
        allyNPC = new Team(3, true);
        enemyNPC = new Team(4, true);

        for (org.bukkit.entity.Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            registerPlayer(onlinePlayer, false);
        }

        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_1%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(1));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_2%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(2));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_3%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(3));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_4%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(4));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_5%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(5));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_6%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(6));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_7%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(7));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_8%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(8));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_9%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(9));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_10%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(10));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_11%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(11));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_12%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(12));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_13%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(13));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_scoreboard_14%", 300, player -> OverwatchPlaceholders.generateScoreboardTable(players.get(player.getUniqueId())).get(14));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_npc_0%", 300, player -> OverwatchPlaceholders.getNPCDisplay(this, 0));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_npc_1%", 300, player -> OverwatchPlaceholders.getNPCDisplay(this, 1));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_npc_2%", 300, player -> OverwatchPlaceholders.getNPCDisplay(this, 2));
        TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder("%ow_npc_3%", 300, player -> OverwatchPlaceholders.getNPCDisplay(this, 3));

        TabAPI.getInstance().getPlaceholderManager().registerRelationalPlaceholder("%rel_ow_name%", 1000, (viewer, target) -> {
            Player oViewer = players.get(viewer.getUniqueId());
            Player oTarget = players.get(target.getUniqueId());
            if (oViewer.isAlly(oTarget)) {
                return OverwatchRelationalPlaceholders.ally + target.getName();
            } else {
                return OverwatchRelationalPlaceholders.enemy + target.getName();
            }
        });
    }

    public byte[] decodeUsingBigInteger(String hexString) {
        byte[] byteArray = new BigInteger(hexString, 16)
                .toByteArray();
        if (byteArray[0] == 0) {
            byte[] output = new byte[byteArray.length - 1];
            System.arraycopy(
                    byteArray, 1, output,
                    0, output.length);
            return output;
        }
        return byteArray;
    }

    @EventHandler
    public void resourcePackHandler(PlayerResourcePackStatusEvent event) {
        Player overwatchPlayer = Overwatch.players.get(event.getPlayer().getUniqueId());
        switch (event.getStatus()) {
            case ACCEPTED, SUCCESSFULLY_LOADED -> overwatchPlayer.setHasAcceptedResourcePack(true);
            case DECLINED, FAILED_DOWNLOAD -> overwatchPlayer.setHasAcceptedResourcePack(false);
        }
    }

    public void registerPlayer(org.bukkit.entity.Player player, boolean npc) {
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(10);
        if (!npc) {
            player.setResourcePack(packURL, decodeUsingBigInteger(SHA_1_HASH));
        }
        if (!players.containsKey(player.getUniqueId())) {
            Player overwatchPlayer = new Player(player, this, npc);
            players.put(player.getUniqueId(), overwatchPlayer);

            if (!npc) {
                new HotbarManager(this, overwatchPlayer);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) {
                            overwatchPlayer.getTeam().removePlayer(overwatchPlayer);
                            players.remove(player.getUniqueId());
                            cancel();
                            return;
                        }
                        actionbarManager.updateActionBar(player);

                    }
                }.runTaskTimer(this, 5L, 5L);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() && !npc) {
                        cancel();
                        return;
                    }
                    if (overwatchPlayer.getHero() instanceof RegenerativeEntity regenerativeEntity) {
                        long msSinceDmg = overwatchPlayer.getMSSinceLastDamaged();
                        long regenDelay = regenerativeEntity.getRegenerationDelay().getMs();
                        if (msSinceDmg >= regenDelay) {
                            switch (regenerativeEntity.regenType()) {
                                case HEALTH -> overwatchPlayer.heal(null, regenerativeEntity.getRegenerationRate() / 20f);
                                case SHIELDS -> overwatchPlayer.healShields(regenerativeEntity.getRegenerationRate());
                            }
                        }
                    }
                    List<Player> bufferRemove = new ArrayList<>();
                    overwatchPlayer.getRecentAffectors().forEach((player1, map) -> {
                        if (System.currentTimeMillis() >= map.getTimestamp() && map.getTimestamp() != -1L) {
                            bufferRemove.add(player1);
                        }
                    });
                    for (Player player1 : bufferRemove) {
                        overwatchPlayer.getRecentAffectors().remove(player1);
                    }
                    List<Player.Debuff> toRemove = new ArrayList<>();
                    for (Player.Debuff debuff : overwatchPlayer.getIsAntied()) {
                        if (System.currentTimeMillis() > debuff.expires().getMs()) {
                            toRemove.add(debuff);
                        }
                    }
                    for (Player.Debuff debuff : toRemove) {
                        overwatchPlayer.getIsAntied().remove(debuff);
                    }

                    List<Player.ValuedDebuff> toRemoveHealingBoost = new ArrayList<>();
                    for (Player.ValuedDebuff valuedDebuff : overwatchPlayer.getHealingBoostPercentage()) {
                        if (System.currentTimeMillis() > valuedDebuff.expires().getMs()) {
                            toRemoveHealingBoost.add(valuedDebuff);
                        }
                    }
                    for (Player.ValuedDebuff valuedDebuff : toRemoveHealingBoost) {
                        overwatchPlayer.getHealingBoostPercentage().remove(valuedDebuff);
                    }

                    List<Player.ValuedDebuff> toReduce = new ArrayList<>();
                    for (Player.ValuedDebuff valuedDebuff : overwatchPlayer.getDamageReduction()) {
                        if (System.currentTimeMillis() > valuedDebuff.expires().getMs()) {
                            toReduce.add(valuedDebuff);
                        }
                    }
                    for (Player.ValuedDebuff valuedDebuff : toReduce) {
                        overwatchPlayer.getDamageReduction().remove(valuedDebuff);
                    }

                    List<Player.ValuedDebuff> toRemoveDamageAmp = new ArrayList<>();
                    for (Player.ValuedDebuff valuedDebuff : overwatchPlayer.getDamageDealtAmplification()) {
                        if (System.currentTimeMillis() > valuedDebuff.expires().getMs()) {
                            toRemoveDamageAmp.add(valuedDebuff);
                        }
                    }
                    for (Player.ValuedDebuff valuedDebuff : toRemoveDamageAmp) {
                        overwatchPlayer.getDamageDealtAmplification().remove(valuedDebuff);
                    }

                    List<Player.ValuedDebuff> toRemoveAmplification = new ArrayList<>();
                    for (Player.ValuedDebuff valuedDebuff : overwatchPlayer.getDamageTakenAmplification()) {
                        if (System.currentTimeMillis() > valuedDebuff.expires().getMs()) {
                            toRemoveAmplification.add(valuedDebuff);
                        }
                    }
                    for (Player.ValuedDebuff valuedDebuff : toRemoveAmplification) {
                        overwatchPlayer.getDamageTakenAmplification().remove(valuedDebuff);
                    }
                }
            }.runTaskTimer(this, 5L, 1L);
        } else {
            throw new RuntimeException("Player is already registered: " + player.getName());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        registerPlayer(event.getPlayer(), false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) sender;
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("skillissue")) {
                    if (args.length > 1) {
                        for (org.bukkit.entity.Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (onlinePlayer.getName().equalsIgnoreCase(args[1])) {
                                onlinePlayer.showDemoScreen();
                            }
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("test")) {
                    bukkitPlayer.sendMessage("Applying zoom phase 1:");
                    bukkitPlayer.setWalkSpeed(-1);
                    bukkitPlayer.sendMessage("Applying zoom phase 3:");
                    bukkitPlayer.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(-0.25f);
                    if (args.length > 1) {
                        PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, PotionEffect.INFINITE_DURATION, Integer.parseInt(args[1]), true, false, false);
                        effect.apply(bukkitPlayer);
                    }
                }
                if (args[0].equalsIgnoreCase("save")) {
                    bukkitPlayer.removePotionEffect(PotionEffectType.SLOW);
                    bukkitPlayer.setWalkSpeed(0.2f);
                    bukkitPlayer.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    bukkitPlayer.setHealth(20);
                    bukkitPlayer.setFreezeTicks(0);
                    bukkitPlayer.setFoodLevel(20);
                }
                if (args[0].equalsIgnoreCase("damage")) {
                    if (args.length > 1) {
                        for (Player registeredPlayer : players.values()) {
                            if (registeredPlayer.getPlayerBind().getName().equalsIgnoreCase(args[1])) {
                                if (args.length > 2) {
                                    int damage = Integer.parseInt(args[2]);
                                    Player player = players.get(bukkitPlayer.getUniqueId());
                                    Player victim = players.get(registeredPlayer.getPlayerBind().getUniqueId());
                                    victim.damage(player, damage);
                                    sender.sendMessage("§aDamaged §6" + registeredPlayer.getPlayerBind().getName() + "§a for §b" + damage + "§a raw damage");
                                    registeredPlayer.getPlayerBind().sendMessage("§cYou were damaged with §b" + damage + "§c raw damage by §6" + bukkitPlayer.getName());
                                }
                            }
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("heal")) {
                    if (args.length > 1) {
                        for (Player registeredPlayer : players.values()) {
                            if (registeredPlayer.getPlayerBind().getName().equalsIgnoreCase(args[1])) {
                                if (args.length > 2) {
                                    int healing = Integer.parseInt(args[2]);
                                    Player healer = players.get(bukkitPlayer.getUniqueId());
                                    Player healed = players.get(registeredPlayer.getPlayerBind().getUniqueId());
                                    healed.heal(healer, healing);
                                    sender.sendMessage("§aHealed §6" + registeredPlayer.getPlayerBind().getName() + "§a for §b" + healing + "§a raw healing");
                                    registeredPlayer.getPlayerBind().sendMessage("§aYou were healed for §b" + healing + "§a raw healing by §6" + bukkitPlayer.getName());
                                }
                            }
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("cleanse")) {
                    if (args.length > 1) {
                        for (Player registeredPlayer : players.values()) {
                            if (registeredPlayer.getPlayerBind().getName().equalsIgnoreCase(args[1])) {
                                Player cleanser = players.get(bukkitPlayer.getUniqueId());
                                Player cleansed = players.get(registeredPlayer.getPlayerBind().getUniqueId());
                                cleansed.cleanse(cleanser);
                                sender.sendMessage("§aCleansed §6" + registeredPlayer.getPlayerBind().getName() + "§a!");
                                registeredPlayer.getPlayerBind().sendMessage("§aYou were cleansed by §6" + bukkitPlayer.getName() + "§a!");
                            }
                        }
                    } else {
                        Player player = players.get(bukkitPlayer.getUniqueId());
                        player.cleanse(player);
                        sender.sendMessage("§aYou cleansed yourself.");
                    }
                }
                if (args[0].equalsIgnoreCase("ignite")) {
                    if (args.length > 1) {
                        for (Player registeredPlayer : players.values()) {
                            if (registeredPlayer.getPlayerBind().getName().equalsIgnoreCase(args[1])) {
                                Player ignited = players.get(registeredPlayer.getPlayerBind().getUniqueId());
                                ignited.setOnFire(true);
                                sender.sendMessage("§aIgnited §6" + registeredPlayer.getPlayerBind().getName() + "§a!");
                                registeredPlayer.getPlayerBind().sendMessage("§cYou were set on fire by §6" + bukkitPlayer.getName() + "§c!");
                            }
                        }
                    } else {
                        Player player = players.get(bukkitPlayer.getUniqueId());
                        player.setOnFire(true);
                        sender.sendMessage("§aYou set yourself on fire.");
                    }
                }
                if (args[0].equalsIgnoreCase("setstat")) {
                    if (args.length > 1) {
                        for (Player registeredPlayer : players.values()) {
                            if (registeredPlayer.getPlayerBind().getName().equalsIgnoreCase(args[1])) {
                                if (args.length > 3) {
                                    Player changed = players.get(registeredPlayer.getPlayerBind().getUniqueId());

                                    switch (args[2].toUpperCase()) {
                                        case "K","KILLS","E","ELIMS","ELIMINATIONS","KILL","ELIM" -> changed.setEliminations((int) Float.parseFloat(args[3]));
                                        case "A","ASSISTS","ASSIST" -> changed.setAssists((int) Float.parseFloat(args[3]));
                                        case "D","DEATH","DEATHS","DIE","DIED","DEAD" -> changed.setDeaths((int) Float.parseFloat(args[3]));
                                        case "DMG","DAMAGE","DAMAGE_DEALT","DAMAGE-DEALT","DMGDEALT","DD" -> changed.setDamageDealt(Float.parseFloat(args[3]));
                                        case "H","HEALING_DONE","HEALING-DONE","HEALS","HEALED","HEALING","HEAL" -> changed.setHealingDone(Float.parseFloat(args[3]));
                                        case "MIT","MITIGATED","DAMAGE_MIT","DAMAGE-MIT","DAMAGE_MITIGATED","DAMAGE-MITIGATED","DMG_MITIGATED","DMG-MITIGATED" -> changed.setDamageMitigated(Float.parseFloat(args[3]));
                                        case "MODIFIER","MOD" -> {
                                            switch (args[3].toUpperCase()) {
                                                case "ANTI","ANTIED" -> {
                                                    if (args.length > 4) {
                                                        changed.setAntied(null, TimeUnit.Converter.SECOND.of(Float.parseFloat(args[4])));
                                                    }
                                                }
                                                case "OVERHEALTH","OVERHP","OH","OVER" -> {
                                                    if (args.length > 5) {
                                                        changed.setOverhealth(Float.parseFloat(args[4]));
                                                        new BukkitRunnable() {
                                                            @Override
                                                            public void run() {
                                                                changed.setOverhealth(0);
                                                            }
                                                        }.runTaskLater(this, Long.parseLong(args[5]) * 20L);
                                                    }
                                                }
                                                case "ARMOR","ARMOUR" -> {
                                                    if (args.length > 4) {
                                                        changed.setArmor(Float.parseFloat(args[4]));
                                                    }
                                                }
                                                case "AMMO","ROUNDS","MAG","MAGAZINE","BULLETS" -> {
                                                    if (args.length > 4) {
                                                        if (args.length > 5) {
                                                            if (Boolean.parseBoolean(args[5])) {
                                                                changed.setOverAmmo(Integer.parseInt(args[4]));
                                                            }
                                                        } else {
                                                            changed.setAmmo(Integer.parseInt(args[4]));
                                                        }
                                                    }
                                                }
                                                case "HEALTH","HP" -> {
                                                    if (args.length > 4) {
                                                        changed.setHealth(Float.parseFloat(args[4]));
                                                    }
                                                }
                                                case "SHIELDS","SHIELD" -> {
                                                    if (args.length > 4) {
                                                        changed.setShields(Float.parseFloat(args[4]));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("create-npc")) {
                    if (enemyNPC.isFull() && allyNPC.isFull()) {
                        sender.sendMessage("§cCould not create NPC: Both teams are full");
                        return true;
                    }
                    String name = "vlonk";
                    if (args.length > 1) {
                        name = args[1];
                    }
                    NPC npc = registry.createNPC(EntityType.PLAYER, name);
                    npc.data().setPersistent(NPC.Metadata.DEFAULT_PROTECTED, false);
                    npc.data().setPersistent(NPC.Metadata.COLLIDABLE, true);
                    npc.data().setPersistent(NPC.Metadata.DAMAGE_OTHERS, true);
                    npc.data().setPersistent(NPC.Metadata.FLUID_PUSHABLE, true);
                    npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, false);
                    npc.data().setPersistent(NPC.Metadata.RESPAWN_DELAY, 1);
                    npc.data().setPersistent(NPC.Metadata.REMOVE_FROM_PLAYERLIST, false);
                    npc.spawn(((org.bukkit.entity.Player) sender).getLocation(), SpawnReason.CREATE);

                    if (npc.getEntity() instanceof org.bukkit.entity.Player player) {
                        registerPlayer(player, true);
                        sender.sendMessage("§aSuccessfully spawned npc with name: §6" + name + "§8 (" + npc.getId() + ")");
                        npc.getOrAddTrait(HologramTrait.class).addLine("%overwatch_npc-display_" + npc.getId() + "%");
                        npc.getOrAddTrait(HologramTrait.class).addLine("%overwatch_" + npc.getId() + "_display_total_40%");
                        npc.getOrAddTrait(HologramTrait.class).setLineHeight(0.25);
                    } else {
                        sender.sendMessage("§cError: Could not register npc player");
                    }
                }
                if (args[0].equalsIgnoreCase("remove-all")) {
                    registry.forEach(npc -> {
                        Player player = Overwatch.players.get(npc.getUniqueId());
                        player.getTeam().removePlayer(player);
                        players.remove(npc.getUniqueId());
                        sender.sendMessage("§aSuccessfully removed npc: §6" + npc.getName() + "§7(" + npc.getUniqueId() + "§7)");
                    });
                    registry.despawnNPCs(DespawnReason.REMOVAL);
                    registry.deregisterAll();
                }
                if (args[0].equalsIgnoreCase("getnpchotbar")) {
                    org.bukkit.entity.Player player = (org.bukkit.entity.Player) registry.getById(0).getEntity();
                    sender.sendMessage(ActionbarManager.createHealthString(100, Overwatch.players.get(player.getUniqueId()), false));
                }
                if (args[0].equalsIgnoreCase("switchteam")) {
                    if (args.length > 1) {
                        for (Player registeredPlayer : players.values()) {
                            if (registeredPlayer.getPlayerBind().getName().equalsIgnoreCase(args[1])) {
                                Player player = players.get(registeredPlayer.getPlayerBind().getUniqueId());
                                Team prev = player.getTeam();
                                Team to = player.switchTeam();
                                if (prev.equals(to)) {
                                    sender.sendMessage("§cCould not switch teams: Team §b" + to.getTeamID() + "§c is full.");
                                    return true;
                                }
                                sender.sendMessage("§aSuccessfully switched §6" + registeredPlayer.getPlayerBind().getName() + "'s §ateam to: §b" + to.getTeamID());
                                registeredPlayer.getPlayerBind().sendMessage("§aYour team was switched to §b" + to.getTeamID() + "§a by §6" + sender.getName());
                                break;
                            }
                        }
                    } else {
                        Player overwatchPlayer = players.get(bukkitPlayer.getUniqueId());
                        Team team = overwatchPlayer.switchTeam();
                        sender.sendMessage("§aSuccessfully switched your team to: §b" + team.getTeamID());
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onDisable() {
        registry.despawnNPCs(DespawnReason.REMOVAL);
        registry.deregisterAll();
    }
}
