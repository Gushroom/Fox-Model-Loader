package com.elfmcys.yesstevemodel.client.entity;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.model.ModelAssembly;
import com.elfmcys.yesstevemodel.geckolib3.geo.render.built.GeoModel;
import com.elfmcys.yesstevemodel.client.animation.condition.ArmorConditions;
import com.elfmcys.yesstevemodel.capability.PlayerCapability;
import com.elfmcys.yesstevemodel.geckolib3.core.builder.AnimationController;
import com.elfmcys.yesstevemodel.geckolib3.core.builder.Animation;
import com.elfmcys.yesstevemodel.geckolib3.core.event.predicate.AnimationEvent;
import com.elfmcys.yesstevemodel.molang.runtime.Struct;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;

public class PlayerGeoEntity extends GeoEntity<LocalPlayer> {

    private final PlayerCapability playerCapability;

    @Nullable
    private Struct roamingContainer;

    private int lastRenderDebugTick = -1000;

    public PlayerGeoEntity(LocalPlayer player, PlayerCapability capability) {
        super(player, false);
        this.playerCapability = capability;
        this.roamingContainer = capability.getServerVarContainer();
        YesSteveModel.LOGGER.info(
                "[YSM-FP-ARM] arm-entity-created player={} modelId={} modelHash={} cap={} roaming={}",
                player.getGameProfile().getName(),
                capability.getModelId(),
                capability.getCurrentModelHashId(),
                id(capability),
                id(this.roamingContainer));
        setModelId(capability.getModelId());
    }

    @Override
    public void registerAnimationControllers() {
        getModelAssembly().getAnimationBundle().getArmControllerInstaller().accept(this);
    }

    public PlayerCapability getPlayerCapability() {
        return this.playerCapability;
    }

    @Override
    public boolean shouldSkipAnimation(AnimationEvent<?> event) {
        return true;
    }

    @Override
    public void tickModel() {
        super.tickModel();
        if (this.playerCapability.getModelAssembly() != getModelAssembly()) {
            this.roamingContainer = this.playerCapability.getServerVarContainer();
            YesSteveModel.LOGGER.info(
                    "[YSM-FP-ARM] arm-model-sync player={} modelId={} modelHash={} cap={} roaming={} oldAssembly={} newAssembly={}",
                    getEntity().getGameProfile().getName(),
                    this.playerCapability.getModelId(),
                    this.playerCapability.getCurrentModelHashId(),
                    id(this.playerCapability),
                    id(this.roamingContainer),
                    id(getModelAssembly()),
                    id(this.playerCapability.getModelAssembly()));
            setModelId(this.playerCapability.getModelId());
            return;
        }
        Struct currentRoamingContainer = this.playerCapability.getServerVarContainer();
        if (this.roamingContainer != currentRoamingContainer) {
            YesSteveModel.LOGGER.info(
                    "[YSM-FP-ARM] arm-roaming-container-changed player={} modelId={} modelHash={} cap={} oldRoaming={} newRoaming={} controllersBefore={}",
                    getEntity().getGameProfile().getName(),
                    this.playerCapability.getModelId(),
                    this.playerCapability.getCurrentModelHashId(),
                    id(this.playerCapability),
                    id(this.roamingContainer),
                    id(currentRoamingContainer),
                    getAnimationData().getAnimationControllers().size());
            this.roamingContainer = currentRoamingContainer;
            clearAnimationControllers();
            YesSteveModel.LOGGER.info(
                    "[YSM-FP-ARM] arm-roaming-container-reset player={} modelId={} modelHash={} cap={} roaming={} controllersAfter={}",
                    getEntity().getGameProfile().getName(),
                    this.playerCapability.getModelId(),
                    this.playerCapability.getCurrentModelHashId(),
                    id(this.playerCapability),
                    id(this.roamingContainer),
                    getAnimationData().getAnimationControllers().size());
        }
    }

    public void logRenderState(HumanoidArm arm, int textureIndex, ResourceLocation textureLocation) {
        int tick = getEntity().tickCount;
        if (tick - this.lastRenderDebugTick < 40) {
            return;
        }
        this.lastRenderDebugTick = tick;
        YesSteveModel.LOGGER.info(
                "[YSM-FP-ARM] arm-render player={} tick={} arm={} modelId={} modelHash={} cap={} roaming={} entityAssembly={} capAssembly={} controllers={} textureIndex={} texture={}",
                getEntity().getGameProfile().getName(),
                tick,
                arm,
                this.playerCapability.getModelId(),
                this.playerCapability.getCurrentModelHashId(),
                id(this.playerCapability),
                id(this.playerCapability.getServerVarContainer()),
                id(getModelAssembly()),
                id(this.playerCapability.getModelAssembly()),
                getAnimationData().getAnimationControllers().size(),
                textureIndex,
                textureLocation);
    }

    @Override
    @Nullable
    public GeoEntity.ModelWrapper buildRenderShape(ModelAssembly modelAssembly, boolean isDefault) {
        return this.playerCapability.getRenderShape();
    }

    @Override
    @Nullable
    public AnimationController getAnimationEntries(String str) {
        return getModelAssembly().getAnimationBundle().getAnimationEntries().get(str);
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return this.playerCapability.getTextureLocation();
    }

    @Override
    public float getHeightScale() {
        return getModelAssembly().getModelData().getModelProperties().getHeightScale();
    }

    @Override
    public float getWidthScale() {
        return getModelAssembly().getModelData().getModelProperties().getWidthScale();
    }

    @Override
    @Nullable
    public Animation getAnimation(String str) {
        return getModelAssembly().getAnimationBundle().getArmAnimations().get(str);
    }

    public ArmorConditions getArmModelProcessor() {
        return getModelAssembly().getAnimationBundle().getModelProcessor();
    }

    @Override
    public GeoModel getAnimationProcessor() {
        return getModelAssembly().getAnimationBundle().getArmModel();
    }

    @Override
    public void setupAnim(float seekTime, boolean isFirstPerson) {
        getEvaluationContext().setRoamingProperties(this.playerCapability.getServerVarContainer());
    }

    private static String id(Object object) {
        if (object == null) {
            return "null";
        }
        return object.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(object));
    }
}
