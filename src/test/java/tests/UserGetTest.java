package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Данные пользователя")
@Feature("Данные")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @Description("Тест получает данные пользователя с id=2 без входа")
    @DisplayName("Данные пользователя без входа")
    public void testGetUserDateNotAuth() {
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Description("Тест получает данные пользователя с id=2 с входом")
    @DisplayName("Данные пользователя после входа")
    @Test
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");


        Response responseGetAuth = apiCoreRequests.makePostRequestGetUser(
                "https://playground.learnqa.ru/ajax/api/user/login", authData);

        String header = this.getHeaders(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests.makeGetRequestGetUser(
                "https://playground.learnqa.ru/api/user/",
                header, cookie, 2);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @Description("Тест входит под пользователем id=2 и получает данные пользователя id=91045")
    @DisplayName("Чужие пользовательские данные")
    public void testGetUserDetailsAuthAsAnyUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");


        Response responseGetAuth = apiCoreRequests.makePostRequestGetUser(
                "https://playground.learnqa.ru/ajax/api/user/login", authData);

        String header = this.getHeaders(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests.makeGetRequestGetUser(
                "https://playground.learnqa.ru/api/user/",
                header, cookie, 91045);

        String[] unexpectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);
        Assertions.assertJsonHasField(responseUserData, "username");
    }
}
