package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.CreateUserDto;
import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.Adress;
import com.switchfully.eurder.domain.City;
import com.switchfully.eurder.domain.User;
import com.switchfully.eurder.domain.repositories.CityRepository;
import com.switchfully.eurder.domain.repositories.RoleRepository;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.domain.security.Role;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
    @LocalServerPort
    int port;
    @Autowired
    private UserRepository users;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CityRepository cityRepository;

    String adminToken;
    String secret = "HhEMkchwPljhhzs6osa4nlb8GWiZiJoE";
    String clientId = "eurder-dries";
    String serverUrl = "https://keycloak.switchfully.com/auth/realms/java-oct-2022";

    @BeforeEach
    void initTestToken() {
        cityRepository.save(new City("2000", "Antwerpen"));
        roleRepository.save(new Role("CUSTOMER"));

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
    void givenAllInput_whenCreatingAUser_thenResultEquals() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("firstname", "User");
        requestParams.put("lastname", "Tester");
        requestParams.put("email", "testuser@test.com");
        requestParams.put("phoneNumber", "0123456789");
        requestParams.put("street", "test");
        requestParams.put("houseNumber", "test nr");
        requestParams.put("zip", "2000");
        requestParams.put("password", "pwd");


        UserDto result = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON).port(port)
                .when().body(requestParams).post("/users")
                .then().assertThat().statusCode(HttpStatus.SC_CREATED).extract().as(UserDto.class);
        assertEquals("testuser@test.com", result.email());
    }

    @Test
    void givenANonExistingZip_whenCreatingAUser_thenResultEquals() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("firstname", "User");
        requestParams.put("lastname", "Tester");
        requestParams.put("email", "testuser@test.com");
        requestParams.put("phoneNumber", "0123456789");
        requestParams.put("street", "test");
        requestParams.put("houseNumber", "test nr");
        requestParams.put("zip", "9999999");
        requestParams.put("password", "pwd");

        Map<String, String> result = RestAssured.given().port(port).contentType("application/json").body(requestParams)
                .when().post("/users")
                .then().statusCode(400).and().extract().as(new TypeRef<Map<String, String>>() {
                });


        String responseMessage = new JSONObject(result).get("message").toString();
        assertEquals("Incorrect Zip", responseMessage);
    }

    @Test
    void givenAllEmptyFields_whenCreatingAUser_thenResultTrowsIlligalArgument() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("firstname", "");
        requestParams.put("lastname", "");
        requestParams.put("email", "");
        requestParams.put("phoneNumber", "");
        requestParams.put("street", "");
        requestParams.put("houseNumber", "");
        requestParams.put("zip", "");
        requestParams.put("password", "");

        JSONObject result = RestAssured.given().port(port).contentType("application/json").body(requestParams)
                .when().post("/users")
                .then().statusCode(400).and().extract().as(JSONObject.class);


        assertEquals("Firstname needs to be filled in", result.get("firstname"));
        assertEquals("Password needs to be filled in", result.get("password"));
        assertEquals("Phone number needs to be filled in", result.get("phoneNumber"));
        assertEquals("Street needs to be filled in", result.get("street"));
        assertEquals("House number needs to be filled in", result.get("houseNumber"));
        assertEquals("Zip needs to be filled in", result.get("zip"));
        assertEquals("Lastname needs to be filled in", result.get("lastname"));
    }

    @Test
    void givenAWrongEmail_whenCreatingAUser_thenResultTrowsIlligalArgument() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("firstname", "Test");
        requestParams.put("lastname", "Tester");
        requestParams.put("email", "testtest.com");
        requestParams.put("phoneNumber", "0123456789");
        requestParams.put("street", "test");
        requestParams.put("houseNumber", "test nr");
        requestParams.put("zip", "2000");
        requestParams.put("password", "pwd");

        JSONObject result = RestAssured.given().port(port).contentType("application/json").body(requestParams)
                .when().post("/users")
                .then().statusCode(400).and().extract().as(JSONObject.class);

        assertEquals("Email is not valid", result.get("email"));
    }

    @Test
    void givenDoubleUsers_whenCreatingAUser_thenResultTrowsIlligalArgument() {
        users.save(new User("Test", "Tester", "test@test.com", "0123456789", new Adress("straat", "nummer", new City("2000", "Antwerpen")), new Role(1, "CUSTOMER")));

        JSONObject requestParams = new JSONObject();
        requestParams.put("firstname", "Test");
        requestParams.put("lastname", "Tester");
        requestParams.put("email", "test@test.com");
        requestParams.put("phoneNumber", "0123456789");
        requestParams.put("street", "test");
        requestParams.put("houseNumber", "test nr");
        requestParams.put("zip", "2000");
        requestParams.put("password", "pwd");


        Map<String, String> result = RestAssured.given().port(port).contentType("application/json").body(requestParams)
                .when().post("/users")
                .then().statusCode(400).and().extract().as(new TypeRef<Map<String, String>>() {
                });

        String responseMessage = new JSONObject(result).get("message").toString();
        assertEquals("This user already exists", responseMessage);
    }

    @Test
    void whenViewingAllCustomers() {
        users.save(new User("Test", "Tester", "test@test.com", "0123456789", new Adress("straat", "nummer", new City("2000", "Antwerpen")), new Role(1, "CUSTOMER")));
        List<UserDto> result =
                RestAssured.given().port(port).header("authorization", "bearer " + adminToken)
                        .with().get("users/customers").then().statusCode(200).and().extract().as(new TypeRef<List<UserDto>>() {
                        });
        assertTrue(result.size() > 0);
    }

    @Test
    void whenViewingASpecificCustomer() {
        users.save(new User("Test", "Tester", "test@test.com", "0123456789", new Adress("straat", "nummer", new City("2000", "Antwerpen")), new Role(1, "CUSTOMER")));
        users.save(new User("Test", "Tester", "specificUser@gmail.com", "0123456789", new Adress("straat", "nummer", new City("2000", "Antwerpen")), new Role(1, "CUSTOMER")));
        UserDto result =
                RestAssured.given().port(port).header("authorization", "bearer " + adminToken)
                        .with().get("users/customers/2").then().statusCode(200).and().extract().as(UserDto.class);
        assertEquals("specificUser@gmail.com", result.email());
    }

    @Test
    void whenViewingSpecificNonExistingUsers() {
        Map<String, String> result =
                RestAssured.given().port(port).header("authorization", "bearer " + adminToken)
                        .with().get("users/customers/9999").then().statusCode(400).and().extract().as(new TypeRef<Map<String, String>>() {
                        });
        String responseMessage = new JSONObject(result).get("message").toString();
        assertEquals("User id 9999 not found.", responseMessage);
    }

    @Test
    void whenViewingSpecificCustomerWithWrongIdFormat() {
        Map<String, String> result =
                RestAssured.given().port(port).header("authorization", "bearer " + adminToken)
                        .with().get("users/customers/test").then().statusCode(400).and().extract().as(new TypeRef<Map<String, String>>() {
                        });
        String responseMessage = new JSONObject(result).get("message").toString();
        assertEquals("Invalid user ID", responseMessage);
    }

    @AfterEach
    void cleanup() {
        RestAssured.reset();
    }
}