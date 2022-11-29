package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.CreateItemDto;
import com.switchfully.eurder.api.dtos.ItemDto;
import com.switchfully.eurder.domain.security.Feature;
import com.switchfully.eurder.services.ItemService;
import com.switchfully.eurder.services.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    public ItemDto createItem(@RequestHeader String authorization, @RequestBody CreateItemDto createItemDto) {
        securityService.validateAuthorisation(authorization, Feature.ADD_ITEM);
        return itemService.addItem(createItemDto);
    }

    @GetMapping()
    public List<ItemDto> getAllItems(@RequestHeader String authorization) {
        securityService.validateAuthorisation(authorization, Feature.GET_ITEMS);
        return itemService.getAllItems();
    }

    @GetMapping(params = "supply")
    public List<ItemDto> getAllItemsBySuplly(@RequestParam String supply, @RequestHeader String authorization) {
        securityService.validateAuthorisation(authorization, Feature.GET_ITEMS);
        return itemService.getAllItemsBySuplly(supply);
    }
}
