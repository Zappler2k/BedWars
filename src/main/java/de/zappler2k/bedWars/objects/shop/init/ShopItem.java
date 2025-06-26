package de.zappler2k.bedWars.objects.shop.init;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShopItem extends Item {

    private PriceType priceType;
    private int price;

}
