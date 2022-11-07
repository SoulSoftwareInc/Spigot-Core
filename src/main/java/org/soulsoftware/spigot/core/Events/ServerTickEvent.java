package org.soulsoftware.spigot.core.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired every server tick.
 */
public class ServerTickEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}