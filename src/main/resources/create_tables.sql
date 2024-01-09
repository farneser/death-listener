CREATE TABLE IF NOT EXISTS player_deaths
(
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id     TEXT,
    player_name   TEXT,
    death_x       REAL,
    death_y       REAL,
    death_z       REAL,
    death_time    TEXT,
    death_message TEXT
);