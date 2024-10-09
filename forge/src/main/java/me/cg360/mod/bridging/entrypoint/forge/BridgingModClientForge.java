package me.cg360.mod.bridging.entrypoint.forge;

import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.forge.DynamicCrosshairCompat;
import me.cg360.mod.bridging.config.BridgingConfigUI;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.util.Optional;
import java.util.regex.Pattern;

public class BridgingModClientForge {

    private static final String DYNAMIC_CROSSHAIR_MOD = "dynamiccrosshair";

    public BridgingModClientForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerBindings);
    }


    public void init(FMLClientSetupEvent event) {
        BridgingMod.init();

        if(BridgingMod.isConfigSuccessfullyInitialized()) {
            boolean finalIsInvalidYacl = doesYaclVersionCrash();

            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                    new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> {
                        return finalIsInvalidYacl
                                ? new AlertScreen(
                                        parent::onClose,
                                        Component.translatable("config.bridgingmod.broken_yacl.title"),
                                        Component.translatable("config.bridgingmod.broken_yacl.description")
                                  )
                                : BridgingConfigUI.buildConfig().generateScreen(parent);
                    }

                    ));

        }

        if(ModList.get().isLoaded(DYNAMIC_CROSSHAIR_MOD))
            InterModComms.sendTo(DYNAMIC_CROSSHAIR_MOD, "register_api", DynamicCrosshairCompat::new);
    }

    // i cba - this code looks gross but it works.
    private static boolean doesYaclVersionCrash() {
        boolean isInvalidYacl = false;

        Optional<? extends ModContainer> yaclContainer = ModList.get().getModContainerById("yet_another_config_lib_v3");

        try {
            // Some versions of YACL crash on 1.20.1
            if (yaclContainer.isPresent()) {
                ArtifactVersion yaclVersion = yaclContainer.get().getModInfo().getVersion();
                String yaclVersionString = yaclVersion.getQualifier(); // getMajor, Minor, etc were all 0???
                String coreVersion = yaclVersionString.split(Pattern.quote("+"))[0];
                String[] versionComponents = coreVersion.split(Pattern.quote("."));

                if(versionComponents.length != 3) return false; // malformed version I guess?

                int[] version = new int[] {
                        Integer.parseInt(versionComponents[0]),
                        Integer.parseInt(versionComponents[1]),
                        Integer.parseInt(versionComponents[2])
                };

                BridgingMod.getLogger().info("Running YACL version check on v%s.%s.%s (%s)".formatted(
                        version[0], version[1], version[2],
                        yaclVersionString
                ));

                if (version[0] == 3) {

                    // YACL 3.5.0 is broken. This code was written before any 3.5.1
                    if (version[1] == 5 && version[2] == 0) {
                        BridgingMod.getLogger().warn("YACL version found is a known crasher! Disabling config. Please use YACL v3.4.2");
                        isInvalidYacl = true;
                    }

                    // 3.4.x is WEIRD. 3.4.0? Crash! 3.4.3? Crash! 3.4.2? yeah sure.
                    if (version[1] == 4 && version[2] != 2) {
                        BridgingMod.getLogger().warn("YACL version found is a known crasher! Disabling config. Please use YACL v3.4.2");
                        isInvalidYacl = true;
                    }
                }
            } else {
                BridgingMod.getLogger().warn("YACL Container not found! Unable to run version check.");
            }
        } catch (Exception err) {
            BridgingMod.getLogger().error("Unable to run YACL version check due to exception", err);
        }

        return isInvalidYacl;
    }


    public void registerBindings(RegisterKeyMappingsEvent event) {
        BridgingKeyMappings.forEachKeybindingDo(event::register);
    }

}
