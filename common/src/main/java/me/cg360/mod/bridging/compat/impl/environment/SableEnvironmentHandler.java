package me.cg360.mod.bridging.compat.impl.environment;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.type.SpecialBridgingEnvironmentHandler;
import me.cg360.mod.bridging.raytrace.BridgingPreContext;
import me.cg360.mod.bridging.raytrace.Perspective;
import net.minecraft.core.Position;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;

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

        Position pos = initialContext.cameraPerspective().getPosition();

        return SableCompanion.INSTANCE.runIncludingSubLevels(
                initialContext.level(), pos, true, targetSubLevel,
                (sublevel, block) -> {

                    if (sublevel == null)
                        return Optional.empty(); // Sanity check ig. Should be covered by above I think.

                    // calc based on subLevel Logical pose
                    Perspective newPlayer = inSublevel(initialContext.playerPerspective(), sublevel);
                    Perspective newCamera = inSublevel(initialContext.cameraPerspective(), sublevel);

                    BridgingMod.getLogger().info("Camera: {}, {}", newCamera.getPosition(), newCamera.getLookVector());
                    BridgingMod.getLogger().info("Player: {}, {}", newPlayer.getPosition(), newPlayer.getLookVector());
                    BridgingMod.getLogger().info("Sublevel: {}", sublevel.boundingBox());

                    return Optional.of(new BridgingPreContext(
                            initialContext.level(),
                            newCamera,
                            newPlayer,
                            initialContext.player()
                    ));
                }
        );
    }


    public static Perspective inSublevel(Perspective perspective, SubLevelAccess sublevel) {
        // todo: this probs needs work to rotate it around the correct origin. Look into new pose.

        return new Perspective(
                () -> sublevel.logicalPose().transformPosition(perspective.getPosition()),
                () -> perspective.getLookVector().rotate(new Quaternionf(sublevel.logicalPose().orientation()))
        );
    }
}
