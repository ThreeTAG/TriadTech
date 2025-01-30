package net.threetag.triadtech.forge;

import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.threetag.triadtech.TriadTech;
import net.threetag.triadtech.tweaks.KeyTardisCallTweak;
import whocraft.tardis_refined.registry.TRBlockRegistry;

@Mod.EventBusSubscriber(modid = TriadTech.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TriadTechEventHandler {

    @SubscribeEvent
    public static void rightClickItem(PlayerInteractEvent.RightClickItem e) {
        var result = KeyTardisCallTweak.rightClick(e.getEntity(), e.getLevel(), e.getHand(), null);

        if (result.getResult() != InteractionResult.PASS) {
            e.setCancellationResult(result.getResult());
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock e) {
        if (e.getLevel().getBlockState(e.getPos()).getBlock() != TRBlockRegistry.GLOBAL_SHELL_BLOCK.get() && e.getLevel().getBlockState(e.getPos()).getBlock() != TRBlockRegistry.LANDING_PAD.get()) {
            var result = KeyTardisCallTweak.rightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getPos().above());

            if (result.getResult() != InteractionResult.PASS) {
                e.setCancellationResult(result.getResult());
                e.setCanceled(true);
            }
        }
    }

}
