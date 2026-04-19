package com.iridium.mixins.client.rendering;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.CullingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class AnimationCullingMixin {

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void iridium$skipWhenOffscreen(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide) {
            return;
        }
        if (!IridiumConfig.get().animationCullingEnabled) {
            return;
        }
        if (self.noCulling) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }
        if (self == mc.getCameraEntity()) {
            return;
        }
        if (self.isPassenger() && self.getVehicle() == mc.getCameraEntity()) {
            return;
        }
        if (!CullingHelper.isEntityVisible(self)) {
            ci.cancel();
        }
    }
}
