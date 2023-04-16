package me.vlink102.personal.overwatch.abilities.types;

import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.heroes.SpecificStatistic;
import me.vlink102.personal.overwatch.minecraftlink.Player;
import me.vlink102.personal.overwatch.minecraftlink.TimeUnit;
import org.bukkit.scheduler.BukkitRunnable;

public interface Scoped extends HitscanAbility {
    TimeUnit scopeInTime();
    TimeUnit scopeOutTime();

    void scopedAffect(Player shooter, Player shot, boolean headShot);

    /**
     * number >0 and as 0.1 = 10%
     */
    float scopedMovementPenalty();

    class ScopedAccuracy extends SpecificStatistic {
        @Override
        public StatType getStatType() {
            return StatType.PERCENTAGE;
        }

        @Override
        public String getStatName() {
            return "Scoped Accuracy";
        }
    }
}
