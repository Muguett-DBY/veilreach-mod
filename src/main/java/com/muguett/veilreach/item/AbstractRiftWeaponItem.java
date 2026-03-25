package com.muguett.veilreach.item;

import com.muguett.veilreach.registry.VeilreachSoundEvents;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import java.util.function.Consumer;

public abstract class AbstractRiftWeaponItem extends Item {
    private final Item ammoItem;
    private final int cooldownTicks;
    private final String tooltipKey;
    private final String tooltipKey2;

    protected AbstractRiftWeaponItem(Settings settings, Item ammoItem, int cooldownTicks, String tooltipKey, String tooltipKey2) {
        super(settings.maxCount(1));
        this.ammoItem = ammoItem;
        this.cooldownTicks = cooldownTicks;
        this.tooltipKey = tooltipKey;
        this.tooltipKey2 = tooltipKey2;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown(stack)) {
            return ActionResult.CONSUME;
        }
        if (!world.isClient()) {
            fire(world, user, stack);
            if (!user.isCreative() && hasAmmo(user)) {
                consumeAmmo(user);
            }
            user.getItemCooldownManager().set(stack, cooldownTicks);
        }
        user.swingHand(hand);
        return ActionResult.SUCCESS;
    }

    public Item getAmmoItem() {
        return ammoItem;
    }

    public String getTooltipKey() {
        return tooltipKey;
    }

    public String getTooltipKey2() {
        return tooltipKey2;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
        textConsumer.accept(Text.translatable(tooltipKey));
        textConsumer.accept(Text.translatable(tooltipKey2));
        textConsumer.accept(Text.translatable(ammoTooltipKey()));
    }

    private String ammoTooltipKey() {
        String translationKey = ammoItem.getTranslationKey();
        String path = translationKey.substring(translationKey.indexOf('.') + 1);
        return "item.veilreach." + path + ".tooltip";
    }

    protected abstract void fire(World world, PlayerEntity user, ItemStack stack);

    protected boolean hasAmmo(PlayerEntity player) {
        if (player.isCreative()) {
            return true;
        }
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            if (player.getInventory().getStack(slot).isOf(ammoItem)) {
                return true;
            }
        }
        return false;
    }

    protected boolean consumeAmmo(PlayerEntity player) {
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            ItemStack inventoryStack = player.getInventory().getStack(slot);
            if (!inventoryStack.isOf(ammoItem)) {
                continue;
            }
            inventoryStack.decrement(1);
            if (inventoryStack.isEmpty()) {
                player.getInventory().setStack(slot, ItemStack.EMPTY);
            }
            return true;
        }
        return false;
    }
}
