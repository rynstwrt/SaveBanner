package art.ryanstew.savebanner;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class SaveBanner extends JavaPlugin
{

    private File bannerConfigFile;
    private FileConfiguration bannerConfig;


    @Override
    public void onEnable()
    {
        saveDefaultConfig();
        saveConfig();

        loadBannerConfig();
//        saveBannerConfig();

        Objects.requireNonNull(getCommand("savebanner")).setExecutor(new SaveBannerCommand(this));
    }


    @Override
    public void onDisable() { }


    public void loadBannerConfig()
    {
        bannerConfigFile = new File(getDataFolder(), "banners.yml");
        if (!bannerConfigFile.exists())
        {
            bannerConfigFile.getParentFile().mkdirs();
            saveResource("banners.yml", false);
        }

        bannerConfig = YamlConfiguration.loadConfiguration(bannerConfigFile);
    }


    public FileConfiguration getBannerConfig()
    {
        return bannerConfig;
    }


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


    public void sendFormattedMessage(CommandSender sender, String message, boolean prefixed)
    {
        if (prefixed)
            message = getConfig().getString("prefix") + message;

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
