package de.zekro.fdrpc;

import de.zekro.fdrpc.config.Config;
import de.zekro.fdrpc.rpc.RPCHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

/**
 * ForgeDiscordRPC Forge Minecraft Modification.
 * @author zekro (Ringo Hoffmann)
 */
@Mod(modid = ForgeDiscordRPC.MOD_ID, name = ForgeDiscordRPC.NAME, version = ForgeDiscordRPC.VERSION)
public class ForgeDiscordRPC {
    public static final String MOD_ID = "fdrpc";
    public static final String NAME = "ForgeDiscordRPC";
    public static final String VERSION = "1.2.0";

    public static KeyBinding toggleKeyBinding = new KeyBinding("key.toggleactive", Keyboard.KEY_NONE, "key.category.main");

    private static Logger logger;
    private static Config config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Config(event);
        config.init();

        logger = event.getModLog();

        RPCHandler.connect();
        RPCHandler.setInitializing();
        RPCHandler.updatePresence();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientRegistry.registerKeyBinding(toggleKeyBinding);
        RPCHandler.setMainMenu();
        RPCHandler.updatePresence();
    }

    public static Config getConfig() {
        return config;
    }

    public static Logger getLogger() {
        return logger;
    }
}
