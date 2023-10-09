package com.stereowalker.violentvillagers.mixin;

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.stereowalker.unionlib.util.VersionHelper;
import com.stereowalker.violentvillagers.world.entity.ai.sensing.VSensorType;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager implements ReputationEventHandler, VillagerDataHolder {
	private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, new MemoryModuleType[]{MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY});
    
    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
		super(entityType, level);
	}

    @Overwrite
    public Brain.Provider<Villager> brainProvider() {
        return Brain.provider(MEMORY_TYPES, 
        		new ImmutableList.Builder<SensorType<? extends Sensor<? super Villager>>>()
        		.add(VSensorType.VILLAGER_ATTACKABLES)
        		.addAll(Villager.SENSOR_TYPES).build());
    }
    
	private static boolean continueAttacking(Villager vill) {
		return false;
	}
	
	private static void onStopAttacking(Villager vill, LivingEntity target) {
        Entity entity;
        DamageSource damageSource;
        Level level = VersionHelper.entityLevel(vill);
        if (target.isDeadOrDying() && (damageSource = target.getLastDamageSource()) != null && (entity = damageSource.getEntity()) != null && entity.getType() == EntityType.PLAYER) {
            Player player = (Player)entity;
            List<Player> list = level.getEntitiesOfClass(Player.class, vill.getBoundingBox().inflate(20.0));
            if (list.contains(player)) {
            	//vill.applySupportingEffects(player);
            }
        }
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Villager vill) {
        /*if (AxolotlAi.isBreeding(axolotl)) {
            return Optional.empty();
        }*/
        return vill.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
    }
    
    /**
     * This is registered after Activity.CORE to ensure that fighting is their utmost priority
     */
    @Inject(method = "registerBrainGoals", at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/world/entity/ai/Brain;addActivity(Lnet/minecraft/world/entity/schedule/Activity;Lcom/google/common/collect/ImmutableList;)V"))
    protected void registerBrainGoals1(Brain<Villager> villagerBrain, CallbackInfo ci) {
    	//1.19.2
      //villagerBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0, ImmutableList.of(new StopAttackingIfTargetInvalid<Villager>(VillagerMixin::onStopAttacking), new SetWalkTargetFromAttackTargetIfTargetOutOfReach(/*AxolotlAi::getSpeedModifierChasing*/1.0f), new MeleeAttack(20), new EraseMemoryIf<Villager>(VillagerMixin::continueAttacking, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    	//1.19.3+
    	villagerBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0, ImmutableList.of(VersionHelper.createStopAttackingIfTargetInvalid(VillagerMixin::onStopAttacking), VersionHelper.createSetWalkTargetFromAttackTargetIfTargetOutOfReach(/*AxolotlAi::getSpeedModifierChasing*/1.0f), VersionHelper.createMeleeAttack(20), VersionHelper.createEraseMemoryIf(VillagerMixin::continueAttacking, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
          
    }
    
    /**
     * This is overwrites Activity.IDLE to allow our villager to enter a state of violence later
     */
    @Redirect(method = "registerBrainGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/VillagerGoalPackages;getIdlePackage(Lnet/minecraft/world/entity/npc/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
    protected ImmutableList<?> registerBrainGoals2(VillagerProfession villagerProfession, float speedModifier) {
    	//1.19.2
      //return new ImmutableList.Builder<Pair<Integer,? extends Behavior<? super Villager>>>().add(Pair.of(3, new StartAttacking<Villager>(VillagerMixin::findNearestValidAttackTarget))).addAll(VillagerGoalPackages.getIdlePackage(villagerProfession, speedModifier)).build();
    	//1.19.3+
    	return new ImmutableList.Builder<Pair<Integer,?>>().add(Pair.of(3, VersionHelper.createStartAttacking(VillagerMixin::findNearestValidAttackTarget))).addAll(VillagerGoalPackages.getIdlePackage(villagerProfession, speedModifier)).build();
          
    }
	
    private static void updateActivity(Villager vill) {
        Brain<Villager> brain = vill.getBrain();
        Activity activity = brain.getActiveNonCoreActivity().orElse(null);
        if (brain.getMemory(MemoryModuleType.NEAREST_ATTACKABLE).isPresent()) {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
            //if (activity == Activity.FIGHT && brain.getActiveNonCoreActivity().orElse(null) != Activity.FIGHT) {
            //    brain.setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
            //}
        }
    }

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    protected void customServerAiStep2(CallbackInfo ci) {
        VersionHelper.entityLevel(this).getProfiler().push("villagerActivityUpdate");
        updateActivity((Villager)(Object)this);
        VersionHelper.entityLevel(this).getProfiler().pop();
    }
    
    @Shadow public int getPlayerReputation(Player player) {return 0;}
}
