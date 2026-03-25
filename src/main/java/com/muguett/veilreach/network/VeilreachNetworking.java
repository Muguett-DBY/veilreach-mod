package com.muguett.veilreach.network;

import com.muguett.veilreach.entity.vehicle.AbstractArmedVehicleEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;

public final class VeilreachNetworking {
    private VeilreachNetworking() {
    }

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(LittleBirdActionPayload.ID, LittleBirdActionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(HushlandsProgressPayload.ID, HushlandsProgressPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(LittleBirdActionPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                Entity vehicle = context.player().getVehicle();
                if (!(vehicle instanceof AbstractArmedVehicleEntity armedVehicle) || armedVehicle.getControllingPassenger() != context.player()) {
                    return;
                }
                armedVehicle.handleVehicleAction(context.player(), payload);
            });
        });
    }
}
