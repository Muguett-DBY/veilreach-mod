package com.muguett.veilreach.entity.vehicle;

public enum VehicleSeatRole {
    PILOT("hud.veilreach.vehicle.role.pilot"),
    GUNNER("hud.veilreach.vehicle.role.gunner"),
    PASSENGER("hud.veilreach.vehicle.role.passenger");

    private final String translationKey;

    VehicleSeatRole(String translationKey) {
        this.translationKey = translationKey;
    }

    public String translationKey() {
        return translationKey;
    }
}
