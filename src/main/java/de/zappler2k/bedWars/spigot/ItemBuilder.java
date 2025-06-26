package de.zappler2k.bedWars.spigot;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        itemMeta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        List<String> currentLore = itemMeta.getLore();
        if (currentLore == null) {
            currentLore = new ArrayList<>();
        }
        currentLore.addAll(Arrays.asList(lore));
        itemMeta.setLore(currentLore);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach((enchantment, level) -> itemMeta.addEnchant(enchantment, level, true));
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        itemMeta.removeEnchant(enchantment);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder removeItemFlags(ItemFlag... flags) {
        itemMeta.removeItemFlags(flags);
        return this;
    }

    public ItemBuilder setCustomModelData(Integer customModelData) {
        itemMeta.setCustomModelData(customModelData);
        return this;
    }

    // Leather Armor specific methods
    public ItemBuilder setLeatherArmorColor(Color color) {
        if (itemMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) itemMeta).setColor(color);
        }
        return this;
    }

    public ItemBuilder setPotionColor(Color color) {
        if (itemMeta instanceof PotionMeta) {
            ((PotionMeta) itemMeta).setColor(color);
        }
        return this;
    }

    // Book specific methods
    public ItemBuilder setBookTitle(String title) {
        if (itemMeta instanceof BookMeta) {
            ((BookMeta) itemMeta).setTitle(title);
        }
        return this;
    }

    public ItemBuilder setBookAuthor(String author) {
        if (itemMeta instanceof BookMeta) {
            ((BookMeta) itemMeta).setAuthor(author);
        }
        return this;
    }

    public ItemBuilder setBookPages(String... pages) {
        if (itemMeta instanceof BookMeta) {
            ((BookMeta) itemMeta).setPages(pages);
        }
        return this;
    }

    // Skull specific methods
    public ItemBuilder setSkullOwner(String owner) {
        if (itemMeta instanceof SkullMeta) {
            ((SkullMeta) itemMeta).setOwner(owner);
        }
        return this;
    }

    // Firework specific methods
    public ItemBuilder setFireworkPower(int power) {
        if (itemMeta instanceof FireworkMeta) {
            ((FireworkMeta) itemMeta).setPower(power);
        }
        return this;
    }

    // Compass specific methods
    public ItemBuilder setCompassLodestone(org.bukkit.Location location) {
        if (itemMeta instanceof CompassMeta) {
            ((CompassMeta) itemMeta).setLodestone(location);
            ((CompassMeta) itemMeta).setLodestoneTracked(true);
        }
        return this;
    }

    // Banner specific methods
    public ItemBuilder addBannerPattern(org.bukkit.block.banner.Pattern pattern) {
        if (itemMeta instanceof BannerMeta) {
            ((BannerMeta) itemMeta).addPattern(pattern);
        }
        return this;
    }

    public ItemBuilder setBannerPatterns(List<org.bukkit.block.banner.Pattern> patterns) {
        if (itemMeta instanceof BannerMeta) {
            ((BannerMeta) itemMeta).setPatterns(patterns);
        }
        return this;
    }

    // Map specific methods
    public ItemBuilder setMapColor(Color color) {
        if (itemMeta instanceof MapMeta) {
            ((MapMeta) itemMeta).setColor(color);
        }
        return this;
    }

    public ItemBuilder setMapLocationName(String locationName) {
        if (itemMeta instanceof MapMeta) {
            ((MapMeta) itemMeta).setLocationName(locationName);
        }
        return this;
    }

    // Crossbow specific methods
    public ItemBuilder addCrossbowProjectile(ItemStack projectile) {
        if (itemMeta instanceof CrossbowMeta) {
            ((CrossbowMeta) itemMeta).addChargedProjectile(projectile);
        }
        return this;
    }

    public ItemBuilder setCrossbowProjectiles(List<ItemStack> projectiles) {
        if (itemMeta instanceof CrossbowMeta) {
            ((CrossbowMeta) itemMeta).setChargedProjectiles(projectiles);
        }
        return this;
    }

    // Tropical Fish specific methods
    public ItemBuilder setTropicalFishBodyColor(DyeColor color) {
        if (itemMeta instanceof TropicalFishBucketMeta) {
            ((TropicalFishBucketMeta) itemMeta).setBodyColor(color);
        }
        return this;
    }

    public ItemBuilder setTropicalFishPattern(org.bukkit.entity.TropicalFish.Pattern pattern) {
        if (itemMeta instanceof TropicalFishBucketMeta) {
            ((TropicalFishBucketMeta) itemMeta).setPattern(pattern);
        }
        return this;
    }

    public ItemBuilder setTropicalFishPatternColor(DyeColor color) {
        if (itemMeta instanceof TropicalFishBucketMeta) {
            ((TropicalFishBucketMeta) itemMeta).setPatternColor(color);
        }
        return this;
    }

    // Suspicious Stew specific methods
    public ItemBuilder addSuspiciousStewEffect(org.bukkit.potion.PotionEffect effect) {
        if (itemMeta instanceof SuspiciousStewMeta) {
            ((SuspiciousStewMeta) itemMeta).addCustomEffect(effect, true);
        }
        return this;
    }

    // Knowledge Book specific methods
    public ItemBuilder addKnowledgeBookRecipe(NamespacedKey recipe) {
        if (itemMeta instanceof KnowledgeBookMeta) {
            ((KnowledgeBookMeta) itemMeta).addRecipe(recipe);
        }
        return this;
    }

    public ItemBuilder addKnowledgeBookRecipes(List<NamespacedKey> recipes) {
        if (itemMeta instanceof KnowledgeBookMeta) {
            for (NamespacedKey recipe : recipes) {
                ((KnowledgeBookMeta) itemMeta).addRecipe(recipe);
            }
        }
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    // Utility methods
    public static ItemBuilder fromConfig(Map<String, Object> config) {
        Material material = Material.valueOf((String) config.get("material"));
        ItemBuilder builder = new ItemBuilder(material);
        
        if (config.containsKey("amount")) {
            builder.setAmount((int) config.get("amount"));
        }
        
        if (config.containsKey("displayName")) {
            builder.setDisplayName((String) config.get("displayName"));
        }
        
        if (config.containsKey("lore")) {
            List<String> lore = (List<String>) config.get("lore");
            builder.setLore(lore);
        }
        
        if (config.containsKey("enchantments")) {
            List<Map<String, Object>> enchants = (List<Map<String, Object>>) config.get("enchantments");
            for (Map<String, Object> enchant : enchants) {
                String type = (String) enchant.get("type");
                int level = (int) enchant.get("level");
                Enchantment enchantment = Enchantment.getByName(type);
                if (enchantment != null) {
                    builder.addEnchantment(enchantment, level);
                }
            }
        }
        
        if (config.containsKey("unbreakable")) {
            builder.setUnbreakable((boolean) config.get("unbreakable"));
        }
        
        if (config.containsKey("customModelData")) {
            builder.setCustomModelData((Integer) config.get("customModelData"));
        }
        
        return builder;
    }
}
