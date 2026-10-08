package com.simson.shopsphere.controller.api;

import com.simson.shopsphere.dto.ApiResponse;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.UserService;
import com.simson.shopsphere.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist API", description = "Endpoints for managing user wishlist")
public class ApiWishlistController {

    public ApiWishlistController(WishlistService wishlistService, UserService userService) {
        this.wishlistService = wishlistService;
        this.userService = userService;
    }


    private final WishlistService wishlistService;
    private final UserService userService;

    @PostMapping("/toggle/{productId}")
    @Operation(summary = "Toggle product in user wishlist")
    public ResponseEntity<ApiResponse<Boolean>> toggleWishlist(@PathVariable Long productId) {
        User user = userService.getCurrentAuthenticatedUser();
        boolean inWishlist = wishlistService.isProductInWishlist(user, productId);
        if (inWishlist) {
            wishlistService.removeFromWishlist(user, productId);
            return ResponseEntity.ok(ApiResponse.ok("Removed from wishlist", false));
        } else {
            wishlistService.addToWishlist(user, productId);
            return ResponseEntity.ok(ApiResponse.ok("Added to wishlist", true));
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Get total count of items in user wishlist")
    public ResponseEntity<ApiResponse<Integer>> getWishlistCount() {
        User user = userService.getCurrentAuthenticatedUser();
        int count = wishlistService.getWishlistItemCount(user);
        return ResponseEntity.ok(ApiResponse.ok("Wishlist count retrieved", count));
    }
}
