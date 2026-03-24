package com.muguett.veilreach;

import com.muguett.veilreach.registry.VeilreachBlockItems;
import com.muguett.veilreach.registry.VeilreachBlocks;
import com.muguett.veilreach.registry.VeilreachEntities;
import com.muguett.veilreach.registry.VeilreachItemGroups;
import com.muguett.veilreach.registry.VeilreachItems;
import com.muguett.veilreach.world.biome.HushlandsBiomes;
import com.muguett.veilreach.world.dimension.VeilreachDimensions;
import com.muguett.veilreach.world.gen.HushlandsWorldgen;
import com.muguett.veilreach.world.HushlandsHazardManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VeilreachMod implements ModInitializer {
    public static final String MOD_ID = "veilreach";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        VeilreachDimensions.initialize();
        VeilreachBlocks.initialize();
        VeilreachEntities.initialize();
        VeilreachItems.initialize();
        VeilreachBlockItems.initialize();
        VeilreachItemGroups.initialize();
        HushlandsBiomes.initialize();
        HushlandsWorldgen.initialize();
        HushlandsHazardManager.initialize();
        LOGGER.info("Veilreach initialized: the Hushlands are waiting.");
    }
}
