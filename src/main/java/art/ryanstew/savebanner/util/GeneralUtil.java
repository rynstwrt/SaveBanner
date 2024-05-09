package art.ryanstew.savebanner.util;

import art.ryanstew.savebanner.SaveBanner;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GeneralUtil
{

    private final SaveBanner plugin;


    public GeneralUtil(SaveBanner plugin)
    {
        this.plugin = plugin;
    }


    /**
     * Sends a formatted message to a CommandSender.
     *
     * @param sender The CommandSender to send the message to.
     * @param message The message to be sent.
     * @param prefixed True if the message should have the plugin prefix.
     * @param padded True if the message should have whitespace above and below itself.
     */
    public void sendFormattedMessage(CommandSender sender, String message, boolean prefixed, boolean padded)
    {
        if (prefixed)
            message = plugin.getConfig().getString("prefix") + message;

        if (padded)
            message = String.format("\n%s\n&r", message);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
