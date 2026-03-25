package com.muguett.veilreach.entity.vehicle;

import net.minecraft.text.Text;

import java.util.List;

public record VehicleHudState(
        Text callsign,
        Text seatLabel,
        float health,
        float maxHealth,
        double speed,
        int altitude,
        float throttle,
        List<VehicleWeaponMount> mounts,
        Text targetText,
        Text hintText,
        int accentColor,
        int warningColor,
        boolean lockActive,
        boolean damaged,
        boolean critical,
        boolean showFlightSymbology
) {
}
