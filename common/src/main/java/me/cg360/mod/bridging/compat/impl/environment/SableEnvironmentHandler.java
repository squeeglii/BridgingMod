package me.cg360.mod.bridging.compat.impl.environment;

import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.type.SpecialBridgingEnvironmentHandler;
import me.cg360.mod.bridging.raytrace.BridgingPreContext;
import me.cg360.mod.bridging.raytrace.Perspective;
import net.minecraft.core.Position;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class SableEnvironmentHandler implements SpecialBridgingEnvironmentHandler {

    // Starting with a simple implementation - change the path for the structure being stood on.
    // A fully featured impl could check all the local structures, but would that get expensive?
    @Override
    public Optional<BridgingPreContext> generatePlacementContextOverride(BridgingPreContext initialContext) {
        Player player = initialContext.player();

        SubLevelAccess targetSubLevel = SableCompanion.INSTANCE.getTrackingSubLevel(player);

        // Not on a sublevel, so don't check bridging for it
        if(targetSubLevel == null)
            return Optional.empty();

        BridgingMod.getLogger().info("SubLevel check pass!");

        Position pos = initialContext.perspective().getPosition();

        return SableCompanion.INSTANCE.runIncludingSubLevels(
                initialContext.level(), pos, true, targetSubLevel,
                (sublevel, block) -> {

                    if (sublevel == null)
                        return Optional.empty(); // Sanity check ig. Should be covered by above I think.

                    // calc based on subLevel Logical pose
                    Perspective newPerspective = new Perspective(

                    );

                    return Optional.of(new BridgingPreContext(
                            initialContext.level(),
                            newPerspective,
                            initialContext.player()
                    )); // okay it'll be in this
                }
        );
    }
}
