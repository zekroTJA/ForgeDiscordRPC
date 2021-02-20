package de.zekro.fdrpc.config;

import de.zekro.fdrpc.ForgeDiscordRPC;
import de.zekro.fdrpc.util.Consts;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * Handler for initializing and reading config
 * values from confutation file.
 */
public class Config {

    private final File mainConfigFile;

    private String discordAppID = "";
    private String mainImageAlt = "";
    private HashMap<String, String> dimensionNames = new HashMap<>();

    /**
     * Initializes ConfigHandler instance, creates path to config
     * file and opens config file handler.
     * 
     * @param event forge pre-initialization event
     */
    public Config(FMLPreInitializationEvent event) {
        File mainConfigLocation = new File(event.getModConfigurationDirectory() + "/" + ForgeDiscordRPC.MOD_ID);
        mainConfigFile = new File(mainConfigLocation.getPath(), ForgeDiscordRPC.MOD_ID + ".cfg");
    }

    /**
     * Initializes configuration values from existing config
     * file or creates default config file with default
     * defined values.
     */
    public void init() {
        Configuration mainConfig = new Configuration(mainConfigFile);

        String category;

        // -------------------------------------------------------------------------------------------------------------
        // --- CAT: DISCORD ---

        category = "discord";
        mainConfig.addCustomCategoryComment(category, "General Discord API app settings");

        discordAppID = mainConfig.getString(
                "app_id", category, Consts.DEFAULT_CLIENT_ID,
                "The ID of the Discord API application created at https://discordapp.com/developers/applications."
        );

        mainImageAlt = mainConfig.getString(
                "main_image_alt", category, "",
                "The alt text of the main Discord RPC image."
        );

        // -------------------------------------------------------------------------------------------------------------
        // --- CAT: MISC ---

        category = "misc";
        mainConfig.addCustomCategoryComment(category, "Miscellaneous settings");
        final String[] dimensionNameList = mainConfig.getStringList(
                "dimension_names", category,
                new String[]{
                        "overworld:Overworld",
                        "the_nether:!In The Nether",
                        "the_end:The End"
                }, "ID of the dimension and the display name separated with a colon (':').\n" +
                        "You can prefix the value with '!' to bypass the default dimension prefix 'In ',\n"+
                        "so that way, you can define custom display formats.\n");

        Arrays.stream(dimensionNameList)
                .map(Config::formatDimensionEntry)
                .filter(Objects::nonNull)
                .forEach(t -> dimensionNames.put(t.getFirst(), t.getSecond()));

        mainConfig.save();
    }

    public String getDiscordAppID() {
        return discordAppID;
    }

    public String getMainImageAlt() {
        return mainImageAlt;
    }

    public HashMap<String, String> getDimensionNames() {
        return dimensionNames;
    }

    private static Tuple<String, String> formatDimensionEntry(String e) {
        final String[] split = e.split(":");
        if (split.length < 2)
            return null;

        final String v = split[1].startsWith("!")
                ? split[1].substring(1)
                : "In " + split[1];

        return new Tuple<>(split[0], v);
    }
}
