package org.cantaloupe.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.cantaloupe.entity.FakeEntity;
import org.cantaloupe.player.Player;

public class PlayerAttackFakeEntityEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player             player;
    private final FakeEntity         entity;

    public PlayerAttackFakeEntityEvent(Player player, FakeEntity entity) {
        this.player = player;
        this.entity = entity;
    }

    /**
     * Gets the player.
     * 
     * @return The player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the attacked entity.
     * 
     * @return The entity
     */
    public FakeEntity getAttacked() {
        return this.entity;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}