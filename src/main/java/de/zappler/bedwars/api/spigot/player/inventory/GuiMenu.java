package de.zappler.bedwars.api.spigot.player.inventory;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

@Getter
public abstract class GuiMenu implements Listener {

    private String name;
    public Inventory inventory;
    public boolean isClickable;
    public Player player;
    private HashMap<Integer, Consumer<InventoryClickEvent>> consumedItems;

    public GuiMenu(String name, int size, Player player, Plugin plugin) {
        this.name = name;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, size, name);
        this.consumedItems = new HashMap<>();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        isClickable = true;
    }

    public GuiMenu(String name, InventoryType type, Plugin plugin) {
        this.name = name;
        this.inventory = Bukkit.createInventory(null, type, name);
        this.consumedItems = new HashMap<>();
        System.out.println("test");
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        isClickable = true;
    }

    public void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public void fillInventoryFromTo(int from, int to, List<ItemStack> itemStacks) {
        for (int i = from; i < to; i++) {
            int finalI = i;
            itemStacks.stream().forEach(itemStack -> setItem(finalI, itemStack));
        }
    }

    public void fillInventory(ItemStack itemStack) {
        fillInventoryFromTo(0, inventory.getSize(), Arrays.asList(itemStack));
    }

    public Consumer<InventoryClickEvent> getInventoryClickEvent(int slot) {
        return consumedItems.get(slot);
    }

    public boolean isInventoryClickEvent(int slot) {
        return getInventoryClickEvent(slot) != null;
    }

    public void setConsumedItem(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> event) {
        if (!isInventoryClickEvent(slot)) {
            setItem(slot, itemStack);
            consumedItems.put(slot, event);
        } else {
            removeConsumedItem(slot);
            setConsumedItem(slot, itemStack, event);
        }
    }

    public void removeConsumedItem(int slot) {
        if (isInventoryClickEvent(slot)) {
            setItem(slot, new ItemStack(Material.AIR));
            consumedItems.remove(slot);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER) {
            return;
        }

        if (!isClickable) {
            event.setCancelled(true);
        }

        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) {
            return;
        }
        if (!event.getWhoClicked().equals(player)) {
            return;
        }

        if (consumedItems.containsKey(event.getSlot())) {
            getInventoryClickEvent(event.getSlot()).accept(event);
        }
    }
}
