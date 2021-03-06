package org.cantaloupe.inventory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.cantaloupe.Cantaloupe;
import org.cantaloupe.nbt.NBTTagCompound;
import org.cantaloupe.service.services.NMSService;
import org.cantaloupe.text.Text;
import org.cantaloupe.util.ReflectionHelper;

/**
 * A class used to create an itemstack.
 * 
 * @author Dylan Scheltens
 *
 */
public class ItemStack {
    private org.bukkit.inventory.ItemStack handle;
    private boolean                        isGlowing = false, isDurabilityHidden = false;

    private ItemStack(org.bukkit.inventory.ItemStack handle) {
        this.handle = handle;
    }

    /**
     * Creates and returns a new itemstack.
     * 
     * @param material
     *            The material
     * @return The itemstack
     */
    public static ItemStack of(Material material) {
        return new ItemStack(new org.bukkit.inventory.ItemStack(material));
    }

    /**
     * Creates and returns a new itemstack.
     * 
     * @param material
     *            The material
     * @param data
     *            The data
     * @return The itemstack
     */
    public static ItemStack of(Material material, byte data) {
        return new ItemStack(new org.bukkit.inventory.ItemStack(material, 1, data));
    }

    /**
     * Creates and returns a new itemstack.
     * 
     * @param material
     *            The material
     * @param amount
     *            The amount
     * @return The itemstack
     */
    public static ItemStack of(Material material, int amount) {
        return new ItemStack(new org.bukkit.inventory.ItemStack(material, amount));
    }

    /**
     * Creates and returns a new itemstack.
     * 
     * @param material
     *            The material
     * @param durability
     *            The durability
     * @return The itemstack
     */
    public static ItemStack of(Material material, short durability) {
        return new ItemStack(new org.bukkit.inventory.ItemStack(material)).setDurability(durability);
    }

    /**
     * Creates and returns a new itemstack.
     * 
     * @param material
     *            The material
     * @param color
     *            The color
     * @return The itemstack
     */
    public static ItemStack of(Material material, EnumColor color) {
        return new ItemStack(new org.bukkit.inventory.ItemStack(material)).setDurability(color.getColor());
    }

    /**
     * Creates and returns a new itemstack.
     * 
     * @param material
     *            The material
     * @param amount
     *            The amount
     * @param durability
     *            The durability
     * @return The itemstack
     */
    public static ItemStack of(Material material, int amount, short durability) {
        return new ItemStack(new org.bukkit.inventory.ItemStack(material, amount)).setDurability(durability);
    }

    /**
     * Creates and returns a new itemstack.
     * 
     * @param material
     *            The material
     * @param amount
     *            The amount
     * @param color
     *            The color
     * @return The itemstack
     */
    public static ItemStack of(Material material, EnumColor color, int amount) {
        return new ItemStack(new org.bukkit.inventory.ItemStack(material, amount)).setDurability(color.getColor());
    }

    /**
     * Creates and returns a new itemstack.
     * 
     * @param material
     *            The material
     * @param amount
     *            The amount
     * @param data
     *            The data
     * @return The itemstack
     */
    public static ItemStack of(Material material, int amount, byte data) {
        return new ItemStack(new org.bukkit.inventory.ItemStack(material, amount, data));
    }

    /**
     * Creates and returns a new itemstack.
     * 
     * @param handle
     *            The handle
     * @return The itemstack
     */
    public static ItemStack of(org.bukkit.inventory.ItemStack handle) {
        return new ItemStack(handle);
    }

    /**
     * Sets the display name of the itemstack.
     * 
     * @param name
     *            The display name
     * @return The itemstack
     */
    public ItemStack setDisplayName(Text name) {
        ItemMeta meta = this.handle.getItemMeta();
        meta.setDisplayName(name.toLegacy());

        this.handle.setItemMeta(meta);

        return this;
    }

    /**
     * Sets the lore of the itemstack.
     * 
     * @param lore
     *            The lore
     * @return The itemstack
     */
    public ItemStack setLore(Text... lore) {
        return this.setLore(Arrays.asList(lore));
    }

    /**
     * Sets the lore of the itemstack.
     * 
     * @param lore
     *            The lore
     * @return The itemstack
     */
    public ItemStack setLore(List<Text> lore) {
        List<String> loreTmp = new ArrayList<String>();

        for (Text l : lore) {
            loreTmp.add(l.toLegacy());
        }

        ItemMeta meta = this.handle.getItemMeta();
        meta.setLore(loreTmp);

        this.handle.setItemMeta(meta);

        return this;
    }

    /**
     * Sets the tag of the itemstack.
     * 
     * @param tag
     *            The tag
     * @return The itemstack
     */
    public ItemStack setTag(NBTTagCompound tag) {
        NMSService service = Cantaloupe.getServiceManager().provide(NMSService.class);

        try {
            Object nmsStack = service.getItemStack(this);

            ReflectionHelper.invokeMethod("setTag", nmsStack, tag.toNMS());

            this.handle = (org.bukkit.inventory.ItemStack) ReflectionHelper.invokeStaticMethod("asBukkitCopy", service.BUKKIT_INVENTORY_CRAFTITEMSTACK_CLASS, service.NMS_ITEMSTACK_CLASS.cast(nmsStack));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Sets the item meta of the itemstack.
     * 
     * @param meta
     *            The item meta
     * @return The itemstack
     */
    public ItemStack setItemMeta(ItemMeta meta) {
        this.handle.setItemMeta(meta);

        return this;
    }

    /**
     * Sets the amount of the itemstack.
     * 
     * @param amount
     *            The amount
     * @return The itemstack
     */
    public ItemStack setAmount(int amount) {
        this.handle.setAmount(amount);

        return this;
    }

    /**
     * Sets the material of the itemstack.
     * 
     * @param material
     *            The material
     * @return The itemstack
     */
    public ItemStack setMaterial(Material material) {
        this.handle.setType(material);

        return this;
    }

    /**
     * Sets the durability of the itemstack.
     * 
     * @param durability
     *            The durability
     * @return The itemstack
     */
    public ItemStack setDurability(short durability) {
        this.handle.setDurability(durability);

        return this;
    }

    /**
     * Sets whether or not the item has the enchanted effect.
     * 
     * @param isGlowing
     *            Whether or not it has the effect
     * @return The itemstack
     */
    public ItemStack setGlowing(boolean isGlowing) {
        if (isGlowing) {
            if (!this.isGlowing) {
                ItemMeta meta = this.getItemMeta();
                meta.addEnchant(Enchantment.WATER_WORKER, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                this.setItemMeta(meta);
            }
        } else {
            if (this.isGlowing) {
                ItemMeta meta = this.getItemMeta();
                meta.getEnchants().keySet().forEach(meta::removeEnchant);
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);

                this.setItemMeta(meta);
            }
        }

        this.isGlowing = isGlowing;

        return this;
    }

    /**
     * Sets whether or not the durability of the item should be hidden.
     * 
     * @param hideDurability
     *            Whether or not the durability is hidden
     * @return The itemstack
     */
    public ItemStack setHideDurability(boolean hideDurability) {
        if (hideDurability) {
            if (!this.isDurabilityHidden) {
                ItemMeta meta = this.getItemMeta();
                meta.setUnbreakable(true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

                this.setItemMeta(meta);
            }
        } else {
            if (this.isDurabilityHidden) {
                ItemMeta meta = this.getItemMeta();
                meta.setUnbreakable(false);
                meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

                this.setItemMeta(meta);
            }
        }

        this.isDurabilityHidden = hideDurability;

        return this;
    }

    /**
     * Checks if the itemstack has a tag.
     * 
     * @return True if it does, false if not.
     */
    public boolean hasTag() {
        NMSService service = Cantaloupe.getServiceManager().provide(NMSService.class);

        try {
            return (boolean) ReflectionHelper.invokeMethod("hasTag", service.getItemStack(this));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if the itemstack has an item meta.
     * 
     * @return True if it does, false if not.
     */
    public boolean hasMeta() {
        return this.handle.hasItemMeta();
    }

    /**
     * Checks if the itemstack is empty.
     * 
     * @return True if it is, false if not.
     */
    public boolean isEmpty() {
        return this.handle == null;
    }

    public boolean isGlowing() {
        return this.isGlowing;
    }

    public boolean isDurabilityHidden() {
        return this.isDurabilityHidden;
    }

    /**
     * Returns the handle of the itemstack.
     * 
     * @return The handle
     */
    public org.bukkit.inventory.ItemStack toHandle() {
        return this.handle;
    }

    /**
     * Returns the NMS version of the itemstack.
     * 
     * @return The NMS itemstack
     */
    public Object toNMS() {
        NMSService service = Cantaloupe.getServiceManager().provide(NMSService.class);

        return service.getItemStack(this);
    }

    /**
     * Gets the display name of the itemstack.
     * 
     * @return The display name
     */
    public Text getDisplayName() {
        return this.handle.getItemMeta().getDisplayName() != null ? Text.fromLegacy(this.handle.getItemMeta().getDisplayName()) : null;
    }

    /**
     * Gets the lore of the itemstack.
     * 
     * @return The lore
     */
    public List<Text> getLore() {
        List<Text> lore = new ArrayList<Text>();

        for (String l : this.handle.getItemMeta().getLore()) {
            lore.add(Text.fromLegacy(l));
        }

        return lore;
    }

    /**
     * Gets the tag of the itemstack.
     * 
     * @return The tag
     */
    public NBTTagCompound getTag() {
        NMSService service = Cantaloupe.getServiceManager().provide(NMSService.class);

        try {
            Object nmsTag = ReflectionHelper.invokeMethod("getTag", service.getItemStack(this));

            if (nmsTag != null) {
                return NBTTagCompound.of(nmsTag);
            }

            return NBTTagCompound.of();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }

        return NBTTagCompound.of();
    }

    /**
     * Gets the item meta of the itemstack.
     * 
     * @return The item meta
     */
    public ItemMeta getItemMeta() {
        return this.handle.getItemMeta();
    }

    /**
     * Gets the material of the itemstack.
     * 
     * @return The material
     */
    public Material getMaterial() {
        return this.handle.getType();
    }

    /**
     * Gets the amount of the itemstack.
     * 
     * @return The amount
     */
    public int getAmount() {
        return this.handle.getAmount();
    }

    /**
     * Gets the durability of the itemstack.
     * 
     * @return The durability
     */
    public short getDurability() {
        return this.handle.getDurability();
    }

    /**
     * Gets the color of the itemstack.
     * 
     * @return The color
     */
    public EnumColor getColor() {
        return EnumColor.valueOf(this.handle.getDurability());
    }

    @Override
    public boolean equals(Object other) {
        if (this.handle == null && other == null) {
            return true;
        } else if (this.handle == null || other == null) {
            return false;
        }

        if (other instanceof ItemStack) {
            return this.handle.equals(((ItemStack) other).toHandle());
        } else if (other instanceof org.bukkit.inventory.ItemStack) {
            return this.handle.equals(other);
        }

        return false;
    }

    @Override
    public ItemStack clone() {
        return ItemStack.of(this.handle.clone());
    }
}