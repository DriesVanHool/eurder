package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.ItemDto;
import com.switchfully.eurder.api.dtos.ItemShippingDto;
import com.switchfully.eurder.domain.*;
import com.switchfully.eurder.domain.repositories.ItemRepository;
import com.switchfully.eurder.domain.repositories.OrderRepository;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.domain.security.Role;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemControllerTest {

    @LocalServerPort
    int port;
    @Autowired
    private ItemRepository items;

    @Autowired
    private OrderRepository orders;

    @Autowired
    private UserRepository userRepository;

    String adminToken;
    String secret = "HhEMkchwPljhhzs6osa4nlb8GWiZiJoE";
    String clientId = "eurder-dries";
    String serverUrl = "https://keycloak.switchfully.com/auth/realms/java-oct-2022";

    @BeforeEach
    void initTestToken() {
        adminToken = RestAssured.given()
                .auth()
                .preemptive()
                .basic(clientId, secret)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .baseUri(serverUrl)
                .body("username=admin&password=pwd&grant_type=password")
                .post("/protocol/openid-connect/token")
                .then().extract().response().jsonPath().getString("access_token");

    }

    @Test
    void givenAllInput_whenCreatingAnItem_thenResultEquals() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "Tv");
        requestParams.put("description", "A device to watch shows and movies");
        requestParams.put("price", "200");
        requestParams.put("amount", "8");

        ItemDto result = RestAssured.given().port(port).header("authorization", "bearer " + adminToken).contentType("application/json").body(requestParams)
                .when().post("/stock")
                .then().statusCode(201).and().extract().as(ItemDto.class);
        assertEquals("Tv", result.name());
    }

    @Test
    void givenAnIncorrectValues_whenCreatingAnItem_thenResultEquals() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "");
        requestParams.put("description", "");
        requestParams.put("price", -1);
        requestParams.put("amount", -1);

        JSONObject result = RestAssured.given().port(port).header("authorization", "bearer " + adminToken).contentType("application/json").body(requestParams)
                .when().post("/stock")
                .then().statusCode(400).and().extract().as(JSONObject.class);

        assertEquals("Name needs to be filled in", result.get("name"));
        assertEquals("Description needs to be filled in", result.get("description"));
        assertEquals("Price needs to be filled in", result.get("price"));
        assertEquals("Amount needs to be filled in", result.get("amount"));
    }

    @Test
    void givenAllInput_whenUpdatingAnItem_thenResultEquals() {
        items.save(new Item("Laptop", "A portable computer", 2500, 3));
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "Laptop");
        requestParams.put("description", "A foldable computer");
        requestParams.put("price", "2000");
        requestParams.put("amount", "5");

        ItemDto result = RestAssured.given().port(port).header("authorization", "bearer " + adminToken).contentType("application/json").body(requestParams)
                .when().put("/stock/1")
                .then().statusCode(200).and().extract().as(ItemDto.class);
        assertEquals("A foldable computer", result.description());
        assertEquals(5, result.amount());
    }

    @Test
    void givenAnInvalidItemId_whenUpdatingAnItem_thenResultEquals() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "");
        requestParams.put("description", "");
        requestParams.put("price", "");
        requestParams.put("amount", "");

        JSONObject result = RestAssured.given().port(port).header("authorization", "bearer " + adminToken).contentType("application/json").body(requestParams)
                .when().put("/stock/zyx")
                .then().statusCode(400).and().extract().as(JSONObject.class);
        assertEquals("Invalid id", result.get("message").toString());
    }

    @Test
    void givenANonExistingItemId_whenUpdatingAnItem_thenResultEquals() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "");
        requestParams.put("description", "");
        requestParams.put("price", "");
        requestParams.put("amount", "");

        JSONObject result = RestAssured.given().port(port).header("authorization", "bearer " + adminToken).contentType("application/json").body(requestParams)
                .when().put("/stock/99999")
                .then().statusCode(400).and().extract().as(JSONObject.class);
        assertEquals("No item exists with id :99999", result.get("message").toString());
    }

    @Test
    void whenGetAllItems() {
        items.save(new Item("Car", "To ride in", 21000, 2));
        items.save(new Item("phone", "To ride in", 21000, 11));
        List<ItemDto> result = RestAssured.given().port(port).header("authorization", "bearer " + adminToken).contentType("application/json")
                .when().get("/stock")
                .then().statusCode(200).and().extract().as(new TypeRef<List<ItemDto>>() {
                });
        assertTrue(result.size() > 0);
        assertSame(StockLvl.STOCK_HIGH, result.get(0).stockLvl());
    }

    @Test
    void whenGetAllLowStockItems() {
        items.save(new Item("Plane", "To fly in", 21000, 2));
        items.save(new Item("Computers", "For working and gaming", 2000, 11));
        List<ItemDto> result = RestAssured.given().port(port).header("authorization", "bearer " + adminToken).contentType("application/json")
                .when().get("stock?supply=low")
                .then().statusCode(200).and().extract().as(new TypeRef<List<ItemDto>>() {
                });
        List<Item> listToCheck = items.findAll().stream().filter(item -> item.getStockLvl() == StockLvl.STOCK_LOW).toList();
        assertSame(listToCheck.size(), result.size());
        assertSame(StockLvl.STOCK_LOW, result.get(0).stockLvl());
    }

    @Test
    void whenGetAllMediumStockItems() {
        items.save(new Item("Radio", "To fly in", 21000, 2));
        items.save(new Item("Phone", "For working and gaming", 2000, 11));
        items.save(new Item("Monitor", "For working and gaming", 2000, 6));
        List<ItemDto> result = RestAssured.given().port(port).header("authorization", "bearer " + adminToken).contentType("application/json")
                .when().get("stock?supply=medium")
                .then().statusCode(200).and().extract().as(new TypeRef<List<ItemDto>>() {
                });
        List<Item> listToCheck = items.findAll().stream().filter(item -> item.getStockLvl() == StockLvl.STOCK_MEDIUM).toList();
        assertSame(listToCheck.size(), result.size());
        assertSame(StockLvl.STOCK_MEDIUM, result.get(0).stockLvl());
    }


/*    @Test
    void getAllItemsToShippToday() {
        userRepository.save(new User("Test", "Tester", "test@test.com", "0123456789", new Adress("straat", "nummer", new City("2000", "Antwerpen")), new Role(1, "CUSTOMER")));
        Item itemNotToShip = items.save(new Item("Laptop", "To type on", 3000, 2));
        Item itemToShip = items.save(new Item("Phone", "For calling", 2000, 12));

        ItemGroup itemGroup1 = new ItemGroup(itemNotToShip.getId(), itemNotToShip.getName(), 2, itemNotToShip.getPrice(), LocalDate.now().plusDays(7));
        ItemGroup itemGroup2 = new ItemGroup(itemToShip.getId(), itemToShip.getName(), 1, itemToShip.getPrice(), LocalDate.now());
        orders.save(new Order("11", List.of(itemGroup1, itemGroup2)));


        List<ItemShippingDto> result = RestAssured.given().port(port).auth().preemptive().basic("1", "pwd").contentType("application/json")
                .when().get("stock/shipToday")
                .then().statusCode(200).and().extract().as(new TypeRef<List<ItemShippingDto>>() {
                });

        assertEquals(1, result.size());
        assertEquals(result.get(0).itemGroup().getItemId(), itemToShip.getId());
    }*/

}