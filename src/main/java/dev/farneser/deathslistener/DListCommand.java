package dev.farneser.deathslistener;

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
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        final int pageSize = 2;

        int page = 1;

        if (strings.length > 0) {
            try {
                page = Integer.parseInt(strings[0]);
            } catch (NumberFormatException ex) {
                commandSender.sendMessage("Failed to parce page: " + strings[0]);

                return true;
            }
        }

        if (page < 1) {
            commandSender.sendMessage("Page cannot be less then one");

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
                if (commandSender.getName().equals(resultSet.getString("player_name")) || commandSender.isOp()) {
                    messages.add(resultSet.getString("player_name") + " | " + resultSet.getString("death_message"));
                    messages.add(resultSet.getString("death_time") + " on: " + Math.round(resultSet.getDouble("death_x")) + " " + Math.round(resultSet.getDouble("death_y")) + " " + Math.round(resultSet.getDouble("death_z")));
                }
            }

            resultSet.close();
        } catch (SQLException e) {
            commandSender.sendMessage("Failed to get your deaths");

            return true;
        }

        if (messages.isEmpty()) {
            commandSender.sendMessage("Deaths not found");

            return true;
        }

        commandSender.sendMessage("Here is your deaths on page: " + page + ". Try to die less often :]");

        messages.forEach(commandSender::sendMessage);

        return true;
    }
}
