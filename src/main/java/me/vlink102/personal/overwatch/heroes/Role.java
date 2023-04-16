package me.vlink102.personal.overwatch.heroes;

public abstract class Role extends Hero {
    @Override
    public float baseMovementSpeed() {
        return 5.5f;
    }

    @Override
    public float backwardsMovementPenalty() {
        return 0.1f; // 90% of base
    }

    @Override
    public float diagonalMovementPenalty() {
        return 0.0666666f; // 93.333..% of base
    }

    @Override
    public float crouchingSpeed() {
        return 3;
    }
}
