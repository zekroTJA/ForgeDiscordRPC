package de.zekro.fdrpc.handler;

import de.zekro.fdrpc.ForgeDiscordRPC;
import de.zekro.fdrpc.gui.GuiJoinRequest;
import de.zekro.fdrpc.rpc.RPCHandler;
import de.zekro.fdrpc.util.Chat;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

@Mod.EventBusSubscriber
public class KeyHandler {

    private static boolean rpcEnabled = true;

    @SubscribeEvent(priority= EventPriority.NORMAL, receiveCanceled=true)
    public static void onKeyPress(InputEvent.KeyInputEvent event) {
        if (ForgeDiscordRPC.toggleKeyBinding.isPressed()) {
            if (rpcEnabled) {
                RPCHandler.shutdown();
                Chat.sendI18NMessage("chat.toggle.disabled");
            } else {
                RPCHandler.connect();
                EventHandler.update(true);
                Chat.sendI18NMessage("chat.toggle.enabled");
            }
            rpcEnabled = !rpcEnabled;
        }
    }
}
