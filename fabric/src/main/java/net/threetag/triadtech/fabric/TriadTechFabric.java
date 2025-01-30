package net.threetag.triadtech.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResult;
import net.threetag.triadtech.TriadTech;
import net.threetag.triadtech.tweaks.KeyTardisCallTweak;
import whocraft.tardis_refined.registry.TRBlockRegistry;

public final class TriadTechFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        TriadTech.init();

        UseItemCallback.EVENT.register((player, level, hand) -> KeyTardisCallTweak.rightClick(player, level, hand, null));
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (level.getBlockState(hitResult.getBlockPos()).getBlock() != TRBlockRegistry.GLOBAL_SHELL_BLOCK.get() && level.getBlockState(hitResult.getBlockPos()).getBlock() != TRBlockRegistry.LANDING_PAD.get()) {
                return KeyTardisCallTweak.rightClick(player, level, hand, hitResult.getBlockPos().above()).getResult();
            }
            return InteractionResult.PASS;
        });
    }
}
