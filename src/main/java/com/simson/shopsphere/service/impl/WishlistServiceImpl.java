package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.entity.Product;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.entity.Wishlist;
import com.simson.shopsphere.entity.WishlistItem;
import com.simson.shopsphere.exception.ResourceNotFoundException;
import com.simson.shopsphere.repository.ProductRepository;
import com.simson.shopsphere.repository.WishlistItemRepository;
import com.simson.shopsphere.repository.WishlistRepository;
import com.simson.shopsphere.service.CartService;
import com.simson.shopsphere.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WishlistServiceImpl.class);

    public WishlistServiceImpl(WishlistRepository wishlistRepository, WishlistItemRepository wishlistItemRepository, ProductRepository productRepository, CartService cartService) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }


    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public Wishlist getOrCreateUserWishlist(User user) {
        return wishlistRepository.findByUser(user).orElseGet(() -> {
            Wishlist newWishlist = Wishlist.builder().user(user).build();
            return wishlistRepository.save(newWishlist);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getUserWishlistProducts(User user) {
        if (user == null) {
            return List.of();
        }
        Wishlist wishlist = getOrCreateUserWishlist(user);
        return wishlist.getItems().stream()
                .map(WishlistItem::getProduct)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addToWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Wishlist wishlist = getOrCreateUserWishlist(user);

        if (!wishlistItemRepository.existsByWishlistAndProduct(wishlist, product)) {
            WishlistItem item = WishlistItem.builder()
                    .wishlist(wishlist)
                    .product(product)
                    .build();
            wishlistItemRepository.save(item);
            log.info("Product {} added to wishlist for user {}", product.getName(), user.getEmail());
        }
    }

    @Override
    @Transactional
    public void removeFromWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Wishlist wishlist = getOrCreateUserWishlist(user);
        wishlistItemRepository.deleteByWishlistAndProduct(wishlist, product);
        log.info("Product {} removed from wishlist for user {}", product.getName(), user.getEmail());
    }

    @Override
    @Transactional
    public void moveToCart(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        cartService.addToCart(user, productId, 1);
        removeFromWishlist(user, productId);
        log.info("Moved product {} from wishlist to cart for user {}", product.getName(), user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInWishlist(User user, Long productId) {
        if (user == null || productId == null) {
            return false;
        }
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return false;
        }
        Wishlist wishlist = getOrCreateUserWishlist(user);
        return wishlistItemRepository.existsByWishlistAndProduct(wishlist, product);
    }

    @Override
    @Transactional(readOnly = true)
    public int getWishlistItemCount(User user) {
        if (user == null) {
            return 0;
        }
        return wishlistRepository.findByUser(user)
                .map(w -> w.getItems().size())
                .orElse(0);
    }
}
