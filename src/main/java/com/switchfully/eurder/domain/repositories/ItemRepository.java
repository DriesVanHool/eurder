package com.switchfully.eurder.domain.repositories;

import com.switchfully.eurder.domain.Item;
import com.switchfully.eurder.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> getItemsById(int id);
    List<Item> findAll();

}
