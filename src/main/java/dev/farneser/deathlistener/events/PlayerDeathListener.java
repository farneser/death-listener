package dev.farneser.deathlistener.events;

import dev.farneser.deathlistener.config.HibernateConfig;
import dev.farneser.deathlistener.models.DeathMessage;
import dev.farneser.deathlistener.repository.DeathMessageRepository;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.Timestamp;
import java.util.Objects;

public class PlayerDeathListener implements Listener {
    private final DeathMessageRepository deathMessageRepository = new DeathMessageRepository(HibernateConfig.getSessionFactory());

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        String deathMsg = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(event.deathMessage()));

        DeathMessage message = DeathMessage.builder()
                .playerId(event.getEntity().getUniqueId().toString())
                .playerName(event.getEntity().getName())
                .deathX(event.getEntity().getLocation().getX())
                .deathY(event.getEntity().getLocation().getY())
                .deathZ(event.getEntity().getLocation().getZ())
                .deathTime(new Timestamp(event.getEntity().getWorld().getFullTime()))
                .deathMessage(deathMsg)
                .build();

        deathMessageRepository.saveOrUpdate(message);
    }
}