package com.simson.shopsphere.controller;

import com.simson.shopsphere.dto.AddressDto;
import com.simson.shopsphere.entity.Address;
import com.simson.shopsphere.entity.User;
import com.simson.shopsphere.service.AddressService;
import com.simson.shopsphere.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/account/addresses")
public class AddressController {

    public AddressController(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }


    private final AddressService addressService;
    private final UserService userService;

    @GetMapping
    public String listAddresses(Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        List<Address> addresses = addressService.getUserAddresses(user);
        model.addAttribute("addresses", addresses);
        if (!model.containsAttribute("addressDto")) {
            model.addAttribute("addressDto", new AddressDto());
        }
        return "account/addresses";
    }

    @GetMapping("/add")
    public String showAddAddressForm(Model model) {
        if (!model.containsAttribute("addressDto")) {
            model.addAttribute("addressDto", new AddressDto());
        }
        model.addAttribute("editMode", false);
        return "account/add-address";
    }

    @PostMapping("/add")
    public String addAddress(
            @Valid @ModelAttribute("addressDto") AddressDto addressDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        if (bindingResult.hasErrors()) {
            return "account/add-address";
        }

        addressService.createAddress(user, addressDto);
        redirectAttributes.addFlashAttribute("successMessage", "Address added successfully!");
        return "redirect:/account/addresses";
    }

    @GetMapping("/edit/{id}")
    public String showEditAddressForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentAuthenticatedUser();
        try {
            Address address = addressService.getAddressById(id, user);
            AddressDto dto = AddressDto.builder()
                    .id(address.getId())
                    .fullName(address.getFullName())
                    .phone(address.getPhone())
                    .addressLine1(address.getAddressLine1())
                    .addressLine2(address.getAddressLine2())
                    .city(address.getCity())
                    .state(address.getState())
                    .pincode(address.getPincode())
                    .addressType(address.getAddressType())
                    .defaultAddress(address.isDefault())
                    .build();
            model.addAttribute("addressDto", dto);
            model.addAttribute("editMode", true);
            model.addAttribute("addressId", id);
            return "account/add-address";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/account/addresses";
        }
    }

    @PostMapping("/edit/{id}")
    public String editAddress(
            @PathVariable Long id,
            @Valid @ModelAttribute("addressDto") AddressDto addressDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        User user = userService.getCurrentAuthenticatedUser();
        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", true);
            model.addAttribute("addressId", id);
            return "account/add-address";
        }

        addressService.updateAddress(id, user, addressDto);
        redirectAttributes.addFlashAttribute("successMessage", "Address updated successfully!");
        return "redirect:/account/addresses";
    }

    @PostMapping("/delete/{id}")
    public String deleteAddress(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentAuthenticatedUser();
        addressService.deleteAddress(id, user);
        redirectAttributes.addFlashAttribute("infoMessage", "Address removed.");
        return "redirect:/account/addresses";
    }

    @PostMapping("/default/{id}")
    public String setDefaultAddress(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentAuthenticatedUser();
        addressService.setDefaultAddress(id, user);
        redirectAttributes.addFlashAttribute("successMessage", "Default address updated.");
        return "redirect:/account/addresses";
    }
}
