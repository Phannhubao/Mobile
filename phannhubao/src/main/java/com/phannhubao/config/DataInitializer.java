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
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@org.springframework.core.annotation.Order(1)
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

        private final ProductRepository productRepository;
        private final TagRepository tagRepository;
        private final CategoryRepository categoryRepository;

        @Override
        public void run(String... args) throws Exception {
                if (productRepository.count() > 0) {
                        log.info("Product data already exists; skipping sample product seed.");
                        return;
                }

                // Ensure tags exist
                Tag tagNew = tagRepository.findByTagNameIgnoreCase("NEW")
                                .orElseGet(() -> tagRepository.save(Tag.builder().tagName("NEW").build()));

                Tag tagSale = tagRepository.findByTagNameIgnoreCase("SALE")
                                .orElseGet(() -> tagRepository.save(Tag.builder().tagName("SALE").build()));

                Tag tagTops = tagRepository.findByTagNameIgnoreCase("TOPS")
                                .orElseGet(() -> tagRepository.save(Tag.builder().tagName("TOPS").build()));

                // Ensure categories exist
                Category catNew = categoryRepository.findByCategoryNameIgnoreCase("New")
                                .orElseGet(() -> categoryRepository.save(Category.builder()
                                                .categoryName("New")
                                                .categoryDescription("New arrivals")
                                                .image("/images/cat_new.jpg")
                                                .active(true)
                                                .createdAt(java.time.OffsetDateTime.now())
                                                .updatedAt(java.time.OffsetDateTime.now())
                                                .build()));

                Category catClothes = categoryRepository.findByCategoryNameIgnoreCase("Clothes")
                                .orElseGet(() -> categoryRepository.save(Category.builder()
                                                .categoryName("Clothes")
                                                .categoryDescription("Clothes collection")
                                                .image("/images/cat_clothes.jpg")
                                                .active(true)
                                                .createdAt(java.time.OffsetDateTime.now())
                                                .updatedAt(java.time.OffsetDateTime.now())
                                                .build()));

                Category catShoes = categoryRepository.findByCategoryNameIgnoreCase("Shoes")
                                .orElseGet(() -> categoryRepository.save(Category.builder()
                                                .categoryName("Shoes")
                                                .categoryDescription("Shoes collection")
                                                .image("/images/cat_shoes.jpg")
                                                .active(true)
                                                .createdAt(java.time.OffsetDateTime.now())
                                                .updatedAt(java.time.OffsetDateTime.now())
                                                .build()));

                Category catAccessories = categoryRepository.findByCategoryNameIgnoreCase("Accessories")
                                .orElseGet(() -> categoryRepository.save(Category.builder()
                                                .categoryName("Accessories")
                                                .categoryDescription("Accessories collection")
                                                .image("/images/cat_accessories.jpg")
                                                .active(true)
                                                .createdAt(java.time.OffsetDateTime.now())
                                                .updatedAt(java.time.OffsetDateTime.now())
                                                .build()));

                // Initialize basic products
                log.info("Initializing basic product and tag data with sizes and colors...");
                Product p1 = Product.builder()
                                .productName("Evening Dress")
                                .brandName("Dorothy Perkins")
                                .slug("evening-dress")
                                .imageUrl("/images/product1.jpg")
                                .salePrice(12.0)
                                .comparePrice(15.0)
                                .quantity(50)
                                .shortDescription("Đầm dạ hội thanh lịch dáng hồng")
                                .productDescription(
                                                "Đầm dạ hội phong cách thanh lịch, chất liệu mát mẻ phù hợp cho mùa hè.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(10)
                                .note("Red")
                                .sizes(new HashSet<>(Set.of("S", "M", "L")))
                                .colors(new HashSet<>(Set.of("Red", "Black")))
                                .tags(new HashSet<>(Set.of(tagSale)))
                                .build();

                Product p2 = Product.builder()
                                .productName("Sport Dress")
                                .brandName("Sitlly")
                                .slug("sport-dress")
                                .imageUrl("/images/product2.jpg")
                                .salePrice(19.0)
                                .comparePrice(22.0)
                                .quantity(30)
                                .shortDescription("Đầm thể thao năng động co giãn")
                                .productDescription(
                                                "Đầm thể thao dáng suông dài năng động, chất liệu cotton co giãn cao cấp.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(10)
                                .note("Navy")
                                .sizes(new HashSet<>(Set.of("S", "M", "L", "XL")))
                                .colors(new HashSet<>(Set.of("Navy", "Grey")))
                                .tags(new HashSet<>(Set.of(tagSale)))
                                .build();

                Product p3 = Product.builder()
                                .productName("Oversize T-Shirt")
                                .brandName("GUCCI")
                                .slug("oversize-tshirt-gucci")
                                .imageUrl("/images/product3.jpg")
                                .salePrice(650.0)
                                .comparePrice(0.0)
                                .quantity(15)
                                .shortDescription("Áo thun form rộng Gucci họa tiết cao cấp")
                                .productDescription(
                                                "Áo thun form rộng cao cấp từ thương hiệu Gucci thời trang phong cách.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(4.5)
                                .reviewCount(8)
                                .note("Black")
                                .sizes(new HashSet<>(Set.of("M", "L", "XL")))
                                .colors(new HashSet<>(Set.of("Black", "White")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                p1 = productRepository.save(p1);
                p2 = productRepository.save(p2);
                p3 = productRepository.save(p3);

                linkProductToCategory(p1, catClothes);
                linkProductToCategory(p2, catClothes);
                linkProductToCategory(p3, catNew);

                // Initialize TOPS products
                log.info("Initializing TOPS product data with sizes and colors...");
                Product p4 = Product.builder()
                                .productName("T-Shirt SPANISH")
                                .brandName("Mango")
                                .slug("t-shirt-spanish-mango")
                                .imageUrl("/images/top1.jpg")
                                .salePrice(9.0)
                                .comparePrice(0.0)
                                .quantity(20)
                                .shortDescription("Áo thun T-Shirt SPANISH Mango")
                                .productDescription("Áo thun T-Shirt SPANISH Mango chất liệu cotton mềm mại thoải mái.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(4.0)
                                .reviewCount(3)
                                .note("Beige")
                                .sizes(new HashSet<>(Set.of("S", "M", "L")))
                                .colors(new HashSet<>(Set.of("Black", "Beige", "Red")))
                                .tags(new HashSet<>(Set.of(tagTops, tagNew)))
                                .build();

                Product p5 = Product.builder()
                                .productName("Blouse")
                                .brandName("Dorothy Perkins")
                                .slug("blouse-dorothy-perkins")
                                .imageUrl("/images/top2.jpg")
                                .salePrice(14.0)
                                .comparePrice(21.0)
                                .quantity(15)
                                .shortDescription("Áo blouse Dororthy nữ tính thanh lịch")
                                .productDescription("Áo blouse Dorothy Perkins chất liệu voan nhẹ mềm mại nữ tính.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(10)
                                .note("White")
                                .sizes(new HashSet<>(Set.of("XS", "S", "M")))
                                .colors(new HashSet<>(Set.of("White", "Beige", "Navy")))
                                .tags(new HashSet<>(Set.of(tagTops, tagSale)))
                                .build();

                Product p6 = Product.builder()
                                .productName("Shirt")
                                .brandName("Mango")
                                .slug("shirt-mango")
                                .imageUrl("/images/top3.jpg")
                                .salePrice(9.0)
                                .comparePrice(0.0)
                                .quantity(50)
                                .shortDescription("Áo sơ mi Mango cơ bản")
                                .productDescription("Áo sơ mi nữ Mango kiểu dáng basic dễ phối đồ.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(0.0)
                                .reviewCount(0)
                                .note("White")
                                .sizes(new HashSet<>(Set.of("S", "M", "L", "XL")))
                                .colors(new HashSet<>(Set.of("White", "Grey", "Black")))
                                .tags(new HashSet<>(Set.of(tagTops, tagNew)))
                                .build();

                Product p9 = Product.builder()
                                .productName("Leather Shoes")
                                .brandName("GUCCI")
                                .slug("leather-shoes-gucci")
                                .imageUrl("/images/cat_shoes.jpg")
                                .salePrice(320.0)
                                .comparePrice(0.0)
                                .quantity(10)
                                .shortDescription("Giày da nam GUCCI cao cấp")
                                .productDescription(
                                                "Giày da nam GUCCI chất liệu da thật 100%, thiết kế lịch lãm, sang trọng.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(5)
                                .note("Black")
                                .sizes(new HashSet<>(Set.of("39", "40", "41", "42")))
                                .colors(new HashSet<>(Set.of("Black", "Brown")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p11 = Product.builder()
                                .productName("Classic Watch")
                                .brandName("s.Oliver")
                                .slug("classic-watch-soliver")
                                .imageUrl("/images/cat_accessories.jpg")
                                .salePrice(150.0)
                                .comparePrice(0.0)
                                .quantity(8)
                                .shortDescription("Đồng hồ nam s.Oliver cổ điển")
                                .productDescription(
                                                "Đồng hồ đeo tay s.Oliver thiết kế mặt kính sapphire chống xước, dây da cao cấp.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(4.7)
                                .reviewCount(14)
                                .note("Black")
                                .sizes(new HashSet<>(Set.of("One Size")))
                                .colors(new HashSet<>(Set.of("Gold", "Silver", "Black")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                // Seed products from /pic directory
                Product p14 = Product.builder()
                                .productName("Trendy Jacket")
                                .brandName("Mango")
                                .slug("trendy-jacket-mango")
                                .imageUrl("/images/image.png")
                                .salePrice(75.0)
                                .comparePrice(0.0)
                                .quantity(12)
                                .shortDescription("Áo khoác gió thời trang Mango")
                                .productDescription("Áo khoác gió Mango thiết kế cá tính, chống gió chống nước nhẹ.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(4.5)
                                .reviewCount(6)
                                .note("Black")
                                .sizes(new HashSet<>(Set.of("S", "M", "L")))
                                .colors(new HashSet<>(Set.of("Black", "Beige")))
                                .tags(new HashSet<>(Set.of(tagTops, tagNew)))
                                .build();

                Product p15 = Product.builder()
                                .productName("Casual Hoodie")
                                .brandName("adidas Originals")
                                .slug("casual-hoodie-adidas")
                                .imageUrl("/images/image (1).png")
                                .salePrice(65.0)
                                .comparePrice(0.0)
                                .quantity(20)
                                .shortDescription("Áo nỉ Hoodie adidas Originals")
                                .productDescription(
                                                "Áo nỉ có mũ adidas Originals mềm mại phong cách năng động thể thao.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(4.7)
                                .reviewCount(18)
                                .note("Gray")
                                .sizes(new HashSet<>(Set.of("M", "L", "XL")))
                                .colors(new HashSet<>(Set.of("Gray", "Black", "Red")))
                                .tags(new HashSet<>(Set.of(tagTops, tagNew)))
                                .build();

                Product p16 = Product.builder()
                                .productName("Active Wear")
                                .brandName("adidas")
                                .slug("active-wear-adidas")
                                .imageUrl("/images/image (2).png")
                                .salePrice(40.0)
                                .comparePrice(60.0)
                                .quantity(30)
                                .shortDescription("Bộ đồ thể thao adidas năng động")
                                .productDescription("Bộ quần áo thể thao adidas thoáng mát phù hợp luyện tập.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(4.3)
                                .reviewCount(8)
                                .note("Black")
                                .sizes(new HashSet<>(Set.of("S", "M", "L")))
                                .colors(new HashSet<>(Set.of("Black", "White")))
                                .tags(new HashSet<>(Set.of(tagTops, tagSale)))
                                .build();

                Product p17 = Product.builder()
                                .productName("Denim Jacket")
                                .brandName("Jack & Jones")
                                .slug("denim-jacket-jack-jones")
                                .imageUrl("/images/image (3).png")
                                .salePrice(89.0)
                                .comparePrice(0.0)
                                .quantity(15)
                                .shortDescription("Áo khoác bò Denim Jack & Jones")
                                .productDescription(
                                                "Áo khoác Denim bò nam từ thương hiệu Jack & Jones phong cách bụi bặm.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(4.8)
                                .reviewCount(10)
                                .note("Blue")
                                .sizes(new HashSet<>(Set.of("M", "L", "XL")))
                                .colors(new HashSet<>(Set.of("Blue", "Grey")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p18 = Product.builder()
                                .productName("Warm Parka")
                                .brandName("Blend")
                                .slug("warm-parka-blend")
                                .imageUrl("/images/image (4).png")
                                .salePrice(110.0)
                                .comparePrice(160.0)
                                .quantity(15)
                                .shortDescription("Áo khoác ấm phao Parka Blend")
                                .productDescription("Áo khoác giữ ấm chống tuyết Parka Blend phong cách Bắc Âu ấm áp.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(4.6)
                                .reviewCount(22)
                                .note("Navy")
                                .sizes(new HashSet<>(Set.of("S", "M", "L", "XL")))
                                .colors(new HashSet<>(Set.of("Navy", "Black")))
                                .tags(new HashSet<>(Set.of(tagSale)))
                                .build();

                Product p19 = Product.builder()
                                .productName("Elegant Suit")
                                .brandName("Boutique Moschino")
                                .slug("elegant-suit-moschino")
                                .imageUrl("/images/image (5).png")
                                .salePrice(450.0)
                                .comparePrice(0.0)
                                .quantity(5)
                                .shortDescription("Bộ Suit Moschino sang trọng quý phái")
                                .productDescription(
                                                "Bộ Comple Suit thiết kế cao cấp từ thương hiệu Boutique Moschino thanh lịch.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(2)
                                .note("Black")
                                .sizes(new HashSet<>(Set.of("XS", "S", "M", "L")))
                                .colors(new HashSet<>(Set.of("Black", "White", "Beige")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p20 = Product.builder()
                                .productName("Winter Coat")
                                .brandName("Champion")
                                .slug("winter-coat-champion")
                                .imageUrl("/images/image (6).png")
                                .salePrice(125.0)
                                .comparePrice(0.0)
                                .quantity(18)
                                .shortDescription("Áo khoác phao dày ấm Champion")
                                .productDescription("Áo phao dày mùa đông Champion giữ ấm tối ưu phom dáng thể thao.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(4.4)
                                .reviewCount(14)
                                .note("Black")
                                .sizes(new HashSet<>(Set.of("S", "M", "L")))
                                .colors(new HashSet<>(Set.of("Black", "Grey")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p21 = Product.builder()
                                .productName("Fashion Cardigan")
                                .brandName("s.Oliver")
                                .slug("fashion-cardigan-soliver")
                                .imageUrl("/images/image (7).png")
                                .salePrice(45.0)
                                .comparePrice(65.0)
                                .quantity(25)
                                .shortDescription("Áo len mỏng Cardigan s.Oliver")
                                .productDescription("Áo len cài khuy mỏng nhẹ Cardigan s.Oliver trẻ trung năng động.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(4.6)
                                .reviewCount(9)
                                .note("Beige")
                                .sizes(new HashSet<>(Set.of("S", "M", "L")))
                                .colors(new HashSet<>(Set.of("Beige", "White")))
                                .tags(new HashSet<>(Set.of(tagSale)))
                                .build();

                Product p22 = Product.builder()
                                .productName("Slim Fit Jeans")
                                .brandName("Diesel")
                                .slug("slim-fit-jeans-diesel")
                                .imageUrl("/images/image (8).png")
                                .salePrice(110.0)
                                .comparePrice(0.0)
                                .quantity(35)
                                .shortDescription("Quần Jeans bò Diesel dáng ôm")
                                .productDescription("Quần bò nam Jeans dáng ôm Slim Fit co giãn thoải mái hiệu Diesel.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(4.7)
                                .reviewCount(17)
                                .note("Blue")
                                .sizes(new HashSet<>(Set.of("30", "31", "32", "33")))
                                .colors(new HashSet<>(Set.of("Blue", "Black")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p23 = Product.builder()
                                .productName("Leather Handbag")
                                .brandName("Red Valentino")
                                .slug("leather-handbag-valentino")
                                .imageUrl("/images/image (9).png")
                                .salePrice(290.0)
                                .comparePrice(0.0)
                                .quantity(8)
                                .shortDescription("Túi xách da cao cấp Red Valentino")
                                .productDescription(
                                                "Túi xách nữ chất liệu da cao cấp chính hãng Red Valentino sang xịn mịn.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(3)
                                .note("Red")
                                .sizes(new HashSet<>(Set.of("One Size")))
                                .colors(new HashSet<>(Set.of("Red", "Black", "Beige")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p24 = Product.builder()
                                .productName("Wonyoung Summer Dress")
                                .brandName("Mango")
                                .slug("wonyoung-summer-dress")
                                .imageUrl("/images/jang wonyoung.jpg")
                                .salePrice(95.0)
                                .comparePrice(0.0)
                                .quantity(12)
                                .shortDescription("Đầm hè phong cách Jang WonYoung")
                                .productDescription("Đầm hè phong cách trẻ trung phối ren hoa ngọt ngào hiệu Mango.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(4.9)
                                .reviewCount(19)
                                .note("White")
                                .sizes(new HashSet<>(Set.of("XS", "S", "M")))
                                .colors(new HashSet<>(Set.of("White", "Red", "Beige")))
                                .tags(new HashSet<>(Set.of(tagTops, tagNew)))
                                .build();

                Product p25 = Product.builder()
                                .productName("Sport T-Shirt")
                                .brandName("adidas")
                                .slug("sport-tshirt-adidas")
                                .imageUrl("/images/z7888710050382_c815de657ca1f5b7ddcbf867874e8f03.jpg")
                                .salePrice(29.0)
                                .comparePrice(0.0)
                                .quantity(50)
                                .shortDescription("Áo thun thể thao adidas thoáng mát")
                                .productDescription("Áo thun thể thao adidas chất liệu thoáng khí, co giãn cực tốt.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(0)
                                .sizes(new HashSet<>(Set.of("S", "M", "L")))
                                .colors(new HashSet<>(Set.of("Black", "Red")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p26 = Product.builder()
                                .productName("Stylish Blouse")
                                .brandName("Mango")
                                .slug("stylish-blouse-mango")
                                .imageUrl("/images/z7888710132105_a4427b5a67d19b57addc1da878907bb5.jpg")
                                .salePrice(39.0)
                                .comparePrice(0.0)
                                .quantity(50)
                                .shortDescription("Áo blouse nữ Mango thời trang thanh lịch")
                                .productDescription(
                                                "Áo blouse Mango dáng ôm thanh lịch thích hợp cho môi trường công sở.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(0)
                                .sizes(new HashSet<>(Set.of("XS", "S", "M")))
                                .colors(new HashSet<>(Set.of("White", "Beige")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p29 = Product.builder()
                                .productName("Knit Pullover")
                                .brandName("Dorothy Perkins")
                                .slug("knit-pullover-dorothy")
                                .imageUrl("/images/z7889472051071_732f5bad4ebafe1b181a790c05127cd2.jpg")
                                .salePrice(49.0)
                                .comparePrice(0.0)
                                .quantity(50)
                                .shortDescription("Áo len cổ tròn Dorothy Perkins nhẹ nhàng")
                                .productDescription("Áo len cổ tròn dệt kim Dorothy Perkins mỏng nhẹ, giữ ấm vừa phải.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(0)
                                .sizes(new HashSet<>(Set.of("S", "M", "L")))
                                .colors(new HashSet<>(Set.of("Gray", "Blue")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p30 = Product.builder()
                                .productName("Summer Crop Top")
                                .brandName("Mango")
                                .slug("summer-crop-top-mango")
                                .imageUrl("/images/z7889472114938_990a1e9bb7b8741d00593e620a50ab96.jpg")
                                .salePrice(19.0)
                                .comparePrice(0.0)
                                .quantity(50)
                                .shortDescription("Áo thun crop top Mango mát mẻ")
                                .productDescription(
                                                "Áo thun crop top Mango năng động trẻ trung chất liệu cotton mát mẻ.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(0)
                                .sizes(new HashSet<>(Set.of("XS", "S", "M")))
                                .colors(new HashSet<>(Set.of("White", "Black")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p31 = Product.builder()
                                .productName("Luxury Handbag")
                                .brandName("GUCCI")
                                .slug("luxury-handbag-gucci")
                                .imageUrl("/images/z7896061153845_9b96b466a48f9b69b37bef5bba366cd6.jpg")
                                .salePrice(1200.0)
                                .comparePrice(0.0)
                                .quantity(50)
                                .shortDescription("Túi xách da GUCCI sang trọng quý phái")
                                .productDescription(
                                                "Túi xách da cao cấp từ thương hiệu xa xỉ GUCCI, thiết kế đẳng cấp thượng lưu.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(0)
                                .sizes(new HashSet<>(Set.of("One Size")))
                                .colors(new HashSet<>(Set.of("Black", "Beige")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p32 = Product.builder()
                                .productName("Designer Shirt")
                                .brandName("GUCCI")
                                .slug("designer-shirt-gucci")
                                .imageUrl("/images/z7896061153897_a1affd7cafd0e1981adaca17c1a00f05.jpg")
                                .salePrice(450.0)
                                .comparePrice(0.0)
                                .quantity(50)
                                .shortDescription("Áo sơ mi Gucci họa tiết độc đáo")
                                .productDescription(
                                                "Áo sơ mi lụa Gucci in họa tiết đặc trưng nổi bật và cá tính thời thượng.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(0)
                                .sizes(new HashSet<>(Set.of("S", "M", "L", "XL")))
                                .colors(new HashSet<>(Set.of("White", "Black")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p33 = Product.builder()
                                .productName("Spring Jacket")
                                .brandName("Jack & Jones")
                                .slug("spring-jacket-jj")
                                .imageUrl("/images/z7896061215982_3e3ff4b587a384f8f26741ae8971bdd5.jpg")
                                .salePrice(89.0)
                                .comparePrice(0.0)
                                .quantity(50)
                                .shortDescription("Áo khoác nhẹ Jack & Jones cho mùa xuân")
                                .productDescription(
                                                "Áo khoác nhẹ Jack & Jones phong cách trẻ trung năng động cho ngày xuân mát mẻ.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(0)
                                .sizes(new HashSet<>(Set.of("M", "L", "XL")))
                                .colors(new HashSet<>(Set.of("Black", "Blue")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                Product p34 = Product.builder()
                                .productName("Cozy Sweater")
                                .brandName("Blend")
                                .slug("cozy-sweater-blend")
                                .imageUrl("/images/z7896061244018_33d1f0a4cc39f828e69a0ff3725f7383.jpg")
                                .salePrice(55.0)
                                .comparePrice(0.0)
                                .quantity(50)
                                .shortDescription("Áo nỉ Blend ấm áp giữ nhiệt tốt")
                                .productDescription(
                                                "Áo nỉ dài tay chui đầu hiệu Blend chất liệu cotton nỉ bông siêu ấm áp.")
                                .productType("simple")
                                .published(true)
                                .ratingAverage(5.0)
                                .reviewCount(0)
                                .sizes(new HashSet<>(Set.of("S", "M", "L", "XL")))
                                .colors(new HashSet<>(Set.of("Gray", "Blue")))
                                .tags(new HashSet<>(Set.of(tagNew)))
                                .build();

                p4 = productRepository.save(p4);
                p5 = productRepository.save(p5);
                p6 = productRepository.save(p6);
                p9 = productRepository.save(p9);
                p11 = productRepository.save(p11);
                p14 = productRepository.save(p14);
                p15 = productRepository.save(p15);
                p16 = productRepository.save(p16);
                p17 = productRepository.save(p17);
                p18 = productRepository.save(p18);
                p19 = productRepository.save(p19);
                p20 = productRepository.save(p20);
                p21 = productRepository.save(p21);
                p22 = productRepository.save(p22);
                p23 = productRepository.save(p23);
                p24 = productRepository.save(p24);
                p25 = productRepository.save(p25);
                p26 = productRepository.save(p26);
                p29 = productRepository.save(p29);
                p30 = productRepository.save(p30);
                p31 = productRepository.save(p31);
                p32 = productRepository.save(p32);
                p33 = productRepository.save(p33);
                p34 = productRepository.save(p34);

                linkProductToCategory(p4, catNew);
                linkProductToCategory(p5, catClothes);
                linkProductToCategory(p6, catClothes);
                linkProductToCategory(p9, catShoes);
                linkProductToCategory(p11, catAccessories);
                linkProductToCategory(p14, catClothes);
                linkProductToCategory(p15, catClothes);
                linkProductToCategory(p16, catClothes);
                linkProductToCategory(p17, catClothes);
                linkProductToCategory(p18, catClothes);
                linkProductToCategory(p19, catClothes);
                linkProductToCategory(p20, catClothes);
                linkProductToCategory(p21, catClothes);
                linkProductToCategory(p22, catClothes);
                linkProductToCategory(p23, catAccessories);
                linkProductToCategory(p24, catClothes);
                linkProductToCategory(p25, catClothes, catNew);
                linkProductToCategory(p26, catClothes, catNew);
                linkProductToCategory(p29, catClothes, catNew);
                linkProductToCategory(p30, catClothes, catNew);
                linkProductToCategory(p31, catAccessories, catNew);
                linkProductToCategory(p32, catClothes, catNew);
                linkProductToCategory(p33, catClothes, catNew);
                linkProductToCategory(p34, catClothes, catNew);
                log.info("TOPS and other product data initialized successfully.");

        }

        private void linkProductToCategory(Product product, Category... categories) {
                if (product.getCategories() == null) {
                        product.setCategories(new HashSet<>());
                }
                boolean modified = false;
                for (Category category : categories) {
                        boolean exists = product.getCategories().stream()
                                        .anyMatch(c -> c.getId().equals(category.getId()));
                        if (!exists) {
                                product.getCategories().add(category);
                                modified = true;
                        }
                }
                if (modified) {
                        productRepository.save(product);
                }
        }
}
