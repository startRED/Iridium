package com.iridium.mixins.server.tick;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.PerformanceMonitor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public abstract class FireSpreadThrottleMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void iridium$throttleIfNoPlayerNearby(
            BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
            CallbackInfo ci) {
        if (!IridiumConfig.get().fireSpreadThrottleEnabled) {
            return;
        }
        int radius = IridiumConfig.get().fireSpreadActiveRadius;
        if (radius <= 0) {
            return;
        }
        if (level == null || pos == null) {
            return;
        }
        if (level.hasNearbyAlivePlayer(
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, (double) radius)) {
            return;
        }
        int delay = 40 + random.nextInt(41);
        level.scheduleTick(pos, (Block) (Object) this, delay);
        PerformanceMonitor.onFireSpreadThrottled();
        ci.cancel();
    }
}
