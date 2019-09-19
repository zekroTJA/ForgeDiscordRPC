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

/**
 * Static RPCHandler handling connection to
 * Discord RPC interface.
 */
public class RPCHandler {

    private static final String IMAGE_MAIN_LOGO = "mainicon";
    private static final String MAIN_IMAGE_ALT = "Evolved Technics";

    private static long startTime = 0;
    private static World currWorld;

    /**
     * TickEventHandler running RPC Callback everytime
     * TickEvent was fired.
     */
    private static final Object discordCallbackExecutor = new Object() {
        @SubscribeEvent
        public void tickEvent(TickEvent.ClientTickEvent e) {
            DiscordRPC.discordRunCallbacks();
        }
    };

    /**
     * Discord RPC Ready callback handler fired
     * when Discord RPC connection is ready.
     */
    private static ReadyCallback connectHandler = (user) -> {
        System.out.printf("Discord RPC connected to user account %s#%s (%s)",
                user.username, user.discriminator, user.userId);
    };

    /**
     * Callback handler fired when Discord RPC
     * connection failed.
     */
    private static ErroredCallback errorHandler = (error, errStr) -> {
        System.err.println(error);
    };

    /**
     * Try to connect to Discord RPC interface, set
     * handlers and register tick event executor.
     */
    public static void connect() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(connectHandler)
                .setErroredEventHandler(errorHandler)
                .build();

        DiscordRPC.discordInitialize(ForgeDiscordRPC.configHandler.discordAppID, handlers, true);

        MinecraftForge.EVENT_BUS.register(discordCallbackExecutor);
    }

    /**
     * Returns a presence builder with set state
     * text, main logo and time stamp of the start
     * time of the Minecraft session.
     * 
     * @param state state text
     * @param bigImageAlt alternative hover text which is shown
     *                    when hovering over large image
     * @return the RPC builder
     */
    public static Builder getPresenceBuilder(String state, String bigImageAlt) {
        return new DiscordRichPresence.Builder(state)
                .setBigImage(IMAGE_MAIN_LOGO, bigImageAlt)
                .setStartTimestamps(startTime);
    }

    /**
     * Returns the pre-configured presence builder for
     * the main menu state.
     */
    public static Builder getMainMenuPresence() {
        return getPresenceBuilder("In Main Menu", MAIN_IMAGE_ALT);
    }

    /**
     * Finalizes the passed presence builder and
     * sets the presence to Discord.
     */
    public static void setPresence(Builder presence) {
        DiscordRPC.discordUpdatePresence(presence.build());
    }

    /**
     * Sets the current RPC state to 'Initializing'.
     */
    public static void setInitializing() {
        startTime = System.currentTimeMillis();

        setPresence(RPCHandler
                .getPresenceBuilder("Initializing...", MAIN_IMAGE_ALT)
                .setDetails("Initializing Forge & Minecraft..."));
    }

    /**
     * Sets the current RPC state to 'In Main Menu'
     */
    public static void setMainMenu() {
        setPresence(RPCHandler.getMainMenuPresence());
    }

    /**
     * Sets the current RPC state to 'In Game' with the
     * passed worlds dimension as detail text.
     * The dimension ID will be tried to be replaced with
     * the friendly name set in the config. If this was not
     * found, the dimension name ID will be used instead.
     *
     * @param worldIn world the player is in
     * @param curr current player count
     * @param max max player count
     */
    public static void setDimension(World worldIn, int curr, int max) {
        final String dimensionName = worldIn.provider.getDimensionType().getName();
        final String displayName = ForgeDiscordRPC.configHandler.dimensionNames.getOrDefault(dimensionName, dimensionName);

        currWorld = worldIn;

        final Builder builder = RPCHandler
                .getPresenceBuilder("In " + displayName, MAIN_IMAGE_ALT)
                .setDetails("In Game");

        if (curr > 0 && max > 0) {
            builder.setParty("Party", curr, max);
        }

        setPresence(builder);
    }

    /**
     * Sets the current RPC state to 'In Game' with the
     * passed worlds dimension as detail text.
     * The dimension ID will be tried to be replaced with
     * the friendly name set in the config. If this was not
     * found, the dimension name ID will be used instead.
     *
     * @param worldIn world the player is in
     */
    public static void setDimension(World worldIn) {
        setDimension(worldIn, 0, 0);
    }

    /**
     * Updates the party player count.
     *
     * @param curr current player count
     * @param max max player count
     */
    public static void setPlayerCount(int curr, int max) {
        if (currWorld == null) return;

        setDimension(currWorld, curr, max);
    }

}
