package de.zappler2k.bedWars.objects.shop.init;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public abstract class DefaultShopPage {

    private Inventory shopInventory;
    private List<Item> items;
    private int siteNumber;

    public DefaultShopPage(Inventory shopInventory, int siteNumber) {
        this.shopInventory = shopInventory;
        this.items = new ArrayList<>();
        this.siteNumber = siteNumber;
    }

    public abstract void initialize();
}
