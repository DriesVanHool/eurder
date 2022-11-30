package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.ItemDto;
import com.switchfully.eurder.api.dtos.ItemShippingDto;
import com.switchfully.eurder.api.dtos.OrderDto;
import com.switchfully.eurder.domain.Adress;
import com.switchfully.eurder.domain.Item;
import com.switchfully.eurder.domain.StockLvl;
import com.switchfully.eurder.domain.User;
import com.switchfully.eurder.domain.repositories.ItemRepository;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.domain.security.Role;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerTest {

    @LocalServerPort
    int port;
    @Autowired
    private ItemRepository items;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("Item creation")
    @Nested
    class CreateItem {
        @Test
        void givenAllInput_whenCreatingAnItem_thenResultEquals() {
            JSONObject requestParams = new JSONObject();
            requestParams.put("name", "Tv");
            requestParams.put("description", "A device to watch shows and movies");
            requestParams.put("price", "200");
            requestParams.put("amount", "8");

            ItemDto result = RestAssured.given().port(port).auth().preemptive().basic("1", "pwd").contentType("application/json").body(requestParams)
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

            Map<String, String> result = RestAssured.given().port(port).auth().preemptive().basic("1", "pwd").contentType("application/json").body(requestParams)
                    .when().post("/stock")
                    .then().statusCode(400).and().extract().as(new TypeRef<Map<String, String>>() {
                    });
            String responseMessage = new JSONObject(result).get("message").toString();
            assertEquals("The following fields are invalid: name, description, price, amount", responseMessage);
        }

        @Test
        void givenAllInput_whenUpdatingAnItem_thenResultEquals() {
            Item itemToCheck = items.save(new Item("1000", "Laptop", "A portable computer", 2500, 3));
            JSONObject requestParams = new JSONObject();
            requestParams.put("name", "Laptop");
            requestParams.put("description", "A foldable computer");
            requestParams.put("price", "2000");
            requestParams.put("amount", "5");

            ItemDto result = RestAssured.given().port(port).auth().preemptive().basic("1", "pwd").contentType("application/json").body(requestParams)
                    .when().put("/stock/1000")
                    .then().statusCode(200).and().extract().as(ItemDto.class);
            assertEquals("A foldable computer", result.description());
            assertEquals(5, result.amount());
            assertEquals(1, items.getAllItems().stream().filter(item -> item.getId().equals("1000")).count());
        }

        @Test
        void givenANonExistingItemId_whenUpdatingAnItem_thenResultEquals() {
            JSONObject requestParams = new JSONObject();
            requestParams.put("name", "");
            requestParams.put("description", "");
            requestParams.put("price", "");
            requestParams.put("amount", "");

            JSONObject result = RestAssured.given().port(port).auth().preemptive().basic("1", "pwd").contentType("application/json").body(requestParams)
                    .when().put("/stock/zyx")
                    .then().statusCode(400).and().extract().as(JSONObject.class);
            assertEquals("No item exists with id :zyx", result.get("message").toString());
        }

    }

    @DisplayName("View items")
    @Nested
    class ViewItems {
        @Test
        void whenGetAllItems() {
            items.save(new Item("9", "Car", "To ride in", 21000, 2));
            items.save(new Item("2", "phone", "To ride in", 21000, 11));
            List<ItemDto> result = RestAssured.given().port(port).auth().preemptive().basic("1", "pwd").contentType("application/json")
                    .when().get("/stock")
                    .then().statusCode(200).and().extract().as(new TypeRef<List<ItemDto>>() {
                    });
            assertTrue(result.size() > 0);
            assertSame(StockLvl.STOCK_HIGH, result.get(0).stockLvl());
        }

        @Test
        void whenGetAllLowStockItems() {
            items.save(new Item("10", "Plane", "To fly in", 21000, 2));
            items.save(new Item("11", "Computers", "For working and gaming", 2000, 11));
            List<ItemDto> result = RestAssured.given().port(port).auth().preemptive().basic("1", "pwd").contentType("application/json")
                    .when().get("stock?supply=low")
                    .then().statusCode(200).and().extract().as(new TypeRef<List<ItemDto>>() {
                    });
            List<Item> listToCheck = items.getAllItems().stream().filter(item -> item.getStockLvl() == StockLvl.STOCK_LOW).toList();
            assertSame(listToCheck.size(), result.size());
            assertSame(StockLvl.STOCK_LOW, result.get(0).stockLvl());
        }

        @Test
        void whenGetAllMediumStockItems() {
            items.save(new Item("12", "Radio", "To fly in", 21000, 2));
            items.save(new Item("13", "Phone", "For working and gaming", 2000, 11));
            items.save(new Item("14", "Monitor", "For working and gaming", 2000, 6));
            List<ItemDto> result = RestAssured.given().port(port).auth().preemptive().basic("1", "pwd").contentType("application/json")
                    .when().get("stock?supply=medium")
                    .then().statusCode(200).and().extract().as(new TypeRef<List<ItemDto>>() {
                    });
            List<Item> listToCheck = items.getAllItems().stream().filter(item -> item.getStockLvl() == StockLvl.STOCK_MEDIUM).toList();
            assertSame(listToCheck.size(), result.size());
            assertSame(StockLvl.STOCK_MEDIUM, result.get(0).stockLvl());
        }

        @Test
        void getAllItemsToShippToday() {
            userRepository.save(new User("11", "Test", "Tester", "test@test.com", "0123456789", new Adress("Street", "number", "City Name"), "pwd", Role.CUSTOMER));
            items.save(new Item("40", "Laptop", "To type on", 3000, 2));
            Item itemToShip = items.save(new Item("50", "Phone", "For calling", 2000, 12));

            JSONObject item1 = new JSONObject();
            JSONObject item2 = new JSONObject();
            item1.put("itemId", "40");
            item1.put("amount", "2");
            item2.put("itemId", "50");
            item2.put("amount", "1");
            List<JSONObject> order1 = new ArrayList<>();
            order1.add(item1);
            order1.add(item2);


            RestAssured.given().port(port).auth().preemptive().basic("11", "pwd").log().all().contentType("application/json")
                    .body(order1)
                    .when().post("orders")
                    .then().statusCode(201).extract().body().as(OrderDto.class);


            List<ItemShippingDto> result = RestAssured.given().port(port).auth().preemptive().basic("1", "pwd").contentType("application/json")
                    .when().get("stock/shipToday")
                    .then().statusCode(200).and().extract().as(new TypeRef<List<ItemShippingDto>>() {
                    });

            List<Item> listToCheck = items.getAllItems().stream().filter(item -> item.getStockLvl() == StockLvl.STOCK_MEDIUM).toList();
            assertEquals(1, result.size());
            assertEquals(result.get(0).itemGroup().getItemId(), itemToShip.getId());
        }

    }


}