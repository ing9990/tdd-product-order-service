package com.productorderservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

class ProductOrderServiceApplicationTests {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        ProductRepository productRepository = new ProductRepository();
        ProductPort productPort = new ProductAdapter(productRepository);
        productService = new ProductService(productPort);
    }

    @Test
    void contextLoads() {

    }

    @DisplayName("POJO 상품 등록 서비스")
    @Test
    void 상품_등록() {
        final AddProductRequest request = 상품등록요청_생성();

        productService.addProduct(request);
    }

    private static AddProductRequest 상품등록요청_생성() {
        final String name = "상품명";
        final int price = 10000;
        final DiscountPolicy discountPolicy = DiscountPolicy.NONE;

        return new AddProductRequest(name, price, discountPolicy);
    }

    private record AddProductRequest(String name, int price, DiscountPolicy discountPolicy) {
        private AddProductRequest {
            Assert.hasText(name, "상품명은 필수 값입니다.");
            Assert.isTrue(price > 0, "상품 가격은 0보다 커야합니다.");
            Assert.notNull(discountPolicy, "할인 정책은 필수 값입니다.");
        }
    }

    public enum DiscountPolicy {
        NONE,
    }

    private class ProductService {
        private final ProductPort productPort;

        private ProductService(ProductPort productPort) {
            this.productPort = productPort;
        }

        public void addProduct(AddProductRequest request) {
            final Product product = new Product(request.name, request.price, request.discountPolicy);
            productPort.addProduct(product);
        }

    }

    private class Product {
        private Long id;

        private final String name;
        private final int price;
        private final DiscountPolicy discountPolicy;

        public Product(final String name, final int price, final DiscountPolicy discountPolicy) {
            Assert.hasText(name, "이름은 필수 값입니다.");
            Assert.isTrue(price > 0, "수량은 최소 한개 이상입니다.");
            Assert.notNull(discountPolicy, "할인 정책은 필수 값입니다.");

            this.name = name;
            this.price = price;
            this.discountPolicy = discountPolicy;
        }

        public Long getId() {
            return id;
        }

        public void assginId(Long id) {
            this.id = id;
        }
    }

    private interface ProductPort {
        void addProduct(Product product);
    }

    private class ProductAdapter implements ProductPort {

        private final ProductRepository productRepository;

        private ProductAdapter(final ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        @Override
        public void addProduct(final Product product) {
            productRepository.save(product);
        }
    }

    private class ProductRepository {
        private Long seq = 0L;
        private final Map<Long, Product> persistence = new HashMap<>();

        public void save(Product product) {
            product.assginId(++seq);
            persistence.put(product.getId(), product);
        }
    }
}










