package com.elfmcys.yesstevemodel.capability.neoforge;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.AuthModelsCapability;
import com.elfmcys.yesstevemodel.capability.ModelInfoCapability;
import com.elfmcys.yesstevemodel.capability.ProjectileModelCapability;
import com.elfmcys.yesstevemodel.capability.StarModelsCapability;
import com.elfmcys.yesstevemodel.capability.VehicleModelCapability;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class NeoForgeCapabilityTypes {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, YesSteveModel.MOD_ID);

    public static final Supplier<AttachmentType<ModelInfoCapability>> MODEL_INFO =
            ATTACHMENTS.register("model_info", () -> AttachmentType.builder(ModelInfoCapability::new).build());
    public static final Supplier<AttachmentType<AuthModelsCapability>> AUTH_MODELS =
            ATTACHMENTS.register("auth_models", () -> AttachmentType.builder(AuthModelsCapability::new).build());
    public static final Supplier<AttachmentType<StarModelsCapability>> STAR_MODELS =
            ATTACHMENTS.register("star_models", () -> AttachmentType.builder(StarModelsCapability::new).build());
    public static final Supplier<AttachmentType<ProjectileModelCapability>> PROJECTILE_MODEL =
            ATTACHMENTS.register("projectile_model", () -> AttachmentType.builder(holder -> new ProjectileModelCapability()).build());
    public static final Supplier<AttachmentType<VehicleModelCapability>> VEHICLE_MODEL =
            ATTACHMENTS.register("vehicle_model", () -> AttachmentType.builder(holder -> new VehicleModelCapability()).build());

    private NeoForgeCapabilityTypes() {
    }

    public static void register(IEventBus modBus) {
        ATTACHMENTS.register(modBus);
    }
}
