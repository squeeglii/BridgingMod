package me.cg360.mod.bridging;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;

public class BridgingMod implements ClientModInitializer, ModInitializer {

    public static final ResourceLocation PLACEMENT_ICONS_TEXTURE = ResourceLocation.tryBuild("bridgingmod", "textures/gui/placement_icons.png");

    @Override
    public void onInitializeClient() {

    }

    @Override
    public void onInitialize() {

    }
}
