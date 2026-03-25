package com.muguett.veilreach.network;

import com.muguett.veilreach.entity.LittleBirdEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;

public final class VeilreachNetworking {
    private VeilreachNetworking() {
    }

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(LittleBirdActionPayload.ID, LittleBirdActionPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(LittleBirdActionPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                Entity vehicle = context.player().getVehicle();
                if (!(vehicle instanceof LittleBirdEntity helicopter) || helicopter.getControllingPassenger() != context.player()) {
                    return;
                }

                switch (payload.action()) {
                    case LittleBirdActionPayload.FIRE_ROCKET -> helicopter.tryFireRocket(context.player(), payload.yaw(), payload.pitch());
                    case LittleBirdActionPayload.DROP_BOMB -> helicopter.tryDropBomb(context.player());
                    case LittleBirdActionPayload.UPDATE_CONTROLS ->
                            helicopter.applyPilotInputs(payload.forward(), payload.turn(), payload.ascend(), payload.descend(), payload.yaw(), payload.pitch());
                    default -> {
                    }
                }
            });
        });
    }
}
