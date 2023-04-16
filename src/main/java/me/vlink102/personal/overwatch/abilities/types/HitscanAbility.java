package me.vlink102.personal.overwatch.abilities.types;

import me.vlink102.personal.overwatch.minecraftlink.Player;

public interface HitscanAbility {
    void affect(Player shooter, Player shot, boolean headShot);
}
