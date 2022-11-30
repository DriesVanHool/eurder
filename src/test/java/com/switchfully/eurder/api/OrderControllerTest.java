package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.OrderDto;
import com.switchfully.eurder.api.dtos.TotalOrderReportDto;
import com.switchfully.eurder.domain.Adress;
import com.switchfully.eurder.domain.Item;
import com.switchfully.eurder.domain.StockLvl;
import com.switchfully.eurder.domain.User;
import com.switchfully.eurder.domain.repositories.ItemRepository;
import com.switchfully.eurder.domain.repositories.OrderRepository;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.domain.security.Role;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    private OrderRepository orders;

    @Autowired
    private UserRepository users;

    @Autowired
    private ItemRepository items;

    @Test
    void whenOrderingExistingItems() {
        items.save(new Item("15", "Laptop", "To type on", 2000, 2));
        items.save(new Item("16", "Phone", "For calling", 1800, 11));
        Item itemTocheck2 = items.save(new Item("17", "Monitor", "For watching", 300, 6));

        JSONObject item1 = new JSONObject();
        JSONObject item2 = new JSONObject();
        item1.put("itemId", "15");
        item1.put("amount", "3");
        item2.put("itemId", "17");
        item2.put("amount", "5");
        List<JSONObject> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        OrderDto result = RestAssured.given().port(port).auth().preemptive().basic("1", "pwd").log().all().contentType("application/json")
                .body(items)
                .when().post("orders")
                .then().statusCode(201).extract().body().as(OrderDto.class);

        Assertions.assertEquals(7500, result.totalPrice());
        Assertions.assertEquals(LocalDate.now().plusDays(7), result.itemGroups().get(0).shippingDate());
        Assertions.assertEquals(LocalDate.now(), result.itemGroups().get(1).shippingDate());
        Assertions.assertEquals(1, itemTocheck2.getAmount());
        Assertions.assertEquals(StockLvl.STOCK_LOW, itemTocheck2.getStockLvl());
    }

    @Test
    void whenOrderingANonExistingItem() {
        JSONObject item1 = new JSONObject();
        item1.put("itemId", "xyz");
        item1.put("amount", "3");
        List<JSONObject> items = new ArrayList<>();
        items.add(item1);

        Map<String, String> result = RestAssured.given().port(port).auth().preemptive().basic("1", "pwd").log().all().contentType("application/json")
                .body(items)
                .when().post("orders")
                .then().statusCode(400).extract().body().as(new TypeRef<Map<String, String>>() {
                });
        String responseMessage = result.get("message");

        assertEquals("No item exists with id :xyz", responseMessage);

    }


    @Test
    void whenGettingOrderReport() {
        users.save(new User("10", "Test", "Tester", "test@test.com", "0123456789", new Adress("Street", "number", "City Name"), "pwd", Role.CUSTOMER));
        items.save(new Item("20", "Laptop", "To type on", 3000, 2));
        items.save(new Item("30", "Phone", "For calling", 2000, 12));

        JSONObject item1 = new JSONObject();
        JSONObject item2 = new JSONObject();
        item1.put("itemId", "20");
        item1.put("amount", "1");
        item2.put("itemId", "30");
        item2.put("amount", "3");
        List<JSONObject> order1 = new ArrayList<>();
        order1.add(item1);
        order1.add(item2);

        List<JSONObject> order2 = new ArrayList<>();
        order1.add(item2);

        RestAssured.given().port(port).auth().preemptive().basic("10", "pwd").log().all().contentType("application/json")
                .body(order1)
                .when().post("orders")
                .then().statusCode(201).extract().body().as(OrderDto.class);
        RestAssured.given().port(port).auth().preemptive().basic("10", "pwd").log().all().contentType("application/json")
                .body(order2)
                .when().post("orders")
                .then().statusCode(201).extract().body().as(OrderDto.class);


        TotalOrderReportDto result = RestAssured.given().port(port).auth().preemptive().basic("10", "pwd").log().all().contentType("application/json")
                .body(order2)
                .when().get("orders")
                .then().statusCode(200).extract().body().as(TotalOrderReportDto.class);

        assertEquals("Phone", result.orders().get(0).itemGroups().get(1).name());
        assertEquals(15000, result.totalPrice());
    }
}