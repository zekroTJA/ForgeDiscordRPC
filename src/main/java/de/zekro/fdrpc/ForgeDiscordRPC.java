package de.zekro.fdrpc;

import de.zekro.fdrpc.config.Config;
import de.zekro.fdrpc.rpc.RPCHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * ForgeDiscordRPC Forge Minecraft Modification.
 * @author zekro (Ringo Hoffmann)
 */
@Mod(modid = ForgeDiscordRPC.MOD_ID, name = ForgeDiscordRPC.NAME, version = ForgeDiscordRPC.VERSION)
public class ForgeDiscordRPC {
    public static final String MOD_ID = "fdrpc";
    public static final String NAME = "ForgeDiscordRPC";
    public static final String VERSION = "1.0.1";

    private static Config config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Config(event);
        config.init();

        RPCHandler.connect();
        RPCHandler.setInitializing();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        RPCHandler.setMainMenu();
    }

    public static Config getConfig() {
        return config;
    }
}
