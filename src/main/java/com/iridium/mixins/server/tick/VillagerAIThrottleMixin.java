package com.iridium.mixins.server.tick;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.PerformanceMonitor;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerAIThrottleMixin {

    @Inject(method = "customServerAiStep", at = @At("HEAD"), cancellable = true)
    private void iridium$throttleBrain(CallbackInfo ci) {
        if (!IridiumConfig.get().villagerAiThrottleEnabled) {
            return;
        }
        Villager self = (Villager) (Object) this;
        int radius = IridiumConfig.get().villagerAiActiveRadius;
        if (radius <= 0) {
            return;
        }
        if (self.level() == null) {
            return;
        }
        if (self.level().hasNearbyAlivePlayer(self.getX(), self.getY(), self.getZ(), (double) radius)) {
            return;
        }
        if ((self.tickCount & 3) != 0) {
            PerformanceMonitor.onVillagerBrainThrottled();
            ci.cancel();
        }
    }
}
