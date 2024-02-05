package tests;

import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.qameta.allure.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Epic("Регистрация пользователя")
@Feature("Регистрация")
public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Тест производит регистрацияю ранее существующего пользователя")
    @DisplayName("Регистрация существующего пользователя")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/ajax/api/user/", userData);


        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    @Description("Тест производит регистрацияю нового пользователя")
    @DisplayName("Регистрация пользователя")
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/ajax/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Description("Тест производит регистрацияю пользователя с email без @")
    @DisplayName("email без @")
    public void testCreateUserWithEmailWithoutAtSing() {
        String email = "vinkotovexample.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/ajax/api/user/", userData);


        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @ParameterizedTest
    @ValueSource(strings = {"v", "drjqgakqrlgeawvkbiisfvpvjubqjwmcmzajstjzmohcnmvvqdqruznobrdaobrnbykxgldakdinipkdetcjvdkojwgjvnxksqwbtvtwldrewiqxobmkztvihhhamxthbtegxnpjusdylfnhsjhqqquoaazonybaeraisltsqpimihdhqlglzynbfrziskrhamjxnkbrygfhofffdahjkyjgyttasfeejksjwnpfobgandmhxjufjyhxnye"})
    @Description("Тест производит регистрацияю пользователя с не корректным userName")
    @DisplayName("не корректный username")
    public void testCreateUserWithOneSymbolUserName(String userName) {
        Map<String, String> userData = new HashMap<>();
        userData.put("username", userName);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/ajax/api/user/", userData);


        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        if (Objects.equals(userName, "v")) {
            Assertions.assertResponseTextEquals(responseCreateAuth,
                    "The value of 'username' field is too short");
        } else {
            Assertions.assertResponseTextEquals(responseCreateAuth,
                    "The value of 'username' field is too long");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "firstName"})
    @Description("Тест производит регистрацияю пользователя с одним пустым параметром")
    public void testCreateUserWithoutSymbolValue(String parameter) {
        Map<String, String> userData = new HashMap<>();
        userData.put(parameter, "");
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/ajax/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of '" + parameter + "' field is too short");

    }
}
