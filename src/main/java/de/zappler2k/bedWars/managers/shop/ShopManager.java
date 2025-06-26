package de.zappler2k.bedWars.managers.shop;

import de.zappler2k.bedWars.objects.shop.Shop;
import de.zappler2k.bedWars.objects.shop.init.DefaultSite;
import de.zappler2k.bedWars.objects.shop.init.DefaultShopPage;
import de.zappler2k.bedWars.objects.shop.init.Item;
import de.zappler2k.bedWars.objects.shop.init.PriceType;
import de.zappler2k.bedWars.objects.shop.init.ShopItem;
import de.zappler2k.bedWars.normal.shop.DefaultShop;
import de.zappler2k.bedWars.objects.shop.init.CategoryItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ShopManager {

    private final Map<String, Shop> shops;
    private final File dirConfig;
    private final Plugin plugin;

    public ShopManager(Plugin plugin) {
        this.plugin = plugin;
        this.shops = new HashMap<>();
        this.dirConfig = new File(plugin.getDataFolder(), "configs/shops");
        if (!dirConfig.exists()) {
            dirConfig.mkdirs();
        }
    }

    public void saveShop(Shop shop) {
        if (shop == null) return;

        File shopFile = new File(dirConfig, shop.getShopName() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(shopFile);

        // Save basic shop info
        config.set("shopName", shop.getShopName());
        if (shop.getLandingCategorySite() != null) {
            config.set("landingCategorySite", shop.getLandingCategorySite().getName());
        }

        // Save all sites and their pages
        if (shop.getLandingCategorySite() != null) {
            List<DefaultSite> sites = new ArrayList<>();
            sites.add(shop.getLandingCategorySite());
            // Add other sites if they exist
            if (shop instanceof DefaultShop) {
                sites.addAll(((DefaultShop) shop).getCategorySites());
            }

            for (DefaultSite site : sites) {
                String sitePath = "sites." + site.getName();
                config.set(sitePath + ".name", site.getName());

                // Save pages for this site
                if (site.getDefaultShopPages() != null) {
                    for (int i = 0; i < site.getDefaultShopPages().size(); i++) {
                        DefaultShopPage page = site.getDefaultShopPages().get(i);
                        String pagePath = sitePath + ".pages." + i;
                        config.set(pagePath + ".siteNumber", page.getSiteNumber());

                        // Save items in this page
                        if (page.getItems() != null) {
                            for (int j = 0; j < page.getItems().size(); j++) {
                                Item item = page.getItems().get(j);
                                String itemPath = pagePath + ".items." + j;

                                if (item instanceof ShopItem) {
                                    ShopItem shopItem = (ShopItem) item;
                                    ItemStack itemStack = shopItem.getItemStack();
                                    ItemMeta meta = itemStack.getItemMeta();
                                    
                                    config.set(itemPath + ".type", "shop");
                                    config.set(itemPath + ".material", itemStack.getType().name());
                                    config.set(itemPath + ".amount", itemStack.getAmount());
                                    
                                    // Save display name and lore
                                    if (meta != null) {
                                        if (meta.hasDisplayName()) {
                                            config.set(itemPath + ".displayName", meta.getDisplayName());
                                        }
                                        if (meta.hasLore()) {
                                            config.set(itemPath + ".lore", meta.getLore());
                                        }
                                    }
                                    
                                    // Save enchantments
                                    if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchants()) {
                                        List<Map<String, Object>> enchants = new ArrayList<>();
                                        for (Map.Entry<Enchantment, Integer> enchant : itemStack.getEnchantments().entrySet()) {
                                            Map<String, Object> enchantData = new HashMap<>();
                                            enchantData.put("type", enchant.getKey().getName());
                                            enchantData.put("level", enchant.getValue());
                                            enchants.add(enchantData);
                                        }
                                        config.set(itemPath + ".enchantments", enchants);
                                    }
                                    
                                    config.set(itemPath + ".priceType", shopItem.getPriceType().name());
                                    config.set(itemPath + ".price", shopItem.getPrice());
                                    config.set(itemPath + ".index", shopItem.getIndex());
                                    config.set(itemPath + ".isPlaceHolder", shopItem.isPlaceHolder());
                                } else if (item instanceof CategoryItem) {
                                    CategoryItem categoryItem = (CategoryItem) item;
                                    ItemStack itemStack = categoryItem.getItemStack();
                                    ItemMeta meta = itemStack.getItemMeta();
                                    
                                    config.set(itemPath + ".type", "category");
                                    config.set(itemPath + ".material", itemStack.getType().name());
                                    config.set(itemPath + ".amount", itemStack.getAmount());
                                    
                                    // Save display name and lore
                                    if (meta != null) {
                                        if (meta.hasDisplayName()) {
                                            config.set(itemPath + ".displayName", meta.getDisplayName());
                                        }
                                        if (meta.hasLore()) {
                                            config.set(itemPath + ".lore", meta.getLore());
                                        }
                                    }
                                    
                                    // Save enchantments
                                    if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchants()) {
                                        List<Map<String, Object>> enchants = new ArrayList<>();
                                        for (Map.Entry<Enchantment, Integer> enchant : itemStack.getEnchantments().entrySet()) {
                                            Map<String, Object> enchantData = new HashMap<>();
                                            enchantData.put("type", enchant.getKey().getName());
                                            enchantData.put("level", enchant.getValue());
                                            enchants.add(enchantData);
                                        }
                                        config.set(itemPath + ".enchantments", enchants);
                                    }
                                    
                                    config.set(itemPath + ".category", categoryItem.getDefaultSite().getName());
                                    config.set(itemPath + ".index", categoryItem.getIndex());
                                    config.set(itemPath + ".isPlaceHolder", categoryItem.isPlaceHolder());
                                }
                            }
                        }
                    }
                }
            }
        }

        try {
            String yamlString = config.saveToString();
            java.io.FileOutputStream fos = new java.io.FileOutputStream(shopFile);
            fos.write(yamlString.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            fos.close();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save shop configuration for " + shop.getShopName(), e);
        }
    }

    public Shop loadShop(File shopFile) {
        if (!shopFile.exists() || !shopFile.getName().endsWith(".yml")) {
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(shopFile);
        
        // Validate basic structure
        if (!validateConfig(config)) {
            plugin.getLogger().warning("Invalid shop configuration in " + shopFile.getName());
            return null;
        }

        // Get shop name from filename (remove .yml extension)
        String shopName = shopFile.getName().replace(".yml", "");
        Shop shop = new Shop() {
            @Override
            public void initialize() {
                // Initialize with loaded values
            }
        };
        shop.setShopName(shopName);

        // Load sites
        if (config.contains("sites")) {
            for (String siteKey : config.getConfigurationSection("sites").getKeys(false)) {
                String sitePath = "sites." + siteKey;
                String siteName = config.getString(sitePath + ".name");
                
                DefaultSite site = new DefaultSite(siteName, null, null) {
                    @Override
                    public void initialize() {
                        // Load pages for this site
                        if (config.contains(sitePath + ".pages")) {
                            for (String pageKey : config.getConfigurationSection(sitePath + ".pages").getKeys(false)) {
                                String pagePath = sitePath + ".pages." + pageKey;
                                int siteNumber = config.getInt(pagePath + ".siteNumber");
                                
                                DefaultShopPage page = new DefaultShopPage(null, siteNumber) {
                                    @Override
                                    public void initialize() {
                                        // Load items for this page
                                        if (config.contains(pagePath + ".items")) {
                                            for (String itemKey : config.getConfigurationSection(pagePath + ".items").getKeys(false)) {
                                                String itemPath = pagePath + ".items." + itemKey;
                                                String itemType = config.getString(itemPath + ".type");
                                                
                                                if ("shop".equals(itemType)) {
                                                    ShopItem shopItem = new ShopItem();
                                                    ItemStack itemStack = new ItemStack(
                                                        Material.valueOf(config.getString(itemPath + ".material")),
                                                        config.getInt(itemPath + ".amount", 1)
                                                    );
                                                    
                                                    // Load display name and lore
                                                    ItemMeta meta = itemStack.getItemMeta();
                                                    if (meta != null) {
                                                        if (config.contains(itemPath + ".displayName")) {
                                                            meta.setDisplayName(config.getString(itemPath + ".displayName"));
                                                        }
                                                        if (config.contains(itemPath + ".lore")) {
                                                            meta.setLore(config.getStringList(itemPath + ".lore"));
                                                        }
                                                        itemStack.setItemMeta(meta);
                                                    }
                                                    
                                                    // Load enchantments
                                                    if (config.contains(itemPath + ".enchantments")) {
                                                        List<Map<?, ?>> enchants = config.getMapList(itemPath + ".enchantments");
                                                        for (Map<?, ?> enchantData : enchants) {
                                                            String enchantName = (String) enchantData.get("type");
                                                            int level = (int) enchantData.get("level");
                                                            Enchantment enchant = Enchantment.getByName(enchantName);
                                                            if (enchant != null) {
                                                                itemStack.addEnchantment(enchant, level);
                                                            }
                                                        }
                                                    }
                                                    
                                                    shopItem.setItemStack(itemStack);
                                                    shopItem.setPriceType(PriceType.valueOf(config.getString(itemPath + ".priceType")));
                                                    shopItem.setPrice(config.getInt(itemPath + ".price"));
                                                    shopItem.setIndex(config.getInt(itemPath + ".index"));
                                                    shopItem.setPlaceHolder(config.getBoolean(itemPath + ".isPlaceHolder"));
                                                    this.getItems().add(shopItem);
                                                } else if ("category".equals(itemType)) {
                                                    CategoryItem categoryItem = new CategoryItem();
                                                    ItemStack itemStack = new ItemStack(
                                                        Material.valueOf(config.getString(itemPath + ".material")),
                                                        config.getInt(itemPath + ".amount", 1)
                                                    );
                                                    
                                                    // Load display name and lore
                                                    ItemMeta meta = itemStack.getItemMeta();
                                                    if (meta != null) {
                                                        if (config.contains(itemPath + ".displayName")) {
                                                            meta.setDisplayName(config.getString(itemPath + ".displayName"));
                                                        }
                                                        if (config.contains(itemPath + ".lore")) {
                                                            meta.setLore(config.getStringList(itemPath + ".lore"));
                                                        }
                                                        itemStack.setItemMeta(meta);
                                                    }
                                                    
                                                    // Load enchantments
                                                    if (config.contains(itemPath + ".enchantments")) {
                                                        List<Map<?, ?>> enchants = config.getMapList(itemPath + ".enchantments");
                                                        for (Map<?, ?> enchantData : enchants) {
                                                            String enchantName = (String) enchantData.get("type");
                                                            int level = (int) enchantData.get("level");
                                                            Enchantment enchant = Enchantment.getByName(enchantName);
                                                            if (enchant != null) {
                                                                itemStack.addEnchantment(enchant, level);
                                                            }
                                                        }
                                                    }
                                                    
                                                    categoryItem.setItemStack(itemStack);
                                                    categoryItem.setIndex(config.getInt(itemPath + ".index"));
                                                    categoryItem.setPlaceHolder(config.getBoolean(itemPath + ".isPlaceHolder"));
                                                    this.getItems().add(categoryItem);
                                                }
                                            }
                                        }
                                    }
                                };
                                page.initialize();
                                this.getDefaultShopPages().add(page);
                            }
                        }
                    }
                };
                site.initialize();
                
                // Set as landing site if it matches the config
                if (siteName.equals(config.getString("landingCategorySite"))) {
                    shop.setLandingCategorySite(site);
                }
            }
        }

        // Add shop to map with filename as key
        shops.put(shopName, shop);
        return shop;
    }

    private boolean validateConfig(YamlConfiguration config) {
        // Check basic required fields
        if (!config.contains("shopName")) {
            return false;
        }

        // Check sites structure
        if (config.contains("sites")) {
            for (String siteKey : config.getConfigurationSection("sites").getKeys(false)) {
                String sitePath = "sites." + siteKey;
                
                // Check site name
                if (!config.contains(sitePath + ".name")) {
                    return false;
                }

                // Check pages structure
                if (config.contains(sitePath + ".pages")) {
                    for (String pageKey : config.getConfigurationSection(sitePath + ".pages").getKeys(false)) {
                        String pagePath = sitePath + ".pages." + pageKey;
                        
                        // Check page number
                        if (!config.contains(pagePath + ".siteNumber")) {
                            return false;
                        }

                        // Check items structure
                        if (config.contains(pagePath + ".items")) {
                            for (String itemKey : config.getConfigurationSection(pagePath + ".items").getKeys(false)) {
                                String itemPath = pagePath + ".items." + itemKey;
                                
                                // Check item type
                                if (!config.contains(itemPath + ".type")) {
                                    return false;
                                }

                                String itemType = config.getString(itemPath + ".type");
                                if ("shop".equals(itemType)) {
                                    // Validate shop item fields
                                    if (!config.contains(itemPath + ".material") ||
                                        !config.contains(itemPath + ".priceType") ||
                                        !config.contains(itemPath + ".price")) {
                                        return false;
                                    }
                                } else if ("category".equals(itemType)) {
                                    // Validate category item fields
                                    if (!config.contains(itemPath + ".material") ||
                                        !config.contains(itemPath + ".category")) {
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public Shop getShop(String name) {
        return shops.get(name);
    }

    public Map<String, Shop> getAllShops() {
        return shops;
    }
}
