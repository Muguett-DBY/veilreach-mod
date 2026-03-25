package com.muguett.veilreach.progress;

public interface HushlandsProgressAccess {
    boolean veilreach$hasDefeatedWarden();

    void veilreach$setDefeatedWarden(boolean value);

    boolean veilreach$hasCompletedCathedral();

    void veilreach$setCompletedCathedral(boolean value);

    boolean veilreach$hasCompletedSanctum();

    void veilreach$setCompletedSanctum(boolean value);

    boolean veilreach$hasDefeatedSovereign();

    void veilreach$setDefeatedSovereign(boolean value);

    void veilreach$copyProgressFrom(HushlandsProgressAccess other);
}
