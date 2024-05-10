package art.ryanstew.savebanner.util;

import art.ryanstew.savebanner.SaveBanner;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class CommandUtil
{
    private final SaveBanner plugin;
    private final String helpMessage;



    public CommandUtil(SaveBanner plugin)
    {
        this.plugin = plugin;

        helpMessage =
            """
            
            &8------------------------------------
            %s&8:
            &8- &7/banner help
            &8- &7/banner list
            &8- &7/banner save <name>
            &8- &7/banner load <name>
            &8- &7/banner delete <name>
            &8------------------------------------
            &r""".formatted(plugin.getConfig().get("prefix"));
    }



    /**
     * Sends the plugin's help message to a CommandSender.
     *
     * @param sender The CommandSender to send the message to.
     */
    public void sendHelpMessage(CommandSender sender)
    {
        plugin.getGeneralUtil().sendFormattedMessage(sender, helpMessage, false);
    }



    /**
     * Get an integer banner list page number from a
     * passed string argument.
     *
     * @param argument The passed string argument.
     * @return An integer page number if possible, -1 if the
     * argument is not an integer.
     */
    public int getListPageIntegerFromArgument(String argument)
    {
        try
        {
            return Integer.parseInt(argument);
        }
        catch (NumberFormatException err)
        {
            return -1;
        }
    }



    /**
     * Checks if a banner exists in the banner config by name.
     *
     * @param name The banner name to search for.
     * @return True if the banner name is in the banner config, false if not.
     */
    public boolean bannerNameExists(String name)
    {
        ConfigurationSection savedBannersSection = plugin.getConfigManager().getBannerConfig().getConfigurationSection("savedBanners");
        return savedBannersSection != null && savedBannersSection.contains(name.toLowerCase());
    }



    /**
     * Gets the banner list entries for a specified page.
     *
     * @param entries All the banner entries.
     * @param page The page to get.
     * @return A set of strings that are the banner list entries for that page.
     */
    public Set<String> getPageOfBannerList(Set<String> entries, int numEntriesPerPage, int page)
    {
        int startIndex = (page - 1) * numEntriesPerPage;
        int stopIndex = startIndex + numEntriesPerPage;

        List<String> allEntries = new ArrayList<>(entries);
        int numEntries = allEntries.size();

        Set<String> pageEntries = new LinkedHashSet<>();
        for (int i = startIndex; i < stopIndex; ++i)
        {
            if (i == numEntries)
                break;

            pageEntries.add(allEntries.get(i));
        }

        return pageEntries;
    }
}
