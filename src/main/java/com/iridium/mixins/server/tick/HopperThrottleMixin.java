package com.iridium.mixins.server.tick;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.PerformanceMonitor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HopperBlockEntity.class)
public abstract class HopperThrottleMixin {

    @Shadow
    private int cooldownTime;

    @Inject(method = "pushItemsTick", at = @At("RETURN"))
    private static void iridium$extendCooldownOnIdle(
            Level level, BlockPos pos, BlockState state, HopperBlockEntity hopper,
            CallbackInfo ci) {
        IridiumConfig cfg = IridiumConfig.get();
        if (!cfg.hopperThrottleEnabled) {
            return;
        }
        if (hopper == null) {
            return;
        }
        HopperThrottleMixin self = (HopperThrottleMixin) (Object) hopper;
        if (self.cooldownTime > 0) {
            return;
        }
        int idle = cfg.hopperIdleCooldownTicks;
        if (idle < 1) {
            idle = 1;
        }
        self.cooldownTime = idle;
        PerformanceMonitor.onHopperThrottled();
    }
}
