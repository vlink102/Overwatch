package me.vlink102.personal.overwatch.heroes.roles;

import me.vlink102.personal.overwatch.heroes.properties.RegenerativeEntity;
import me.vlink102.personal.overwatch.heroes.Role;
import me.vlink102.personal.overwatch.minecraftlink.TimeUnit;

public abstract class Support extends Role implements RegenerativeEntity {
    @Override
    public int getRegenerationRate() {
        return 15;
    }

    /**
     * In milliseconds
     */
    @Override
    public TimeUnit getRegenerationDelay() {
        return TimeUnit.Converter.MILLISECOND.of(1500); // 1.5s
    }

    @Override
    public RegenerationType regenType() {
        return RegenerationType.HEALTH;
    }

    @Override
    public TimeUnit getRegenerationSpeed() {
        return TimeUnit.Converter.SECOND.of(1);
    }
}
