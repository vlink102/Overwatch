package me.vlink102.personal.overwatch.heroes;

public abstract class GenericStatistic {
    public abstract StatType getStatType();
    public abstract String getStatName();

    public enum StatType {
        PERCENTAGE,
        VALUE
    }
}
