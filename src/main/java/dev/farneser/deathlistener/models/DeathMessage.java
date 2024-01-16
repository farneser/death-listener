package dev.farneser.deathlistener.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "player_deaths")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeathMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "player_id")
    private String playerId;

    @Column(name = "player_name")
    private String playerName;

    @Column(name = "death_x")
    private double deathX;

    @Column(name = "death_y")
    private double deathY;

    @Column(name = "death_z")
    private double deathZ;

    @Column(name = "death_time")
    private Timestamp deathTime;

    @Column(name = "death_message")
    private String deathMessage;
}
