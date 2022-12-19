package com.switchfully.eurder.services;

import com.switchfully.eurder.api.dtos.CreateItemDto;
import com.switchfully.eurder.api.dtos.ItemDto;
import com.switchfully.eurder.api.dtos.ItemShippingDto;
import com.switchfully.eurder.domain.*;
import com.switchfully.eurder.domain.exceptions.InvallidInputException;
import com.switchfully.eurder.domain.repositories.ItemRepository;
import com.switchfully.eurder.domain.repositories.OrderRepository;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.services.mappers.ItemMapper;
import org.springframework.security.core.Transient;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemService {

ItemRepository itemRepository;

    OrderRepository orderRepository;

    UserRepository userRepository;
    ItemMapper itemMapper;

    public ItemService(ItemRepository itemRepository, OrderRepository orderRepository, UserRepository userRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }

    public ItemDto addItem(CreateItemDto createItemDto) throws InvallidInputException {
        Item item = new Item(createItemDto.name(), createItemDto.description(), createItemDto.price(), createItemDto.amount());
        return itemMapper.toDto(itemRepository.save(item));
    }

    public ItemDto updateItem(String id, CreateItemDto createItemDto) throws InvallidInputException {
        Item item = itemRepository.getItemsById(validateItemId(id)).stream().findFirst().orElseThrow(()->new RuntimeException("Database inconsistency"));
        item.setName(createItemDto.name());
        item.setDescription(createItemDto.description());
        item.setPrice(createItemDto.price());
        item.setAmount(createItemDto.amount());
        return itemMapper.toDto(item);
    }

    public int validateItemId(String id) {
        int itemId;
        try {
            itemId = Integer.parseInt(id);
        }catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("Invalid id");
        }
        if (new ArrayList<>(itemRepository.getItemsById(itemId)).isEmpty()) throw new NoSuchElementException("No item exists with id :" + id);
        return itemId;
    }



    public List<ItemDto> getAllItems() {
        List<Item> itemsHighToLowSupply = itemRepository.findAll().stream().sorted(Comparator.comparing(Item::getAmount).reversed()).toList();
        return itemMapper.toDto(itemsHighToLowSupply);
    }


    public List<ItemDto> getAllItemsBySupply(String supply) {
        StockLvl lvl = switch (supply.toLowerCase()) {
            case "low" -> StockLvl.STOCK_LOW;
            case "medium" -> StockLvl.STOCK_MEDIUM;
            case "high" -> StockLvl.STOCK_HIGH;
            default -> throw new IllegalArgumentException("You can only filter on \"low\", \"medium\" or \"high\".");
        };
        return getAllItems().stream().filter(itemDto -> itemDto.stockLvl() == lvl).collect(Collectors.toList());
    }

/*
    public List<ItemShippingDto> getAllItemsToShipToday() {
        return getAllItemsToShip().stream().filter(itemShippingDto -> itemShippingDto.itemGroup().getShippingDate().equals(LocalDate.now())).toList();
    }

    public List<ItemShippingDto> getAllItemsToShip() {
        List<ItemShippingDto> itemShippings = new ArrayList<>();

        for (Order order : orderRepository.getOrders()) {
            Optional<User> user = userRepository.getUserById(order.getCustomerId());
            Adress adress;
            if (user.isEmpty()) {
                adress = null;
            } else {
                adress = user.get().getAdress();
            }
            for (ItemGroup itemGroup : order.getItemGroups()) {
                itemShippings.add(new ItemShippingDto(itemGroup, adress));
            }
        }
        return itemShippings;
    }*/
}
