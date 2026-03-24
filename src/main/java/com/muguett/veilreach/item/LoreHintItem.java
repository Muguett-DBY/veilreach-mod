package com.muguett.veilreach.item;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class LoreHintItem extends Item {
    private final String[] tooltipKeys;

    public LoreHintItem(Settings settings, String... tooltipKeys) {
        super(settings);
        this.tooltipKeys = tooltipKeys;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
        for (String tooltipKey : this.tooltipKeys) {
            textConsumer.accept(Text.translatable(tooltipKey));
        }
    }
}
