package de.zappler.bedwars.api.spigot.player.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GuiManager implements Listener {

    public Player player;
    private List<GuiMenu> guiMenus;
    private Map<ItemStack, Consumer<PlayerInteractEvent>> consumedItemsHotBar;
    private HashMap<Integer, Consumer<InventoryClickEvent>> consumedItemsInventory;
    public boolean isClickable;
    public GuiManager(Player player, Plugin plugin) {
        this.player = player;
        this.guiMenus = new ArrayList<>();
        this.consumedItemsHotBar = new HashMap<>();
        this.consumedItemsInventory = new HashMap<>();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        isClickable = true;
    }

    public void setItem(Integer slot, ItemStack itemStack) {
        player.getInventory().setItem(slot, itemStack);
    }

    public void addItem(ItemStack... itemStack) {
        player.getInventory().addItem(itemStack);
    }

    public GuiMenu getMenu(String name) {
        return guiMenus.stream().filter(guiMenu -> guiMenu.getName().equals(name)).findAny().orElse(null);
    }

    public boolean isExistsMenu(String name) {
        return getMenu(name) != null;
    }

    public void addMenu(GuiMenu guiMenu) {
        if (!isExistsMenu(guiMenu.getName())) {
            guiMenus.add(guiMenu);
        }
    }

    public void removeMenu(GuiMenu guiMenu) {
        if (isExistsMenu(guiMenu.getName())) {
            guiMenus.remove(guiMenu);
        }
    }

    public void openGui(String name) {
        player.openInventory(getMenu(name).getInventory());
    }

    public Consumer<PlayerInteractEvent> getPlayerInteractEvent(ItemStack itemStack) {
        return consumedItemsHotBar.get(itemStack);
    }

    public boolean isPlayerInteractEvent(ItemStack itemStack) {
        return getPlayerInteractEvent(itemStack) != null;
    }

    public void setConsumedItemInHotBar(int slot, ItemStack itemStack, Consumer<PlayerInteractEvent> event) {
        if (slot > 8) {
            return;
        }
        if (!isPlayerInteractEvent(itemStack)) {
            setItem(slot, itemStack);
            consumedItemsHotBar.put(itemStack, event);
        } else {
            removeConsumedItemFromHotBar(itemStack);
            setConsumedItemInHotBar(slot, itemStack, event);
        }
    }

    public void removeConsumedItemFromHotBar(ItemStack itemStack) {
        if (isPlayerInteractEvent(itemStack)) {
            consumedItemsHotBar.remove(itemStack);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }

        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR)) {
            return;
        }
        if (!event.getPlayer().equals(player)) {
            return;
        }

        if (consumedItemsHotBar.containsKey(event.getItem())) {
            getPlayerInteractEvent(event.getItem()).accept(event);
        }
    }






    public Consumer<InventoryClickEvent> getInventoryClickEvent(int slot) {
        return consumedItemsInventory.get(slot);
    }

    public boolean isInventoryClickEvent(int slot) {
        return getInventoryClickEvent(slot) != null;
    }

    public void setConsumedItemInInventory(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> event) {
        if (slot < 8) {
            return;
        }
        if (!isInventoryClickEvent(slot)) {
            setItem(slot, itemStack);
            consumedItemsInventory.put(slot, event);
        } else {
            removeConsumedItemFromInventory(slot);
            setConsumedItemInInventory(slot, itemStack, event);
        }
    }

    public void removeConsumedItemFromInventory(int slot) {
        if (isInventoryClickEvent(slot)) {
            setItem(slot, new ItemStack(Material.AIR));
            consumedItemsInventory.remove(slot);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() != InventoryType.PLAYER) {
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

        if (consumedItemsInventory.containsKey(event.getSlot())) {
            getInventoryClickEvent(event.getSlot()).accept(event);
        }
    }
}