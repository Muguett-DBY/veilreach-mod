package com.muguett.veilreach.client.render.entity.state;

import net.minecraft.client.render.entity.state.EntityRenderState;

public final class HoverVehicleRenderState extends EntityRenderState {
    public float bodyYaw;
    public float bodyPitch;
    public float bodyRoll;
    public float throttle;
    public float damageFlash;
    public boolean damaged;
    public boolean critical;
    public boolean hideEntireCraft;
}
