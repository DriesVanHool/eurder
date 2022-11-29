package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.Adress;
import com.switchfully.eurder.domain.User;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.domain.security.Role;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
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
            requestParams.put("firstname", "User");
            requestParams.put("lastname", "Tester");
            requestParams.put("email", "user@test.com");
            requestParams.put("phoneNumber", "0123456789");
            requestParams.put("adress", new Adress("Street", "number", "City Name"));
            requestParams.put("password", "pwd");

            UserDto result = RestAssured.given().port(port).contentType("application/json").body(requestParams)
                    .when().post("/users")
                    .then().statusCode(201).and().extract().as(UserDto.class);
            assertEquals("user@test.com", result.email());
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

        @Test
        void givenDoubleUsers_whenCreatingAUser_thenResultTrowsIlligalArgument() {
            users.save(new User("10", "Test", "Tester", "test@test.com", "0123456789", new Adress("Street", "number", "City Name"), "pwd", Role.CUSTOMER));

            JSONObject requestParams = new JSONObject();
            requestParams.put("firstname", "Test");
            requestParams.put("lastname", "Tester");
            requestParams.put("email", "test@test.com");
            requestParams.put("phoneNumber", "0123456789");
            requestParams.put("adress", new Adress("Street", "number", "City Name"));
            requestParams.put("password", "pwd");


            Map<String, String> result = RestAssured.given().port(port).contentType("application/json").body(requestParams)
                    .when().post("/users")
                    .then().statusCode(400).and().extract().as(new TypeRef<Map<String, String>>() {
                    });

            String responseMessage = new JSONObject(result).get("message").toString();
            assertEquals("This user allready exists", responseMessage);
        }

        @Test
        void givenDoubleEmailAdress_whenCreatingAUser_thenResultTrowsIlligalArgument() {
            users.save(new User("10", "Test", "Tester", "test@test.com", "0123456789", new Adress("Street", "number", "City Name"), "pwd", Role.CUSTOMER));

            JSONObject requestParams = new JSONObject();
            requestParams.put("firstname", "Firstname");
            requestParams.put("lastname", "Lastname");
            requestParams.put("email", "test@test.com");
            requestParams.put("phoneNumber", "0123456789");
            requestParams.put("adress", new Adress("Street", "number", "City Name"));
            requestParams.put("password", "pwd");


            Map<String, String> result = RestAssured.given().port(port).contentType("application/json").body(requestParams)
                    .when().post("/users")
                    .then().statusCode(400).and().extract().as(new TypeRef<Map<String, String>>() {
                    });

            String responseMessage = new JSONObject(result).get("message").toString();
            assertEquals("This emailadress allready has an account", responseMessage);
        }
    }

    @DisplayName("View users")
    @Nested
    class ViewUsers {
        @Test
        void whenViewingAllCustomers() {
            users.save(new User("2", "Voornaam", "Achternaam", "email@gmail.com", "123", new Adress("Straat", "5", "Antwerpen"), "pwd", Role.CUSTOMER));
            List<UserDto> result =
                    RestAssured.given().port(port).auth().preemptive().basic("1", "pwd")
                            .with().get("users/customers").then().statusCode(200).and().extract().as(new TypeRef<List<UserDto>>() {
                            });
            assertTrue(result.size() > 0);
        }

        @Test
        void whenViewingASpecificCustomerCustomers() {
            users.save(new User("2", "Voornaam", "Achternaam", "specificUser@gmail.com", "123", new Adress("Straat", "5", "Antwerpen"), "pwd", Role.CUSTOMER));
            UserDto result =
                    RestAssured.given().port(port).auth().preemptive().basic("1", "pwd")
                            .with().get("users/customers/2").then().statusCode(200).and().extract().as(UserDto.class);
            assertEquals("specificUser@gmail.com", result.email());
        }
    }

    @AfterEach
    void cleanup() {
        RestAssured.reset();
    }
}