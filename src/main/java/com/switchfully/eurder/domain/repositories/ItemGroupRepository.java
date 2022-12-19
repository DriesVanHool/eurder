package com.switchfully.eurder.domain.repositories;

import com.switchfully.eurder.domain.ItemGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemGroupRepository extends JpaRepository<ItemGroup, Integer> {
    List<ItemGroup> getItemGroupsById(int id);
}
