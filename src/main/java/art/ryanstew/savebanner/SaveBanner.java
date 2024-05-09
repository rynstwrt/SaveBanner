package art.ryanstew.savebanner;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SaveBanner extends JavaPlugin
{

    private static String prefix;


    @Override
    public void onEnable()
    {
        saveDefaultConfig();
        saveConfig();
        prefix = getConfig().getString("prefix");

        Objects.requireNonNull(getCommand("savebanner")).setExecutor(new SaveBannerCommand(this));
    }


    @Override
    public void onDisable() { }


    public void sendFormattedMessage(CommandSender sender, String message, boolean prefixed)
    {
        if (prefixed)
            message = prefix + message;
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
