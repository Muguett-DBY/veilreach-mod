package com.muguett.veilreach.item;

import com.muguett.veilreach.entity.LittleBirdEntity;
import com.muguett.veilreach.registry.VeilreachEntities;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

public final class LittleBirdItem extends Item {
    public LittleBirdItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        Vec3d spawnPos = context.getBlockPos().toCenterPos().add(0.0D, 1.1D, 0.0D);

        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        LittleBirdEntity helicopter = new LittleBirdEntity(VeilreachEntities.LITTLE_BIRD, serverWorld);
        helicopter.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, context.getPlayerYaw(), 0.0F);

        if (!serverWorld.getOtherEntities(helicopter, helicopter.getBoundingBox().expand(-0.1D)).isEmpty()) {
            return ActionResult.FAIL;
        }

        serverWorld.spawnEntity(helicopter);
        PlayerEntity player = context.getPlayer();
        if (player != null && !player.isCreative()) {
            context.getStack().decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.veilreach.little_bird.tooltip"));
        textConsumer.accept(Text.translatable("item.veilreach.little_bird.tooltip2"));
        textConsumer.accept(Text.translatable("item.veilreach.little_bird.tooltip3"));
    }
}
