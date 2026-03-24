package com.muguett.veilreach.item;

import com.muguett.veilreach.world.dimension.VeilreachDimensions;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class HushWardCharmItem extends Item {
    public HushWardCharmItem(Settings settings) {
        super(settings.maxCount(1).useCooldown(1.5F));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        int duration = world.getRegistryKey() == VeilreachDimensions.HUSHLANDS_WORLD ? 20 * 90 : 20 * 45;
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, duration, 0, false, true));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, duration, 0, false, true));
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 20 * 20, 0, false, true));
        serverWorld.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 0.8F, 1.2F);
        user.sendMessage(Text.translatable("item.veilreach.hush_ward_charm.use"), true);
        user.getItemCooldownManager().set(stack, 20 * 30);
        return ActionResult.CONSUME;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
        textConsumer.accept(Text.translatable("item.veilreach.hush_ward_charm.tooltip"));
    }
}
