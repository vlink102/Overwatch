package me.vlink102.personal.overwatch.minecraftlink;

import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.abilities.Ability;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class Projectile {
    private final Entity bind;
    private final Player shooter;
    private final Vector velocity;

    public Projectile(Player shooter, Vector velocity, Entity bind, Ability bindAbility) {
        this.shooter = shooter;
        this.velocity = velocity;
        this.bind = bind;
        // set metadata persistent
        PersistentDataContainer container = bind.getPersistentDataContainer();
        container.set(Overwatch.abilityOwnerKey, PersistentDataType.STRING, shooter.getPlayerBind().getUniqueId().toString());
        container.set(Overwatch.abilityTypeKey, PersistentDataType.INTEGER, bindAbility.abilityID());
    }

    public Projectile(org.bukkit.entity.Projectile projectile) {
        if (projectile.getShooter() instanceof org.bukkit.entity.Player playerShooter) {
            this.shooter = Overwatch.players.get(playerShooter.getUniqueId());
        } else {
            this.shooter = null;
        }
        this.velocity = projectile.getVelocity();
        this.bind = projectile;
    }

    public Player getShooter() {
        return shooter;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public Entity getBind() {
        return bind;
    }
}
