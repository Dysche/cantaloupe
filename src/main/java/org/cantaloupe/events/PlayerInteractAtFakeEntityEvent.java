package org.cantaloupe.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.cantaloupe.entity.FakeEntity;
import org.cantaloupe.player.Player;
import org.joml.Vector3d;

public class PlayerInteractAtFakeEntityEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player             player;
    private final FakeEntity         entity;
    private final Vector3d           clickedPosition;

    public PlayerInteractAtFakeEntityEvent(Player player, FakeEntity entity, Vector3d clickedPosition) {
        this.player = player;
        this.entity = entity;
        this.clickedPosition = clickedPosition;
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
     * Gets the right clicked entity.
     * 
     * @return The entity
     */
    public FakeEntity getRightClicked() {
        return this.entity;
    }

    /**
     * Gets position where the player clicked the entity.
     * 
     * @return The clicked position
     */
    public Vector3d getClickedPosition() {
        return this.clickedPosition;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}