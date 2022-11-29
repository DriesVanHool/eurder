package com.switchfully.eurder.domain.repositories;

import com.switchfully.eurder.domain.Item;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {
    private final Map<String, Item> itemMap = new HashMap<>();

    public Item save(Item item) {
        itemMap.put(item.getId(), item);
        return item;
    }

    public List<Item> getAllItems() {
        return itemMap.values().stream().toList();
    }
}
