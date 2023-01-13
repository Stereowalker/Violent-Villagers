/*
 * Decompiled with CFR 0.0.9 (FabricMC cc05e23f).
 */
package com.stereowalker.violentvillagers.world.entity.ai.sensing;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestVisibleLivingEntitySensor;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

public class VillagerAttackablesSensor extends NearestVisibleLivingEntitySensor {
    public static final float TARGET_DETECTION_DISTANCE = 8.0f;

    @Override
    protected boolean isMatchingEntity(LivingEntity attacker, LivingEntity target) {
    	Villager villager = (Villager)attacker;
    	if(target instanceof Player) {
    		Player player = (Player)target;
    		return isSurvival(player) && isClose(attacker, target) && isReputationTooLow(player, villager);
    	} else return false;
    }
    
    private boolean isReputationTooLow(Player player, Villager villager) {
    	int reputation = villager.getPlayerReputation(player);
    	return reputation < 0;
    }
    
    private boolean isSurvival(Player player) {
    	return !player.isSpectator() && !player.isCreative();
    }

    private boolean isClose(LivingEntity attacker, LivingEntity target) {
        return target.distanceToSqr(attacker) <= 64.0;
    }

    @Override
    protected MemoryModuleType<LivingEntity> getMemory() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }
}

