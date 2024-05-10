package art.ryanstew.savebanner;

import art.ryanstew.savebanner.util.ConfigManager;
import art.ryanstew.savebanner.util.GeneralUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


public final class SaveBanner extends JavaPlugin
{
    private GeneralUtil generalUtil;
    private ConfigManager configManager;



    /**
     * Called whenever the plugin is enabling, such as when
     * the server is starting or finishing reloading.
     */
    @Override
    public void onEnable()
    {
        generalUtil = new GeneralUtil(this);
        configManager = new ConfigManager(this);

        Objects.requireNonNull(getCommand("savebanner")).setExecutor(new SaveBannerCommand(this));
    }



    /**
     * Called whenever the plugin is disabling, such as when
     * the server is stopping or starting reloading.
     */
    @Override
    public void onDisable()
    {
    }



    /**
     * Getter method for the GeneralUtil instance.
     *
     * @return The GeneralUtil instance.
     */
    public GeneralUtil getGeneralUtil()
    {
        return generalUtil;
    }



    /**
     * Getter method for the ConfigManager instance.
     *
     * @return The ConfigManager instance.
     */
    public ConfigManager getConfigManager()
    {
        return configManager;
    }

}
