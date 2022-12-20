package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.OrderDto;
import com.switchfully.eurder.api.dtos.TotalOrderReportDto;
import com.switchfully.eurder.domain.*;
import com.switchfully.eurder.domain.repositories.*;
import com.switchfully.eurder.domain.security.Role;
import io.restassured.RestAssured;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    private OrderRepository orders;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CityRepository cityRepository;

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
    private String customerToken;
    String secret = "HhEMkchwPljhhzs6osa4nlb8GWiZiJoE";
    String clientId = "eurder-dries";
    String serverUrl = "https://keycloak.switchfully.com/auth/realms/java-oct-2022";

    @BeforeEach
    void initialize() {
        cityRepository.save(new City("2000", "Antwerpen"));
        roleRepository.save(new Role("CUSTOMER"));
        user = users.save(new User("Test", "Tester", "customer@gmail.com", "0123456789", new Adress("straat", "nummer", new City("2000", "Antwerpen")), new Role(1, "CUSTOMER")));
        users.save(new User("Test", "Tester", "customer5@gmail.com", "0123456789", new Adress("straat", "nummer", new City("2000", "Antwerpen")), new Role(1, "CUSTOMER")));
        item1 = new Item("Laptop", "To type on", 2000, 2);
        item2 = new Item("Phone", "For calling", 1800, 11);
        item3 = new Item("Monitor", "For watching", 300, 6);
        item4 = new Item("Laptop", "To type on", 3000, 2);
        item5 = new Item("Phone", "For calling", 2000, 12);

        jsonItem1 = new JSONObject();
        jsonItem2 = new JSONObject();
        order1 = new ArrayList<>();
        order2 = new ArrayList<>();

        customerToken = RestAssured.given()
                .auth()
                .preemptive()
                .basic(clientId, secret)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .baseUri(serverUrl)
                .body("username=customer@gmail.com&password=pwd&grant_type=password")
                .post("/protocol/openid-connect/token")
                .then().extract().response().jsonPath().getString("access_token");

    }

    @Test
    void whenOrderingExistingItems() {
        items.save(item1);
        items.save(item2);
        items.save(item3);

        jsonItem1.put("itemId", "1");
        jsonItem1.put("amount", "3");
        jsonItem2.put("itemId", "2");
        jsonItem2.put("amount", "5");
        order1.add(jsonItem1);
        order1.add(jsonItem2);

        OrderDto result = RestAssured.given().port(port).header("authorization", "bearer " + customerToken).log().all().contentType("application/json")
                .body(order1)
                .when().post("orders")
                .then().statusCode(201).extract().body().as(OrderDto.class);

        Item item2ToCheck = items.getItemsById(2).stream().findFirst().orElseThrow();

        Assertions.assertEquals(15000, result.totalPrice());
        Assertions.assertEquals(LocalDate.now().plusDays(7), result.itemGroups().get(0).shippingDate());
        Assertions.assertEquals(LocalDate.now().plusDays(1), result.itemGroups().get(1).shippingDate());
        Assertions.assertEquals(6, item2ToCheck.getAmount());
        Assertions.assertEquals(StockLvl.STOCK_MEDIUM, item2ToCheck.getStockLvl());
    }

    @Test
    void whenOrderingANonExistingItem() {
        jsonItem1.put("itemId", "xyz");
        jsonItem1.put("amount", "3");
        order1.add(jsonItem1);

        JSONObject result = RestAssured.given().port(port).header("authorization", "bearer " + customerToken).log().all().contentType("application/json")
                .body(order1)
                .when().post("orders")
                .then().statusCode(400).extract().body().as(JSONObject.class);
        String responseMessage = result.get("message").toString();

        assertEquals("Invalid int value", responseMessage);

    }

    @Test
    void whenGettingOrderReport() {
        items.save(item4);
        items.save(item5);


        jsonItem1.put("itemId", "1");
        jsonItem1.put("amount", "1");
        jsonItem2.put("itemId", "2");
        jsonItem2.put("amount", "3");

        order1.add(jsonItem1);
        order1.add(jsonItem2);
        order2.add(jsonItem2);

        RestAssured.given().port(port).header("authorization", "bearer " + customerToken).log().all().contentType("application/json")
                .body(order1)
                .when().post("orders")
                .then().statusCode(201).extract().body().as(OrderDto.class);

        RestAssured.given().port(port).header("authorization", "bearer " + customerToken).log().all().contentType("application/json")
                .body(order2)
                .when().post("orders")
                .then().statusCode(201).extract().body().as(OrderDto.class);


        TotalOrderReportDto result = RestAssured.given().port(port).header("authorization", "bearer " + customerToken).log().all().contentType("application/json")
                .body(order2)
                .when().get("orders")
                .then().statusCode(200).extract().body().as(TotalOrderReportDto.class);

        assertEquals("Phone", result.orders().get(0).itemGroups().get(1).name());
        assertEquals(15000, result.totalPrice());
    }

    @Test
    void whenReorderingAnOrderAsAUser() {
        items.save(item1);
        items.save(item2);

        jsonItem1.put("itemId", "1");
        jsonItem1.put("amount", "3");
        jsonItem2.put("itemId", "2");
        jsonItem2.put("amount", "5");
        order1.add(jsonItem1);
        order1.add(jsonItem2);

        RestAssured.given().port(port).header("authorization", "bearer " + customerToken).log().all().contentType("application/json")
                .body(order1)
                .when().post("orders")
                .then().statusCode(201).extract().body().as(OrderDto.class);



        OrderDto result = RestAssured.given().port(port).header("authorization", "bearer " + customerToken).log().all().contentType("application/json")
                .when().post("orders/1")
                .then().statusCode(201).extract().body().as(OrderDto.class);

        assertEquals(2, result.itemGroups().size());
        assertEquals(2, result.itemGroups().get(1).itemId());
    }


    @Test
    void whenReorderingAnOrderAsAWrongUser() {
        String wrongUser = RestAssured.given()
                .auth()
                .preemptive()
                .basic(clientId, secret)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .baseUri(serverUrl)
                .body("username=customer5@gmail.com&password=pwd&grant_type=password")
                .post("/protocol/openid-connect/token")
                .then().extract().response().jsonPath().getString("access_token");


        items.save(item1);
        items.save(item2);

        jsonItem1.put("itemId", "1");
        jsonItem1.put("amount", "3");
        jsonItem2.put("itemId", "2");
        jsonItem2.put("amount", "5");
        order1.add(jsonItem1);
        order1.add(jsonItem2);

        RestAssured.given().port(port).header("authorization", "bearer " + customerToken).log().all().contentType("application/json")
                .body(order1)
                .when().post("orders")
                .then().statusCode(201).extract().body().as(OrderDto.class);

        JSONObject result = RestAssured.given().port(port).header("authorization", "bearer " + wrongUser).log().all().contentType("application/json")
                .when().post("orders/1")
                .then().statusCode(400).extract().body().as(JSONObject.class);

        assertEquals("This is not your order", result.get("message"));
    }

    @Test
    void whenReOrderingANotExistingOrder() {

        JSONObject result = RestAssured.given().port(port).header("authorization", "bearer " + customerToken).log().all().contentType("application/json")
                .when().post("orders/98")
                .then().statusCode(400).extract().body().as(JSONObject.class);

        assertEquals("No order found with id: 98", result.get("message"));
    }

    @AfterEach
    void clear() {
        RestAssured.reset();
    }
}