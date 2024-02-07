package tests;

import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Удаление пользователя")
@Feature("Удаление")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Тест производит логин под пользователем с ID=2 и попытку его удалить")
    @DisplayName("Удаление пользователя ID=2")
    @Owner("Иванов Дмитрий") //Позволяет указать ответственное лицо за тест.
    @Severity(value = SeverityLevel.BLOCKER) //Позволяет указать уровень критичности функционала, проверяемого автотестом.
    public void deleteUserID2() {
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/ajax/api/user/login", authData);

        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String header = this.getHeaders(responseGetAuth, "x-csrf-token");
        int userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequestEditUserWithTokenCookie(
                "https://playground.learnqa.ru/api/user/", header, cookie, userIdOnAuth);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Description("Тест создает пользолвателя, логинится под ним, выполняет удаление пользователя, после чего проверяет что пользователь не найден")
    @DisplayName("Удаление пользователя")
    public void testDeleteJustCreatedTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreatedAuth = apiCoreRequests
                .makePostJsonPath("https://playground.learnqa.ru/ajax/api/user/", userData);

        int userId = responseCreatedAuth.getInt("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequestGetUser(
                "https://playground.learnqa.ru/ajax/api/user/login", authData);

        String header = this.getHeaders(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequestEditUserWithTokenCookie(
                "https://playground.learnqa.ru/api/user/", header, cookie, userId);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestGetUser(
                "https://playground.learnqa.ru/api/user/",
                header, cookie, userId);


        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test
    @Description("Тест производит создание новых пользователей, вход под вторым и попытку удалить первого пользователя")
    @DisplayName("Удаление другого пользователя")
    public void testDeleteAnyUserJustCreatedTest() throws InterruptedException {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreatedAuth = apiCoreRequests
                .makePostJsonPath("https://playground.learnqa.ru/ajax/api/user/", userData);

        int userId = responseCreatedAuth.getInt("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));


        Response responseGetAuth = apiCoreRequests.makePostRequestGetUser(
                "https://playground.learnqa.ru/ajax/api/user/login", authData);

        String header = this.getHeaders(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");


        Thread.sleep(2000);

        //GENERATE USER2
        Map<String, String> userData2 = DataGenerator.getRegistrationData();

        JsonPath responseCreatedAuth2 = apiCoreRequests
                .makePostJsonPath("https://playground.learnqa.ru/ajax/api/user/", userData2);

        //LOGIN2
        Map<String, String> authData2 = new HashMap<>();
        authData2.put("email", userData2.get("email"));
        authData2.put("password", userData2.get("password"));


        Response responseGetAuth2 = apiCoreRequests.makePostRequestGetUser(
                "https://playground.learnqa.ru/ajax/api/user/login", authData2);

        String header2 = this.getHeaders(responseGetAuth2, "x-csrf-token");
        String cookie2 = this.getCookie(responseGetAuth2, "auth_sid");


        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequestEditUserWithTokenCookie(
                "https://playground.learnqa.ru/api/user/", header2, cookie2, userId);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestGetUser(
                "https://playground.learnqa.ru/api/user/",
                header, cookie, userId);

        String[] unexpectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, unexpectedFields);
    }
}
