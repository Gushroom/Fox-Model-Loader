package com.elfmcys.yesstevemodel.neoforge;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.ClientModelManager;
import com.elfmcys.yesstevemodel.client.event.RenderFirstPlayerBackground;
import com.elfmcys.yesstevemodel.client.event.ReplacePlayerHandRenderEvent;
import com.elfmcys.yesstevemodel.client.event.ReplacePlayerRenderEvent;
import com.elfmcys.yesstevemodel.client.gui.ExtraPlayerConfigScreen;
import com.elfmcys.yesstevemodel.client.gui.PlayerModelScreen;
import com.elfmcys.yesstevemodel.client.renderer.AnimationDebugOverlay;
import com.elfmcys.yesstevemodel.client.renderer.ExtraPlayerOverlay;
import com.elfmcys.yesstevemodel.client.renderer.ModelSyncStateOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import rip.ysm.api.client.HudOverlay;

public final class NeoForgeClientEvents {
    private NeoForgeClientEvents() {
    }

    public static void register(IEventBus modBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) ->
                new ExtraPlayerConfigScreen(parent instanceof PlayerModelScreen playerModelScreen ? playerModelScreen : null));

        modBus.addListener(NeoForgeClientEvents::onClientSetup);
        modBus.addListener(NeoForgeClientEvents::onRegisterGuiLayers);

        NeoForge.EVENT_BUS.addListener(NeoForgeClientEvents::onRenderPlayerPre);
        NeoForge.EVENT_BUS.addListener(NeoForgeClientEvents::onRenderArm);
        NeoForge.EVENT_BUS.addListener(NeoForgeClientEvents::onRenderHand);
        NeoForge.EVENT_BUS.addListener(NeoForgeClientEvents::onRenderLevelStage);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }
        event.enqueueWork(ClientModelManager::loadDefaultModel);
    }

    private static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        if (!YesSteveModel.isAvailable()) {
            return;
        }
        HudOverlay debugOverlay = AnimationDebugOverlay.createOverlay();
        HudOverlay loadingOverlay = new ExtraPlayerOverlay();
        HudOverlay syncOverlay = new ModelSyncStateOverlay();
        event.registerAbove(VanillaGuiLayers.DEBUG_OVERLAY, layerId("ysm_debug_info"), (guiGraphics, deltaTracker) -> {
            Minecraft mc = Minecraft.getInstance();
            debugOverlay.render(guiGraphics, mc.font, deltaTracker.getGameTimeDeltaTicks(), guiGraphics.guiWidth(), guiGraphics.guiHeight());
        });
        event.registerAbove(VanillaGuiLayers.DEBUG_OVERLAY, layerId("ysm_extra_player"), (guiGraphics, deltaTracker) -> {
            Minecraft mc = Minecraft.getInstance();
            loadingOverlay.render(guiGraphics, mc.font, deltaTracker.getGameTimeDeltaTicks(), guiGraphics.guiWidth(), guiGraphics.guiHeight());
        });
        event.registerAbove(VanillaGuiLayers.DEBUG_OVERLAY, layerId("ysm_loading_state"), (guiGraphics, deltaTracker) -> {
            Minecraft mc = Minecraft.getInstance();
            syncOverlay.render(guiGraphics, mc.font, deltaTracker.getGameTimeDeltaTicks(), guiGraphics.guiWidth(), guiGraphics.guiHeight());
        });
    }

    private static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (ReplacePlayerRenderEvent.onRenderPlayerPre(event.getEntity(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight())) {
            event.setCanceled(true);
        }
    }

    private static void onRenderArm(RenderArmEvent event) {
        if (ReplacePlayerHandRenderEvent.onRenderArm(event.getPlayer(), event.getArm(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight())) {
            event.setCanceled(true);
        }
    }

    private static void onRenderHand(RenderHandEvent event) {
        RenderFirstPlayerBackground.onRenderHand(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPartialTick());
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            RenderFirstPlayerBackground.resetFrame();
        }
    }

    private static ResourceLocation layerId(String path) {
        return ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, path);
    }
}
