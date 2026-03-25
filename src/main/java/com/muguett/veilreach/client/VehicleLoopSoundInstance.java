package com.muguett.veilreach.client;

import com.muguett.veilreach.entity.vehicle.AbstractVehicleEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public final class VehicleLoopSoundInstance extends MovingSoundInstance {
    private final AbstractVehicleEntity vehicle;

    public VehicleLoopSoundInstance(AbstractVehicleEntity vehicle, SoundEvent soundEvent) {
        super(soundEvent, SoundCategory.NEUTRAL, vehicle.getRandom());
        this.vehicle = vehicle;
        this.repeat = true;
        this.repeatDelay = 0;
        this.attenuationType = SoundInstance.AttenuationType.LINEAR;
        this.volume = 0.0F;
        this.pitch = 1.0F;
    }

    @Override
    public void tick() {
        if (vehicle.isRemoved() || vehicle.getControllingPassenger() == null) {
            setDone();
            return;
        }

        this.x = vehicle.getX();
        this.y = vehicle.getY();
        this.z = vehicle.getZ();
        this.volume = vehicle.getLoopSoundVolume();
        this.pitch = vehicle.getLoopSoundPitch();
    }
}
