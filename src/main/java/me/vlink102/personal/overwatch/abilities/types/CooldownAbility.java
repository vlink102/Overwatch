package me.vlink102.personal.overwatch.abilities.types;

import me.vlink102.personal.overwatch.abilities.Ability;
import me.vlink102.personal.overwatch.abilities.cooldowns.Cooldown;
import me.vlink102.personal.overwatch.minecraftlink.Player;

public abstract class CooldownAbility extends Ability {
    public abstract int getAbilityCooldown();

    public void putOnCooldown(Player player) {
        Cooldown.add(player, this, getAbilityCooldown(), System.currentTimeMillis());
    }

    public boolean isOnCooldown(Player player) {
        return Cooldown.isCooling(player, this);
    }

    public void removeCooldown(Player player) {
        Cooldown.removeCooldown(player, this);
    }

    public String getRemainingTime(Player player) {
        return String.valueOf(Cooldown.getRemaining(player, this));
    }
}
