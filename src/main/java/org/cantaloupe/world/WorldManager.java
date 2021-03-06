package org.cantaloupe.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.cantaloupe.Cantaloupe;
import org.cantaloupe.service.services.ScheduleService;

public class WorldManager {
    private final Map<String, World> worlds;

    private WorldManager() {
        this.worlds = new HashMap<String, World>();
    }
    
    public static WorldManager of() {
        return new WorldManager();
    }

    public void load() {
        Bukkit.getWorlds().forEach(world -> {
            this.registerWorld(world);
        });

        Cantaloupe.getServiceManager().provide(ScheduleService.class).repeat("worldTicker", new Runnable() {
            @Override
            public void run() {
                worlds.forEach((name, world) -> {
                    world.tick();
                });
            }
        }, 0L);
    }

    public void unload() {
        this.worlds.forEach((name, world) -> world.unload());
        this.worlds.clear();

        Cantaloupe.getServiceManager().provide(ScheduleService.class).cancel("worldTicker");
    }

    public void loadWorld(String name) {
        this.registerWorld(Bukkit.createWorld(new WorldCreator(name)));
    }

    public void registerWorld(org.bukkit.World handle) {
        if (!this.worlds.containsKey(handle.getName())) {
            World world = new World(handle);
            world.load();

            this.worlds.put(world.getName(), world);
        }
    }

    public void unloadWorld(String name, boolean save) {
        Bukkit.unloadWorld(name, save);

        this.unregisterWorld(name);
    }

    public void unloadWorld(World world, boolean save) {
        Bukkit.unloadWorld(world.getName(), save);

        this.unregisterWorld(world);
    }

    public void unregisterWorld(String name) {
        this.worlds.remove(name);
    }

    public void unregisterWorld(World world) {
        this.worlds.remove(world.getName());
    }

    public boolean isWorldRegistered(String name) {
        return this.worlds.containsKey(name);
    }

    public World getWorld(String name) {
        return this.worlds.get(name);
    }

    public World getWorldFromHandle(org.bukkit.World handle) {
        return this.worlds.get(handle.getName());
    }

    public Collection<World> getWorlds() {
        return this.worlds.values();
    }
}