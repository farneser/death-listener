package dev.farneser.deathlistener;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public final class DeathListener extends JavaPlugin {

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("dlist")).setExecutor(new DListCommand());
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }

    @Override
    public void onDisable() {
        try {
            ConnectionFactory.getInstance().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
