package org.cantaloupe.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.cantaloupe.Cantaloupe;
import org.cantaloupe.inject.Injector;
import org.cantaloupe.text.Text;
import org.cantaloupe.user.UserManager.Scopes;
import org.cantaloupe.user.permission.IPermissionHolder;
import org.cantaloupe.user.permission.IPermittable;
import org.cantaloupe.user.permission.group.Group;
import org.cantaloupe.user.permission.group.GroupManager;

public class User implements IPermittable, IPermissionHolder {
	private Player handle = null;
	private Injector<User> injector = null;
	private PermissionAttachment permissionAttachment = null;
	private Map<String, List<String>> permissions = new HashMap<String, List<String>>();
	private ArrayList<Group> groups = null;

	private User(Player handle) {
		this.handle = handle;
		this.injector = new Injector<User>();

		this.permissionAttachment = this.handle.addAttachment(Cantaloupe.getInstance());
		this.groups = new ArrayList<Group>();
	}

	public static User of(Player handle) {
		return new User(handle);
	}

	public void onJoin() {
		Optional<List<Consumer<User>>> consumers = this.getInjector().getConsumers(Scopes.JOIN);
		if (consumers.isPresent()) {
			for (Consumer<User> consumer : consumers.get()) {
				consumer.accept(this);
			}
		}
	}

	public void onLoad() {
		Optional<List<Consumer<User>>> consumers = this.getInjector().getConsumers(Scopes.LOAD);
		if (consumers.isPresent()) {
			for (Consumer<User> consumer : consumers.get()) {
				consumer.accept(this);
			}
		}
	}

	public void onLeave() {
		Optional<List<Consumer<User>>> consumers = this.getInjector().getConsumers(Scopes.LEAVE);
		if (consumers.isPresent()) {
			for (Consumer<User> consumer : consumers.get()) {
				consumer.accept(this);
			}
		}
	}

	public void onUnload() {
		Optional<List<Consumer<User>>> consumers = this.getInjector().getConsumers(Scopes.UNLOAD);
		if (consumers.isPresent()) {
			for (Consumer<User> consumer : consumers.get()) {
				consumer.accept(this);
			}
		}

		this.getInjector().clear();
	}

	public void onWorldSwitch(World old, World current) {
		this.updatePermissionsWorld();
	}

	public void sendMessage(Text text) {
		this.handle.spigot().sendMessage(text.getComponent());
	}

	public void sendMessage(String message) {
		this.handle.sendMessage(message);
	}

	public void sendLegacyMessage(String message) {
		this.handle.spigot().sendMessage(Text.fromLegacy(message).getComponent());
	}

	public boolean isInGroup(String name) {
		for (Group group : this.groups) {
			if (group.getName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	public boolean isInGroup(Group group) {
		return this.groups.contains(group);
	}

	public void joinGroup(String name) {
		if (this.isInGroup(name)) {
			return;
		}

		Group group = GroupManager.getGroup(name);
		for (String node : group.getPermissions(null)) {
			this.permissionAttachment.setPermission(node, node.startsWith("-") ? false : true);
		}

		this.groups.add(group);
	}

	public void joinGroup(Group group) {
		for (String node : group.getPermissions(null)) {
			this.permissionAttachment.setPermission(node, node.startsWith("-") ? false : true);
		}

		this.groups.add(group);
	}

	public void leaveGroup(String name) {
		Group group = GroupManager.getGroup(name);

		for (String node : group.getPermissions(null)) {
			boolean hasPermission = false;

			for (Group g : this.groups) {
				if (g.hasPermission(node)) {
					hasPermission = true;
				}
			}

			if (!hasPermission && !this.hasPermissionGlobal(node) && !this.hasPermissionWorld(node)) {
				this.permissionAttachment.unsetPermission(node);
			}
		}

		this.groups.remove(group);
	}

	public void leaveGroup(Group group) {
		for (String node : group.getPermissions(null)) {
			boolean hasPermission = false;

			for (Group g : this.groups) {
				if (g.hasPermission(node)) {
					hasPermission = true;
				}
			}

			if (!hasPermission && !this.hasPermissionGlobal(node) && !this.hasPermissionWorld(node)) {
				this.permissionAttachment.unsetPermission(node);
			}
		}

		this.groups.remove(group);
	}

	@Override
	public void setPermission(String node) {
		this.permissions.get("_global_").add(node);
		this.permissionAttachment.setPermission(node, node.startsWith("-") ? false : true);
	}

	@Override
	public void setPermission(World world, String node) {
		if (!this.permissions.containsKey(world.getName())) {
			this.permissions.put(world.getName(), new ArrayList<String>());
		}

		this.permissions.get(world.getName()).add(node);
		this.permissionAttachment.setPermission(node, node.startsWith("-") ? false : true);
	}

	@Override
	public void unsetPermission(String node) {
		this.permissions.get("_global_").remove(node);

		if (!this.hasPermissionGroup(node) && !this.hasPermissionWorld(node)) {
			this.permissionAttachment.unsetPermission(node);
		}
	}

	@Override
	public void unsetPermission(World world, String node) {
		if (this.permissions.containsKey(world.getName())) {
			this.permissions.get(world.getName()).remove(node);

			if (!this.hasPermissionGlobal(node) && !this.hasPermissionGroup(node)) {
				this.permissionAttachment.unsetPermission(node);
			}
		}
	}

	@Override
	public boolean hasPermission(String node) {
		return this.handle.hasPermission(node);
	}

	private boolean hasPermissionGlobal(String node) {
		return this.permissions.get("_global_").contains(node);
	}

	private boolean hasPermissionGroup(String node) {
		for (Group group : this.groups) {
			if (group.hasPermission(node)) {
				return true;
			}
		}

		return false;
	}

	private boolean hasPermissionWorld(String node) {
		for (String world : this.permissions.keySet()) {
			if (this.toHandle().getWorld().getName().equals(world)) {
				if (this.permissions.get(world).contains(node)) {
					return true;
				} else {
					return false;
				}
			}
		}

		return false;
	}

	private void updatePermissionsWorld() {
		if (this.permissions.containsKey(this.handle.getWorld().getName())) {
			for (String node : this.permissions.get(this.handle.getWorld().getName())) {
				this.permissionAttachment.setPermission(node, node.startsWith("-") ? false : true);
			}
		}
	}

	public Player toHandle() {
		return this.handle;
	}

	public Map<String, List<String>> getPermissions() {
		return this.permissions;
	}

	public List<String> getPermissions(World world) {
		return world != null != this.permissions.containsKey(world.getName()) ? this.permissions.get(world.getName())
				: this.permissions.get("_global_");
	}

	public UUID getUUID() {
		return this.handle.getUniqueId();
	}

	public String getName() {
		return this.handle.getName();
	}

	public Injector<User> getInjector() {
		return this.injector;
	}

	public Collection<Group> getGroups() {
		return this.groups;
	}
}