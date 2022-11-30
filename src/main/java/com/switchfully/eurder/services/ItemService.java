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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
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
        ArrayList<String> errors = validateUserInput(createItemDto);
        if (errors.size() > 0) throw new InvallidInputException(errors);
        Item item = new Item(createItemDto.name(), createItemDto.description(), createItemDto.price(), createItemDto.amount());
        return itemMapper.toDto(itemRepository.save(item));
    }

    public ItemDto updateItem(String id, CreateItemDto createItemDto) throws InvallidInputException {
        ArrayList<String> errors = validateUserInput(createItemDto);
        if (validateItemId(id)) throw new NoSuchElementException("No item exists with id :" + id);
        if (errors.size() > 0) throw new InvallidInputException(errors);
        Item item = new Item(id, createItemDto.name(), createItemDto.description(), createItemDto.price(), createItemDto.amount());
        return itemMapper.toDto(itemRepository.save(item));
    }

    public ArrayList<String> validateUserInput(CreateItemDto createItemDto) {
        ArrayList<String> errors = new ArrayList<>();
        if (createItemDto.name().isEmpty()) {
            errors.add("name");
        }
        if (createItemDto.description().isEmpty()) {
            errors.add("description");
        }
        if (createItemDto.price() < 0) {
            errors.add("price");
        }
        if (createItemDto.amount() < 0) {
            errors.add("amount");
        }
        return errors;
    }

    public boolean validateItemId(String id) {
        return itemRepository.getItemById(id).isEmpty();
    }

    public List<ItemDto> getAllItems() {
        List<Item> itemsHighToLowSupply = itemRepository.getAllItems().stream().sorted(Comparator.comparing(Item::getAmount).reversed()).toList();
        return itemMapper.toDto(itemsHighToLowSupply);
    }


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

}
