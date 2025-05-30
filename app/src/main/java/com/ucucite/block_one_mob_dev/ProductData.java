package com.ucucite.block_one_mob_dev;

import java.util.ArrayList;
import java.util.List;

public class ProductData {

    public static List<Product> getProducts() {
        List<Product> products = new ArrayList<>();

        // Sample product data - replace with your actual data
        products.add(new Product(1, "Cooked Dog Food", "₱170.00", R.drawable.product1, "4.8 (2.2k)", "Natural Balance",
                "Chicken Thighs, Long-Grain White Rice, Long-Grain Brown Rice, Chicken Liver, Kale, Carrots, Applies, Sunflower Oil, Flaxseed Oil, Omega Marine Microalgae Oil, JustFoodForDogs Nutrient Blend; (Dicalcium Phosphate, Calcium Carbonate, Salt, Choline Bitartrate, Potassium Iodide, Zinc Amino Acid Chelate, Magnesium Amino Acid Chelate, Vitamin E Supplement, Ferrous Amino Acid Chelate, Copper Amino Acid Chelate, Cholecalciferol (source of Vitamin D3), d-Calcium Pantothenate, Riboflavin, Vitamin B12 Supplement)."));

        products.add(new Product(2, "Plant Based Cat Litter ", "₱250.00", R.drawable.product2, "4.7 (1.8k)", "Royal Canin",
                "Wheat, barley, corn, cassava, soybean fiber, pine, cedar, fir.Natural probiotics, chamomile, lavender, activated charcoal, baking soda, guar gum, citric acid, essential oils, plant-based binders, clumping agents, odor-neutralizing enzymes, and natural fragrances."));

        products.add(new Product(3, "Tuna Canned Cat Food", "₱320.00", R.drawable.product3, "4.5 (950)", "Aqueon",
                "Chicken, beef liver, beef heart, turkey. Brown rice, oatmeal, potatoes, barley, sweet potatoes. Sunflower oil, fish oil, flaxseed oil.Organic cane sugar, ground celery, dried cultured skim milk, paprika, hickory smoke flavor, rosemary extract, mixed tocopherols (natural antioxidants), citric acid, natural flavors, yeast extract, and salt."));

        products.add(new Product(4, "Cooked Dog Food", "₱145.00", R.drawable.product4, "4.6 (1.2k)", "Kaytee",
                "Ground turkey, beef, chicken, sardines, organ meats (e.g., liver). Brown rice, quinoa, oats, sweet potatoes. Broccoli, carrots, spinach, kale, red bell peppers. Blueberries, apples, pumpkin puree. Eggs, kelp powder, ground eggshells (for calcium), hempseeds or hempseed oil, ground ginger, turmeric, parsley, rosemary, and fish oil."));

        products.add(new Product(5, "Dog Dental  Treat", "₱180.00", R.drawable.product5, "4.4 (800)", "FURminator",
                "Wheat flour, wheat gluten, glycerin, gelatin. Dried poultry digest, beefhide. Maltodextrins, hydrolyzed corn protein, animal digest, potassium sorbate (preservative), xanthan gum, Yellow 5 (coloring), chlorophyll, parsley leaf powder, spearmint, natural flavors, cellulose, sunflower oil, and mixed tocopherols (natural antioxidants)."));

        products.add(new Product(6, " Regular Dog Treats", "₱220.00", R.drawable.product6, "4.3 (600)", "Silent Runner",
                "Wheat flour, wheat gluten, glycerin, gelatin. Dried poultry digest, beefhide. Maltodextrins, hydrolyzed corn protein, animal digest, potassium sorbate (preservative), xanthan gum, Yellow 5 (coloring), chlorophyll, parsley leaf powder, spearmint, natural flavors, cellulose, sunflower oil, and mixed tocopherols (natural antioxidants)."));

        products.add(new Product(7, "Baked Dog Treats", "₱280.00", R.drawable.product7, "4.7 (2.5k)", "Arm & Hammer",
                "Whole wheat flour, brown rice flour, oats, unsweetened applesauce, egg, cheese, fresh parsley, mint, anise seed, baking powder, baking soda, olive oil, honey, cinnamon, peanut butter (xylitol-free), and water."));

        products.add(new Product(8, "Dental Wipes", "₱195.00", R.drawable.product8, "4.5 (1.1k)", "Kong",
                "Water, sodium bicarbonate, natural banana flavor, natural spearmint flavor, sage extract, pomegranate extract, papain, zinc gluconate, polysorbate 80, phenoxyethanol, and ethylhexylglycerin."));

        products.add(new Product(9, "Poop Bags", "₱125.00", R.drawable.product9, "4.6 (750)", "Oxbow",
                "Cornstarch, polylactic acid (PLA), polybutylene adipate terephthalate (PBAT), vegetable-based ink, and biodegradable polymer compounds."));

        products.add(new Product(10, "Bone Dog Toy", "₱350.00", R.drawable.product10, "4.4 (520)", "Tetra",
                "Natural rubber, nylon, thermoplastic elastomers (TPE), polyurethane, food-grade dye, cotton rope fibers, and non-toxic adhesives."));

        products.add(new Product(11, "Dry Dog Food", "₱450.00", R.drawable.product11, "4.8 (1.5k)", "Sherpa",
                "Whole grain corn, poultry byproduct meal, animal fat (preserved with mixed tocopherols), corn gluten meal, meat and bone meal, brewers rice, soybean meal, barley, whole grain wheat, animal digest, calcium carbonate, salt, calcium phosphate, potassium chloride, L-lysine, choline chloride, zinc sulfate, vitamin E supplement, zinc proteinate, ferrous sulfate, Red 40, Yellow 5, DL-methionine, manganese sulfate, niacin, vitamin A supplement, copper sulfate, calcium pantothenate, garlic oil, pyridoxine hydrochloride, vitamin B12 supplement, thiamine mononitrate, vitamin D3 supplement, riboflavin supplement, calcium iodate, menadione sodium bisulfite complex, folic acid, biotin, and sodium selenite."));

        products.add(new Product(12, "Reversible Dog Jacket", "₱165.00", R.drawable.product12, "4.5 (900)", "Vitakraft",
                "Polyester, fleece, nylon, cotton padding, Velcro, snap buttons, reflective tape, elastic trim, plastic zippers, water-resistant coating, dye for coloring, thread, inner mesh lining, and reinforced stitching materials."));

        products.add(new Product(13, "Frozen Goat Milk", "₱240.00", R.drawable.product13, "4.6 (1.3k)", "Nylabone",
                "Raw goat milk, Lactobacillus acidophilus, Bifidobacterium animalis, digestive enzymes (amylase, lipase, protease), vitamin D, calcium, phosphorus, vitamin B12, potassium, zinc, selenium, magnesium, inulin (prebiotic), turmeric, cinnamon, and ginger."));

        products.add(new Product(14, "Salmon Cat Food ", "₱380.00", R.drawable.product14, "4.7 (1.8k)", "SmartCat",
                "Water sufficient for processing, tuna flakes, tapioca starch, sunflower oil, guar gum, locust bean gum, vitamin E supplement, taurine, vitamin A supplement, thiamine mononitrate, vitamin B12 supplement, zinc sulfate, potassium iodide, niacin supplement, calcium carbonate, magnesium sulfate, DL-methionine, and folic acid."));

        products.add(new Product(15, "Dried Raw Dog Treats", "₱95.00", R.drawable.product15, "4.4 (2.1k)", "TetraMin",
                "Beef, beef liver, chicken, chicken heart, lamb, lamb liver, ground bone, carrots, spinach, kale, blueberries, apples, pumpkin, eggs, fish oil, flaxseed, kelp, vitamin E, vitamin D3, vitamin A, zinc, copper, manganese, and selenium."));

        products.add(new Product(16, "Grooming Pet Wipes", "₱320.00", R.drawable.product16, "4.8 (1.7k)", "Furhaven",
                " Water, aloe vera juice, glycerin, chamomile extract, calendula extract, vitamin E, citric acid, polysorbate 20, decyl glucoside, sodium benzoate, potassium sorbate, tocopherol, cucumber extract, green tea extract, lavender oil, witch hazel, and natural preservatives."));

        products.add(new Product(17, "Digestive Aid  Stick", "₱275.00", R.drawable.product17, "4.3 (650)", "Zoo Med",
                "Pumpkin, sweet potatoes, chicory root, Bacillus coagulans, ginger root, parsley, slippery elm bark, licorice root, fennel seed, marshmallow root, apple cider vinegar, turmeric, flaxseed meal, inulin, vitamin C, and rosemary extract."));

        products.add(new Product(18, "Lickable Cat Treats", "₱135.00", R.drawable.product18, "4.5 (1.4k)", "Blue Buffalo",
                "Water, tuna, chicken, tapioca starch, natural flavor, guar gum, green tea extract, vitamin E supplement, taurine, calcium lactate, disodium phosphate, potassium chloride, thiamine mononitrate, riboflavin supplement, nicotinic acid, folic acid, biotin, zinc sulfate, magnesium sulfate, and fish extract."));

        return products;
    }

    public static Product getProductById(int id) {
        List<Product> products = getProducts();
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return products.get(0); // Return first product as default
    }
}