package me.vlink102.personal.overwatch.minecraftlink;

import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.Team;
import me.vlink102.personal.overwatch.abilities.Ability;
import me.vlink102.personal.overwatch.abilities.types.*;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.DustData;

import java.awt.*;
import java.util.List;

public class HitscanListener implements Listener {

    @EventHandler
    public void onShoot(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SPYGLASS) {
            System.out.println("Spyglass used");
        }
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                Player overwatchPlayer = Overwatch.players.get(event.getPlayer().getUniqueId());
                if (overwatchPlayer.getSelected() instanceof HitscanAbility hitscanAbility) {
                    ((Ability) hitscanAbility).useAbility(overwatchPlayer, null);
                }
            }
        }
    }

    public static void defaultHitScan(org.bukkit.entity.Player player, Player overwatchPlayer, HitscanAbility hitscanAbility, boolean showLine, Color lineColor, float allyParticleWidth, Color enemyLineColor, float enemyParticleWidth, boolean hitAllies) {
        Location hand = getRightHandLocation(player);
        double raySize = 0;
        if (overwatchPlayer.getSelected() instanceof LinearProjectileAbility projectileAbility) {
            raySize = projectileAbility.projectilePinpointRadius();
        }
        OverwatchTraceResult result = getTargetEntity(overwatchPlayer, raySize, hitAllies);
        if (result.hitEntity() != null) {
            if (showLine) {
                line(hand, result.hitPosition().toLocation(player.getWorld()), ParticleEffect.ELECTRIC_SPARK, lineColor, allyParticleWidth, enemyLineColor, enemyParticleWidth, overwatchPlayer.getTeam().getSavedBukkitPlayers(), Team.getEnemyTeam(overwatchPlayer.getTeam()).getSavedBukkitPlayers());
            }
            Player shot = Overwatch.players.get(result.hitEntity().getUniqueId());
            hitscanAbility.affect(overwatchPlayer, shot, result.headShot());
        } else {
            if (showLine) {
                Location end = player.getLocation().add(player.getLocation().getDirection().multiply(100));
                line(hand, end, ParticleEffect.REDSTONE, lineColor, allyParticleWidth, enemyLineColor, enemyParticleWidth, overwatchPlayer.getTeam().getSavedBukkitPlayers(), Team.getEnemyTeam(overwatchPlayer.getTeam()).getSavedBukkitPlayers());
            }
        }
    }

    public record OverwatchTraceResult(Entity hitEntity, Vector hitPosition, boolean headShot) {}

    public static OverwatchTraceResult getTargetEntity(Player player, double raySize, boolean hitAllies) {
        org.bukkit.entity.Player entity = player.getPlayerBind();
        RayTraceResult result = entity.getWorld().rayTrace(entity.getEyeLocation(), entity.getLocation().getDirection(), 50, FluidCollisionMode.NEVER, true, raySize, entity1 -> entity1 != entity && (hitAllies || Overwatch.players.get(entity1.getUniqueId()).getTeam().getTeamID() != player.getTeam().getTeamID()));
        if (result == null) {
            return new OverwatchTraceResult(null, null, false);
        }
        Entity hitEntity = result.getHitEntity();
        Vector hitPosition = result.getHitPosition();

        LivingEntity livingEntity = (LivingEntity) hitEntity;
        boolean headShot = (result.getHitPosition().getY() - entity.getLocation().getY()) > 1.375 || (livingEntity instanceof org.bukkit.entity.Player && ((org.bukkit.entity.Player) livingEntity).isSneaking() && (result.getHitPosition().getY() - entity.getLocation().getY()) >  1.1);
        return new OverwatchTraceResult(livingEntity, hitPosition, headShot);
    }

    public Location getEyeLookingFor(Location eyeLocation, double length) {
        final Vector directionVector = eyeLocation.getDirection().normalize();
        return eyeLocation.add(directionVector.multiply(length));
    }

    public static void line(final Location start, final Location end, ParticleEffect particle, Color color, float allyParticleWidth, Color enemyColor, float enemyParticleWidth, List<org.bukkit.entity.Player> team, List<org.bukkit.entity.Player> enemy) {
        ParticleBuilder builder = new ParticleBuilder(particle);
        ParticleBuilder enemyBuilder = new ParticleBuilder(particle);
        DustData data = new DustData(color, allyParticleWidth);
        DustData enemyData = new DustData(enemyColor, enemyParticleWidth);
        builder.setParticleData(data);
        enemyBuilder.setParticleData(enemyData);
        builder.setColor(color);
        enemyBuilder.setColor(enemyColor);
        double interval = 1 / 3D;

        double distance = start.distance(end);
        Vector difference = end.toVector().subtract(start.toVector());
        double points = Math.ceil(distance / interval);
        difference.multiply(1D / points);

        Location location = start.clone();
        for (int i = 0; i <= points; i++) {
            builder.setLocation(location);
            enemyBuilder.setLocation(location);
            builder.display(team);
            enemyBuilder.display(enemy);
            location.add(difference);
        }
    }

    /**
     * Returns a location with a specified distance away from the right side of
     * a location.
     *
     * @param location The origin location
     * @param distance The distance to the right
     * @return the location of the distance to the right
     */
    public static Location getRightSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    /**
     * Gets a location with a specified distance away from the left side of a
     * location.
     *
     * @param location The origin location
     * @param distance The distance to the left
     * @return the location of the distance to the left
     */
    public static Location getLeftSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    public static Location getRightHandLocation(org.bukkit.entity.Player player) {
        double yawRightHandDirection = Math.toRadians(-1 * player.getEyeLocation().getYaw() - 45);
        double x = 0.5 * Math.sin(yawRightHandDirection) + player.getLocation().getX();
        double y = player.getLocation().getY() + 1;
        double z = 0.5 * Math.cos(yawRightHandDirection) + player.getLocation().getZ();
        return new Location(player.getWorld(), x, y, z);
    }


}
