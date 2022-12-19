package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.OrderDto;
import com.switchfully.eurder.api.dtos.TotalOrderReportDto;
import com.switchfully.eurder.domain.*;
import com.switchfully.eurder.domain.repositories.ItemRepository;
import com.switchfully.eurder.domain.repositories.OrderRepository;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.domain.security.Role;
import io.restassured.RestAssured;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class OrderControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    private OrderRepository orders;

    @Autowired
    private UserRepository users;

    @Autowired
    private ItemRepository items;

    private Item item1;
    private Item item2;
    private Item item3;
    private Item item4;
    private Item item5;
    private JSONObject jsonItem1;
    private JSONObject jsonItem2;
    private List<JSONObject> order1;
    private List<JSONObject> order2;
    private User user;
    private String defaultId;
    private String defaultPw;

/*    @BeforeEach
    void initialize() {
        user = new User("10", "Test", "Tester", "test@test.com", "0123456789", new Adress("Street", "number", "City Name"), "pwd", Role.CUSTOMER);
        item1 = new Item("15", "Laptop", "To type on", 2000, 2);
        item2 = new Item("16", "Phone", "For calling", 1800, 11);
        item3 = new Item("17", "Monitor", "For watching", 300, 6);
        item4 = new Item("20", "Laptop", "To type on", 3000, 2);
        item5 = new Item("30", "Phone", "For calling", 2000, 12);

        jsonItem1 = new JSONObject();
        jsonItem2 = new JSONObject();
        order1 = new ArrayList<>();
        order2 = new ArrayList<>();

        defaultId = "10";
        defaultPw = "pwd";
    }*/

/*
    @Test
    void whenOrderingExistingItems() {
        items.save(item1);
        items.save(item2);
        items.save(item3);

        jsonItem1.put("itemId", "15");
        jsonItem1.put("amount", "3");
        jsonItem2.put("itemId", "17");
        jsonItem2.put("amount", "5");
        order1.add(jsonItem1);
        order1.add(jsonItem2);

        OrderDto result = RestAssured.given().port(port).auth().preemptive().basic("1", defaultPw).log().all().contentType("application/json")
                .body(order1)
                .when().post("orders")
                .then().statusCode(201).extract().body().as(OrderDto.class);

        Assertions.assertEquals(7500, result.totalPrice());
        Assertions.assertEquals(LocalDate.now().plusDays(7), result.itemGroups().get(0).shippingDate());
        Assertions.assertEquals(LocalDate.now().plusDays(1), result.itemGroups().get(1).shippingDate());
        Assertions.assertEquals(1, item3.getAmount());
        Assertions.assertEquals(StockLvl.STOCK_LOW, item3.getStockLvl());
    }

    @Test
    void whenOrderingANonExistingItem() {
        jsonItem1.put("itemId", "xyz");
        jsonItem1.put("amount", "3");
        order1.add(jsonItem1);

        JSONObject result = RestAssured.given().port(port).auth().preemptive().basic("1", defaultPw).log().all().contentType("application/json")
                .body(order1)
                .when().post("orders")
                .then().statusCode(400).extract().body().as(JSONObject.class);
        String responseMessage = result.get("message").toString();

        assertEquals("No item exists with id :xyz", responseMessage);

    }


    @Test
    void whenGettingOrderReport() {
        users.save(user);
        items.save(item4);
        items.save(item5);


        jsonItem1.put("itemId", "20");
        jsonItem1.put("amount", "1");
        jsonItem2.put("itemId", "30");
        jsonItem2.put("amount", "3");

        order1.add(jsonItem1);
        order1.add(jsonItem2);
        order2.add(jsonItem2);

        RestAssured.given().port(port).auth().preemptive().basic(defaultId, defaultPw).log().all().contentType("application/json")
                .body(order1)
                .when().post("orders")
                .then().statusCode(201).extract().body().as(OrderDto.class);
        RestAssured.given().port(port).auth().preemptive().basic(defaultId, defaultPw).log().all().contentType("application/json")
                .body(order2)
                .when().post("orders")
                .then().statusCode(201).extract().body().as(OrderDto.class);


        TotalOrderReportDto result = RestAssured.given().port(port).auth().preemptive().basic(defaultId, defaultPw).log().all().contentType("application/json")
                .body(order2)
                .when().get("orders")
                .then().statusCode(200).extract().body().as(TotalOrderReportDto.class);

        assertEquals("Phone", result.orders().get(0).itemGroups().get(1).name());
        assertEquals(15000, result.totalPrice());
    }

    @Test
    void whenReorderingAnOrderAsAUser() {
        Item itemNotToShip = items.save(item1);
        Item itemToShip = items.save(item2);

        ItemGroup itemGroup1 = new ItemGroup(itemNotToShip.getId(), itemNotToShip.getName(), 2, itemNotToShip.getPrice(), LocalDate.now().plusDays(7));
        ItemGroup itemGroup2 = new ItemGroup(itemToShip.getId(), itemToShip.getName(), 1, itemToShip.getPrice(), LocalDate.now());
        orders.save(new Order("99", "10", List.of(itemGroup1, itemGroup2)));


        OrderDto result = RestAssured.given().port(port).auth().preemptive().basic(defaultId, defaultPw).log().all().contentType("application/json")
                .when().post("orders/99")
                .then().statusCode(201).extract().body().as(OrderDto.class);

        assertEquals(2, result.itemGroups().size());
        assertEquals("16", result.itemGroups().get(1).itemId());
    }

    @Test
    void whenReorderingAnOrderAsAWrongUser() {
        Item itemNotToShip = items.save(item1);
        Item itemToShip = items.save(item2);

        ItemGroup itemGroup1 = new ItemGroup(itemNotToShip.getId(), itemNotToShip.getName(), 2, itemNotToShip.getPrice(), LocalDate.now().plusDays(7));
        ItemGroup itemGroup2 = new ItemGroup(itemToShip.getId(), itemToShip.getName(), 1, itemToShip.getPrice(), LocalDate.now());
        orders.save(new Order("99", "10", List.of(itemGroup1, itemGroup2)));


        JSONObject result = RestAssured.given().port(port).auth().preemptive().basic("11", defaultPw).log().all().contentType("application/json")
                .when().post("orders/99")
                .then().statusCode(403).extract().body().as(JSONObject.class);

        assertEquals("Unauthorized", result.get("message"));
    }

    @Test
    void whenReOrderingANotExistingOrder() {

        JSONObject result = RestAssured.given().port(port).auth().preemptive().basic(defaultId, defaultPw).log().all().contentType("application/json")
                .when().post("orders/98")
                .then().statusCode(400).extract().body().as(JSONObject.class);

        assertEquals("No order found with id: 98", result.get("message"));
    }
*/

    @AfterEach
    void clear() {
        RestAssured.reset();
    }
}