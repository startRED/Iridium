package com.iridium.mixins.client.rendering;

import com.iridium.compatibility.ExternalModDetector;
import com.iridium.config.IridiumConfig;
import com.iridium.helpers.MapTextureCache;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapRenderer.class)
public abstract class MapRendererMixin {

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void iridium$skipIfFingerprintMatches(MapId mapId, MapItemSavedData data, CallbackInfo ci) {
        if (mapId == null || data == null) {
            return;
        }
        if (!IridiumConfig.get().mapTextureCacheEnabled) {
            return;
        }
        // ImmediatelyFast otimiza MapRenderer#update nativamente — cede autoridade para evitar double-work.
        if (ExternalModDetector.isImmediatelyFastLoaded()) {
            return;
        }
        if (MapTextureCache.matchesAndUpdate(mapId, data)) {
            ci.cancel();
        }
    }
}
