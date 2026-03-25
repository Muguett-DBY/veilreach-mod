package com.muguett.veilreach.item;

import com.muguett.veilreach.entity.LittleBirdEntity;
import com.muguett.veilreach.registry.VeilreachEntities;

public final class LittleBirdItem extends VehicleDeployItem {
    public LittleBirdItem(Settings settings) {
        super(settings.maxCount(1), world -> new LittleBirdEntity(VeilreachEntities.LITTLE_BIRD, world), 1.1D,
                "item.veilreach.little_bird.tooltip",
                "item.veilreach.little_bird.tooltip2",
                "item.veilreach.little_bird.tooltip3");
    }
}
