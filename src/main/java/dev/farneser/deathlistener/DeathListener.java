package dev.farneser.deathlistener;

import dev.farneser.deathlistener.commands.DListCommand;
import dev.farneser.deathlistener.events.PlayerDeathListener;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

@Slf4j
@SuppressWarnings("unused")
public final class DeathListener extends JavaPlugin {

    @Override
    public void onEnable() {
        initConfig();

        initDataBase();

        // Register commands
        Objects.requireNonNull(getCommand("dlist")).setExecutor(new DListCommand(getConfig().get("page-size")));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }

    private void initDataBase() {
        // Create database connection
        try {
            ConnectionFactory.buildConnection(getDataFolder().getAbsolutePath() + "/player_deaths.db");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        try {
            ConnectionFactory.getInstance().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initConfig() {
        // Create config file if it doesn't exist
        if (!getDataFolder().exists() && getDataFolder().mkdir()) {
            log.info("Created config file");
        }

        saveDefaultConfig();
    }
}
