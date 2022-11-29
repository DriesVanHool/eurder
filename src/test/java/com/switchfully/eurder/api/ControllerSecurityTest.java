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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllerSecurityTest {
    @LocalServerPort
    int port;
    @Autowired
    private UserRepository users;

    @Test
    void whenNotAuthorized() {
        users.save(new User("10", "Unauthorized", "Achternaam", "unauthorized@gmail.com", "123", new Adress("Straat", "5", "Antwerpen"), "pwd", Role.CUSTOMER));
        Map<String, String> result = RestAssured.given().port(port).auth().preemptive().basic("10", "pwd")
                .with().get("users/customers").then().statusCode(403).and().extract().as(new TypeRef<Map<String, String>>() {
                });

        String responseMessage = new JSONObject(result).get("message").toString();
        assertEquals("Unauthorized", responseMessage);
    }

    @Test
    void whenWrongPassword() {
        users.save(new User("11", "Wrong", "Password", "wrongpw@gmail.com", "123", new Adress("Straat", "5", "Antwerpen"), "pwd", Role.ADMIN));
        Map<String, String> result = RestAssured.given().port(port).auth().preemptive().basic("11", "xyz")
                .with().get("users/customers").then().statusCode(403).and().extract().as(new TypeRef<Map<String, String>>() {
                });

        String responseMessage = new JSONObject(result).get("message").toString();
        assertEquals("Unauthorized", responseMessage);
    }
}
