package com.phannhubao.config;

import com.phannhubao.entity.Category;
import com.phannhubao.entity.Product;
import com.phannhubao.entity.Tag;
import com.phannhubao.repository.CategoryRepository;
import com.phannhubao.repository.ProductRepository;
import com.phannhubao.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(4)
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

        private final ProductRepository productRepository;
        private final TagRepository tagRepository;
        private final CategoryRepository categoryRepository;

        @Override
        @Transactional
        public void run(String... args) {

                /*
                 * Nếu muốn seed lại sạch từ đầu thì mở dòng này.
                 * Nếu không muốn xóa sản phẩm cũ thì để comment.
                 */
                // productRepository.deleteAll();

                Tag tagNew = getOrCreateTag("NEW");
                Tag tagSale = getOrCreateTag("SALE");
                Tag tagWomen = getOrCreateTag("WOMEN");
                Tag tagClothes = getOrCreateTag("CLOTHES");
                Tag tagShoes = getOrCreateTag("SHOES");
                Tag tagAccessories = getOrCreateTag("ACCESSORIES");

                Category catNew = getOrCreateCategory("New", "New arrivals", "/images/cat_new.jpg", null);
                Category catClothes = getOrCreateCategory("Clothes", "Clothes collection", "/images/cat_clothes.jpg",
                                null);
                Category catShoes = getOrCreateCategory("Shoes", "Shoes collection", "/images/cat_shoes.jpg", null);
                Category catAccessories = getOrCreateCategory("Accessories", "Accessories collection",
                                "/images/cat_accessories.jpg", null);

                Category tops = getOrCreateCategory("Tops", "Tops and upper-body clothing", "/images/cat_tops.jpg",
                                catClothes);
                Category shirts = getOrCreateCategory("Shirts & Blouses", "Shirts and blouses",
                                "/images/cat_shirts.jpg", catClothes);
                Category cardigans = getOrCreateCategory("Cardigans & Sweaters", "Cardigans and sweaters",
                                "/images/cat_cardigans.jpg", catClothes);
                Category knitwear = getOrCreateCategory("Knitwear", "Knitted clothing", "/images/cat_knitwear.jpg",
                                catClothes);
                Category blazers = getOrCreateCategory("Blazers", "Blazers and tailored jackets",
                                "/images/cat_blazers.jpg", catClothes);
                Category outerwear = getOrCreateCategory("Outerwear", "Jackets, coats and parkas",
                                "/images/cat_outerwear.jpg", catClothes);
                Category pants = getOrCreateCategory("Pants", "Pants and trousers", "/images/cat_pants.jpg",
                                catClothes);
                Category jeans = getOrCreateCategory("Jeans", "Denim jeans", "/images/cat_jeans.jpg", catClothes);
                Category shorts = getOrCreateCategory("Shorts", "Shorts", "/images/cat_shorts.jpg", catClothes);
                Category skirts = getOrCreateCategory("Skirts", "Skirts", "/images/cat_skirts.jpg", catClothes);
                Category dresses = getOrCreateCategory("Dresses", "Dresses", "/images/cat_dresses.jpg", catClothes);

                // =========================
                // TOPS
                // =========================
                seedProduct("Basic Cotton Top", "Mango", "basic-cotton-top", "/images/women/tops_1.webp",
                                19.0, 25.0, 60,
                                "Áo top nữ cotton basic dễ phối đồ",
                                "Áo top nữ chất liệu cotton mềm mại, phù hợp đi học, đi chơi hoặc mặc hằng ngày.",
                                "White",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("White", "Black", "Beige"),
                                Set.of(tagWomen, tagClothes, tagNew),
                                tops, catNew);

                seedProduct("Ribbed Tank Top", "H&M", "ribbed-tank-top", "/images/women/tops_2.webp",
                                15.0, 20.0, 70,
                                "Áo ba lỗ nữ gân ôm dáng",
                                "Áo tank top nữ chất thun gân co giãn, thiết kế trẻ trung và năng động.",
                                "Beige",
                                Set.of("XS", "S", "M"),
                                Set.of("Beige", "Brown", "Black"),
                                Set.of(tagWomen, tagClothes),
                                tops);

                seedProduct("Summer Crop Top", "Zara", "summer-crop-top", "/images/women/tops_3.webp",
                                22.0, 30.0, 45,
                                "Áo crop top nữ mùa hè",
                                "Áo crop top nữ dáng ngắn, chất liệu thoáng mát, phù hợp phối cùng quần jeans hoặc chân váy.",
                                "Pink",
                                Set.of("XS", "S", "M"),
                                Set.of("Pink", "White", "Black"),
                                Set.of(tagWomen, tagSale),
                                tops);

                seedProduct("Off Shoulder Top", "Shein", "off-shoulder-top", "/images/women/tops_4.webp",
                                18.0, 24.0, 50,
                                "Áo trễ vai nữ thanh lịch",
                                "Áo trễ vai nữ phong cách nhẹ nhàng, phù hợp đi chơi, hẹn hò hoặc chụp ảnh.",
                                "Cream",
                                Set.of("S", "M", "L"),
                                Set.of("Cream", "White", "Blue"),
                                Set.of(tagWomen, tagClothes),
                                tops);

                seedProduct("Lace Trim Cami Top", "Pull&Bear", "lace-trim-cami-top", "/images/women/tops_5.webp",
                                20.0, 28.0, 40,
                                "Áo hai dây nữ viền ren",
                                "Áo cami top nữ viền ren nhẹ nhàng, thiết kế nữ tính, dễ phối với áo khoác mỏng.",
                                "Black",
                                Set.of("XS", "S", "M"),
                                Set.of("Black", "White", "Red"),
                                Set.of(tagWomen, tagNew),
                                tops, catNew);

                // =========================
                // SHIRTS & BLOUSES
                // =========================
                seedProduct("White Office Blouse", "Mango", "white-office-blouse", "/images/women/shirt_1.webp",
                                29.0, 39.0, 55,
                                "Áo blouse trắng nữ công sở",
                                "Áo blouse trắng nữ thiết kế thanh lịch, phù hợp đi làm, thuyết trình hoặc gặp khách hàng.",
                                "White",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("White", "Beige"),
                                Set.of(tagWomen, tagClothes),
                                shirts);

                seedProduct("Silk Satin Shirt", "Zara", "silk-satin-shirt", "/images/women/shirt_2.webp",
                                45.0, 59.0, 35,
                                "Áo sơ mi satin nữ cao cấp",
                                "Áo sơ mi nữ chất satin bóng nhẹ, tạo cảm giác sang trọng và mềm mại khi mặc.",
                                "Champagne",
                                Set.of("S", "M", "L"),
                                Set.of("Champagne", "Black", "Navy"),
                                Set.of(tagWomen, tagNew),
                                shirts, catNew);

                seedProduct("Floral Chiffon Blouse", "Dorothy Perkins", "floral-chiffon-blouse",
                                "/images/women/shirt_3.webp",
                                32.0, 45.0, 42,
                                "Áo blouse voan hoa nữ",
                                "Áo blouse voan họa tiết hoa nhẹ nhàng, phù hợp phong cách nữ tính và dịu dàng.",
                                "Floral",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Pink", "White", "Blue"),
                                Set.of(tagWomen, tagSale),
                                shirts);

                seedProduct("Striped Casual Shirt", "H&M", "striped-casual-shirt", "/images/women/shirt_4.webp",
                                27.0, 35.0, 50,
                                "Áo sơ mi sọc nữ casual",
                                "Áo sơ mi sọc form rộng, có thể mặc riêng hoặc khoác ngoài áo thun.",
                                "Blue",
                                Set.of("S", "M", "L", "XL"),
                                Set.of("Blue", "White"),
                                Set.of(tagWomen, tagClothes),
                                shirts);

                seedProduct("Bow Tie Blouse", "Mango", "bow-tie-blouse", "/images/women/shirt_5.webp",
                                34.0, 46.0, 30,
                                "Áo blouse nữ cổ nơ",
                                "Áo blouse cổ nơ thanh lịch, phù hợp phong cách công sở nhẹ nhàng.",
                                "Ivory",
                                Set.of("XS", "S", "M"),
                                Set.of("Ivory", "Black", "Pink"),
                                Set.of(tagWomen, tagNew),
                                shirts, catNew);

                // =========================
                // CARDIGANS & SWEATERS
                // =========================
                seedProduct("Soft Knit Cardigan", "s.Oliver", "soft-knit-cardigan", "/images/women/cardigan_1.webp",
                                39.0, 55.0, 40,
                                "Áo cardigan len mềm nữ",
                                "Áo cardigan nữ chất len mềm, thiết kế cài khuy nhẹ nhàng, phù hợp thời tiết se lạnh.",
                                "Beige",
                                Set.of("S", "M", "L"),
                                Set.of("Beige", "Brown", "White"),
                                Set.of(tagWomen, tagClothes),
                                cardigans);

                seedProduct("Cropped Cardigan", "Zara", "cropped-cardigan", "/images/women/cardigan_2.webp",
                                36.0, 49.0, 38,
                                "Áo cardigan dáng ngắn nữ",
                                "Áo cardigan crop nữ form ngắn trẻ trung, dễ phối cùng đầm hoặc quần cạp cao.",
                                "Pink",
                                Set.of("XS", "S", "M"),
                                Set.of("Pink", "White", "Black"),
                                Set.of(tagWomen),
                                cardigans);

                seedProduct("V Neck Sweater", "H&M", "v-neck-sweater", "/images/women/cardigan_3.webp",
                                42.0, 58.0, 45,
                                "Áo sweater cổ V nữ",
                                "Áo sweater nữ cổ V, chất liệu dệt kim ấm nhẹ, phù hợp mặc đi học hoặc đi làm.",
                                "Grey",
                                Set.of("S", "M", "L", "XL"),
                                Set.of("Grey", "Navy", "Cream"),
                                Set.of(tagWomen, tagSale),
                                cardigans);

                seedProduct("Button Knit Cardigan", "Mango", "button-knit-cardigan", "/images/women/cardigan_4.webp",
                                44.0, 60.0, 32,
                                "Áo cardigan nữ cài nút",
                                "Áo cardigan nữ cài nút phong cách Hàn Quốc, mềm mại và nữ tính.",
                                "Cream",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Cream", "Blue", "Brown"),
                                Set.of(tagWomen, tagClothes),
                                cardigans);

                seedProduct("Oversize Sweater", "Pull&Bear", "oversize-sweater", "/images/women/cardigan_5.webp",
                                49.0, 69.0, 36,
                                "Áo sweater nữ form rộng",
                                "Áo sweater form rộng dành cho nữ, phù hợp phong cách casual và street style.",
                                "Navy",
                                Set.of("S", "M", "L", "XL"),
                                Set.of("Navy", "Grey", "Black"),
                                Set.of(tagWomen),
                                cardigans);

                // =========================
                // KNITWEAR
                // =========================
                seedProduct("Ribbed Knit Top", "Zara", "ribbed-knit-top", "/images/women/knit_1.jpg",
                                31.0, 42.0, 44,
                                "Áo dệt kim ôm dáng nữ",
                                "Áo knit top nữ chất dệt kim co giãn, ôm dáng nhẹ, phù hợp mặc thu đông.",
                                "Black",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Black", "White", "Brown"),
                                Set.of(tagWomen, tagClothes),
                                knitwear);

                seedProduct("Cable Knit Sweater", "Mango", "cable-knit-sweater", "/images/women/knit_2.jpg",
                                55.0, 75.0, 28,
                                "Áo len vặn thừng nữ",
                                "Áo len nữ họa tiết vặn thừng cổ điển, giữ ấm tốt và dễ phối đồ.",
                                "Cream",
                                Set.of("S", "M", "L"),
                                Set.of("Cream", "Beige", "Grey"),
                                Set.of(tagWomen, tagNew),
                                knitwear, catNew);

                seedProduct("Knit Polo Top", "H&M", "knit-polo-top", "/images/women/knit_3.jpg",
                                37.0, 49.0, 35,
                                "Áo polo dệt kim nữ",
                                "Áo polo dệt kim nữ thanh lịch, phù hợp phong cách basic nhưng vẫn hiện đại.",
                                "Green",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Green", "White", "Navy"),
                                Set.of(tagWomen),
                                knitwear);

                seedProduct("Sleeveless Knit Vest", "Pull&Bear", "sleeveless-knit-vest", "/images/women/knit_4.jpg",
                                33.0, 45.0, 40,
                                "Áo gile len nữ không tay",
                                "Áo gile len nữ không tay, dễ phối với sơ mi hoặc áo thun dài tay.",
                                "Beige",
                                Set.of("S", "M", "L"),
                                Set.of("Beige", "Brown", "Black"),
                                Set.of(tagWomen, tagClothes),
                                knitwear);

                seedProduct("Turtleneck Knit Sweater", "Uniqlo", "turtleneck-knit-sweater", "/images/women/knit_5.jpg",
                                50.0, 65.0, 34,
                                "Áo len cổ lọ nữ",
                                "Áo len cổ lọ nữ giữ ấm tốt, thiết kế tối giản phù hợp nhiều hoàn cảnh.",
                                "Grey",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Grey", "Black", "Ivory"),
                                Set.of(tagWomen),
                                knitwear);

                // =========================
                // BLAZERS
                // =========================
                seedProduct("Classic Black Blazer", "Mango", "classic-black-blazer", "/images/women/blazer_1.webp",
                                79.0, 99.0, 25,
                                "Áo blazer đen nữ basic",
                                "Blazer nữ màu đen dáng basic, phù hợp công sở, phỏng vấn hoặc sự kiện.",
                                "Black",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Black", "Grey"),
                                Set.of(tagWomen, tagClothes),
                                blazers);

                seedProduct("Beige Tailored Blazer", "Zara", "beige-tailored-blazer", "/images/women/blazer_2.webp",
                                89.0, 120.0, 20,
                                "Áo blazer beige nữ may đo",
                                "Blazer nữ màu beige dáng tailored, tạo phong cách thanh lịch và sang trọng.",
                                "Beige",
                                Set.of("S", "M", "L"),
                                Set.of("Beige", "White", "Brown"),
                                Set.of(tagWomen),
                                blazers);

                seedProduct("Oversize Office Blazer", "H&M", "oversize-office-blazer", "/images/women/blazer_3.webp",
                                69.0, 89.0, 30,
                                "Áo blazer nữ form rộng",
                                "Blazer nữ form rộng trẻ trung, dễ phối cùng áo croptop, quần jeans hoặc chân váy.",
                                "Grey",
                                Set.of("S", "M", "L", "XL"),
                                Set.of("Grey", "Black", "Navy"),
                                Set.of(tagWomen, tagSale),
                                blazers);

                seedProduct("Cropped Blazer", "Pull&Bear", "cropped-blazer", "/images/women/blazer_4.webp",
                                72.0, 95.0, 22,
                                "Áo blazer nữ dáng ngắn",
                                "Blazer dáng ngắn phong cách hiện đại, phù hợp phối cùng quần cạp cao.",
                                "White",
                                Set.of("XS", "S", "M"),
                                Set.of("White", "Black", "Pink"),
                                Set.of(tagWomen, tagClothes),
                                blazers);

                seedProduct("Plaid Blazer", "Dorothy Perkins", "plaid-blazer", "/images/women/blazer_5.webp",
                                83.0, 110.0, 18,
                                "Áo blazer nữ caro",
                                "Blazer họa tiết caro nữ tính, phù hợp phong cách vintage và công sở.",
                                "Plaid",
                                Set.of("S", "M", "L"),
                                Set.of("Brown", "Grey", "Beige"),
                                Set.of(tagWomen),
                                blazers);

                // =========================
                // OUTERWEAR
                // =========================
                seedProduct("Long Trench Coat", "Mango", "long-trench-coat", "/images/women/outerwear_1.jpg",
                                120.0, 155.0, 18,
                                "Áo trench coat dài nữ",
                                "Áo trench coat nữ dáng dài, phong cách thanh lịch, phù hợp thời tiết thu đông.",
                                "Khaki",
                                Set.of("S", "M", "L"),
                                Set.of("Khaki", "Beige", "Black"),
                                Set.of(tagWomen, tagClothes),
                                outerwear);

                seedProduct("Puffer Jacket", "Zara", "puffer-jacket-women", "/images/women/outerwear_2.jpg",
                                95.0, 130.0, 25,
                                "Áo khoác phao nữ giữ ấm",
                                "Áo khoác phao nữ nhẹ nhưng giữ ấm tốt, phù hợp du lịch hoặc mùa lạnh.",
                                "White",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("White", "Black", "Pink"),
                                Set.of(tagWomen),
                                outerwear);

                seedProduct("Denim Jacket Women", "H&M", "denim-jacket-women", "/images/women/outerwear_3.webp",
                                65.0, 85.0, 40,
                                "Áo khoác denim nữ",
                                "Áo khoác denim nữ phong cách trẻ trung, dễ phối với đầm, quần jeans hoặc chân váy.",
                                "Blue",
                                Set.of("S", "M", "L", "XL"),
                                Set.of("Blue", "Light Blue"),
                                Set.of(tagWomen),
                                outerwear);

                seedProduct("Faux Leather Jacket", "Pull&Bear", "faux-leather-jacket", "/images/women/outerwear_4.webp",
                                105.0, 140.0, 20,
                                "Áo khoác da nữ cá tính",
                                "Áo khoác da nữ chất da nhân tạo mềm, phong cách cá tính và thời trang.",
                                "Black",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Black", "Brown"),
                                Set.of(tagWomen, tagClothes),
                                outerwear);

                seedProduct("Wool Blend Coat", "Uniqlo", "wool-blend-coat", "/images/women/outerwear_5.jpg",
                                135.0, 170.0, 16,
                                "Áo khoác dạ nữ dáng dài",
                                "Áo khoác dạ nữ pha len, dáng dài sang trọng, phù hợp mùa đông.",
                                "Camel",
                                Set.of("S", "M", "L"),
                                Set.of("Camel", "Grey", "Black"),
                                Set.of(tagWomen),
                                outerwear);

                // =========================
                // PANTS
                // =========================
                seedProduct("High Waist Trousers", "Mango", "high-waist-trousers", "/images/women/pants_1.webp",
                                49.0, 65.0, 45,
                                "Quần tây nữ cạp cao",
                                "Quần tây nữ cạp cao dáng suông, phù hợp công sở và phong cách thanh lịch.",
                                "Black",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Black", "Beige", "Grey"),
                                Set.of(tagWomen, tagClothes),
                                pants);

                seedProduct("Wide Leg Pants", "Zara", "wide-leg-pants", "/images/women/pants_2.webp",
                                52.0, 70.0, 38,
                                "Quần ống rộng nữ",
                                "Quần ống rộng nữ chất mềm rũ, tạo cảm giác thoải mái và tôn dáng.",
                                "Cream",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Cream", "Black", "Brown"),
                                Set.of(tagWomen),
                                pants);

                seedProduct("Cargo Pants Women", "Pull&Bear", "cargo-pants-women", "/images/women/pants_3.webp",
                                46.0, 62.0, 42,
                                "Quần cargo nữ cá tính",
                                "Quần cargo nữ nhiều túi, phù hợp phong cách streetwear năng động.",
                                "Olive",
                                Set.of("S", "M", "L", "XL"),
                                Set.of("Olive", "Black", "Khaki"),
                                Set.of(tagWomen),
                                pants);

                seedProduct("Straight Office Pants", "H&M", "straight-office-pants", "/images/women/pants_4.webp",
                                44.0, 58.0, 50,
                                "Quần công sở nữ ống đứng",
                                "Quần công sở nữ ống đứng dễ mặc, phù hợp phối cùng áo sơ mi hoặc blazer.",
                                "Navy",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Navy", "Black", "Grey"),
                                Set.of(tagWomen, tagClothes),
                                pants);

                seedProduct("Pleated Pants", "Dorothy Perkins", "pleated-pants", "/images/women/pants_5.webp",
                                48.0, 64.0, 30,
                                "Quần xếp ly nữ thanh lịch",
                                "Quần xếp ly nữ cạp cao, thiết kế mềm mại và sang trọng.",
                                "Beige",
                                Set.of("S", "M", "L"),
                                Set.of("Beige", "White", "Black"),
                                Set.of(tagWomen),
                                pants);

                // =========================
                // JEANS
                // =========================
                seedProduct("Mom Fit Jeans", "Zara", "mom-fit-jeans", "/images/women/jeans_1.webp",
                                55.0, 75.0, 40,
                                "Quần jeans nữ mom fit",
                                "Quần jeans nữ mom fit cạp cao, chất denim dày dặn và dễ phối đồ.",
                                "Blue",
                                Set.of("26", "27", "28", "29", "30"),
                                Set.of("Blue", "Light Blue"),
                                Set.of(tagWomen, tagClothes),
                                jeans);

                seedProduct("Skinny Jeans Women", "Mango", "skinny-jeans-women", "/images/women/jeans_2.webp",
                                49.0, 65.0, 45,
                                "Quần skinny jeans nữ",
                                "Quần skinny jeans nữ co giãn tốt, ôm dáng nhưng vẫn thoải mái.",
                                "Dark Blue",
                                Set.of("26", "27", "28", "29", "30"),
                                Set.of("Dark Blue", "Black"),
                                Set.of(tagWomen, tagSale),
                                jeans);

                seedProduct("Straight Leg Jeans", "H&M", "straight-leg-jeans", "/images/women/jeans_3.webp",
                                52.0, 69.0, 50,
                                "Quần jeans nữ ống đứng",
                                "Quần jeans nữ ống đứng phong cách basic, phù hợp mặc hằng ngày.",
                                "Light Blue",
                                Set.of("26", "27", "28", "29", "30"),
                                Set.of("Light Blue", "Blue"),
                                Set.of(tagWomen),
                                jeans);

                seedProduct("Flared Jeans Women", "Pull&Bear", "flared-jeans-women", "/images/women/jeans_4.webp",
                                58.0, 78.0, 35,
                                "Quần jeans nữ ống loe",
                                "Quần jeans ống loe nữ phong cách retro, giúp tôn dáng và kéo dài chân.",
                                "Blue",
                                Set.of("26", "27", "28", "29"),
                                Set.of("Blue", "Black"),
                                Set.of(tagWomen, tagClothes),
                                jeans);

                seedProduct("Black High Waist Jeans", "Uniqlo", "black-high-waist-jeans", "/images/women/jeans_5.webp",
                                54.0, 72.0, 42,
                                "Quần jeans đen nữ cạp cao",
                                "Quần jeans đen cạp cao dành cho nữ, dễ phối với áo sơ mi, áo thun hoặc blazer.",
                                "Black",
                                Set.of("26", "27", "28", "29", "30"),
                                Set.of("Black"),
                                Set.of(tagWomen),
                                jeans);

                // =========================
                // SHORTS
                // =========================
                seedProduct("Denim Shorts Women", "Zara", "denim-shorts-women", "/images/women/shorts_1.webp",
                                32.0, 45.0, 55,
                                "Quần short jeans nữ",
                                "Quần short jeans nữ cạp cao, phù hợp mùa hè và phong cách năng động.",
                                "Blue",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Blue", "Light Blue"),
                                Set.of(tagWomen, tagClothes),
                                shorts);

                seedProduct("Linen Shorts", "Mango", "linen-shorts", "/images/women/shorts_2.webp",
                                29.0, 39.0, 50,
                                "Quần short linen nữ",
                                "Quần short nữ chất linen thoáng mát, phù hợp đi biển hoặc dạo phố.",
                                "Beige",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Beige", "White", "Brown"),
                                Set.of(tagWomen),
                                shorts);

                seedProduct("Tailored Shorts", "H&M", "tailored-shorts", "/images/women/shorts_3.webp",
                                35.0, 48.0, 40,
                                "Quần short nữ dáng công sở",
                                "Quần short nữ dáng tailored thanh lịch, có thể phối với áo sơ mi hoặc blazer.",
                                "Black",
                                Set.of("S", "M", "L"),
                                Set.of("Black", "Grey", "Cream"),
                                Set.of(tagWomen),
                                shorts);

                seedProduct("Sport Shorts Women", "Adidas", "sport-shorts-women", "/images/women/shorts_4.webp",
                                28.0, 38.0, 60,
                                "Quần short thể thao nữ",
                                "Quần short thể thao nữ chất liệu co giãn, thoáng khí, phù hợp luyện tập.",
                                "Black",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Black", "Pink", "Grey"),
                                Set.of(tagWomen, tagClothes),
                                shorts);

                seedProduct("Pleated Mini Shorts", "Pull&Bear", "pleated-mini-shorts", "/images/women/shorts_5.webp",
                                31.0, 42.0, 36,
                                "Quần short nữ xếp ly",
                                "Quần short xếp ly nữ thiết kế trẻ trung, phù hợp phong cách Hàn Quốc.",
                                "Cream",
                                Set.of("XS", "S", "M"),
                                Set.of("Cream", "Black", "Pink"),
                                Set.of(tagWomen),
                                shorts, catNew);

                // =========================
                // SKIRTS
                // =========================
                seedProduct("Pleated Mini Skirt", "Zara", "pleated-mini-skirt", "/images/women/skirt_1.webp",
                                34.0, 46.0, 45,
                                "Chân váy xếp ly nữ",
                                "Chân váy xếp ly nữ phong cách trẻ trung, phù hợp đi học, đi chơi hoặc chụp ảnh.",
                                "Black",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Black", "Grey", "Navy"),
                                Set.of(tagWomen, tagClothes),
                                skirts);

                seedProduct("A Line Skirt", "Mango", "a-line-skirt", "/images/women/skirt_2.webp",
                                38.0, 52.0, 40,
                                "Chân váy chữ A nữ",
                                "Chân váy chữ A nữ thanh lịch, dễ phối với áo sơ mi hoặc áo blouse.",
                                "Beige",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Beige", "Black", "Brown"),
                                Set.of(tagWomen),
                                skirts);

                seedProduct("Denim Mini Skirt", "H&M", "denim-mini-skirt", "/images/women/skirt_3.webp",
                                36.0, 49.0, 50,
                                "Chân váy jeans nữ",
                                "Chân váy jeans nữ dáng ngắn, phong cách năng động và cá tính.",
                                "Blue",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Blue", "Light Blue"),
                                Set.of(tagWomen),
                                skirts);

                seedProduct("Satin Midi Skirt", "Dorothy Perkins", "satin-midi-skirt", "/images/women/skirt_4.webp",
                                45.0, 62.0, 30,
                                "Chân váy satin midi nữ",
                                "Chân váy satin dáng midi mềm mại, tạo vẻ sang trọng và nữ tính.",
                                "Champagne",
                                Set.of("S", "M", "L"),
                                Set.of("Champagne", "Black", "Pink"),
                                Set.of(tagWomen, tagClothes),
                                skirts);

                seedProduct("Floral Long Skirt", "Pull&Bear", "floral-long-skirt", "/images/women/skirt_5.webp",
                                41.0, 55.0, 34,
                                "Chân váy dài hoa nữ",
                                "Chân váy dài họa tiết hoa nhẹ nhàng, phù hợp đi chơi hoặc du lịch.",
                                "Floral",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Pink", "White", "Blue"),
                                Set.of(tagWomen),
                                skirts);

                // =========================
                // DRESSES
                // =========================
                seedProduct("Elegant Evening Dress", "Dorothy Perkins", "elegant-evening-dress",
                                "/images/women/dress_1.webp",
                                79.0, 105.0, 25,
                                "Đầm dạ hội nữ thanh lịch",
                                "Đầm dạ hội nữ thiết kế sang trọng, phù hợp tiệc tối, sự kiện hoặc lễ kỷ niệm.",
                                "Red",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Red", "Black", "Navy"),
                                Set.of(tagWomen, tagClothes),
                                dresses);

                seedProduct("Floral Summer Dress", "Mango", "floral-summer-dress", "/images/women/dress_2.webp",
                                55.0, 72.0, 40,
                                "Đầm hoa nữ mùa hè",
                                "Đầm hoa nữ chất liệu nhẹ, thoáng mát, phù hợp đi chơi, du lịch hoặc dạo phố.",
                                "Floral",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Pink", "White", "Yellow"),
                                Set.of(tagWomen),
                                dresses);

                seedProduct("Bodycon Mini Dress", "Zara", "bodycon-mini-dress", "/images/women/dress_3.webp",
                                49.0, 65.0, 35,
                                "Đầm body nữ dáng ngắn",
                                "Đầm body nữ ôm dáng, phong cách hiện đại, phù hợp đi tiệc hoặc hẹn hò.",
                                "Black",
                                Set.of("XS", "S", "M"),
                                Set.of("Black", "Red", "White"),
                                Set.of(tagWomen),
                                dresses);

                seedProduct("Korean Shirt Dress", "H&M", "korean-shirt-dress", "/images/women/dress_4.webp",
                                46.0, 60.0, 42,
                                "Đầm sơ mi nữ Hàn Quốc",
                                "Đầm sơ mi nữ phong cách Hàn Quốc, dễ mặc và phù hợp nhiều dáng người.",
                                "Blue",
                                Set.of("S", "M", "L"),
                                Set.of("Blue", "White", "Beige"),
                                Set.of(tagWomen, tagClothes),
                                dresses);

                seedProduct("Satin Slip Dress", "Pull&Bear", "satin-slip-dress", "/images/women/dress_5.webp",
                                59.0, 78.0, 30,
                                "Đầm hai dây satin nữ",
                                "Đầm slip dress chất satin mềm mịn, tạo vẻ nữ tính và sang trọng.",
                                "Champagne",
                                Set.of("XS", "S", "M", "L"),
                                Set.of("Champagne", "Black", "Pink"),
                                Set.of(tagWomen),
                                dresses);

                // =========================
                // SHOES
                // =========================
                seedProduct("White Women Sneakers", "Adidas", "white-women-sneakers", "/images/women/shoes_1.webp",
                                85.0, 110.0, 30,
                                "Giày sneaker trắng nữ",
                                "Giày sneaker nữ màu trắng basic, dễ phối với váy, quần jeans hoặc đồ thể thao.",
                                "White",
                                Set.of("36", "37", "38", "39"),
                                Set.of("White", "Pink"),
                                Set.of(tagWomen, tagShoes),
                                catShoes);

                seedProduct("High Heel Sandals", "Charles & Keith", "high-heel-sandals", "/images/women/shoes_2.webp",
                                70.0, 95.0, 25,
                                "Giày cao gót sandal nữ",
                                "Giày cao gót sandal nữ quai mảnh, phù hợp đi tiệc hoặc sự kiện.",
                                "Black",
                                Set.of("35", "36", "37", "38", "39"),
                                Set.of("Black", "Nude"),
                                Set.of(tagWomen, tagShoes),
                                catShoes);

                seedProduct("Ballet Flats", "Mango", "ballet-flats", "/images/women/shoes_3.webp",
                                45.0, 60.0, 40,
                                "Giày búp bê nữ nhẹ nhàng",
                                "Giày búp bê nữ đế thấp, mang êm chân, phù hợp đi học hoặc đi làm.",
                                "Cream",
                                Set.of("35", "36", "37", "38", "39"),
                                Set.of("Cream", "Black", "Pink"),
                                Set.of(tagWomen, tagShoes),
                                catShoes);

                seedProduct("Platform Loafers Women", "Zara", "platform-loafers-women", "/images/women/shoes_4.webp",
                                78.0, 105.0, 22,
                                "Giày loafer nữ đế cao",
                                "Giày loafer nữ đế cao phong cách hiện đại, phù hợp phối với blazer hoặc chân váy.",
                                "Black",
                                Set.of("36", "37", "38", "39"),
                                Set.of("Black", "Brown"),
                                Set.of(tagWomen, tagShoes),
                                catShoes);

                seedProduct("Summer Flat Sandals", "H&M", "summer-flat-sandals", "/images/women/shoes_5.webp",
                                35.0, 48.0, 45,
                                "Dép sandal nữ mùa hè",
                                "Sandal nữ đế bệt thoải mái, phù hợp đi biển, đi chơi hoặc mặc hằng ngày.",
                                "Beige",
                                Set.of("35", "36", "37", "38", "39"),
                                Set.of("Beige", "White", "Brown"),
                                Set.of(tagWomen, tagShoes),
                                catShoes);

                // =========================
                // ACCESSORIES
                // =========================
                seedProduct("Mini Leather Handbag", "Charles & Keith", "mini-leather-handbag",
                                "/images/women/accessory_1.webp",
                                89.0, 120.0, 20,
                                "Túi xách mini nữ da mềm",
                                "Túi xách mini nữ chất liệu da mềm, thiết kế nhỏ gọn, phù hợp đi chơi hoặc dự tiệc.",
                                "Black",
                                Set.of("One Size"),
                                Set.of("Black", "Beige", "Pink"),
                                Set.of(tagWomen, tagAccessories),
                                catAccessories);

                seedProduct("Pearl Necklace", "Mango", "pearl-necklace", "/images/women/accessory_2.webp",
                                29.0, 39.0, 35,
                                "Dây chuyền ngọc trai nữ",
                                "Dây chuyền ngọc trai nữ thiết kế thanh lịch, phù hợp phối với đầm hoặc áo blouse.",
                                "Pearl",
                                Set.of("One Size"),
                                Set.of("White", "Gold"),
                                Set.of(tagWomen, tagAccessories),
                                catAccessories);

                seedProduct("Fashion Sunglasses", "Zara", "fashion-sunglasses", "/images/women/accessory_3.webp",
                                32.0, 45.0, 40,
                                "Kính mát nữ thời trang",
                                "Kính mát nữ thiết kế hiện đại, phù hợp du lịch, dạo phố và chụp ảnh.",
                                "Black",
                                Set.of("One Size"),
                                Set.of("Black", "Brown"),
                                Set.of(tagWomen, tagAccessories),
                                catAccessories);

                seedProduct("Silk Hair Scarf", "H&M", "silk-hair-scarf", "/images/women/accessory_4.webp",
                                18.0, 25.0, 50,
                                "Khăn lụa buộc tóc nữ",
                                "Khăn lụa nhỏ dành cho nữ, có thể dùng buộc tóc, thắt túi hoặc phối trang phục.",
                                "Floral",
                                Set.of("One Size"),
                                Set.of("Pink", "Blue", "White"),
                                Set.of(tagWomen, tagAccessories),
                                catAccessories);

                seedProduct("Elegant Shoulder Bag", "GUCCI", "elegant-shoulder-bag", "/images/women/accessory_5.webp",
                                650.0, 790.0, 8,
                                "Túi đeo vai nữ sang trọng",
                                "Túi đeo vai nữ cao cấp, thiết kế sang trọng, phù hợp phong cách thanh lịch.",
                                "Brown",
                                Set.of("One Size"),
                                Set.of("Brown", "Black", "Beige"),
                                Set.of(tagWomen, tagAccessories),
                                catAccessories);

                log.info("Seeded women products successfully. Total products: {}", productRepository.count());
        }

        private Tag getOrCreateTag(String tagName) {
                return tagRepository.findByTagNameIgnoreCase(tagName)
                                .orElseGet(() -> tagRepository.save(Tag.builder()
                                                .tagName(tagName)
                                                .build()));
        }

        private Category getOrCreateCategory(String name, String description, String image, Category parent) {
                Category category = categoryRepository.findByCategoryNameIgnoreCase(name)
                                .orElseGet(() -> Category.builder()
                                                .categoryName(name)
                                                .build());

                category.setCategoryName(name);
                category.setCategoryDescription(description);
                category.setImage(image);
                category.setActive(true);

                category.setParent(parent);

                return categoryRepository.save(category);
        }

        private void seedProduct(
                        String productName,
                        String brandName,
                        String slug,
                        String imageUrl,
                        Double salePrice,
                        Double comparePrice,
                        Integer quantity,
                        String shortDescription,
                        String productDescription,
                        String note,
                        Set<String> sizes,
                        Set<String> colors,
                        Set<Tag> tags,
                        Category... categories) {
                Product product = productRepository.findBySlug(slug).orElseGet(Product::new);

                product.setProductName(productName);
                product.setBrandName(brandName);
                product.setSlug(slug);
                product.setImageUrl(imageUrl);
                product.setSalePrice(salePrice);
                boolean isSale = tags.stream().anyMatch(t -> "SALE".equalsIgnoreCase(t.getTagName()));
                product.setComparePrice(isSale ? comparePrice : salePrice);
                product.setQuantity(quantity);
                product.setShortDescription(shortDescription);
                product.setProductDescription(productDescription);
                product.setProductType("simple");
                product.setPublished(true);
                product.setDisableOutOfStock(false);
                if (product.getId() == null) {
                        product.setRatingAverage(0.0);
                        product.setReviewCount(0);
                }
                product.setNote(note);
                product.setSizes(new HashSet<>(sizes));
                product.setColors(new HashSet<>(colors));

                if (product.getTags() == null) {
                        product.setTags(new HashSet<>());
                }
                product.getTags().addAll(tags);

                if (product.getCategories() == null) {
                        product.setCategories(new HashSet<>());
                }
                assignProductCategories(product, categories);

                productRepository.save(product);
        }

        private void assignProductCategories(Product product, Category... categories) {
                Set<Category> requestedCategories = new HashSet<>(Set.of(categories));
                Set<Category> parentCategories = new HashSet<>();

                for (Category category : requestedCategories) {
                        if (category.getParent() != null) {
                                parentCategories.add(category.getParent());
                        }
                }

                requestedCategories.removeIf(category -> hasSameCategoryId(parentCategories, category));
                product.getCategories().removeIf(category -> hasSameCategoryId(parentCategories, category));
                product.getCategories().addAll(requestedCategories);
        }

        private boolean hasSameCategoryId(Set<Category> categories, Category candidate) {
                return categories.stream().anyMatch(category -> category.getId() != null
                                && candidate.getId() != null
                                && category.getId().equals(candidate.getId()));
        }
}
