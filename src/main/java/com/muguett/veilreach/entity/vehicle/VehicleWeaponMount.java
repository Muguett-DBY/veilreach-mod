package com.muguett.veilreach.entity.vehicle;

import net.minecraft.text.Text;

public record VehicleWeaponMount(Text label, int ammo, int maxAmmo, int cooldownTicks, int reloadTicks, int tintColor) {
    public boolean isReloading() {
        return reloadTicks > 0;
    }

    public boolean isCoolingDown() {
        return cooldownTicks > 0;
    }
}
