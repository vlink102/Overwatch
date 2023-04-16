package me.vlink102.personal.overwatch.abilities.ana;

import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.abilities.AbilityEnum;
import me.vlink102.personal.overwatch.abilities.types.TargetedAbility;
import me.vlink102.personal.overwatch.abilities.types.UltimateAbility;
import me.vlink102.personal.overwatch.minecraftlink.Player;
import me.vlink102.personal.overwatch.minecraftlink.TimeUnit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class NanoBoost extends UltimateAbility implements TargetedAbility {
    private final Overwatch overwatch;

    public NanoBoost(Overwatch overwatch) {
        this.overwatch = overwatch;
    }
    @Override
    public String abilityName() {
        return "Nano Boost";
    }

    @Override
    public void useAbility(Player player, Vector dir) {
        /*
         * Ultimate Ability: Nano Boost
         * - Cast-time ultimate type
         * - Ally-targeting hitscan ability type
         * - 50% damage boost, 50% damage resistance
         * - 40 m range
         * - 250 healing on use
         * - 0.2 sec cast
         * - Lasts 8 seconds
         */
        System.out.println("Used nano boost");
    }

    @Override
    public TimeUnit recoveryTime() {
        return TimeUnit.Converter.MILLISECOND.of(600);
    }

    @Override
    public TimeUnit castingTime() {
        return TimeUnit.Converter.MILLISECOND.of(128);
    }

    @Override
    public String abilityDescription() {
        return "Increases an ally's damage, while reducing damage taken.";
    }

    @Override
    public int abilityID() {
        return 6;
    }

    @Override
    public AbilityEnum abilityBind() {
        return AbilityEnum.ANA_NANO_BOOST;
    }

    @Override
    public float targetedAbilityRange() {
        return 40;
    }

    @Override
    public void affect(Player user, Player targeted) {

    }

    @Override
    public TimeUnit timeLimit() {
        return TimeUnit.Converter.SECOND.of(8);
    }
}
