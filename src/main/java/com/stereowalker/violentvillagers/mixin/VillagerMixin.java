package com.stereowalker.violentvillagers.mixin;

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.stereowalker.violentvillagers.world.entity.ai.sensing.VSensorType;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.EraseMemoryIf;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
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
        Level level = vill.level();
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
     * @param villagerBrain
     */
    @Overwrite
    private void registerBrainGoals(Brain<Villager> villagerBrain) {
        VillagerProfession villagerProfession = this.getVillagerData().getProfession();
        if (this.isBaby()) {
            villagerBrain.setSchedule(Schedule.VILLAGER_BABY);
            villagerBrain.addActivity(Activity.PLAY, VillagerGoalPackages.getPlayPackage(0.5f));
        } else {
            villagerBrain.setSchedule(Schedule.VILLAGER_DEFAULT);
            villagerBrain.addActivityWithConditions(Activity.WORK, VillagerGoalPackages.getWorkPackage(villagerProfession, 0.5f), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
        }
        villagerBrain.addActivity(Activity.CORE, VillagerGoalPackages.getCorePackage(villagerProfession, 0.5f));
        //1.19.2
        //villagerBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0, ImmutableList.of(new StopAttackingIfTargetInvalid<Villager>(VillagerMixin::onStopAttacking), new SetWalkTargetFromAttackTargetIfTargetOutOfReach(/*AxolotlAi::getSpeedModifierChasing*/1.0f), new MeleeAttack(20), new EraseMemoryIf<Villager>(VillagerMixin::continueAttacking, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
        //1.19.3+
        villagerBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0, ImmutableList.of(StopAttackingIfTargetInvalid.create(VillagerMixin::onStopAttacking), SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(/*AxolotlAi::getSpeedModifierChasing*/1.0f), MeleeAttack.create(20), EraseMemoryIf.create(VillagerMixin::continueAttacking, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
        villagerBrain.addActivityWithConditions(Activity.MEET, VillagerGoalPackages.getMeetPackage(villagerProfession, 0.5f), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
        villagerBrain.addActivity(Activity.REST, VillagerGoalPackages.getRestPackage(villagerProfession, 0.5f));
        //1.19.2
        //ImmutableList<? extends Pair<Integer, ? extends Behavior<? super Villager>>> ent = new ImmutableList.Builder<Pair<Integer,? extends Behavior<? super Villager>>>().add(Pair.of(3, new StartAttacking<Villager>(VillagerMixin::findNearestValidAttackTarget))).addAll(VillagerGoalPackages.getIdlePackage(villagerProfession, 0.5f)).build();
        //1.19.3+
        ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super Villager>>> ent = new ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super Villager>>>().add(Pair.of(3, StartAttacking.create(VillagerMixin::findNearestValidAttackTarget))).addAll(VillagerGoalPackages.getIdlePackage(villagerProfession, 0.5f)).build();
        villagerBrain.addActivity(Activity.IDLE, ent);
        villagerBrain.addActivity(Activity.PANIC, VillagerGoalPackages.getPanicPackage(villagerProfession, 0.5f));
        villagerBrain.addActivity(Activity.PRE_RAID, VillagerGoalPackages.getPreRaidPackage(villagerProfession, 0.5f));
        villagerBrain.addActivity(Activity.RAID, VillagerGoalPackages.getRaidPackage(villagerProfession, 0.5f));
        villagerBrain.addActivity(Activity.HIDE, VillagerGoalPackages.getHidePackage(villagerProfession, 0.5f));
        villagerBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        villagerBrain.setDefaultActivity(Activity.IDLE);
        villagerBrain.setActiveActivityIfPossible(Activity.IDLE);
        villagerBrain.updateActivityFromSchedule(this.level().getDayTime(), this.level().getGameTime());
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
        this.level().getProfiler().push("villagerActivityUpdate");
        updateActivity((Villager)(Object)this);
        this.level().getProfiler().pop();
    }
    
    @Shadow public int getPlayerReputation(Player player) {return 0;}
}
