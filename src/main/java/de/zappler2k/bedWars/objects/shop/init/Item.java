package de.zappler2k.bedWars.objects.shop.init;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class Item {

    private ItemStack itemStack;
    private int index;
    private boolean isPlaceHolder;
}
