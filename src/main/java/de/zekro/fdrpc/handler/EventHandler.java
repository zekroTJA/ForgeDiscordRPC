package de.zekro.fdrpc.handler;

import de.zekro.fdrpc.ForgeDiscordRPC;
import de.zekro.fdrpc.rpc.RPCHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class EventHandler {

    private static List<World> loadedWorlds = new ArrayList<>();

//    @SubscribeEvent
//    public static void onClonePlayer(PlayerEvent.Clone event) {
//        System.out.println("CLONE PLAYER");
//    }

//    @SubscribeEvent
//    public static void loggedOutHandler(PlayerLoggedOutEvent event) {
//        EntityPlayer player = event.player;
//        World world = player.world;
//        if (!world.isRemote) {
//            ForgeDiscordRPC.rpcHandler.setMainMenu();
//        }
//    }
//
//    @SubscribeEvent
//    public static void loggedInHandler(PlayerLoggedInEvent event) {
//        EntityPlayer player = event.player;
//        World world = player.world;
//        if (!world.isRemote) {
//            ForgeDiscordRPC.rpcHandler.setDimension(world);
//            System.out.println("WORLD JOINED" + player.getName());
//        }
//    }

//    @SubscribeEvent
//    public static void loggedInEvent(FMLNetworkEvent.ClientConnectedToServerEvent event) {
//        ForgeDiscordRPC.rpcHandler.setDimension(event.);
//            System.out.println("WORLD JOINED");
//    }

//    @SubscribeEvent
//    public static void entityJoinEvent(EntityJoinWorldEvent event) {
//        if (event.getEntity() instanceof EntityPlayer) {
//            System.out.println("JOINED WORLD");
//        }
//    }

    @SubscribeEvent
    public static void selfLoggedOutEvent(WorldEvent.Load event) {
        final World world = event.getWorld();
        if (!world.isRemote)
            return;

        loadedWorlds.add(world);

        RPCHandler.setDimension(world);
    }

    @SubscribeEvent
    public static void selfLoggedEvent(WorldEvent.Unload event) {
        final World world = event.getWorld();
        if (!world.isRemote)
            return;

        loadedWorlds.remove(world);

        if (loadedWorlds.size() < 1) {
            RPCHandler.setMainMenu();
        }
    }

}
