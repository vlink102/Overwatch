package me.vlink102.personal.overwatch.abilities.ana;

import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.abilities.AbilityEnum;
import me.vlink102.personal.overwatch.abilities.AbilityLoadout;
import me.vlink102.personal.overwatch.abilities.types.*;
import me.vlink102.personal.overwatch.heroes.CustomIconManager;
import me.vlink102.personal.overwatch.minecraftlink.DefaultFontInfo;
import me.vlink102.personal.overwatch.minecraftlink.Player;
import me.vlink102.personal.overwatch.minecraftlink.Projectile;
import me.vlink102.personal.overwatch.minecraftlink.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BioticGrenade extends CooldownAbility implements ArcedProjectileAbility, AOEAbility, TimeLimited, ItemAbility {
    private final Overwatch overwatch;
    public static final Color yellow = Color.fromBGR(29, 233, 254);
    public static final ChatColor yellowChat = net.md_5.bungee.api.ChatColor.of("#FEF41D");
    public static final Color purple = Color.fromBGR(228, 14, 197);
    public static final ChatColor purpleChat = ChatColor.of("#C50EE4");

    private static final float ALLY_HEALING = 100;
    private static final float ALLY_HEAL_BOOST = 0.5f;
    private static final float ENEMY_DAMAGE = 60;

    public BioticGrenade(Overwatch overwatch) {
        this.overwatch = overwatch;
    }

    @Override
    public String abilityName() {
        return "Biotic Grenade";
    }

    @Override
    public void useAbility(Player player, Vector dir) {
        if (!isOnCooldown(player) && (!player.isCasting() && !player.isRecovering() && !player.isScoped())) {
            player.setCasting(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setCasting(false);
                    putOnCooldown(player);
                    org.bukkit.entity.Player bukkitPlayer = player.getPlayerBind();
                    ThrownPotion projectile = bukkitPlayer.launchProjectile(ThrownPotion.class, dir.normalize().multiply(projectileSpeed() / 20));
                    ItemStack stack = new ItemStack(Material.LINGERING_POTION);
                    ((PotionMeta) Objects.requireNonNull(stack.getItemMeta())).setColor(yellow);
                    projectile.setItem(stack);
                    new Projectile(player, dir, projectile, BioticGrenade.this);
                    player.setRecovering(true);
                    player.getPlayerBind().getInventory().setHeldItemSlot(7);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.setRecovering(false);
                            player.resetHeldItemSlot();
                        }
                    }.runTaskLater(overwatch, recoveryTime().getTicks());
                }
            }.runTaskLater(overwatch, castingTime().getTicks());
        } else {
            player.resetHeldItemSlot();
        }
    }

    @Override
    public boolean affect(Player shooter, Location land, Vector dir) {
        for (Entity entity : shooter.getPlayerBind().getWorld().getNearbyEntities(land, AOE_radius(), AOE_radius(), AOE_radius())) {
            if (entity instanceof org.bukkit.entity.Player player) {
                Player overwatchPlayer = Overwatch.players.get(player.getUniqueId());
                if (shooter.isAlly(overwatchPlayer)) {
                    overwatchPlayer.heal(shooter, ALLY_HEALING);
                    overwatchPlayer.boostHealing(shooter, timeLimit(), ALLY_HEAL_BOOST);
                } else {
                    overwatchPlayer.damage(shooter, ENEMY_DAMAGE);
                    overwatchPlayer.setAntied(shooter, timeLimit());
                }
            }
        }
        return true;
    }

    @Override
    public TimeUnit recoveryTime() {
        return TimeUnit.Converter.MILLISECOND.of(690); // 0.69s
    }

    @Override
    public TimeUnit castingTime() {
        return TimeUnit.Converter.TICK.of(0);
    }

    @Override
    public String abilityDescription() {
        return "Throws a grenade that heals and increases healing on allies, while damaging and preventing healing on enemies.";
    }

    @Override
    public int abilityID() {
        return 3;
    }

    @Override
    public AbilityEnum abilityBind() {
        return AbilityEnum.ANA_BIOTIC_GRENADE;
    }

    @Override
    public int getAbilityCooldown() {
        return 10; // s
    }

    @Override
    public float AOE_radius() {
        return 2; // AOE 4
    }

    @Override
    public float projectileSpeed() {
        return 30; // m/s
    }

    @Override
    public TimeUnit timeLimit() {
        return TimeUnit.Converter.MILLISECOND.of(3500); // 3.5s
    }

    @Override
    public ItemStack getAbilityItem(Player player) {
        ItemStack stack = new ItemStack(Material.LINGERING_POTION);
        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(Color.fromBGR(228, 14, 197));
        }
        if (meta != null) {
            meta.setCustomModelData(abilityID());
            String firstBar = ("§6" + abilityName() + " " + AbilityLoadout.getButton(abilityID()));
            CustomIconManager.CharIcon abilityIcon = CustomIconManager.ABILITY_MAP.get(abilityBind());
            int spaceWidth = 30 * 4;
            String abilityString = "§8Ability Item";
            int textLength = DefaultFontInfo.Utils.getLength(abilityString) + abilityIcon.getScaledWidth();
            int remaining = spaceWidth - textLength;
            meta.setDisplayName(firstBar);
            meta.addItemFlags(ItemFlag.values());
            List<String> lore = new ArrayList<>();
            lore.add(abilityString + " ".repeat(remaining / 4) + (player.hasAcceptedResourcePack() ? "§f" + abilityIcon.getCharacter() : "") + " ");
            lore.add("§8");
            String[] strings = splitDescription().split(":");
            for (String string : strings) {
                lore.add("§7" + string);
            }
            lore.add("§8");
            lore.add("§7Cooldown: §a" + getAbilityCooldown() + "s");
            lore.add("§8");
            lore.add("§bAllies:");
            lore.add("§8 > §7Health: §a" + ALLY_HEALING);
            lore.add("§8 > §7Healing boost: " + yellowChat + Math.round(ALLY_HEAL_BOOST * 100) + "%");
            lore.add("§8");
            lore.add("§cEnemies:");
            lore.add("§8 > §7Damage: §c" + ENEMY_DAMAGE);
            lore.add("§8 > §eAbility: " + purpleChat + "Anti-Heal");
            lore.add("§8");
            lore.add("§eStatistics:");
            lore.add("§8 > §7Casting delay: §a" + castingTime().getSeconds() + "s");
            lore.add("§8 > §7Recovery time: §a" + recoveryTime().getSeconds() + "s");
            lore.add("§8");
            lore.add("§8 > §7AOE Radius: §e" + AOE_radius() + "m");
            lore.add("§8 > §7Projectile speed: §e" + projectileSpeed() + "m/s");
            lore.add("§8");
            if (isOnCooldown(player)) {
                lore.add("§cAbility unavailable (" + getRemainingTime(player) + "s remaining)");
            } else {
                lore.add("§aAbility available");
            }
            meta.setLore(lore);
        }
        stack.setItemMeta(meta);
        return stack;
    }
}
