package de.zappler2k.bedWars.objects.shop.init;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class DefaultSite {

    private String name;
    private List<DefaultShopPage> defaultShopPages;
    private ItemStack backwardItemStack;
    private ItemStack forwardItemStack;

    public DefaultSite(String name, ItemStack backwardItemStack, ItemStack forwardItemStack) {
        this.name = name;
        this.defaultShopPages = new ArrayList<>();
        this.backwardItemStack = backwardItemStack;
        this.forwardItemStack = forwardItemStack;
    }

    public abstract void initialize();
}
