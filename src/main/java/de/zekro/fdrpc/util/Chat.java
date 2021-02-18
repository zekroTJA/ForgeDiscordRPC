package de.zekro.fdrpc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class Chat {
    public static void sendI18NMessage(String ident) {
        sendComponentMessage(new TextComponentString(I18n.format(ident)));
    }

    public static void sendComponentMessage(ITextComponent component) {
        Minecraft.getMinecraft().player.sendMessage(
                new TextComponentString("[FDRPC] ").appendSibling(component));
    }
}
