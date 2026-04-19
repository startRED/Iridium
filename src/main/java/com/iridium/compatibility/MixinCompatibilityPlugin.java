package com.iridium.compatibility;

import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class MixinCompatibilityPlugin implements IMixinConfigPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger("Iridium");

    // Mixins desativados quando Sodium está presente. Vazio enquanto o plano ativo
    // (T1+T2) não inclui mixins que reescrevem o caminho de render do Sodium.
    // Adicionar aqui o nome totalmente qualificado de qualquer mixin que toque
    // WorldRenderer, LightmapTextureManager ou ChunkRenderRegion no futuro.
    private static final Set<String> SODIUM_INCOMPATIBLE_MIXINS = Set.of();

    private boolean sodiumLoaded;

    @Override
    public void onLoad(String mixinPackage) {
        this.sodiumLoaded = SodiumDetector.isSodiumLoaded();
        LOGGER.info("MixinCompatibilityPlugin carregado para '{}' (Sodium presente: {}).",
                mixinPackage, this.sodiumLoaded);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (this.sodiumLoaded && SODIUM_INCOMPATIBLE_MIXINS.contains(mixinClassName)) {
            LOGGER.info("Mixin '{}' desativado: conflita com Sodium.", mixinClassName);
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass,
                         String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass,
                          String mixinClassName, IMixinInfo mixinInfo) {
    }
}
