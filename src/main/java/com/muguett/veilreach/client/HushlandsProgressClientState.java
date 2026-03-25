package com.muguett.veilreach.client;

import com.muguett.veilreach.progress.HushlandsProgression;

public final class HushlandsProgressClientState {
    private static int flags;

    private HushlandsProgressClientState() {
    }

    public static void reset() {
        flags = 0;
    }

    public static void setFlags(int value) {
        flags = HushlandsProgression.clampFlags(value);
    }

    public static int getFlags() {
        return flags;
    }

    public static boolean defeatedWarden() {
        return (flags & HushlandsProgression.DEFEATED_WARDEN) != 0;
    }

    public static boolean completedCathedral() {
        return (flags & HushlandsProgression.COMPLETED_CATHEDRAL) != 0;
    }

    public static boolean completedSanctum() {
        return (flags & HushlandsProgression.COMPLETED_SANCTUM) != 0;
    }

    public static boolean defeatedSovereign() {
        return (flags & HushlandsProgression.DEFEATED_SOVEREIGN) != 0;
    }
}
