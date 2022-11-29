package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.Adress;
import com.switchfully.eurder.domain.repositories.UserRepository;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import net.minidev.json.JSONObject;
import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @LocalServerPort
    int port;
    @Autowired
    private UserRepository users;

    @DisplayName("User creation")
    @Nested
    class CreateUser {
        @Test
        void givenAllInput_whenCreatingAUser_thenResultEquals() {
            JSONObject requestParams = new JSONObject();
            requestParams.put("firstname", "Test");
            requestParams.put("lastname", "Tester");
            requestParams.put("email", "test@test.com");
            requestParams.put("phoneNumber", "0123456789");
            requestParams.put("adress", new Adress("Street", "number", "City Name"));
            requestParams.put("password", "pwd");

            UserDto result = RestAssured.given().port(port).contentType("application/json").body(requestParams)
                    .when().post("/users")
                    .then().statusCode(201).and().extract().as(UserDto.class);
            assertEquals("test@test.com", result.email());
        }

        @Test
        void givenAllEmptyFields_whenCreatingAUser_thenResultTrowsIlligalArgument() {
            JSONObject requestParams = new JSONObject();
            requestParams.put("firstname", "");
            requestParams.put("lastname", "");
            requestParams.put("email", "");
            requestParams.put("phoneNumber", "");
            requestParams.put("adress", new Adress("", "", ""));
            requestParams.put("password", "");

            Map<String, String> result = RestAssured.given().port(port).contentType("application/json").body(requestParams)
                    .when().post("/users")
                    .then().statusCode(400).and().extract().as(new TypeRef<Map<String, String>>() {
                    });

            String responseMessage = new JSONObject(result).get("message").toString();
            assertEquals("The following fields are invalid: firstname  lastname  email  street  house number  city  password ", responseMessage);
        }

        @Test
        void givenAWrongEmail_whenCreatingAUser_thenResultTrowsIlligalArgument() {
            JSONObject requestParams = new JSONObject();
            requestParams.put("firstname", "Test");
            requestParams.put("lastname", "Tester");
            requestParams.put("email", "testtest.com");
            requestParams.put("phoneNumber", "0123456789");
            requestParams.put("adress", new Adress("Street", "number", "City Name"));
            requestParams.put("password", "pwd");

            Map<String, String> result = RestAssured.given().port(port).contentType("application/json").body(requestParams)
                    .when().post("/users")
                    .then().statusCode(400).and().extract().as(new TypeRef<Map<String, String>>() {
                    });

            String responseMessage = new JSONObject(result).get("message").toString();
            assertEquals("The following fields are invalid: email ", responseMessage);
        }
    }
}