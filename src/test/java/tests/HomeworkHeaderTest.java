package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HomeworkHeaderTest {
    @Test
    public void homeworkCookieTest() {
        Response responseHomeworkCookie = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        System.out.println(responseHomeworkCookie.getHeader("x-secret-homework-header"));
        String cookie = responseHomeworkCookie.getHeader("x-secret-homework-header");
        assertEquals("Some secret value", cookie, "Ответ не соответсвует ожиданиям " + cookie);
    }
}