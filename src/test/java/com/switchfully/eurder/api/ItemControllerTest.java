package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.ItemDto;
import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.Adress;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerTest {

    @LocalServerPort
    int port;
    @Autowired
    private ItemRepository items;

    @DisplayName("Item creation")
    @Nested
    class CreateUser {
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


}