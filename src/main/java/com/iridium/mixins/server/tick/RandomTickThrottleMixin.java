package com.iridium.mixins.server.tick;

import com.iridium.config.IridiumConfig;
import com.iridium.helpers.PerformanceMonitor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ServerLevel.class)
public abstract class RandomTickThrottleMixin {

    @Unique
    private static final LevelChunkSection[] IRIDIUM$EMPTY_SECTIONS = new LevelChunkSection[0];

    /**
     * Throttle limitado ao bloco "tickBlocks" do tickChunk: redirecionando
     * chunk.getSections() para um array vazio, o for-loop de random ticks
     * não itera, mas thunder e ice/snow (acima desse ponto) seguem ativos.
     * Preserva escopo do item 3.9.
     */
    @Redirect(
            method = "tickChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;getSections()[Lnet/minecraft/world/level/chunk/LevelChunkSection;"
            ),
            require = 1, allow = 1
    )
    private LevelChunkSection[] iridium$skipRandomTickSections(LevelChunk chunk) {
        if (!IridiumConfig.get().randomTickThrottleEnabled || chunk == null) {
            return chunk == null ? IRIDIUM$EMPTY_SECTIONS : chunk.getSections();
        }
        ServerLevel self = (ServerLevel) (Object) this;
        int radius = IridiumConfig.get().randomTickActiveRadius;
        if (radius <= 0 || iridium$hasPlayerNearby(self, chunk.getPos(), radius)) {
            return chunk.getSections();
        }
        PerformanceMonitor.onRandomTickSkipped();
        return IRIDIUM$EMPTY_SECTIONS;
    }

    @Unique
    private static boolean iridium$hasPlayerNearby(ServerLevel level, ChunkPos chunkPos, int radius) {
        double centerX = (double) chunkPos.getMinBlockX() + 8.0;
        double centerZ = (double) chunkPos.getMinBlockZ() + 8.0;
        double radiusSq = (double) radius * (double) radius;
        List<? extends Player> players = level.players();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (p == null) {
                continue;
            }
            double dx = p.getX() - centerX;
            double dz = p.getZ() - centerZ;
            if (dx * dx + dz * dz <= radiusSq) {
                return true;
            }
        }
        return false;
    }
}
