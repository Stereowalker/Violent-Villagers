package com.stereowalker.violentvillagers.mixin;

import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.violentvillagers.ViolentVillagers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends AbstractChestBlock<ChestBlockEntity> implements SimpleWaterloggedBlock {

	protected ChestBlockMixin(Properties properties, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier) {
		super(properties, supplier);
	}

	@Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;angerNearbyPiglins(Lnet/minecraft/world/entity/player/Player;Z)V"))
	public void use_inject(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		upsetNearbyVillagers(player, pos, false);
	}
	
    private static void upsetNearbyVillagers(Player player, BlockPos pos, boolean angerOnlyIfCanSee) {
        List<Villager> list = player.level.getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(16.0));
        list.stream().filter(villager -> !angerOnlyIfCanSee || BehaviorUtils.canSee(villager, player)).forEach(villager -> {
        	Brain<Villager> brain = villager.getBrain();
        	if (brain.getMemory(MemoryModuleType.HOME).isPresent() || brain.getMemory(MemoryModuleType.JOB_SITE).isPresent()) {
        		if (brain.getMemory(MemoryModuleType.HOME).get().pos().closerThan(pos, 10.0D)) {
            		villager.getGossips().add(player.getUUID(), GossipType.MINOR_NEGATIVE, ViolentVillagers.CONFIG.chest_open_loss);
        		} else if (brain.getMemory(MemoryModuleType.JOB_SITE).get().pos().closerThan(pos, 10.0D)) {
            		villager.getGossips().add(player.getUUID(), GossipType.MINOR_NEGATIVE, ViolentVillagers.CONFIG.chest_open_loss);
        		} 
        	}
        });
    }
}
