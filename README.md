# ForgeDiscordRPC

Minecraft Forge Mod to display current game state in Discord using [Discord RPC](https://discord.com/developers/docs/topics/rpc).

![](https://i.imgur.com/zttMIgL.png)

## Setup

First of all, you need to [create a Discord Application](https://discordapp.com/developers/applications) which is used as Client for Discord RPC.  
Copy the Client ID of the application to your clipboard.  
![](https://i.imgur.com/PGQO52W.png)

After that, open the configuration of the mod which is located at `config/fdrpc/fdepc.cfg`.  
Enter the Client ID as value for the `S:app_id` configuration key. You can also specify preferences like the alt text of the main app image or the name map of the dimensions, which is especially useful for dimensions added by other modifications.

If you upload an image, which then should be displayed in the rich presence, the asset must be exactly named `mainicon`.  
![](https://i.imgur.com/7I9IDPj.png)

## Download

You can download the latest releases from the [**Releases**](https://github.com/zekroTJA/ForgeDiscordRPC/releases) page.

If you want the latest development state, you can download the artifacts of the latest CI builds [**here**](https://github.com/zekroTJA/ForgeDiscordRPC/actions).

---

© 2020 zekro Development (Ringo Hoffmann)  
Covered by the MIT Licence.
