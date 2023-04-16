package me.vlink102.personal.overwatch.abilities.types;

import me.vlink102.personal.overwatch.minecraftlink.Player;

public interface TargetedAbility {
    float targetedAbilityRange();
    void affect(Player user, Player targeted);
}
