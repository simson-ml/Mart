package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.dto.CartDto;
import com.simson.shopsphere.dto.CartItemDto;
import com.simson.shopsphere.entity.Cart;
import com.simson.shopsphere.entity.CartItem;
import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.exception.BadRequestException;
import com.simson.shopsphere.exception.InsufficientStockException;
import com.simson.shopsphere.exception.ResourceNotFoundException;
import com.simson.shopsphere.repository.CartItemRepository;
import com.simson.shopsphere.repository.CartRepository;
import com.simson.shopsphere.repository.ProductRepository;
import com.simson.shopsphere.service.CartService;
import com.simson.shopsphere.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CartServiceImpl.class);

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, CouponService couponService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.couponService = couponService;
    }


    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CouponService couponService;

    private static final BigDecimal FREE_DELIVERY_THRESHOLD = BigDecimal.valueOf(999.00);
    private static final BigDecimal STANDARD_DELIVERY_FEE = BigDecimal.valueOf(99.00);

    @Override
    @Transactional
    public Cart getOrCreateUserCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = Cart.builder().user(user).build();
            return cartRepository.save(newCart);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getCartDto(User user, String couponCode) {
        if (user == null) {
            return CartDto.builder()
                    .subtotal(BigDecimal.ZERO)
                    .originalTotal(BigDecimal.ZERO)
                    .totalSavings(BigDecimal.ZERO)
                    .deliveryCharge(BigDecimal.ZERO)
                    .discountAmount(BigDecimal.ZERO)
                    .netTotal(BigDecimal.ZERO)
                    .totalItemCount(0)
                    .build();
        }

        Cart cart = getOrCreateUserCart(user);
        List<CartItemDto> itemDtos = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal originalTotal = BigDecimal.ZERO;
        int totalItemCount = 0;

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            BigDecimal unitPrice = product.getDiscountedPrice();
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal itemOriginal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            subtotal = subtotal.add(itemSubtotal);
            originalTotal = originalTotal.add(itemOriginal);
            totalItemCount += item.getQuantity();

            itemDtos.add(CartItemDto.builder()
                    .id(item.getId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .productSlug(product.getSlug())
                    .productBrand(product.getBrand())
                    .productImageUrl(product.getImageUrl())
                    .productSku(product.getSku())
                    .originalPrice(product.getPrice())
                    .discountPercentage(product.getDiscountPercentage())
                    .unitPrice(unitPrice)
                    .quantity(item.getQuantity())
                    .availableStock(product.getStockQuantity())
                    .subtotal(itemSubtotal)
                    .build());
        }

        BigDecimal deliveryCharge = BigDecimal.ZERO;
        if (subtotal.compareTo(BigDecimal.ZERO) > 0 && subtotal.compareTo(FREE_DELIVERY_THRESHOLD) < 0) {
            deliveryCharge = STANDARD_DELIVERY_FEE;
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isBlank() && subtotal.compareTo(BigDecimal.ZERO) > 0) {
            try {
                discountAmount = couponService.calculateDiscount(couponCode, subtotal);
            } catch (Exception e) {
                log.debug("Coupon {} could not be applied: {}", couponCode, e.getMessage());
            }
        }

        BigDecimal netTotal = subtotal.subtract(discountAmount).add(deliveryCharge);
        if (netTotal.compareTo(BigDecimal.ZERO) < 0) {
            netTotal = BigDecimal.ZERO;
        }

        BigDecimal totalSavings = originalTotal.subtract(subtotal).add(discountAmount);

        return CartDto.builder()
                .id(cart.getId())
                .items(itemDtos)
                .subtotal(subtotal)
                .originalTotal(originalTotal)
                .totalSavings(totalSavings)
                .deliveryCharge(deliveryCharge)
                .discountAmount(discountAmount)
                .netTotal(netTotal)
                .couponCode(couponCode)
                .totalItemCount(totalItemCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getGuestCartDto(java.util.Map<Long, Integer> guestItems, String couponCode) {
        if (guestItems == null || guestItems.isEmpty()) {
            return CartDto.builder()
                    .subtotal(BigDecimal.ZERO)
                    .originalTotal(BigDecimal.ZERO)
                    .totalSavings(BigDecimal.ZERO)
                    .deliveryCharge(BigDecimal.ZERO)
                    .discountAmount(BigDecimal.ZERO)
                    .netTotal(BigDecimal.ZERO)
                    .totalItemCount(0)
                    .build();
        }

        List<CartItemDto> itemDtos = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal originalTotal = BigDecimal.ZERO;
        int totalItemCount = 0;

        for (java.util.Map.Entry<Long, Integer> entry : guestItems.entrySet()) {
            Long productId = entry.getKey();
            int qty = entry.getValue();
            if (qty <= 0) continue;

            Optional<Product> prodOpt = productRepository.findById(productId);
            if (prodOpt.isEmpty()) continue;
            Product product = prodOpt.get();
            if (!product.isActive()) continue;

            BigDecimal unitPrice = product.getDiscountedPrice();
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(qty));
            BigDecimal itemOriginal = product.getPrice().multiply(BigDecimal.valueOf(qty));

            subtotal = subtotal.add(itemSubtotal);
            originalTotal = originalTotal.add(itemOriginal);
            totalItemCount += qty;

            itemDtos.add(CartItemDto.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productSlug(product.getSlug())
                    .productBrand(product.getBrand())
                    .productImageUrl(product.getImageUrl())
                    .productSku(product.getSku())
                    .originalPrice(product.getPrice())
                    .discountPercentage(product.getDiscountPercentage())
                    .unitPrice(unitPrice)
                    .quantity(qty)
                    .availableStock(product.getStockQuantity())
                    .subtotal(itemSubtotal)
                    .build());
        }

        BigDecimal deliveryCharge = BigDecimal.ZERO;
        if (subtotal.compareTo(BigDecimal.ZERO) > 0 && subtotal.compareTo(FREE_DELIVERY_THRESHOLD) < 0) {
            deliveryCharge = STANDARD_DELIVERY_FEE;
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isBlank() && subtotal.compareTo(BigDecimal.ZERO) > 0) {
            try {
                discountAmount = couponService.calculateDiscount(couponCode, subtotal);
            } catch (Exception e) {
                log.debug("Coupon {} could not be applied: {}", couponCode, e.getMessage());
            }
        }

        BigDecimal netTotal = subtotal.subtract(discountAmount).add(deliveryCharge);
        if (netTotal.compareTo(BigDecimal.ZERO) < 0) {
            netTotal = BigDecimal.ZERO;
        }

        BigDecimal totalSavings = originalTotal.subtract(subtotal).add(discountAmount);

        return CartDto.builder()
                .items(itemDtos)
                .subtotal(subtotal)
                .originalTotal(originalTotal)
                .totalSavings(totalSavings)
                .deliveryCharge(deliveryCharge)
                .discountAmount(discountAmount)
                .netTotal(netTotal)
                .couponCode(couponCode)
                .totalItemCount(totalItemCount)
                .build();
    }

    @Override
    @Transactional
    public void mergeGuestCart(User user, java.util.Map<Long, Integer> guestItems) {
        if (user == null || guestItems == null || guestItems.isEmpty()) {
            return;
        }
        for (java.util.Map.Entry<Long, Integer> entry : guestItems.entrySet()) {
            Long productId = entry.getKey();
            int qty = entry.getValue();
            if (qty > 0) {
                try {
                    addToCart(user, productId, qty);
                } catch (Exception e) {
                    log.warn("Could not merge guest cart item (productId={}): {}", productId, e.getMessage());
                }
            }
        }
    }

    @Override
    @Transactional
    public void addToCart(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!product.isActive()) {
            throw new BadRequestException("This product is currently not available for purchase.");
        }

        if (product.getStockQuantity() <= 0) {
            throw new InsufficientStockException("Sorry, '" + product.getName() + "' is out of stock.");
        }

        Cart cart = getOrCreateUserCart(user);
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItemOpt.isPresent()) {
            CartItem existing = existingItemOpt.get();
            int newQuantity = existing.getQuantity() + quantity;
            if (newQuantity > product.getStockQuantity()) {
                throw new InsufficientStockException("Cannot add " + quantity + " more. Only " + product.getStockQuantity() + " units in stock (" + existing.getQuantity() + " already in your cart).");
            }
            existing.setQuantity(newQuantity);
            cartItemRepository.save(existing);
        } else {
            if (quantity > product.getStockQuantity()) {
                throw new InsufficientStockException("Requested quantity (" + quantity + ") exceeds available stock (" + product.getStockQuantity() + ").");
            }
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cartItemRepository.save(newItem);
        }
    }

    @Override
    @Transactional
    public void updateQuantity(User user, Long productId, int quantity) {
        Cart cart = getOrCreateUserCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (quantity <= 0) {
            cartItemRepository.deleteByCartAndProduct(cart, product);
            return;
        }

        if (quantity > product.getStockQuantity()) {
            throw new InsufficientStockException("Only " + product.getStockQuantity() + " units available in stock.");
        }

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new ResourceNotFoundException("Product not in cart"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Override
    @Transactional
    public void removeFromCart(User user, Long productId) {
        Cart cart = getOrCreateUserCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        cartItemRepository.deleteByCartAndProduct(cart, product);
    }

    @Override
    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateUserCart(user);
        cartItemRepository.deleteAllByCart(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(User user) {
        if (user == null) {
            return 0;
        }
        return cartRepository.findByUser(user)
                .map(Cart::getTotalItemCount)
                .orElse(0);
    }
}
