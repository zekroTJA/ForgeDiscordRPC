package de.zekro.fdrpc.rpc;


import de.zekro.fdrpc.ForgeDiscordRPC;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.callbacks.ErroredCallback;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.arikia.dev.drpc.DiscordRichPresence.Builder;

public class RPCHandler {

    private static final String IMAGE_MAIN_LOGO = "mainicon";

    private static long startTime = 0;

    private static final Object discordCallbackExecutor = new Object() {
        @SubscribeEvent
        public void tickEvent(TickEvent.ClientTickEvent e) {
            DiscordRPC.discordRunCallbacks();
        }
    };

    private static ReadyCallback connectHandler = (user) -> {
        System.out.printf("Discord RPC connected to user account %s#%s (%s)",
                user.username, user.discriminator, user.userId);
    };

    private static ErroredCallback errorHandler = (error, errStr) -> {
        System.err.println(error);
    };

    public static Builder getPresenceBuilder(String state) {
        return new DiscordRichPresence.Builder(state)
                .setBigImage(IMAGE_MAIN_LOGO, "Evolved Combat")
                .setStartTimestamps(startTime);
    }

    public static Builder getMainMenuPresence() {
        return getPresenceBuilder("In Main Menu");
    }

    public static void connect() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(connectHandler)
                .setErroredEventHandler(errorHandler)
                .build();

        DiscordRPC.discordInitialize(ForgeDiscordRPC.configHandler.discordAppID, handlers, true);

        MinecraftForge.EVENT_BUS.register(discordCallbackExecutor);
    }

    public static void setPresence(Builder presence) {
        DiscordRPC.discordUpdatePresence(presence.build());
    }

    public static void setInitializing() {
        startTime = System.currentTimeMillis();

        setPresence(RPCHandler
                .getPresenceBuilder("Initializing...")
                .setDetails("Initializing Forge & Minecraft..."));
    }

    public static void setMainMenu() {
        setPresence(RPCHandler.getMainMenuPresence());
    }

    public static void setDimension(World worldIn) {
        final String dimensionName = worldIn.provider.getDimensionType().getName();
        final String displayName = ForgeDiscordRPC.configHandler.dimensionNames.getOrDefault(dimensionName, dimensionName);

        setPresence(RPCHandler
                .getPresenceBuilder("In " + displayName)
                .setDetails("In Game"));
    }

}
