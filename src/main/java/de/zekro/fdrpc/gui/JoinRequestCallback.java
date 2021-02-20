package de.zekro.fdrpc.gui;

import com.sun.jna.Callback;
import net.arikia.dev.drpc.DiscordRPC;

public interface JoinRequestCallback extends Callback {
    void apply(DiscordRPC.DiscordReply result);
}
