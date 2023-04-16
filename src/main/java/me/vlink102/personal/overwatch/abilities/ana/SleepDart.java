package me.vlink102.personal.overwatch.abilities.ana;

import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.abilities.AbilityEnum;
import me.vlink102.personal.overwatch.abilities.types.CooldownAbility;
import me.vlink102.personal.overwatch.abilities.types.LinearProjectileAbility;
import me.vlink102.personal.overwatch.abilities.types.ProjectileAbility;
import me.vlink102.personal.overwatch.abilities.types.RangedTimeLimit;
import me.vlink102.personal.overwatch.minecraftlink.Player;
import me.vlink102.personal.overwatch.minecraftlink.TimeUnit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class SleepDart extends CooldownAbility implements LinearProjectileAbility, RangedTimeLimit {
    private final Overwatch overwatch;

    public SleepDart(Overwatch overwatch) {
        this.overwatch = overwatch;
    }

    @Override
    public String abilityName() {
        return "Sleep Dart";
    }

    @Override
    public void useAbility(Player player, Vector dir) {
        /*
         * Sleep Dart
         * - Stunning linear projectile type
         * - 5 damage
         * - 60 m/s projectile speed
         * - 0.3 second cast time
         * - Lasts Up to 5.5 seconds
         * - ✘ Cannot headshot
         */
        System.out.println("Used sleep dart");
    }

    @Override
    public TimeUnit recoveryTime() {
        return TimeUnit.Converter.MILLISECOND.of(670);
    }

    @Override
    public TimeUnit castingTime() {
        return TimeUnit.Converter.MILLISECOND.of(300);
    }

    @Override
    public String abilityDescription() {
        return "Fires a dart that puts an enemy to sleep.";
    }

    @Override
    public int abilityID() {
        return 4;
    }

    @Override
    public AbilityEnum abilityBind() {
        return AbilityEnum.ANA_SLEEP_DART;
    }

    @Override
    public int getAbilityCooldown() {
        return 14;
    }

    @Override
    public float projectileSpeed() {
        return 60;
    }

    @Override
    public TimeUnit minimumTime() {
        return TimeUnit.Converter.MILLISECOND.of(1500);
    }

    @Override
    public TimeUnit maximumTime() {
        return TimeUnit.Converter.MILLISECOND.of(5000);
    }

    @Override
    public float projectilePinpointRadius() {
        return 0.2f;
    }
}
