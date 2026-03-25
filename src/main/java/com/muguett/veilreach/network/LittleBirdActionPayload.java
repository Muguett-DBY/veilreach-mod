package com.muguett.veilreach.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record LittleBirdActionPayload(int action, float forward, float turn, boolean ascend, boolean descend, float yaw, float pitch) implements CustomPayload {
    public static final int FIRE_ROCKET = 0;
    public static final int DROP_BOMB = 1;
    public static final int UPDATE_CONTROLS = 2;
    public static final int LOCK_OR_FIRE_TRACKING = 3;

    public static final Id<LittleBirdActionPayload> ID = CustomPayload.id("little_bird_action");
    public static final PacketCodec<RegistryByteBuf, LittleBirdActionPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            LittleBirdActionPayload::action,
            PacketCodecs.FLOAT,
            LittleBirdActionPayload::forward,
            PacketCodecs.FLOAT,
            LittleBirdActionPayload::turn,
            PacketCodecs.BOOLEAN,
            LittleBirdActionPayload::ascend,
            PacketCodecs.BOOLEAN,
            LittleBirdActionPayload::descend,
            PacketCodecs.FLOAT,
            LittleBirdActionPayload::yaw,
            PacketCodecs.FLOAT,
            LittleBirdActionPayload::pitch,
            LittleBirdActionPayload::new
    );

    public static LittleBirdActionPayload fireRocket(float yaw, float pitch) {
        return new LittleBirdActionPayload(FIRE_ROCKET, 0.0F, 0.0F, false, false, yaw, pitch);
    }

    public static LittleBirdActionPayload dropBomb(float yaw, float pitch) {
        return new LittleBirdActionPayload(DROP_BOMB, 0.0F, 0.0F, false, false, yaw, pitch);
    }

    public static LittleBirdActionPayload controls(float forward, float turn, boolean ascend, boolean descend, float yaw, float pitch) {
        return new LittleBirdActionPayload(UPDATE_CONTROLS, forward, turn, ascend, descend, yaw, pitch);
    }

    public static LittleBirdActionPayload lockOrFireTracking(float yaw, float pitch) {
        return new LittleBirdActionPayload(LOCK_OR_FIRE_TRACKING, 0.0F, 0.0F, false, false, yaw, pitch);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
