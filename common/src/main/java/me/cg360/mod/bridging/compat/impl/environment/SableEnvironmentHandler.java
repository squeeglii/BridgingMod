package me.cg360.mod.bridging.compat.impl.environment;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import me.cg360.mod.bridging.compat.impl.SableCompat;
import me.cg360.mod.bridging.compat.type.SpecialBridgingEnvironmentHandler;
import me.cg360.mod.bridging.raytrace.BridgingPreContext;
import me.cg360.mod.bridging.raytrace.Perspective;
import me.cg360.mod.bridging.util.flags.Flags;
import net.minecraft.core.Position;
import net.minecraft.world.entity.player.Player;
import org.joml.*;

import java.util.Optional;

public class SableEnvironmentHandler implements SpecialBridgingEnvironmentHandler {

    // Starting with a simple implementation - change the path for the structure being stood on.
    // A fully featured impl could check all the local structures, but would that get expensive?
    @Override
    public Optional<BridgingPreContext> generatePlacementContextOverride(BridgingPreContext initialContext) {
        Player player = initialContext.player();
        SubLevelAccess targetSubLevel = SableCompanion.INSTANCE.getTrackingSubLevel(player);

        // Not on a sublevel, so don't check bridging for it
        if(targetSubLevel == null) {
            SableCompat.getOpt().ifPresent(SableCompat::nullLastContraptionPose);
            return Optional.empty();
        }

        Position pos = initialContext.cameraPerspective().getPosition();

        return SableCompanion.INSTANCE.runIncludingSubLevels(
                initialContext.level(), pos, true, targetSubLevel,
                (sublevel, block) -> {

                    // Sanity check ig. Should be covered by above I think.
                    if(sublevel == null) {
                        SableCompat.getOpt().ifPresent(SableCompat::nullLastContraptionPose);
                        return Optional.empty();
                    }

                    // calc based on subLevel Logical pose
                    Perspective newPlayer = SableCompat.transformOnPose(initialContext.playerPerspective(), sublevel.logicalPose());
                    Perspective newCamera = SableCompat.transformOnPose(initialContext.cameraPerspective(), sublevel.logicalPose());

                    Optional<SableCompat> optCompat =  SableCompat.getOpt();
                    Flags flags;

                    if(optCompat.isPresent()) {
                        optCompat.get().setLastContraptionPose(sublevel.logicalPose());
                        flags = initialContext.flags().extend(SableCompat.IN_SUB_LEVEL);
                    } else {
                        flags = initialContext.flags().extend(Flags.SKIP_OUTLINE_RENDERING);
                    }

                    return Optional.of(new BridgingPreContext(
                            initialContext.level(),
                            newCamera,
                            newPlayer,
                            initialContext.player(),
                            flags
                    ));
                }
        );
    }
}
