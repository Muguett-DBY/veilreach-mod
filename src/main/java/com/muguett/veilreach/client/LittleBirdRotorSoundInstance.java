package com.muguett.veilreach.client;

import com.muguett.veilreach.entity.LittleBirdEntity;
import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;

public final class LittleBirdRotorSoundInstance extends MovingSoundInstance {
    private final LittleBirdEntity helicopter;

    public LittleBirdRotorSoundInstance(LittleBirdEntity helicopter) {
        super(VeilreachSoundEvents.LITTLE_BIRD_ROTOR_LOOP, SoundCategory.NEUTRAL, helicopter.getRandom());
        this.helicopter = helicopter;
        this.repeat = true;
        this.repeatDelay = 0;
        this.attenuationType = SoundInstance.AttenuationType.LINEAR;
        this.volume = 0.0F;
        this.pitch = 0.85F;
    }

    @Override
    public void tick() {
        if (this.helicopter.isRemoved() || this.helicopter.getControllingPassenger() == null) {
            this.setDone();
            return;
        }

        this.x = this.helicopter.getX();
        this.y = this.helicopter.getY();
        this.z = this.helicopter.getZ();
        this.volume = MathHelper.clamp(0.2F + (this.helicopter.getThrottle() * 0.65F), 0.0F, 1.0F);
        this.pitch = 0.78F + (this.helicopter.getThrottle() * 0.4F);
    }
}
