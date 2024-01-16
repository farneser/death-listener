package dev.farneser.deathlistener.commands;

import dev.farneser.deathlistener.config.HibernateConfig;
import dev.farneser.deathlistener.models.DeathMessage;
import dev.farneser.deathlistener.repository.DeathMessageRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DListCommand implements CommandExecutor {
    private int pageSize = 5;

    public DListCommand(Object pageSize) {
        if (pageSize instanceof Integer && (int) pageSize > 0) {
            this.pageSize = (int) pageSize;
        }
    }

    private Component buildColoredBoldComponent(final String text, TextColor color) {
        return Component.text(text).color(TextColor.color(color)).decoration(TextDecoration.BOLD, true);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        int page = 1;

        if (strings.length > 0) {
            try {
                page = Integer.parseInt(strings[0]);
            } catch (NumberFormatException ex) {
                commandSender.sendMessage(buildColoredBoldComponent("Failed to parce page: " + strings[0], NamedTextColor.RED));

                return true;
            }
        }

        if (page < 1) {
            commandSender.sendMessage(buildColoredBoldComponent("Page cannot be less then one", NamedTextColor.RED));

            return true;
        }

        List<DeathMessage> messages = new DeathMessageRepository(HibernateConfig.getSessionFactory()).getPlayerDeaths(pageSize, page, commandSender.getName(), commandSender.isOp());

        if (messages.isEmpty()) {
            commandSender.sendMessage(buildColoredBoldComponent("Deaths not found", NamedTextColor.YELLOW));

            return true;
        }

        commandSender.sendMessage(buildColoredBoldComponent("Here is your deaths on page: " + page + ". Try to die less often :]", NamedTextColor.GREEN));
        commandSender.sendMessage(Component.text("Use /dlist [n] to get page n of deaths.", NamedTextColor.GRAY));

        for (DeathMessage entity : messages) {
            commandSender.sendMessage(entity.getPlayerName() + " | " + entity.getDeathMessage());
            commandSender.sendMessage(entity.getDeathTime() + " on: " + Math.round(entity.getDeathX()) + " " + Math.round(entity.getDeathY()) + " " + Math.round(entity.getDeathZ()));
        }

        return true;
    }
}
