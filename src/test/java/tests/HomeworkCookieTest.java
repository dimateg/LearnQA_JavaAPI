package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HomeworkCookieTest {
    @Test
    public void homeworkCookieTest() {
        Response responseHomeworkCookie = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        System.out.println(responseHomeworkCookie.getCookie("HomeWork"));
        String cookie = responseHomeworkCookie.getCookie("HomeWork");
        assertEquals("hw_value", cookie, "Ответ не соответсвует ожиданиям " + cookie);
    }
}
