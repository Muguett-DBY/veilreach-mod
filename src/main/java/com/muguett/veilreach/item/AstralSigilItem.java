package com.muguett.veilreach.item;

import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.world.dimension.HushPortalManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

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
                player.sendMessage(Text.literal("The sigil only responds to a Moonwoven Frame."), true);
            }
            return ActionResult.FAIL;
        }

        PlayerEntity player = context.getPlayer();
        if (player != null && HushPortalManager.tryActivate(serverWorld, context.getBlockPos(), player) && !player.isCreative()) {
            context.getStack().decrement(1);
        }
        return ActionResult.SUCCESS;
    }
}
