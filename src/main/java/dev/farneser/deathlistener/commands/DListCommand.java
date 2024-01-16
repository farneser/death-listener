package dev.farneser.deathlistener.commands;

import dev.farneser.deathlistener.ConnectionFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

        List<String> messages = new ArrayList<>();

        Connection connection = ConnectionFactory.getInstance();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM player_deaths ORDER BY id DESC LIMIT ? OFFSET ?");

            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (commandSender.getName().equals(resultSet.getString("player_name")) || commandSender.hasPermission("deathlistener.dlist.admin")) {
                    messages.add(resultSet.getString("player_name") + " | " + resultSet.getString("death_message"));
                    messages.add(resultSet.getString("death_time") + " on: " + Math.round(resultSet.getDouble("death_x")) + " " + Math.round(resultSet.getDouble("death_y")) + " " + Math.round(resultSet.getDouble("death_z")));
                }
            }

            resultSet.close();
        } catch (SQLException e) {
            commandSender.sendMessage(buildColoredBoldComponent("Failed to get your deaths", NamedTextColor.RED));

            return true;
        }

        if (messages.isEmpty()) {
            commandSender.sendMessage(buildColoredBoldComponent("Deaths not found", NamedTextColor.YELLOW));

            return true;
        }

        commandSender.sendMessage(buildColoredBoldComponent("Here is your deaths on page: " + page + ". Try to die less often :]", NamedTextColor.GREEN));

        messages.forEach(commandSender::sendMessage);

        return true;
    }
}
