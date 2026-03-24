package com.muguett.veilreach.item;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class LoreHintItem extends Item {
    private final String[] tooltipKeys;
    private final boolean hasGlint;

    public LoreHintItem(Settings settings, String... tooltipKeys) {
        this(settings, false, tooltipKeys);
    }

    public LoreHintItem(Settings settings, boolean hasGlint, String... tooltipKeys) {
        super(settings);
        this.hasGlint = hasGlint;
        this.tooltipKeys = tooltipKeys;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return this.hasGlint || super.hasGlint(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
        for (String tooltipKey : this.tooltipKeys) {
            textConsumer.accept(Text.translatable(tooltipKey));
        }
    }
}
