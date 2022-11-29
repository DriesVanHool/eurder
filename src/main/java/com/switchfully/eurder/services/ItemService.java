package com.switchfully.eurder.services;

import com.switchfully.eurder.api.dtos.CreateItemDto;
import com.switchfully.eurder.api.dtos.ItemDto;
import com.switchfully.eurder.domain.Item;
import com.switchfully.eurder.domain.exceptions.InvallidInputException;
import com.switchfully.eurder.domain.repositories.ItemRepository;
import com.switchfully.eurder.services.mappers.ItemMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ItemService {

    ItemRepository itemRepository;
    ItemMapper itemMapper;

    public ItemService(ItemRepository itemRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    public ItemDto addItem(CreateItemDto createItemDto) throws InvallidInputException {
        ArrayList<String> errors = validateUserInput(createItemDto);
        if (errors.size() > 0) throw new InvallidInputException(errors);
        Item item = new Item(createItemDto.name(), createItemDto.description(), createItemDto.price(), createItemDto.amount());
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
}
