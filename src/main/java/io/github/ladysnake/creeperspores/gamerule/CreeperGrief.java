package io.github.ladysnake.creeperspores.gamerule;

public enum CreeperGrief {
    VANILLA, CHARGED, NEVER;

    public boolean shouldGrief(boolean charged) {
        switch (this) {
            case NEVER:
                return false;
            case CHARGED:
                return charged;
            case VANILLA:
                return true;
            default:
                throw new AssertionError("Unexpected enum " + this);
        }
    }
}
