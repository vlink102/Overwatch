package me.vlink102.personal.overwatch.minecraftlink;

import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.abilities.types.Scoped;
import me.vlink102.personal.overwatch.heroes.ana.Ana;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ScopedLink {
    public static void scopeIn(Player player, Overwatch overwatch) {
        if (player.getSelected() instanceof Scoped scoped) {
            if (!player.isCasting() && !player.isRecovering() && !player.isScoped()) {
                scopePlayer(player, true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setScoped(true);
                        player.getPlayerBind().sendMessage("§aScoped in");
                    }
                }.runTaskLater(overwatch, scoped.scopeInTime().getTicks());
            }
        }
    }

    public static void scopeOut(Player player, Overwatch overwatch) {
        if (player.getSelected() instanceof Scoped scoped) {
            if (!player.isCasting() && !player.isRecovering() && player.isScoped()) {
                scopePlayer(player, false);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setScoped(false);
                        player.getPlayerBind().sendMessage("§aScoped out");
                    }
                }.runTaskLater(overwatch, scoped.scopeOutTime().getTicks());
            }
        }
    }

    public static void scopePlayer(Player player, boolean on) {
        if (player.getHero() instanceof Ana) {
            sendFreeze(player.getPlayerBind(), on);
        }// else if (player.getHero() instanceof Widowmaker) {

        //}
    }

    public static void sendFreeze(org.bukkit.entity.Player player, boolean on) {
        player.setFreezeTicks(on ? Integer.MAX_VALUE : 0);
    }

    public static void sendPumpkin(org.bukkit.entity.Player player, boolean on) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ItemStack itemStack = new ItemStack(on ? Material.PUMPKIN : Material.AIR);
        craftPlayer.getHandle().connection.send(new ClientboundContainerSetSlotPacket(0, 0, 5, CraftItemStack.asNMSCopy(itemStack)));
    }
}
