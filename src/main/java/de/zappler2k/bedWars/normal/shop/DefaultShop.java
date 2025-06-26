// -*- coding: utf-8 -*-
package de.zappler2k.bedWars.normal.shop;

import de.zappler2k.bedWars.objects.shop.Shop;
import de.zappler2k.bedWars.objects.shop.init.DefaultSite;
import de.zappler2k.bedWars.objects.shop.init.DefaultShopPage;
import de.zappler2k.bedWars.objects.shop.init.PriceType;
import de.zappler2k.bedWars.objects.shop.init.ShopItem;
import de.zappler2k.bedWars.objects.shop.init.CategoryItem;
import de.zappler2k.bedWars.spigot.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DefaultShop extends Shop {

    private static final String[] CATEGORIES = {"Blocks", "Weapons", "Armor", "Tools", "Food", "Special"};
    @Getter
    private List<DefaultSite> categorySites;
    private DefaultSite landingPage;

    public DefaultShop() {
        super("DefaultShop", null);
        this.categorySites = new ArrayList<>();
        initialize();
    }

    @Override
    public void initialize() {
        // Create category sites first
        for (String category : CATEGORIES) {
            DefaultSite site = createCategorySite(category);
            categorySites.add(site);
        }

        // Create landing page with all categories
        landingPage = createLandingPage();
        setLandingCategorySite(landingPage);
    }

    private DefaultSite createLandingPage() {
        DefaultSite landing = new DefaultSite("Categories", null, null) {
            @Override
            public void initialize() {
                DefaultShopPage page = new DefaultShopPage(null, 0) {
                    @Override
                    public void initialize() {
                        List<CategoryItem> items = new ArrayList<>();

                        for (int i = 0; i < categorySites.size(); i++) {
                            DefaultSite category = categorySites.get(i);
                            CategoryItem categoryItem = new CategoryItem();

                            Material material = switch (category.getName()) {
                                case "Blocks" -> Material.WHITE_WOOL;
                                case "Weapons" -> Material.IRON_SWORD;
                                case "Armor" -> Material.IRON_CHESTPLATE;
                                case "Tools" -> Material.IRON_PICKAXE;
                                case "Food" -> Material.BREAD;
                                case "Special" -> Material.TNT;
                                default -> Material.BARRIER;
                            };
                            
                            ItemStack itemStack = new ItemBuilder(material)
                                .setDisplayName("&6" + category.getName())
                                .setLore("&7Click to view " + category.getName() + " items")
                                .build();
                            
                            categoryItem.setItemStack(itemStack);
                            categoryItem.setDefaultSite(category);
                            categoryItem.setIndex(i);
                            categoryItem.setPlaceHolder(false);
                            items.add(categoryItem);
                        }
                        
                        this.setItems(new ArrayList<>(items));
                    }
                };
                page.initialize();
                this.getDefaultShopPages().add(page);
            }
        };
        landing.initialize();
        return landing;
    }

    private DefaultSite createCategorySite(String category) {
        DefaultSite site = new DefaultSite(category, null, null) {
            @Override
            public void initialize() {
                // Create pages for this category
                List<DefaultShopPage> pages = new ArrayList<>();
                
                switch (category) {
                    case "Blocks":
                        pages.add(createBlocksPage());
                        break;
                    case "Weapons":
                        pages.add(createWeaponsPage());
                        break;
                    case "Armor":
                        pages.add(createArmorPage());
                        break;
                    case "Tools":
                        pages.add(createToolsPage());
                        break;
                    case "Food":
                        pages.add(createFoodPage());
                        break;
                    case "Special":
                        pages.add(createSpecialPage());
                        break;
                }
                
                this.getDefaultShopPages().addAll(pages);
            }
        };
        site.initialize();
        return site;
    }

    private DefaultShopPage createBlocksPage() {
        DefaultShopPage page = new DefaultShopPage(null, 0) {
            @Override
            public void initialize() {
                List<ShopItem> items = new ArrayList<>();
                
                // Wool blocks
                ShopItem wool = new ShopItem();
                wool.setItemStack(new ItemBuilder(Material.WHITE_WOOL)
                    .setAmount(16)
                    .setDisplayName("&fWool")
                    .setLore("&7Basic building block", "&7Cost: &c5 Bronze")
                    .build());
                wool.setPriceType(PriceType.BRONZE);
                wool.setPrice(5);
                wool.setIndex(0);
                items.add(wool);
                
                // Sandstone
                ShopItem sandstone = new ShopItem();
                sandstone.setItemStack(new ItemBuilder(Material.SANDSTONE)
                    .setAmount(16)
                    .setDisplayName("&eSandstone")
                    .setLore("&7Stronger than wool", "&7Cost: &c12 Bronze")
                    .build());
                sandstone.setPriceType(PriceType.BRONZE);
                sandstone.setPrice(12);
                sandstone.setIndex(1);
                items.add(sandstone);
                
                // Endstone
                ShopItem endstone = new ShopItem();
                endstone.setItemStack(new ItemBuilder(Material.END_STONE)
                    .setAmount(12)
                    .setDisplayName("&eEnd Stone")
                    .setLore("&7Very strong block", "&7Cost: &f24 Iron")
                    .build());
                endstone.setPriceType(PriceType.IRON);
                endstone.setPrice(24);
                endstone.setIndex(2);
                items.add(endstone);
                
                // Oak Planks
                ShopItem planks = new ShopItem();
                planks.setItemStack(new ItemBuilder(Material.OAK_PLANKS)
                    .setAmount(16)
                    .setDisplayName("&6Oak Planks")
                    .setLore("&7Basic building block", "&7Cost: &c4 Bronze")
                    .build());
                planks.setPriceType(PriceType.BRONZE);
                planks.setPrice(4);
                planks.setIndex(3);
                items.add(planks);
                
                // Stone
                ShopItem stone = new ShopItem();
                stone.setItemStack(new ItemBuilder(Material.STONE)
                    .setAmount(16)
                    .setDisplayName("&7Stone")
                    .setLore("&7Strong building block", "&7Cost: &f12 Iron")
                    .build());
                stone.setPriceType(PriceType.IRON);
                stone.setPrice(12);
                stone.setIndex(4);
                items.add(stone);
                
                // Iron Block
                ShopItem ironBlock = new ShopItem();
                ironBlock.setItemStack(new ItemBuilder(Material.IRON_BLOCK)
                    .setAmount(4)
                    .setDisplayName("&fIron Block")
                    .setLore("&7Very strong block", "&7Cost: &e12 Gold")
                    .build());
                ironBlock.setPriceType(PriceType.GOLD);
                ironBlock.setPrice(12);
                ironBlock.setIndex(5);
                items.add(ironBlock);
                
                // Nether Brick
                ShopItem netherBrick = new ShopItem();
                netherBrick.setItemStack(new ItemBuilder(Material.NETHER_BRICKS)
                    .setAmount(8)
                    .setDisplayName("&4Nether Brick")
                    .setLore("&7Strong block", "&7Cost: &f16 Iron")
                    .build());
                netherBrick.setPriceType(PriceType.IRON);
                netherBrick.setPrice(16);
                netherBrick.setIndex(6);
                items.add(netherBrick);
                
                this.setItems(new ArrayList<>(items));
            }
        };
        page.initialize();
        return page;
    }

    private DefaultShopPage createWeaponsPage() {
        DefaultShopPage page = new DefaultShopPage(null, 0) {
            @Override
            public void initialize() {
                List<ShopItem> items = new ArrayList<>();
                
                // Stone Sword
                ShopItem stoneSword = new ShopItem();
                stoneSword.setItemStack(new ItemBuilder(Material.STONE_SWORD)
                    .setDisplayName("&7Stone Sword")
                    .setLore("&7Basic weapon", "&7Cost: &f10 Iron")
                    .addEnchantment(Enchantment.SHARPNESS, 1)
                    .build());
                stoneSword.setPriceType(PriceType.IRON);
                stoneSword.setPrice(10);
                stoneSword.setIndex(0);
                items.add(stoneSword);
                
                // Iron Sword
                ShopItem ironSword = new ShopItem();
                ironSword.setItemStack(new ItemBuilder(Material.IRON_SWORD)
                    .setDisplayName("&fIron Sword")
                    .setLore("&7Strong weapon", "&7Cost: &e7 Gold")
                    .addEnchantment(Enchantment.SHARPNESS, 2)
                    .build());
                ironSword.setPriceType(PriceType.GOLD);
                ironSword.setPrice(7);
                ironSword.setIndex(1);
                items.add(ironSword);
                
                // Diamond Sword
                ShopItem diamondSword = new ShopItem();
                diamondSword.setItemStack(new ItemBuilder(Material.DIAMOND_SWORD)
                    .setDisplayName("&bDiamond Sword")
                    .setLore("&7Powerful weapon", "&7Cost: &b4 Diamond")
                    .addEnchantment(Enchantment.SHARPNESS, 3)
                    .build());
                diamondSword.setPriceType(PriceType.DIAMOND);
                diamondSword.setPrice(4);
                diamondSword.setIndex(2);
                items.add(diamondSword);
                
                // Bow
                ShopItem bow = new ShopItem();
                bow.setItemStack(new ItemBuilder(Material.BOW)
                    .setDisplayName("&eBow")
                    .setLore("&7Ranged weapon", "&7Cost: &e12 Gold")
                    .addEnchantment(Enchantment.POWER, 1)
                    .build());
                bow.setPriceType(PriceType.GOLD);
                bow.setPrice(12);
                bow.setIndex(3);
                items.add(bow);
                
                // Arrows
                ShopItem arrows = new ShopItem();
                arrows.setItemStack(new ItemBuilder(Material.ARROW)
                    .setAmount(8)
                    .setDisplayName("&fArrows")
                    .setLore("&7Ammunition for bow", "&7Cost: &e2 Gold")
                    .build());
                arrows.setPriceType(PriceType.GOLD);
                arrows.setPrice(2);
                arrows.setIndex(4);
                items.add(arrows);
                
                this.setItems(new ArrayList<>(items));
            }
        };
        page.initialize();
        return page;
    }

    private DefaultShopPage createArmorPage() {
        DefaultShopPage page = new DefaultShopPage(null, 0) {
            @Override
            public void initialize() {
                List<ShopItem> items = new ArrayList<>();
                
                // Chainmail Armor
                ShopItem chainmail = new ShopItem();
                chainmail.setItemStack(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE)
                    .setDisplayName("&7Chainmail Armor")
                    .setLore("&7Basic protection", "&7Cost: &f40 Iron")
                    .addEnchantment(Enchantment.PROTECTION, 1)
                    .build());
                chainmail.setPriceType(PriceType.IRON);
                chainmail.setPrice(40);
                chainmail.setIndex(0);
                items.add(chainmail);
                
                // Iron Armor
                ShopItem iron = new ShopItem();
                iron.setItemStack(new ItemBuilder(Material.IRON_CHESTPLATE)
                    .setDisplayName("&fIron Armor")
                    .setLore("&7Good protection", "&7Cost: &e12 Gold")
                    .addEnchantment(Enchantment.PROTECTION, 2)
                    .build());
                iron.setPriceType(PriceType.GOLD);
                iron.setPrice(12);
                iron.setIndex(1);
                items.add(iron);
                
                // Diamond Armor
                ShopItem diamond = new ShopItem();
                diamond.setItemStack(new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .setDisplayName("&bDiamond Armor")
                    .setLore("&7Best protection", "&7Cost: &b6 Diamond")
                    .addEnchantment(Enchantment.PROTECTION, 3)
                    .build());
                diamond.setPriceType(PriceType.DIAMOND);
                diamond.setPrice(6);
                diamond.setIndex(2);
                items.add(diamond);
                
                this.setItems(new ArrayList<>(items));
            }
        };
        page.initialize();
        return page;
    }

    private DefaultShopPage createToolsPage() {
        DefaultShopPage page = new DefaultShopPage(null, 0) {
            @Override
            public void initialize() {
                List<ShopItem> items = new ArrayList<>();
                
                // Shears
                ShopItem shears = new ShopItem();
                shears.setItemStack(new ItemBuilder(Material.SHEARS)
                    .setDisplayName("&fShears")
                    .setLore("&7Break wool faster", "&7Cost: &f4 Iron")
                    .build());
                shears.setPriceType(PriceType.IRON);
                shears.setPrice(4);
                shears.setIndex(0);
                items.add(shears);
                
                // Pickaxe
                ShopItem pickaxe = new ShopItem();
                pickaxe.setItemStack(new ItemBuilder(Material.IRON_PICKAXE)
                    .setDisplayName("&fIron Pickaxe")
                    .setLore("&7Mine blocks faster", "&7Cost: &f10 Iron")
                    .addEnchantment(Enchantment.EFFICIENCY, 1)
                    .build());
                pickaxe.setPriceType(PriceType.IRON);
                pickaxe.setPrice(10);
                pickaxe.setIndex(1);
                items.add(pickaxe);
                
                // Axe
                ShopItem axe = new ShopItem();
                axe.setItemStack(new ItemBuilder(Material.IRON_AXE)
                    .setDisplayName("&fIron Axe")
                    .setLore("&7Break wood faster", "&7Cost: &f10 Iron")
                    .addEnchantment(Enchantment.EFFICIENCY, 1)
                    .build());
                axe.setPriceType(PriceType.IRON);
                axe.setPrice(10);
                axe.setIndex(2);
                items.add(axe);
                
                // Diamond Pickaxe
                ShopItem diamondPickaxe = new ShopItem();
                diamondPickaxe.setItemStack(new ItemBuilder(Material.DIAMOND_PICKAXE)
                    .setDisplayName("&bDiamond Pickaxe")
                    .setLore("&7Mine blocks very fast", "&7Cost: &b4 Diamond")
                    .addEnchantment(Enchantment.EFFICIENCY, 2)
                    .build());
                diamondPickaxe.setPriceType(PriceType.DIAMOND);
                diamondPickaxe.setPrice(4);
                diamondPickaxe.setIndex(3);
                items.add(diamondPickaxe);
                
                this.setItems(new ArrayList<>(items));
            }
        };
        page.initialize();
        return page;
    }

    private DefaultShopPage createFoodPage() {
        DefaultShopPage page = new DefaultShopPage(null, 0) {
            @Override
            public void initialize() {
                List<ShopItem> items = new ArrayList<>();
                
                // Bread
                ShopItem bread = new ShopItem();
                bread.setItemStack(new ItemBuilder(Material.BREAD)
                    .setAmount(5)
                    .setDisplayName("&eBread")
                    .setLore("&7Basic food", "&7Cost: &c3 Bronze")
                    .build());
                bread.setPriceType(PriceType.BRONZE);
                bread.setPrice(3);
                bread.setIndex(0);
                items.add(bread);
                
                // Golden Apple
                ShopItem goldenApple = new ShopItem();
                goldenApple.setItemStack(new ItemBuilder(Material.GOLDEN_APPLE)
                    .setDisplayName("&6Golden Apple")
                    .setLore("&7Restores health", "&7Cost: &e3 Gold")
                    .build());
                goldenApple.setPriceType(PriceType.GOLD);
                goldenApple.setPrice(3);
                goldenApple.setIndex(1);
                items.add(goldenApple);
                
                // Steak
                ShopItem steak = new ShopItem();
                steak.setItemStack(new ItemBuilder(Material.COOKED_BEEF)
                    .setAmount(3)
                    .setDisplayName("&cSteak")
                    .setLore("&7Good food", "&7Cost: &f6 Iron")
                    .build());
                steak.setPriceType(PriceType.IRON);
                steak.setPrice(6);
                steak.setIndex(2);
                items.add(steak);
                
                this.setItems(new ArrayList<>(items));
            }
        };
        page.initialize();
        return page;
    }

    private DefaultShopPage createSpecialPage() {
        DefaultShopPage page = new DefaultShopPage(null, 0) {
            @Override
            public void initialize() {
                List<ShopItem> items = new ArrayList<>();
                
                // TNT
                ShopItem tnt = new ShopItem();
                tnt.setItemStack(new ItemBuilder(Material.TNT)
                    .setDisplayName("&cTNT")
                    .setLore("&7Explosive block", "&7Cost: &e4 Gold")
                    .build());
                tnt.setPriceType(PriceType.GOLD);
                tnt.setPrice(4);
                tnt.setIndex(0);
                items.add(tnt);
                
                // Ender Pearl
                ShopItem enderPearl = new ShopItem();
                enderPearl.setItemStack(new ItemBuilder(Material.ENDER_PEARL)
                    .setDisplayName("&5Ender Pearl")
                    .setLore("&7Teleport item", "&7Cost: &b4 Diamond")
                    .build());
                enderPearl.setPriceType(PriceType.DIAMOND);
                enderPearl.setPrice(4);
                enderPearl.setIndex(1);
                items.add(enderPearl);
                
                // Water Bucket
                ShopItem waterBucket = new ShopItem();
                waterBucket.setItemStack(new ItemBuilder(Material.WATER_BUCKET)
                    .setDisplayName("&9Water Bucket")
                    .setLore("&7Extinguish fires", "&7Cost: &f3 Iron")
                    .build());
                waterBucket.setPriceType(PriceType.IRON);
                waterBucket.setPrice(3);
                waterBucket.setIndex(2);
                items.add(waterBucket);
                
                // Ladder
                ShopItem ladder = new ShopItem();
                ladder.setItemStack(new ItemBuilder(Material.LADDER)
                    .setAmount(8)
                    .setDisplayName("&7Ladder")
                    .setLore("&7Climbing tool", "&7Cost: &f4 Iron")
                    .build());
                ladder.setPriceType(PriceType.IRON);
                ladder.setPrice(4);
                ladder.setIndex(3);
                items.add(ladder);
                
                // Bridge Egg
                ShopItem bridgeEgg = new ShopItem();
                bridgeEgg.setItemStack(new ItemBuilder(Material.EGG)
                    .setDisplayName("&fBridge Egg")
                    .setLore("&7Creates a bridge", "&7Cost: &b1 Diamond")
                    .build());
                bridgeEgg.setPriceType(PriceType.DIAMOND);
                bridgeEgg.setPrice(1);
                bridgeEgg.setIndex(4);
                items.add(bridgeEgg);
                
                this.setItems(new ArrayList<>(items));
            }
        };
        page.initialize();
        return page;
    }
}
