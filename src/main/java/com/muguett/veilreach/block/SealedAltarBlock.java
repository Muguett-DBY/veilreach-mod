package com.muguett.veilreach.block;

import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SealedAltarBlock extends Block {
    public SealedAltarBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack held = player.getStackInHand(Hand.MAIN_HAND);
        if (!held.isOf(VeilreachItems.WARDEN_SIGIL)) {
            if (!world.isClient()) {
                player.sendMessage(Text.literal("A silent ward rejects you. The altar needs a Warden Sigil."), true);
            }
            return ActionResult.SUCCESS;
        }

        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        serverWorld.setBlockState(pos, VeilreachBlocks.AWAKENED_ALTAR.getDefaultState(), Block.NOTIFY_ALL);
        Block.dropStack(serverWorld, pos.up(), new ItemStack(VeilreachItems.HUSH_WARD_CHARM));
        if (serverWorld.getEntitiesByType(EntityType.PLAYER, playerBox -> playerBox.squaredDistanceTo(pos.toCenterPos()) <= (48.0D * 48.0D)).size() > 0
                && serverWorld.getEntitiesByType(VeilreachEntities.HUSH_SOVEREIGN, entity -> entity.squaredDistanceTo(pos.toCenterPos()) <= (40.0D * 40.0D)).isEmpty()) {
            MobEntity boss = VeilreachEntities.HUSH_SOVEREIGN.spawn(serverWorld, pos.up(), SpawnReason.EVENT);
            if (boss != null) {
                boss.setPersistent();
            }
        }
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0F, 0.85F);
        player.sendMessage(Text.literal("The seal breaks. A hush ward charm drifts from the altar, and the Sovereign answers."), true);
        if (!player.isCreative()) {
            held.decrement(1);
        }
        return ActionResult.SUCCESS;
    }
}
