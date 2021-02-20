package de.zekro.fdrpc.rpc;

import de.zekro.fdrpc.ForgeDiscordRPC;
import de.zekro.fdrpc.gui.GuiJoinRequest;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ErroredCallback;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.arikia.dev.drpc.DiscordRichPresence.Builder;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Function wrapper for {@link DiscordRPC}.
 */
public class RPCHandler {

    private static final String IMAGE_MAIN_LOGO = "mainicon";

    private static long startTime = 0;
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
            ForgeDiscordRPC.getLogger().info(String.format("Discord RPC connected to user account %s#%s (%s)",
                user.username, user.discriminator, user.userId));

    private static final ErroredCallback errorHandler = (error, errStr) ->
            ForgeDiscordRPC.getLogger().error(error);

    /**
     * Connect to the Discord RPC server using the App ID from
     * configuration file and adding the discordCallbackExecutor
     * to the forge event bus.
     */
    public static void connect() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(connectHandler)
                .setErroredEventHandler(errorHandler)
                .setJoinRequestEventHandler(RPCHandler::joinRequestHandler)
                .setJoinGameEventHandler(RPCHandler::joinGameHandler)
                .build();

        DiscordRPC.discordInitialize(ForgeDiscordRPC.getConfig().getDiscordAppID(), handlers, true);
        MinecraftForge.EVENT_BUS.register(discordCallbackExecutor);
    }

    /**
     * Set presence state to "Initializing...".
     */
    public static void setInitializing() {
        startTime = System.currentTimeMillis();

        details = "Initializing...";
        state = "Initializing Forge & Minecraft...";
    }

    /**
     * Set presence state to "In Main Menu".
     */
    public static void setMainMenu() {
        details = "In Main Menu";
        state = "";
    }

    /**
     * Set state to specified worlds dimension.
     *
     * The dimension name will be tried to be translated form
     * the configured dimension map.
     *
     * @param worldIn world the player is in
     */
    public static void setDimension(World worldIn) {
        final String dimensionName = worldIn.provider.getDimensionType().getName();
        final String displayName = ForgeDiscordRPC.getConfig().getDimensionNames()
                .getOrDefault(dimensionName, dimensionName);

        state = displayName;
    }

    /**
     * Sets the presence mode to singleplayer world.
     */
    public static void setSinglePlayer() {
        singlePlayer = true;
        currentPlayers = 0;
        maxPlayers = 0;

        details = "In Singleplayer Game";
    }

    /**
     * Sets the presence mode to multiplayer world
     * with the passed current player count and max
     * player count of the server.
     *
     * @param curr current player count
     * @param max max player count
     */
    public static void setMultiPlayer(int curr, int max) {
        singlePlayer = false;
        currentPlayers = curr;
        maxPlayers = max;

        details = "In Multiplayer Game";
    }

    /**
     * Pushes the presence change to the Discord RPC server.
     */
    public static void updatePresence() {
        Builder builder = getDefaultPresenceBuilder(state, ForgeDiscordRPC.getConfig().getMainImageAlt())
                .setDetails(details);

        if (!singlePlayer && currentPlayers != 0 && maxPlayers != 0)
            builder.setParty("Party", currentPlayers, maxPlayers);

        ServerData currentServer = Minecraft.getMinecraft().getCurrentServerData();
        if (!singlePlayer && ForgeDiscordRPC.getConfig().getAllowInvites() && currentServer != null)
            builder.setSecrets(encodeServerData(currentServer), "");

        DiscordRPC.discordUpdatePresence(builder.build());
    }

    /**
     * Shuts down the Discord RPC connection.
     */
    public static void shutdown() {
        DiscordRPC.discordShutdown();
        MinecraftForge.EVENT_BUS.unregister(discordCallbackExecutor);
    }

    private static Builder getDefaultPresenceBuilder(String state, String bigImageAlt) {
        return new DiscordRichPresence.Builder(state)
                .setBigImage(IMAGE_MAIN_LOGO, bigImageAlt)
                .setStartTimestamps(startTime);
    }

    private static void joinRequestHandler(DiscordUser user) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiJoinRequest(user, (res) -> {
            DiscordRPC.discordRespond(user.userId, res);
        }));
    }

    private static void joinGameHandler(String payload) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getCurrentServerData() != null || mc.isIntegratedServerRunning()) {
            mc.world.sendQuittingDisconnectingPacket();
            mc.loadWorld(null);
            Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
        }
        FMLClientHandler.instance().connectToServer(
                Minecraft.getMinecraft().currentScreen,
                decodeServerData(payload));
    }

    private static String encodeServerData(ServerData data) {
        return String.format(
                "n=%s;ip=%s;l=%b",
                data.serverName, data.serverIP, data.isOnLAN());
    }

    private static ServerData decodeServerData(String data) {
        final Map<String, String> map = Arrays.stream(data.split(";"))
                .filter(kv -> kv.length() > 0)
                .map(kv -> kv.split("="))
                .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

        final String name = map.get("n");
        final String ip = map.get("ip");
        final boolean isLan = Boolean.parseBoolean(map.get("l"));

        return new ServerData(name, ip, isLan);
    }
}
