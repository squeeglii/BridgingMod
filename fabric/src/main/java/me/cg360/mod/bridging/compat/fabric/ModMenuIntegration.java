package me.cg360.mod.bridging.compat.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.cg360.mod.bridging.BridgingConfig;
import me.cg360.mod.bridging.BridgingMod;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return BridgingMod.isConfigSuccessfullyInitialized()
                ? screen -> AutoConfig.getConfigScreen(BridgingConfig.class, screen).get()
                : screen -> new AlertScreen(
                        screen::onClose,
                        Component.translatable("bridgingmod.config.failed_integration.title"),
                        Component.translatable("bridgingmod.config.failed_integration.description")
        );
    }

}
