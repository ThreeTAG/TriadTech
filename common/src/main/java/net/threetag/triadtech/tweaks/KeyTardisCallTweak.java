package net.threetag.triadtech.tweaks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.threetag.triadtech.upgrade.TTUpgrades;
import org.jetbrains.annotations.Nullable;
import whocraft.tardis_refined.common.capability.tardis.TardisLevelOperator;
import whocraft.tardis_refined.common.items.KeyItem;
import whocraft.tardis_refined.common.tardis.TardisNavLocation;
import whocraft.tardis_refined.common.tardis.manager.TardisPilotingManager;
import whocraft.tardis_refined.common.util.DimensionUtil;
import whocraft.tardis_refined.common.util.Platform;
import whocraft.tardis_refined.common.util.PlayerUtil;
import whocraft.tardis_refined.constants.ModMessages;
import whocraft.tardis_refined.registry.TRBlockRegistry;

import java.util.ArrayList;
import java.util.Optional;

public class KeyTardisCallTweak {

    public static InteractionResultHolder<ItemStack> rightClick(Player player, Level level, InteractionHand hand, @Nullable BlockPos pos) {
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.isEmpty() && stack.getItem() instanceof KeyItem && level instanceof ServerLevel serverLevel) {
            ArrayList<ResourceKey<Level>> keyChain = KeyItem.getKeychain(stack);

            if (!keyChain.isEmpty()) {
                pos = findSpot(level, pos, player.blockPosition());

                ResourceKey<Level> dimension = keyChain.get(0);
                if (pos != null && DimensionUtil.isAllowedDimension(level.dimension())) {
                    ServerLevel tardisLevel = Platform.getServer().getLevel(dimension);
                    Optional<TardisLevelOperator> operatorOptional = TardisLevelOperator.get(tardisLevel);

                    if (level.getBlockState(pos).is(TRBlockRegistry.LANDING_PAD.get()) || operatorOptional.isEmpty() || !operatorOptional.get().getUpgradeHandler().isUpgradeUnlocked(TTUpgrades.EMERGENCY_EXIT.get())) {
                        return InteractionResultHolder.pass(stack);
                    }

                    TardisLevelOperator operator = operatorOptional.get();
                    TardisPilotingManager pilotManager = operator.getPilotingManager();

                    if (pilotManager.beginFlight(true) && !pilotManager.isInRecovery()) {
                        pilotManager.setTargetLocation(new TardisNavLocation(pos.above(), player.getDirection().getOpposite(), serverLevel));
                        level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                        PlayerUtil.sendMessage(player, Component.translatable(ModMessages.TARDIS_IS_ON_THE_WAY), true);
                        stack.shrink(1);
                        return InteractionResultHolder.success(stack);
                    }
                }
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    public static BlockPos findSpot(Level level, @Nullable BlockPos pos, BlockPos playerPos) {
        if (pos != null && level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above())) {
            return pos;
        } else {
            var random = RandomSource.create();
            int tries = 0;
            pos = playerPos.offset((int) (random.nextFloat() - 0.5F * 10F), (int) (random.nextFloat() - 0.5F * 10F), (int) (random.nextFloat() - 0.5F * 10F));

            while ((!level.isEmptyBlock(pos) || !level.isEmptyBlock(pos.above())) && tries < 30) {
                pos = playerPos.offset((int) (random.nextFloat() - 0.5F * 10F), (int) (random.nextFloat() - 0.5F * 10F), (int) (random.nextFloat() - 0.5F * 10F));
                tries++;
            }

            if (!level.isEmptyBlock(pos) || !level.isEmptyBlock(pos.above())) {
                return null;
            }

            while (level.isEmptyBlock(pos.below()) && pos.getY() > level.getMinBuildHeight()) {
                pos = pos.below();
            }

            return pos;
        }
    }

}
