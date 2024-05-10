package art.ryanstew.savebanner.util;

import art.ryanstew.savebanner.SaveBanner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;


public class ConfigManager
{

    private final SaveBanner plugin;

    private File bannerConfigFile;
    private FileConfiguration bannerConfig;



    public ConfigManager(SaveBanner plugin)
    {
        this.plugin = plugin;

        plugin.saveDefaultConfig();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        loadBannerConfig();
        saveBannerConfig();
    }



    /**
     * Loads the banner config file and config. Creates
     * the file if it does not already exist.
     */
    public void loadBannerConfig()
    {
        bannerConfigFile = new File(plugin.getDataFolder(), "banners.yml");
        if (!bannerConfigFile.exists())
        {
            bannerConfigFile.getParentFile().mkdirs();
            plugin.saveResource("banners.yml", false);
        }

        bannerConfig = YamlConfiguration.loadConfiguration(bannerConfigFile);
    }



    /**
     * Getter method for the banner config.
     *
     * @return The banner config FileConfiguration instance.
     */
    public FileConfiguration getBannerConfig()
    {
        return bannerConfig;
    }



    /**
     * Saves the banner config.
     *
     * @return True if save successful, false if not.
     */
    public boolean saveBannerConfig()
    {
        try
        {
            bannerConfig.save(bannerConfigFile);
            return true;
        }
        catch (IOException err)
        {
            err.printStackTrace();
            return false;
        }
    }



    /**
     * Reloads the primary config and the banner config files.
     *
     * @return True if reload successful, false if not.
     */
    public boolean reloadConfigs()
    {
        plugin.reloadConfig();
        plugin.saveConfig();

        loadBannerConfig();
        return saveBannerConfig();
    }
}
