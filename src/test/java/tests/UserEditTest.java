package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
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

@Epic("Редактирование пользователя")
@Feature("Редактирование")
public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Тест производит создание нового пользователя изменение его данных и проверку внесенных изменений")
    @DisplayName("Изменение данных нового пользователя")
    public void testEditJustCreatedTest() {
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

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestEditUserWithTokenCookie(
                "https://playground.learnqa.ru/api/user/", header, cookie, userId, editData);


        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestGetUser(
                "https://playground.learnqa.ru/api/user/",
                header, cookie, userId);


        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Description("Тест производит попытку изменений данных пользователя без авторизации")
    @DisplayName("Изменение данных пользователя без авторизации")
    public void testEditJustLogoff() {
        int userId = 91045;
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestEditUser("https://playground.learnqa.ru/api/user/", userId, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }


    @Test
    @Description("Тест производит создание нового пользователя изменение, вход и попытку изменить данные другого пользователя")
    @DisplayName("Изменение данных другого пользователя")
    public void testEditAnyUserJustCreatedTest() throws InterruptedException {
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

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        String header2 = this.getHeaders(responseGetAuth2, "x-csrf-token");
        String cookie2 = this.getCookie(responseGetAuth2, "auth_sid");

        Response responseEditUser = apiCoreRequests.makePutRequestEditUserWithTokenCookie(
                "https://playground.learnqa.ru/api/user/", header2, cookie2, userId, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 200);

        String header = this.getHeaders(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestGetUser(
                "https://playground.learnqa.ru/api/user/",
                header, cookie, userId);

        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }

    @Test
    @Description("Тест производит создание нового пользователя попытку изменить его email на email без символа @")
    @DisplayName("Изменение email без @")
    public void testEditUserWithoutAt() {
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

        //EDIT
        String newEmail = "testemailexample.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests.makePutRequestEditUserWithTokenCookie(
                "https://playground.learnqa.ru/api/user/", header, cookie, userId, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");
}

    @Test
    @Description("Тест производит создание нового пользователя попытку изменить его Firstname на имя с одним символом")
    @DisplayName("Изменение Firstname один символ")
    public void testEditUserFirstnameOneSymbol() {
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

        //EDIT
        String newName = "t";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestEditUserWithTokenCookie(
                "https://playground.learnqa.ru/api/user/", header, cookie, userId, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "Too short value for field firstName");
    }
}
