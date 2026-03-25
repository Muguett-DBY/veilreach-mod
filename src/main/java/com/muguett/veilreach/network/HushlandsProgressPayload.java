package com.muguett.veilreach.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record HushlandsProgressPayload(int flags) implements CustomPayload {
    public static final Id<HushlandsProgressPayload> ID = CustomPayload.id("hushlands_progress");
    public static final PacketCodec<RegistryByteBuf, HushlandsProgressPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            HushlandsProgressPayload::flags,
            HushlandsProgressPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
