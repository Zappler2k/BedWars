package de.zappler2k.bedWars.objects.shop;

import de.zappler2k.bedWars.objects.shop.init.DefaultSite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class Shop {

    private String shopName;
    private DefaultSite landingCategorySite;

    public abstract void initialize();
}
