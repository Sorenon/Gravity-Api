package com.fusionflux.gravity_api;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.sorenon.mcxr.core.api.MCXRCoreAPI;

public class MCXRCompat {

    private static boolean isMCXRCoreInstalled;

    public static void init() {
        isMCXRCoreInstalled = FabricLoader.getInstance().isModLoaded("mcxr-core");
    }

    public static boolean isPlayerInVR(PlayerEntity player) {
        return isMCXRCoreInstalled && MCXRCoreAPI.doesPlayerHaveMCXRActive(player);
    }
}
