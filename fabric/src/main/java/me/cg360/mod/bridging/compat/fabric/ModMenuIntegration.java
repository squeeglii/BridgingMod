package me.cg360.mod.bridging.compat.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.cg360.mod.bridging.config.BridgingConfigUI;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> BridgingConfigUI.buildConfig().generateScreen(screen);
    }

}
