package de.zekro.fdrpc.handler;

import de.zekro.fdrpc.rpc.RPCHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Handler for Minecraft Events.
 */
@Mod.EventBusSubscriber
public class EventHandler {

    private static final List<World> loadedWorlds = new ArrayList<>();

    private static int currentServerPlayers;
    private static int currentServerSlots;

    /**
     * Event which is fired every tick when connected
     * to a world.
     * This handler is responsible for setting the size
     * and player count of the RPC party.
     *
     * @throws NullPointerException This handlers registration may throw a
     *                               NullPointerException under currently
     *                               seemly random circumstances.
     * @param event player event
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void playerEvent(PlayerEvent event) {
        if (event == null || event.getEntityPlayer() == null || event.getEntityPlayer().getEntityWorld().isRemote)
            return;

        final NetHandlerPlayClient netHandlerPlayClient = Minecraft.getMinecraft().getConnection();
        if (netHandlerPlayClient == null)
            return;

        final Collection<NetworkPlayerInfo> playerInfoMap = netHandlerPlayClient.getPlayerInfoMap();

        final int playersSize = playerInfoMap.size();
        final int slots = netHandlerPlayClient.currentServerMaxPlayers;

        if (playersSize == currentServerPlayers && slots == currentServerSlots)
            return;

        currentServerPlayers = playerInfoMap.size();
        currentServerSlots = netHandlerPlayClient.currentServerMaxPlayers;
        RPCHandler.setPlayerCount(currentServerPlayers, currentServerSlots);
    }

    /**
     * Event handler called when a world is loaded.
     * Adds the world to the loaded worlds list and
     * sets the RPC state to 'In Game' with the dimension
     * of the world passed.
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void selfLoggedInEvent(WorldEvent.Load event) {
        final World world = event.getWorld();
//        if (!world.isRemote)
//            return;

        loadedWorlds.add(world);

        currentServerSlots = 0;
        currentServerPlayers = 0;

        RPCHandler.setDimension(world);
    }

    /**
     * Event handler called when the world is unloaded.
     * Removes the world from the loaded worlds list.
     * If this list is empty, the RPC state will be set
     * to 'In Main Menu'.
     */
    @SubscribeEvent
    public static void selfLoggedOutEvent(WorldEvent.Unload event) {
        final World world = event.getWorld();
//        if (!world.isRemote)
//            return;

        loadedWorlds.remove(world);

        if (loadedWorlds.size() < 1)
            RPCHandler.setMainMenu();
    }

}
