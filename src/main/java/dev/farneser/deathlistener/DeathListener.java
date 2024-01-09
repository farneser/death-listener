package dev.farneser.deathlistener;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class DeathListener extends JavaPlugin {

    @Override
    public void onEnable() {
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
