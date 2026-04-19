package com.iridium.mixins.client.rendering;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.NametagCache;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class NametagCacheMixin {

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getDisplayName()Lnet/minecraft/network/chat/Component;"),
            require = 0
    )
    private Component iridium$cachedDisplayName(Entity entity) {
        if (entity == null) {
            return null;
        }
        if (!IridiumConfig.get().nametagCacheEnabled) {
            return entity.getDisplayName();
        }
        return NametagCache.get(entity);
    }
}
