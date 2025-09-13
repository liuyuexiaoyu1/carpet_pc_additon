package com.pigconnon.carpet_pc_addition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import com.pigconnon.carpet_pc_addition.utils.ComponentTranslate;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;

public class CPCAServer implements CarpetExtension, ModInitializer {

    private static final CPCAServer INSTANCE = new CPCAServer();
    public static final String MOD_ID = "carpet_pigconnon_addition";
    public static SettingsManager settingsManager;
    public static final String MOD_VERSION = "1.0-SNAPSHOT";
    public static void loadExtension() {
        CarpetServer.manageExtension(INSTANCE);
    }
    @Override
    public void onInitialize() {
        CPCAServer.loadExtension();
    }
    @Override
    public void onGameStarted() {
        settingsManager = new SettingsManager(MOD_VERSION, MOD_ID, "cpca");
        CarpetServer.settingsManager.parseSettingsClass(CPCASettings.class);

    }
    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return ComponentTranslate.getTranslationFromResourcePath(lang);
    }

}
