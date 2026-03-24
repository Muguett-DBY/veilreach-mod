package com.muguett.veilreach.item;

import net.minecraft.component.type.TooltipDisplayComponent;
import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.world.dimension.HushPortalManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.item.tooltip.TooltipType;

import java.util.function.Consumer;

public class AstralSigilItem extends Item {
    public AstralSigilItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        if (!world.getBlockState(context.getBlockPos()).isOf(VeilreachBlocks.MOONWOVEN_FRAME)) {
            PlayerEntity player = context.getPlayer();
            if (player != null) {
                player.sendMessage(Text.translatable("item.veilreach.astral_sigil.fail_frame"), true);
            }
            return ActionResult.FAIL;
        }

        PlayerEntity player = context.getPlayer();
        if (player != null && HushPortalManager.tryActivate(serverWorld, context.getBlockPos(), player) && !player.isCreative()) {
            context.getStack().decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
        textConsumer.accept(Text.translatable("item.veilreach.astral_sigil.tooltip"));
        textConsumer.accept(Text.translatable("item.veilreach.astral_sigil.tooltip2"));
    }
}
