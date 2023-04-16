package me.vlink102.personal.overwatch.abilities.types;

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;

import static net.minecraft.world.entity.RelativeMovement.X_ROT;
import static net.minecraft.world.entity.RelativeMovement.Y_ROT;

public interface RecoilAbility {
    float recoilUnits();

    TeleportUtils utils = new TeleportUtils();

    @Deprecated
    static void performRecoil(LivingEntity livingEntity, float recoilUnits) {
        Location location = livingEntity.getLocation();
        if (recoilUnits > 0) {
            float pitch = location.getPitch();
            location.setPitch(pitch - recoilUnits);

            Vector playerVelocity = livingEntity.getVelocity();
            livingEntity.teleport(location, PlayerTeleportEvent.TeleportCause.UNKNOWN);
            livingEntity.setVelocity(playerVelocity);
        }

        Vector vector = livingEntity.getLocation().getDirection().normalize().multiply(-0).setY(0);
        livingEntity.setVelocity(vector);
    }

    static void performRecoil(Player player, float recoilUnits) {
        if (recoilUnits > 0) {
            Location loc = player.getLocation();
            float pitch = loc.getPitch();
            loc.setPitch(pitch - recoilUnits);
            utils.teleport(player, loc);
        }
    }

    class TeleportUtils {
        private static final Set<RelativeMovement> TELEPORT_FLAGS = Collections.unmodifiableSet(EnumSet.of(X_ROT, Y_ROT));

        private static Field justTeleportedField;
        private static Field awaitingPositionFromClientField;
        private static Field lastPosXField;
        private static Field lastPosYField;
        private static Field lastPosZField;
        private static Field awaitingTeleportField;
        private static Field awaitingTeleportTimeField;
        private static Field aboveGroundVehicleTickCountField;

        static {
            try {
                justTeleportedField = getField("justTeleported");
                awaitingPositionFromClientField = getField("D"); //awaitingPositionFromClient
                lastPosXField = getField("lastPosX");
                lastPosYField = getField("lastPosY");
                lastPosZField = getField("lastPosZ");
                awaitingTeleportField = getField("E"); //awaitingTeleport
                awaitingTeleportTimeField = getField("F"); //awaitingTeleportTime
                aboveGroundVehicleTickCountField = getField("J"); //aboveGroundVehicleTickCount
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        private static Field getField(String name) throws NoSuchFieldException {
            Field field = ServerGamePacketListenerImpl.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        }

        public void teleport(Player player, Location location) {
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            ServerGamePacketListenerImpl connection = serverPlayer.connection;
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();

            if (serverPlayer.containerMenu != serverPlayer.inventoryMenu) serverPlayer.closeContainer();

            serverPlayer.absMoveTo(x, y, z, serverPlayer.getYRot(), serverPlayer.getXRot());

            int teleportAwait = 0;
            try {
                justTeleportedField.set(connection, true);
                awaitingPositionFromClientField.set(connection, new Vec3(x, y, z));
                lastPosXField.set(connection, x);
                lastPosYField.set(connection, y);
                lastPosZField.set(connection, z);
                teleportAwait = awaitingTeleportField.getInt(connection) + 1;
                if (teleportAwait == 2147483647) teleportAwait = 0;
                awaitingTeleportField.set(connection, teleportAwait);
                awaitingTeleportTimeField.set(connection, aboveGroundVehicleTickCountField.get(connection));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            connection.send(new ClientboundPlayerPositionPacket(x, y, z, 0, 0, TELEPORT_FLAGS, teleportAwait));
        }
    }
}
