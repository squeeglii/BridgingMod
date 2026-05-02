package me.cg360.mod.bridging.compat.impl.environment;

import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.type.SpecialBridgingEnvironmentHandler;
import me.cg360.mod.bridging.raytrace.BridgingPreContext;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;

import static com.ibm.icu.text.PluralRules.Operand.e;

public class SableEnvironmentHandler implements SpecialBridgingEnvironmentHandler {

    // Starting with a simple implementation - change the path for the structure being stood on.
    // A fully featured impl could check all the local structures, but would that get expensive?
    @Override
    public Optional<BridgingPreContext> generatePlacementContextOverride(BridgingPreContext initialContext) {
        Player player = initialContext.player();

        ClientSubLevelAccess subLevel = SableCompanion.INSTANCE.getContainingClient(player);
        //SubLevelAccess subLevel = SableCompanion.INSTANCE.getTrackingSubLevel(player);

        // Not on a sublevel, so don't check bridging for it
        if(subLevel == null)
            return Optional.empty();

        Position pos = initialContext.perspective().getPosition();

        return SableCompanion.INSTANCE.runIncludingSubLevels(
                initialContext.level(), pos, true, subLevel,
                (sublevel, block) -> {
                    return null; // okay it'll be in this
                }
        );
    }
}
