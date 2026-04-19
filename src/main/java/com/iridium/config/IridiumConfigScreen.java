package com.iridium.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

@Environment(EnvType.CLIENT)
public final class IridiumConfigScreen extends Screen implements ModMenuApi {

    private static final int ROW_WIDTH = 260;
    private static final int ROW_HEIGHT = 24;
    private static final int WIDGET_WIDTH = 240;
    private static final int WIDGET_HEIGHT = 20;

    private final Screen parent;

    public IridiumConfigScreen() {
        this(null);
    }

    public IridiumConfigScreen(Screen parent) {
        super(Component.translatable("iridium.config.title"));
        this.parent = parent;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return IridiumConfigScreen::new;
    }

    @Override
    protected void init() {
        int listTop = 32;
        int listBottom = height - 60;
        OptionList list = addRenderableWidget(
                new OptionList(minecraft, width, listBottom - listTop, listTop, ROW_HEIGHT));

        IridiumConfig c = IridiumConfig.get();

        list.addBool("iridium.config.entityCulling", c.entityCullingEnabled,
                v -> c.entityCullingEnabled = v);
        list.addBool("iridium.config.blockEntityCulling", c.blockEntityCullingEnabled,
                v -> c.blockEntityCullingEnabled = v);
        list.addBool("iridium.config.animationCulling", c.animationCullingEnabled,
                v -> c.animationCullingEnabled = v);
        list.addBool("iridium.config.nametagCache", c.nametagCacheEnabled,
                v -> c.nametagCacheEnabled = v);
        list.addBool("iridium.config.beaconBeamCulling", c.beaconBeamCullingEnabled,
                v -> c.beaconBeamCullingEnabled = v);
        list.addBool("iridium.config.heldItemCulling", c.heldItemCullingEnabled,
                v -> c.heldItemCullingEnabled = v);
        list.addBool("iridium.config.mapTextureCache", c.mapTextureCacheEnabled,
                v -> c.mapTextureCacheEnabled = v);

        list.addBool("iridium.config.particleCulling", c.particleCullingEnabled,
                v -> c.particleCullingEnabled = v);
        list.addBool("iridium.config.particleManagerCap", c.particleManagerCapEnabled,
                v -> c.particleManagerCapEnabled = v);
        list.addInt("iridium.config.particleMax", c.particleMax, 100, 10000,
                v -> c.particleMax = v);
        list.addBool("iridium.config.fireworkParticleCap", c.fireworkParticleCapEnabled,
                v -> c.fireworkParticleCapEnabled = v);
        list.addInt("iridium.config.fireworkParticleMax", c.fireworkParticleMax, 50, 2000,
                v -> c.fireworkParticleMax = v);

        list.addBool("iridium.config.dynamicFps", c.dynamicFpsEnabled,
                v -> c.dynamicFpsEnabled = v);
        list.addInt("iridium.config.dynamicFpsUnfocusedFps", c.dynamicFpsUnfocusedFps, 1, 120,
                v -> c.dynamicFpsUnfocusedFps = v);
        list.addInt("iridium.config.dynamicFpsMinimizedFps", c.dynamicFpsMinimizedFps, 1, 60,
                v -> c.dynamicFpsMinimizedFps = v);

        list.addBool("iridium.config.hopperThrottle", c.hopperThrottleEnabled,
                v -> c.hopperThrottleEnabled = v);
        list.addInt("iridium.config.hopperIdleCooldownTicks", c.hopperIdleCooldownTicks, 1, 20,
                v -> c.hopperIdleCooldownTicks = v);
        list.addBool("iridium.config.explosionCache", c.explosionCacheEnabled,
                v -> c.explosionCacheEnabled = v);

        list.addBool("iridium.config.randomTickThrottle", c.randomTickThrottleEnabled,
                v -> c.randomTickThrottleEnabled = v);
        list.addInt("iridium.config.randomTickActiveRadius", c.randomTickActiveRadius, 16, 256,
                v -> c.randomTickActiveRadius = v);

        list.addBool("iridium.config.itemEntityMergeThrottle", c.itemEntityMergeThrottleEnabled,
                v -> c.itemEntityMergeThrottleEnabled = v);
        list.addInt("iridium.config.itemEntityMergeInterval", c.itemEntityMergeInterval, 1, 20,
                v -> c.itemEntityMergeInterval = v);

        list.addBool("iridium.config.villagerAiThrottle", c.villagerAiThrottleEnabled,
                v -> c.villagerAiThrottleEnabled = v);
        list.addInt("iridium.config.villagerAiActiveRadius", c.villagerAiActiveRadius, 16, 128,
                v -> c.villagerAiActiveRadius = v);

        list.addBool("iridium.config.fireSpreadThrottle", c.fireSpreadThrottleEnabled,
                v -> c.fireSpreadThrottleEnabled = v);
        list.addInt("iridium.config.fireSpreadActiveRadius", c.fireSpreadActiveRadius, 16, 128,
                v -> c.fireSpreadActiveRadius = v);

        list.addBool("iridium.config.projectileTickThrottle", c.projectileTickThrottleEnabled,
                v -> c.projectileTickThrottleEnabled = v);
        list.addDouble("iridium.config.projectileFarTickDistance", c.projectileFarTickDistance, 16, 256,
                v -> c.projectileFarTickDistance = v);

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
                .bounds(width / 2 - 100, height - 28, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        IridiumConfig.save();
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }

    private static final class OptionList extends ContainerObjectSelectionList<OptionEntry> {

        OptionList(Minecraft mc, int width, int height, int y, int itemHeight) {
            super(mc, width, height, y, itemHeight);
        }

        void addBool(String key, boolean current, Consumer<Boolean> setter) {
            CycleButton<Boolean> button = CycleButton.onOffBuilder(current)
                    .create(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT,
                            Component.translatable(key),
                            (b, v) -> {
                                setter.accept(v);
                                IridiumConfig.save();
                            });
            addEntry(new OptionEntry(button));
        }

        void addInt(String key, int current, int min, int max, IntConsumer setter) {
            addEntry(new OptionEntry(new IntSlider(key, current, min, max, setter)));
        }

        void addDouble(String key, double current, int min, int max, DoubleConsumer setter) {
            addEntry(new OptionEntry(new IntSlider(key, (int) Math.round(current), min, max,
                    v -> setter.accept((double) v))));
        }

        @Override
        public int getRowWidth() {
            return ROW_WIDTH;
        }

        @Override
        protected int getScrollbarPosition() {
            return width / 2 + ROW_WIDTH / 2 + 6;
        }
    }

    private static final class OptionEntry extends ContainerObjectSelectionList.Entry<OptionEntry> {

        private final AbstractWidget widget;

        OptionEntry(AbstractWidget widget) {
            this.widget = widget;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(widget);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(widget);
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int rowWidth, int rowHeight,
                           int mouseX, int mouseY, boolean hovering, float partialTick) {
            widget.setX(left + (rowWidth - WIDGET_WIDTH) / 2);
            widget.setY(top);
            widget.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    private static final class IntSlider extends AbstractSliderButton {

        private final String labelKey;
        private final int min;
        private final int max;
        private final IntConsumer setter;

        IntSlider(String labelKey, int current, int min, int max, IntConsumer setter) {
            super(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT,
                    Component.empty(),
                    max == min ? 0.0 : (double) (current - min) / (double) (max - min));
            this.labelKey = labelKey;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable("iridium.config.slider",
                    Component.translatable(labelKey), currentValue()));
        }

        @Override
        protected void applyValue() {
            setter.accept(currentValue());
            IridiumConfig.save();
        }

        private int currentValue() {
            return (int) Math.round(min + value * (max - min));
        }
    }
}
