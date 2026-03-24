package com.muguett.veilreach.item;

import com.muguett.veilreach.world.dimension.VeilreachDimensions;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SovereignWardItem extends Item {
    public SovereignWardItem(Settings settings) {
        super(settings.maxCount(1).useCooldown(1.0F));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        int duration = world.getRegistryKey() == VeilreachDimensions.HUSHLANDS_WORLD ? 20 * 120 : 20 * 60;
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, duration, 0, false, true));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, duration, 1, false, true));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 35, 0, false, true));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 35, 0, false, true));
        serverWorld.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.PLAYERS, 0.85F, 0.9F);
        user.sendMessage(Text.literal("The Sovereign Ward hardens the hush around you."), true);
        user.getItemCooldownManager().set(stack, 20 * 45);
        return ActionResult.CONSUME;
    }
}
