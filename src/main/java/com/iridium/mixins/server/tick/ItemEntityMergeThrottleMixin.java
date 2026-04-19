package com.iridium.mixins.server.tick;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.PerformanceMonitor;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMergeThrottleMixin {

    @Inject(method = "mergeWithNeighbours", at = @At("HEAD"), cancellable = true)
    private void iridium$throttleMergeScan(CallbackInfo ci) {
        if (!IridiumConfig.get().itemEntityMergeThrottleEnabled) {
            return;
        }
        int interval = IridiumConfig.get().itemEntityMergeInterval;
        if (interval <= 1) {
            return;
        }
        ItemEntity self = (ItemEntity) (Object) this;
        if (self.level() == null || self.level().isClientSide) {
            return;
        }
        int age = self.tickCount;
        int id = self.getId();
        if (((age + id) % interval) != 0) {
            PerformanceMonitor.onItemMergeThrottled();
            ci.cancel();
        }
    }
}
