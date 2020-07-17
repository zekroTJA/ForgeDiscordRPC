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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Static RPCHandler handling connection to
 * Discord RPC interface.
 */
public class RPCHandler {

    private static final String IMAGE_MAIN_LOGO = "mainicon";

    private static final Logger LOGGER = LogManager.getLogger(ForgeDiscordRPC.MOD_ID);

    private static long startTime = 0;
    private static World currWorld;
    private static String state = "Initializing...";
    private static String details = "Initializing...";
    private static int currentPlayers = 0;
    private static int maxPlayers = 0;
    private static boolean singlePlayer = true;

    private static final Object discordCallbackExecutor = new Object() {
        @SubscribeEvent
        public void tickEvent(TickEvent.ClientTickEvent e) {
            DiscordRPC.discordRunCallbacks();
        }
    };

    private static final ReadyCallback connectHandler = (user) ->
        LOGGER.info(String.format("Discord RPC connected to user account %s#%s (%s)",
                user.username, user.discriminator, user.userId));

    private static final ErroredCallback errorHandler = (error, errStr) ->
        LOGGER.error(error);

    public static void connect() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(connectHandler)
                .setErroredEventHandler(errorHandler)
                .build();

        DiscordRPC.discordInitialize(ForgeDiscordRPC.getConfig().getDiscordAppID(), handlers, true);

        MinecraftForge.EVENT_BUS.register(discordCallbackExecutor);
    }

    public static void setInitializing() {
        startTime = System.currentTimeMillis();

        details = "Initializing...";
        state = "Initializing Forge & Minecraft...";
    }

    public static void setMainMenu() {
        details = "In Main Menu";
        state = "";
    }

    public static void setDimension(World worldIn) {
        final String dimensionName = worldIn.provider.getDimensionType().getName();
        final String displayName = ForgeDiscordRPC.getConfig().getDimensionNames()
                .getOrDefault(dimensionName, dimensionName);

        state = String.format("In %s", displayName);
    }

    public static void setSinglePlayer() {
        singlePlayer = true;
        currentPlayers = 0;
        maxPlayers = 0;

        details = "In Single Player Game";
    }

    public static void setMultiPlayer(int curr, int max) {
        singlePlayer = false;
        currentPlayers = curr;
        maxPlayers = max;

        details = "In Multi Player Game";
    }

    public static void updatePresence() {
        Builder builder = getDefaultPresenceBuilder(state, ForgeDiscordRPC.getConfig().getMainImageAlt())
                .setDetails(details);

        if (!singlePlayer && currentPlayers != 0 && maxPlayers != 0)
            builder.setParty("Party", currentPlayers, maxPlayers);

        DiscordRPC.discordUpdatePresence(builder.build());
    }


    private static Builder getDefaultPresenceBuilder(String state, String bigImageAlt) {
        return new DiscordRichPresence.Builder(state)
                .setBigImage(IMAGE_MAIN_LOGO, bigImageAlt)
                .setStartTimestamps(startTime);
    }
}
