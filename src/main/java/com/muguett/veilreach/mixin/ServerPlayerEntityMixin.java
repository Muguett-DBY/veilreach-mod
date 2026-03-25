package com.muguett.veilreach.mixin;

import com.muguett.veilreach.progress.HushlandsProgressAccess;
import com.muguett.veilreach.progress.HushlandsProgression;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void veilreach$copyProgress(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ((HushlandsProgressAccess) this).veilreach$copyProgressFrom((HushlandsProgressAccess) oldPlayer);
        HushlandsProgression.clearSyncCache((ServerPlayerEntity) (Object) this);
    }
}
