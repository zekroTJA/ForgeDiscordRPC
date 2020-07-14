package de.zekro.fdrpc.config;

import de.zekro.fdrpc.ForgeDiscordRPC;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Handler for initializing and reading config
 * values from confutation file.
 */
public class Config {

    private File mainConfigFile;

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
                "app_id", category, "",
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
                        "the_nether:The Nether",
                        "the_end:The End"
                }, "ID of the dimension and the display name separated with a colon (':').");

        Arrays.asList(dimensionNameList).forEach(e -> {
            final String[] split = e.split(":");
            
            if (split.length > 1)
                dimensionNames.put(split[0], split[1]);
        });

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
}
