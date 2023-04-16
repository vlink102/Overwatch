package me.vlink102.personal.overwatch.abilities.types;

import me.vlink102.personal.overwatch.minecraftlink.Player;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface ArcedProjectileAbility extends ProjectileAbility {
    boolean affect(Player shooter, Location land, Vector dir);
}
