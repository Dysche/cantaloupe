package org.cantaloupe.permission.group;

import java.util.HashMap;

import org.cantaloupe.text.Text;
import org.cantaloupe.player.Player;

public class GroupManager {
    private static HashMap<String, Group> registeredGroups = new HashMap<String, Group>();

    public static void registerGroup(Group group) {
        group.initialize();

        registeredGroups.put(group.getName(), group);
    }

    public static void unregisterGroup(String name) {
        registeredGroups.remove(name);
    }

    public static void unregisterGroup(Group group) {
        registeredGroups.remove(group.getName());
    }

    public static Group getGroup(String name) {
        return registeredGroups.get(name);
    }

    public static Text getPrefixFor(Player player) {
        Text prefix = Text.of();

        for (Group group : player.getGroups()) {
            prefix.addChild(Text.of("[").addChild(group.getPrefix()).addChild(Text.of("]")));
        }

        return prefix;
    }
}