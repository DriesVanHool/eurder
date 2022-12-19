package com.switchfully.eurder.domain.repositories;

import com.switchfully.eurder.domain.Item;
import com.switchfully.eurder.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}
