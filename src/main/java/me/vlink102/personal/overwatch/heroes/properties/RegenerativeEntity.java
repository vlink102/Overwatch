package me.vlink102.personal.overwatch.heroes.properties;

import me.vlink102.personal.overwatch.minecraftlink.TimeUnit;

public interface RegenerativeEntity {
    int getRegenerationRate();
    TimeUnit getRegenerationDelay();
    TimeUnit getRegenerationSpeed();

    RegenerationType regenType();

    enum RegenerationType {
        SHIELDS,
        HEALTH
    }
}
