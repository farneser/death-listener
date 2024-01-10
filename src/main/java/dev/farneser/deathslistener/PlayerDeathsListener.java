package dev.farneser.deathslistener;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class PlayerDeathsListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        try {
            Connection connection = ConnectionFactory.getInstance();

            PreparedStatement statement = connection.prepareStatement("INSERT INTO player_deaths (player_id, player_name, death_x, death_y, death_z, death_time, death_message) VALUES (?, ?, ?, ?, ?, ?, ?);");

            statement.setString(1, event.getPlayer().getUniqueId().toString());
            statement.setString(2, event.getPlayer().getName());
            statement.setDouble(3, event.getPlayer().getLocation().getX());
            statement.setDouble(4, event.getPlayer().getLocation().getY());
            statement.setDouble(5, event.getPlayer().getLocation().getZ());
            statement.setString(6, new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));

            String deathMsg = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(event.deathMessage()));

            statement.setString(7, deathMsg);

            statement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}