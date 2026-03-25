package com.muguett.veilreach.entity.vehicle;

import com.muguett.veilreach.network.LittleBirdActionPayload;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.List;

public abstract class AbstractArmedVehicleEntity extends AbstractVehicleEntity {
    protected AbstractArmedVehicleEntity(EntityType<? extends AbstractArmedVehicleEntity> entityType, World world) {
        super(entityType, world);
    }

    public abstract VehicleHudState createHudState(PlayerEntity viewer);

    public abstract void handleVehicleAction(ServerPlayerEntity pilot, LittleBirdActionPayload payload);

    public abstract void rearmVehicle();

    public List<Entity> getHighlightedTargets() {
        return List.of();
    }
}
