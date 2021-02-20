package de.zekro.fdrpc.gui;

import com.google.common.collect.Lists;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordUser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.List;

public class GuiJoinRequest extends GuiScreen {
    protected JoinRequestCallback callback;
    protected String messageLine1;
    private final List<String> listLines = Lists.<String>newArrayList();
    protected String acceptButtonText;
    protected String denyButtonText;
    protected String ignoreButtonText;
    private int ticksUntilEnable;

    public GuiJoinRequest(DiscordUser user, JoinRequestCallback callback) {
        this.messageLine1 = I18n.format("gui.joinrequest.request",
                user.username + ":" + user.discriminator, user.userId);
        this.acceptButtonText = I18n.format("gui.joinrequest.button.accept");
        this.denyButtonText = I18n.format("gui.joinrequest.button.deny");
        this.ignoreButtonText = I18n.format("gui.joinrequest.button.ignore");
        this.callback = callback;
    }

    public void initGui() {
        final int y = this.height / 6 + 96;
        final int width = 80;
        final int height = 20;
        final int padding = 10;

        this.buttonList.add(new GuiButton(DiscordRPC.DiscordReply.YES.reply,
                this.width/2 - width/2 - width - padding, y, width, height, this.acceptButtonText));
        this.buttonList.add(new GuiButton(DiscordRPC.DiscordReply.NO.reply,
                this.width/2 - width/2, y, width, height, this.denyButtonText));
        this.buttonList.add(new GuiButton(DiscordRPC.DiscordReply.IGNORE.reply,
                this.width/2 - width/2 + width + padding, y, width, height, this.ignoreButtonText));
        this.listLines.clear();
    }

    protected void actionPerformed(GuiButton button) {
        for (DiscordRPC.DiscordReply rep : DiscordRPC.DiscordReply.values()) {
            if (rep.reply == button.id) {
                this.callback.apply(rep);
                break;
            }
        }
        this.mc.displayGuiScreen(null);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.messageLine1, this.width / 2, 70, 16777215);
        int i = 90;

        for (String s : this.listLines)
        {
            this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 16777215);
            i += this.fontRenderer.FONT_HEIGHT;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void updateScreen() {
        super.updateScreen();

        if (--this.ticksUntilEnable == 0)
        {
            for (GuiButton guibutton : this.buttonList)
            {
                guibutton.enabled = true;
            }
        }
    }
}
