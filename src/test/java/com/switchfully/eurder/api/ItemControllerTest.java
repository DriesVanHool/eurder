package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.ItemDto;
import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.Adress;
import com.switchfully.eurder.domain.Item;
import com.switchfully.eurder.domain.StockLvl;
import com.switchfully.eurder.domain.repositories.ItemRepository;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerTest {

    @LocalServerPort
    int port;
    @Autowired
    private ItemRepository items;

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

    }

    @DisplayName("View items")
    @Nested
    class ViewItems {
        @Test
        void whenGetAllItems() {
            items.save(new Item("9", "Car", "To ride in", 21000, 2));
            List<ItemDto> result = RestAssured.given().port(port).auth().preemptive().basic("1", "pwd").contentType("application/json")
                    .when().get("/stock")
                    .then().statusCode(200).and().extract().as(new TypeRef<List<ItemDto>>() {
                    });
            assertTrue(result.size() > 0);
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

    }


}