package me.vlink102.personal.overwatch.minecraftlink;

import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.abilities.Ability;
import me.vlink102.personal.overwatch.abilities.AbilityLoadout;
import me.vlink102.personal.overwatch.abilities.types.DualWeaponPrimary;
import me.vlink102.personal.overwatch.abilities.types.Scoped;
import me.vlink102.personal.overwatch.abilities.types.UltimateAbility;
import me.vlink102.personal.overwatch.heroes.properties.DuelWielding;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AbilityListener implements Listener {
    private final Overwatch overwatch;

    public AbilityListener(Overwatch overwatch) {
        this.overwatch = overwatch;
    }

    @EventHandler
    public void onAbility(PlayerItemHeldEvent event) {
        if (event.getNewSlot() == 8 || event.getNewSlot() == 7) {
            return;
        }

        Player player = Overwatch.players.get(event.getPlayer().getUniqueId());
        if (!(event.getPreviousSlot() == 8 || (player instanceof DuelWielding && event.getPreviousSlot() == 1))) {
            player.resetHeldItemSlot();
            return;
        }

        Vector looking = event.getPlayer().getEyeLocation().getDirection();
        AbilityLoadout loadout = player.getHero().abilities();
        switch (event.getNewSlot()) {
            case 0 -> {
                /*
                 * Weapon 1 (Mercy staff/Torb gun)
                 */
                if (player.getHero() instanceof DuelWielding) {
                    player.setSelected(loadout.primaryAbility());
                }
            }
            case 1 -> {
                /*
                 * Weapon 2 (Mercy blaster/Torb hammer)
                 */
                if (player.getHero() instanceof DuelWielding duelWielding) {
                    Ability ability = duelWielding.a2();
                    player.setSelected(ability);
                }
            }
            case 2 -> {
                /*
                 * Ability 1 (L shift)
                 */
                Ability lShift = loadout.lShift();
                if (lShift != null) {
                    lShift.useAbility(player, looking);
                }
            }
            case 3 -> {
                /*
                 * Ability 2 (e)
                 */
                Ability e = loadout.e();
                if (e != null) {
                    e.useAbility(player, looking);
                }
            }
            case 4 -> {
                /*
                 * Ultimate (q)
                 */
                UltimateAbility ultimateAbility = loadout.ultimateAbility();
                // TODO ult meter
                ultimateAbility.useAbility(player, looking);
            }
            case 5 -> {
                /*
                 * Secondary fire (Right mouse button)
                 */
                if (player.getSelected() instanceof Scoped) {
                    if (player.isScoped()) {
                        ScopedLink.scopeOut(player, overwatch);
                        player.resetHeldItemSlot();
                    } else {
                        ScopedLink.scopeIn(player, overwatch);
                        player.resetHeldItemSlot();
                    }
                } else {
                    Ability ability = loadout.secondaryAbility();
                    if (ability != null) {
                        ability.useAbility(player, looking);
                    }
                }
            }
            case 6 -> /*
             * Reload (R)
             */ player.reload();
        }
    }
}
