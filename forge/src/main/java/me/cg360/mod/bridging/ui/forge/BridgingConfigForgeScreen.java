package me.cg360.mod.bridging.ui.forge;

import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.config.BridgingConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BridgingConfigForgeScreen extends Screen {

    private final Screen parent;

    private boolean bridgingEnabled;
    private boolean onlyBridgeWhenCrouched;
    private boolean showCrosshair;
    private boolean showOutline;
    private boolean showOutlineEvenWhenNotBridging;

    public BridgingConfigForgeScreen(Screen parent) {
        super(Component.literal("Bridging Mod Config"));
        this.parent = parent;

        BridgingConfig config = BridgingMod.getConfig();
        this.bridgingEnabled = config.isBridgingEnabled();
        this.onlyBridgeWhenCrouched = config.shouldOnlyBridgeWhenCrouched();
        this.showCrosshair = config.shouldShowCrosshair();
        this.showOutline = config.shouldShowOutline();
        this.showOutlineEvenWhenNotBridging = config.shouldShowOutlineEvenWhenNotBridging();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 6;
        int rowHeight = 24;

        this.addRenderableWidget(CycleButton.onOffBuilder(this.bridgingEnabled)
                .create(centerX - 155, y, 150, 20, Component.literal("Bridging Assist"),
                        (button, value) -> this.bridgingEnabled = value));

        this.addRenderableWidget(CycleButton.onOffBuilder(this.onlyBridgeWhenCrouched)
                .create(centerX + 5, y, 150, 20, Component.literal("Only When Crouched"),
                        (button, value) -> this.onlyBridgeWhenCrouched = value));

        this.addRenderableWidget(CycleButton.onOffBuilder(this.showCrosshair)
                .create(centerX - 155, y + rowHeight, 150, 20, Component.literal("Show Crosshair"),
                        (button, value) -> this.showCrosshair = value));

        this.addRenderableWidget(CycleButton.onOffBuilder(this.showOutline)
                .create(centerX + 5, y + rowHeight, 150, 20, Component.literal("Show Outline"),
                        (button, value) -> this.showOutline = value));

        this.addRenderableWidget(CycleButton.onOffBuilder(this.showOutlineEvenWhenNotBridging)
                .create(centerX - 155, y + (rowHeight * 2), 310, 20, Component.literal("Show Non-Bridging Outline"),
                        (button, value) -> this.showOutlineEvenWhenNotBridging = value));

        this.addRenderableWidget(Button.builder(Component.literal("Save"), button -> this.saveAndClose())
                .bounds(centerX - 155, y + (rowHeight * 4), 150, 20)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> this.onClose())
                .bounds(centerX + 5, y + (rowHeight * 4), 150, 20)
                .build());
    }

    private void saveAndClose() {
        BridgingConfig config = BridgingMod.getConfig();
        config.setBridgingEnabled(this.bridgingEnabled);
        config.setOnlyBridgeWhenCrouched(this.onlyBridgeWhenCrouched);
        config.setShowCrosshair(this.showCrosshair);
        config.setShowOutline(this.showOutline);
        config.setShowOutlineEvenWhenNotBridging(this.showOutlineEvenWhenNotBridging);
        config.save();
        this.onClose();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
