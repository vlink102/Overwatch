package me.vlink102.personal.overwatch.heroes;

import me.vlink102.personal.overwatch.EntityStats;
import me.vlink102.personal.overwatch.abilities.AbilityLoadout;

public abstract class Hero {
    public abstract String name();
    public abstract EntityStats entityStats();
    public abstract AbilityLoadout abilities();
    public abstract HeroEnum heroBind();
    public abstract float baseMovementSpeed();
    public abstract float backwardsMovementPenalty();
    public abstract float diagonalMovementPenalty();
    public abstract float crouchingSpeed();
}
