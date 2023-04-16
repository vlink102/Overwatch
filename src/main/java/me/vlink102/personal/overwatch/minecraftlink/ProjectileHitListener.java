package me.vlink102.personal.overwatch.minecraftlink;

import me.vlink102.personal.overwatch.Overwatch;

import me.vlink102.personal.overwatch.abilities.Ability;
import me.vlink102.personal.overwatch.abilities.ana.BioticGrenade;
import me.vlink102.personal.overwatch.abilities.types.ArcedProjectileAbility;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class ProjectileHitListener implements Listener {

    @EventHandler
    public void onSplash(LingeringPotionSplashEvent event) {
        if (affectArea(event.getEntity())) {
            PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
            Player player = Overwatch.players.get(UUID.fromString(Objects.requireNonNull(container.get(Overwatch.abilityOwnerKey, PersistentDataType.STRING))));
            Ability ability = player.getHero().abilities().get(container.get(Overwatch.abilityTypeKey, PersistentDataType.INTEGER));
            if (ability instanceof BioticGrenade bioticGrenade) {
                AreaEffectCloud cloud = event.getAreaEffectCloud();
                cloud.setGravity(true);
                cloud.setRadiusOnUse(bioticGrenade.AOE_radius());
                cloud.setDuration(1);
                cloud.clearCustomEffects();
                spawnAreaEffectCloud(cloud.getWorld(), cloud.getLocation(), 2, 197, 14, 228, 2);
                spawnAreaEffectCloud(cloud.getWorld(), cloud.getLocation(), 2, 254, 233, 29, 2);
            }
        }
    }

    public void spawnAreaEffectCloud(World world, Location location, float radius, int r, int g, int b, int particleSize) {
        world.spawn(location, AreaEffectCloud.class, areaEffectCloud -> {
            areaEffectCloud.setGravity(true);
            areaEffectCloud.setRadiusOnUse(radius);
            areaEffectCloud.setParticle(Particle.REDSTONE, new Particle.DustOptions(Color.fromBGR(b, g, r), particleSize));
            areaEffectCloud.setDuration(1);
            areaEffectCloud.clearCustomEffects();
        });
    }

    private boolean affectArea(Projectile entity) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (container.has(Overwatch.abilityOwnerKey, PersistentDataType.STRING)) {
            Player player = Overwatch.players.get(UUID.fromString(Objects.requireNonNull(container.get(Overwatch.abilityOwnerKey, PersistentDataType.STRING))));
            Ability ability = player.getHero().abilities().get(container.get(Overwatch.abilityTypeKey, PersistentDataType.INTEGER));
            if (ability instanceof ArcedProjectileAbility arcedProjectileAbility) {
                return arcedProjectileAbility.affect(player, entity.getLocation(), entity.getVelocity());
            }
        }
        return false;
    }
}
