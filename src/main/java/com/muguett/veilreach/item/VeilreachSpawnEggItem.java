package com.muguett.veilreach.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class VeilreachSpawnEggItem extends SpawnEggItem {
    private final EntityType<?> entityType;

    public VeilreachSpawnEggItem(EntityType<?> entityType, Settings settings) {
        super(settings.spawnEgg(entityType));
        this.entityType = entityType;
    }

    @Override
    public EntityType<?> getEntityType(ItemStack stack) {
        return this.entityType;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        BlockPos spawnPos = context.getBlockPos().offset(context.getSide());
        if (this.entityType.spawn(serverWorld, spawnPos, SpawnReason.SPAWN_ITEM_USE) != null) {
            context.getStack().decrementUnlessCreative(1, context.getPlayer());
            return ActionResult.SUCCESS_SERVER;
        }

        return super.useOnBlock(context);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        BlockHitResult hit = raycast(world, player, RaycastContext.FluidHandling.NONE);
        BlockPos spawnPos = hit.getBlockPos().offset(hit.getSide());
        if (this.entityType.spawn(serverWorld, spawnPos, SpawnReason.SPAWN_ITEM_USE) != null) {
            player.getStackInHand(hand).decrementUnlessCreative(1, player);
            return ActionResult.SUCCESS_SERVER;
        }

        return super.use(world, player, hand);
    }
}
