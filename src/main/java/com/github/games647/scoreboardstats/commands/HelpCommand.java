package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.ScoreboardStats;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.ChatPaginator.ChatPage;
import org.bukkit.util.StringUtil;

/**
 * Shows a help for all commands with their descriptions, usages, aliases
 * and permissions.
 */
public class HelpCommand extends CommandHandler {

    private final SidebarCommands commandManager;
//    private final boolean permissionCheck = false;

    public HelpCommand(ScoreboardStats plugin, SidebarCommands commandManager) {
        super("help", "&aShows a help of all commands", plugin, "h");

        this.commandManager = commandManager;
    }

    @Override
    public void onCommand(CommandSender sender, String subCommand, String... args) {
        if (args.length == 0) {
            showHelpPage(sender, 1);
        } else {
            Integer pageNumber = Integer.getInteger(args[0]);
            if (pageNumber == null) {
                showCommandHelp(sender, args[0]);
            } else {
                showHelpPage(sender, pageNumber);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String subCommand, String... args) {
        if (args.length == 1) {
            List<String> suggestion = Lists.newArrayList();
            for (String command : commandManager.getSubCommands()) {
                if (StringUtil.startsWithIgnoreCase(command, args[0])) {
                    suggestion.add(command);
                }
            }

            return suggestion;
        }

        return null;
    }


    private void showCommandHelp(CommandSender sender, String command) {
        CommandHandler handler = commandManager.getHandler(command);
        if (handler == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Command not known");
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "Help for: " + command);

        sender.sendMessage(ChatColor.DARK_AQUA + "Aliases: " + handler.getAliases());
        sender.sendMessage(ChatColor.DARK_AQUA + "Permission: " + handler.getPermission());
        sender.sendMessage(ChatColor.DARK_AQUA + "Description: " + handler.getDescription());
    }

    private void showHelpPage(CommandSender sender, int pageNumber) {
        ChatPage page = paginate(buildContent("sb"), pageNumber
                , ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH, ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT);
        String header = buildHeader()
                .replace("{page}", Integer.toString(pageNumber))
                .replace("{total}", Integer.toString(page.getTotalPages()));

        sender.sendMessage(header);
        sender.sendMessage(page.getLines());
    }

    private String buildHeader() {
        StringBuilder header = new StringBuilder();
        header.append(ChatColor.YELLOW)
                .append("------ ")
                .append(ChatColor.WHITE).append("Help: ")
                .append(plugin.getName()).append(" Page ")
                .append(ChatColor.RED).append("{page} ")
                .append(ChatColor.GOLD).append("/ ")
                .append(ChatColor.RED).append("{total} ")
                .append(ChatColor.YELLOW);
        for (int i = header.length(); i < ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH; i++) {
            header.append('-');
        }

        return header.toString();
    }


    private String buildContent(String mainCommand) {
        StringBuilder builder = new StringBuilder();
        //prevent duplicates
        Set<CommandHandler> handlers = Sets.newHashSet(commandManager.getHandlers());
        for (CommandHandler handler : handlers) {
            builder.append(ChatColor.AQUA)
                    .append('/').append(mainCommand).append(' ')
                    .append(handler.getSubCommand()).append(' ')
                    .append(ChatColor.DARK_AQUA).append("- ").append(handler.getDescription());
            builder.append('\n');
        }

        return builder.toString();
    }

    private ChatPage paginate(String unpaginatedString, int pageNumber, int lineLength, int pageHeight) {
        String[] lines = ChatPaginator.wordWrap(unpaginatedString, lineLength);

        int totalPages = lines.length / pageHeight + (lines.length % pageHeight == 0 ? 0 : 1);
        int actualPageNumber = pageNumber <= totalPages ? pageNumber : totalPages;

        int from = (actualPageNumber - 1) * pageHeight;
        int to = from + pageHeight <= lines.length ? from + pageHeight : lines.length;
        String[] selectedLines = Arrays.copyOfRange(lines, from, to);

        return new ChatPage(selectedLines, actualPageNumber, totalPages);
    }
}
