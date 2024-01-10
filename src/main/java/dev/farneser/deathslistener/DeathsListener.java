package dev.farneser.deathslistener;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public final class DeathsListener extends JavaPlugin {

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("dlist")).setExecutor(new DListCommand());
        getServer().getPluginManager().registerEvents(new PlayerDeathsListener(), this);
    }

    @Override
    public void onDisable() {
        try {
            ConnectionFactory.getInstance().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // ignored
    }
}
