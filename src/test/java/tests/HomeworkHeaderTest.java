package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HomeworkHeaderTest {
    @Test
    public void homeworkHeaderTest() {
        Response responseHomeworkHeader = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        System.out.println(responseHomeworkHeader.getHeader("x-secret-homework-header"));
        String header = responseHomeworkHeader.getHeader("x-secret-homework-header");
        assertEquals("Some secret value", header, "Ответ не соответсвует ожиданиям " + header);
    }
}