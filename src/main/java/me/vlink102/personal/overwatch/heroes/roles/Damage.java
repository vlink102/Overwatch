package me.vlink102.personal.overwatch.heroes.roles;

import me.vlink102.personal.overwatch.heroes.Role;
import me.vlink102.personal.overwatch.heroes.GenericStatistic;

public abstract class Damage extends Role {
    public class SoloKills extends GenericStatistic {
        @Override
        public StatType getStatType() {
            return StatType.VALUE;
        }

        @Override
        public String getStatName() {
            return "Solo Kills";
        }
    }
}
