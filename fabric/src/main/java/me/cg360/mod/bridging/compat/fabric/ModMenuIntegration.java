package me.cg360.mod.bridging.compat.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.cg360.mod.bridging.config.BridgingConfig;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.config.BridgingConfigUI;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> BridgingConfigUI.buildConfig().generateScreen(screen);
    }

}
