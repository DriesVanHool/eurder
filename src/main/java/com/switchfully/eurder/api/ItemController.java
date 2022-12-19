package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.CreateItemDto;
import com.switchfully.eurder.api.dtos.ItemDto;
import com.switchfully.eurder.api.dtos.ItemShippingDto;
import com.switchfully.eurder.domain.security.Feature;
import com.switchfully.eurder.services.ItemService;
import com.switchfully.eurder.services.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("stock")
public class ItemController {
    SecurityService securityService;
    ItemService itemService;

    public ItemController(SecurityService securityService, ItemService itemService) {
        this.securityService = securityService;
        this.itemService = itemService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ItemDto createItem(@Valid @RequestBody CreateItemDto createItemDto) {
        return itemService.addItem(createItemDto);
    }

    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ItemDto updateItem(@PathVariable String id, @RequestBody CreateItemDto createItemDto) {
        return itemService.updateItem(id, createItemDto);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ItemDto> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping(params = "supply")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ItemDto> getAllItemsBySupply(@RequestParam String supply) {
        return itemService.getAllItemsBySupply(supply);
    }

/*    @GetMapping(path = "shipToday")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ItemShippingDto> getAllItemsToShipToday() {
        return itemService.getAllItemsToShipToday();
    }*/
}
