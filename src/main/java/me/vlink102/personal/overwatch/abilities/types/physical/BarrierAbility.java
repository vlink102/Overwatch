package me.vlink102.personal.overwatch.abilities.types.physical;

import me.vlink102.personal.overwatch.minecraftlink.Projectile;
import me.vlink102.personal.overwatch.abilities.Ability;

import java.util.List;

public abstract class BarrierAbility extends PhysicalAbility implements OwnedAbility {
    public abstract List<Ability> blockedAbilities();
    public abstract List<Projectile> blockedProjectiles();
}
