package com.muguett.veilreach.mixin;

import com.muguett.veilreach.progress.HushlandsProgressAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements HushlandsProgressAccess {
    @Unique
    private static final String VEILREACH_PROGRESS_KEY = "VeilreachProgress";
    @Unique
    private static final String WARDEN_KEY = "DefeatedWarden";
    @Unique
    private static final String CATHEDRAL_KEY = "CompletedCathedral";
    @Unique
    private static final String SANCTUM_KEY = "CompletedSanctum";
    @Unique
    private static final String SOVEREIGN_KEY = "DefeatedSovereign";

    @Unique
    private boolean veilreach$defeatedWarden;
    @Unique
    private boolean veilreach$completedCathedral;
    @Unique
    private boolean veilreach$completedSanctum;
    @Unique
    private boolean veilreach$defeatedSovereign;

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void veilreach$writeProgress(WriteView writeView, CallbackInfo ci) {
        WriteView progressView = writeView.get(VEILREACH_PROGRESS_KEY);
        progressView.putBoolean(WARDEN_KEY, veilreach$defeatedWarden);
        progressView.putBoolean(CATHEDRAL_KEY, veilreach$completedCathedral);
        progressView.putBoolean(SANCTUM_KEY, veilreach$completedSanctum);
        progressView.putBoolean(SOVEREIGN_KEY, veilreach$defeatedSovereign);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void veilreach$readProgress(ReadView readView, CallbackInfo ci) {
        if (readView.getOptionalReadView(VEILREACH_PROGRESS_KEY).isEmpty()) {
            return;
        }
        ReadView progressView = readView.getReadView(VEILREACH_PROGRESS_KEY);
        veilreach$defeatedWarden = progressView.getBoolean(WARDEN_KEY, false);
        veilreach$completedCathedral = progressView.getBoolean(CATHEDRAL_KEY, false);
        veilreach$completedSanctum = progressView.getBoolean(SANCTUM_KEY, false);
        veilreach$defeatedSovereign = progressView.getBoolean(SOVEREIGN_KEY, false);
    }

    @Override
    public boolean veilreach$hasDefeatedWarden() {
        return veilreach$defeatedWarden;
    }

    @Override
    public void veilreach$setDefeatedWarden(boolean value) {
        veilreach$defeatedWarden = value;
    }

    @Override
    public boolean veilreach$hasCompletedCathedral() {
        return veilreach$completedCathedral;
    }

    @Override
    public void veilreach$setCompletedCathedral(boolean value) {
        veilreach$completedCathedral = value;
    }

    @Override
    public boolean veilreach$hasCompletedSanctum() {
        return veilreach$completedSanctum;
    }

    @Override
    public void veilreach$setCompletedSanctum(boolean value) {
        veilreach$completedSanctum = value;
    }

    @Override
    public boolean veilreach$hasDefeatedSovereign() {
        return veilreach$defeatedSovereign;
    }

    @Override
    public void veilreach$setDefeatedSovereign(boolean value) {
        veilreach$defeatedSovereign = value;
    }

    @Override
    public void veilreach$copyProgressFrom(HushlandsProgressAccess other) {
        veilreach$defeatedWarden = other.veilreach$hasDefeatedWarden();
        veilreach$completedCathedral = other.veilreach$hasCompletedCathedral();
        veilreach$completedSanctum = other.veilreach$hasCompletedSanctum();
        veilreach$defeatedSovereign = other.veilreach$hasDefeatedSovereign();
    }
}
