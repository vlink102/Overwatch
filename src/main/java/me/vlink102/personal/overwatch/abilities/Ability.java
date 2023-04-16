package me.vlink102.personal.overwatch.abilities;

import me.vlink102.personal.overwatch.minecraftlink.Player;
import me.vlink102.personal.overwatch.minecraftlink.TimeUnit;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract class Ability {
    public abstract String abilityName();
    public abstract void useAbility(Player player, Vector dir);
    public abstract TimeUnit recoveryTime();
    public abstract TimeUnit castingTime();
    public abstract String abilityDescription();
    public abstract int abilityID();
    public abstract AbilityEnum abilityBind();

    public String splitDescription() {
        return WordUtils.wrap(abilityDescription(), 30, ":",  false);
    }
}
