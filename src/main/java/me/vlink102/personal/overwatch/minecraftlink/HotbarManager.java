package me.vlink102.personal.overwatch.minecraftlink;

import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.abilities.Ability;
import me.vlink102.personal.overwatch.abilities.AbilityLoadout;
import me.vlink102.personal.overwatch.abilities.types.ItemAbility;

public class HotbarManager {
    private final Player bind;

    public HotbarManager(Overwatch overwatch, Player bind) {
        this.bind = bind;
        overwatch.getServer().getScheduler().scheduleSyncRepeatingTask(overwatch, this::manageHotbar, 1L, 1L);
    }

    public Player getBind() {
        return bind;
    }

    private void manageHotbar() {
        AbilityLoadout loadOut = bind.getHero().abilities();
        for (int i = 1; i <= 6; i++) {
            Ability ability = loadOut.get(i);
            int hotbarSlot = loadOut.getHotbarSlot(i);
            if (ability instanceof ItemAbility itemAbility && hotbarSlot != -1) {
                bind.getPlayerBind().getInventory().setItem(hotbarSlot, itemAbility.getAbilityItem(bind));
            }
        }
    }
}
