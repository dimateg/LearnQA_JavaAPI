import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelloWorldTest {

    @Test
    public void testHelloWorld() {
        System.out.println("Hello world");

    }

    @Test
    public void TestHello() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testGetText() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testRestAssured() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "John");

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String name = response.get("answer2");
        if (name == null) {
            System.out.println("The key 'answer2' is absent");
        } else {
            System.out.println(name);
        }
    }

    @Test
    public void testRestAssured2() {
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login2");
        data.put("password", "secret_pass2");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        String responseCookie = responseForGet.getCookie("auth_cookie");

        Map<String, String> cookies = new HashMap<>();
        if (responseCookie != null) {
            cookies.put("auth_cookie", responseCookie);
        }

        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        responseForCheck.print();
    }

    @Test
    public void testEx5() {
        JsonPath responseJSONHomework = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String answer = responseJSONHomework.getString("messages[1].message");
        System.out.println(answer);

    }

    @Test
    public void testEx6() {

        Response responseRedirect = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = responseRedirect.getHeader("Location");
        System.out.println(locationHeader);

    }


    @Test
    public void testEx7() {
        String URL = "https://playground.learnqa.ru/api/long_redirect";
        int count = 0;
        int statusCode;

        do {
            Response responseRedirect = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(URL)
                    .andReturn();

            String locationHeader = responseRedirect.getHeader("Location");

            statusCode = responseRedirect.getStatusCode();

            URL = locationHeader;
            count++;
            System.out.println("URL № " + count + " : " + locationHeader);
        }

        while (statusCode == 301);
        System.out.println("Количетво редиректов = " + count);


    }

    @Test
    public void testEx8() throws InterruptedException {
        JsonPath responseLongTimeJob = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        responseLongTimeJob.prettyPrint();

        String token = responseLongTimeJob.get("token");
        int seconds = responseLongTimeJob.get("seconds");

        System.out.println(token + " " + seconds);

        Map<String, String> data = new HashMap<>();
        data.put("token", token);

        JsonPath responseLongTimeJob2 = RestAssured
                .given()
                .queryParams(data)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        responseLongTimeJob2.prettyPrint();


        String statusEr = responseLongTimeJob2.get("status");
        if (Objects.equals(statusEr, "Job is NOT ready")) {
            System.out.println("Значение параметра status равно ожидаемому 'Job is NOT ready'");
        } else {
            System.out.println("Значение параметра status не соответсвует ожидаемому 'Job is ready': " + statusEr);
        }

        Thread.sleep(seconds * 1000L);

        JsonPath responseLongTimeJob3 = RestAssured
                .given()
                .queryParams(data)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        responseLongTimeJob3.prettyPrint();


        String status = responseLongTimeJob3.get("status");
        if (Objects.equals(status, "Job is ready")) {
            System.out.println("Значение параметра status равно ожидаемому 'Job is ready'");
        } else {
            System.out.println("Значение параметра status не соответсвует ожидаемому 'Job is ready': " + status);
        }
        String result = responseLongTimeJob3.get("result");
        if (result == null) {
            System.out.println("Параметр 'result' отсутвует");
        } else {
            System.out.println("Параметр 'result' = " + result);
        }
    }

    @Test
    public void testEx9() {
        String[] passwords_list = {"password", "123456", "12345678", "qwerty", "abc123", "monkey", "1234567", "letmein", "trustno1",
                "dragon", "baseball", "111111", "iloveyou", "master", "sunshine", "ashley", "bailey", "passw0rd",
                "shadow", "123123", "654321", "superman", "qazwsx", "michael", "Football", "welcome", "jesus",
                "ninja", "mustang", "password1", "123456789", "adobe123", "admin", "1234567890", "photoshop",
                "1234", "12345", "princess", "azerty", "0000000", "access", "696969", "batman", "1qaz2wsx", "login",
                "qwertyuiop", "solo", "starwars", "121212", "flower", "hottie", "loveme", "zaq1zaq1", "hello",
                "freedom", "whatever", "666666", "!@#$%^&*", "charlie", "aa123456", "donald", "qwerty123",
                "1q2w3e4r", "555555", "lovely", "7777777", "888888", "123qwe"};


        String url1 = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
        String url2 = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";

        for (int i = 0; i < passwords_list.length; i++) {
            String password = passwords_list[i];

            Map<String, String> data = new HashMap<>();
            data.put("login", "super_admin");
            data.put("password", password);
            Response responseSecret = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post(url1)
                    .andReturn();

            String responseCookie = responseSecret.getCookie("auth_cookie");

            Map<String, String> cookies = new HashMap<>();
            if (responseCookie != null) {
                cookies.put("auth_cookie", responseCookie);
            }

            Response responseForCheck = RestAssured
                    .given()
                    .body(data)
                    .cookies(cookies)
                    .when()
                    .post(url2)
                    .andReturn();


            String key = responseForCheck.print();
            if (Objects.equals(key, "You are NOT authorized")) {
                System.out.println("Пароль " + password + " не верный");
            } else {
                System.out.println("Правильный пароль " + password);
                break;
            }
        }
    }

    @Test
    public void ex31200() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/map")
                .andReturn();
        assertEquals(200, response.statusCode(), "Неожиданный статус код");
    }

    @Test
    public void ex31404() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/map1")
                .andReturn();
        assertEquals(404, response.statusCode(), "Неожиданный статус код");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "John", "Pete"})
    public void testHelloMethodWithoutName(String name) {
        Map<String, String> queryParams = new HashMap<>();

        if(name.length() > 0){
            queryParams.put("name", name);
        }

        JsonPath response = RestAssured
                .given()
                .queryParams(queryParams)
                .get("https://playground.learnqa.ru/ajax/api/hello")
                .jsonPath();
        String answer = response.getString("answer");
        String expectedName = (name.length() > 0) ? name : "someone";
        assertEquals("Hello, " + expectedName, answer, "The answer in not expected");
    }

    @Test
    public void testHelloMethodWithName() {
        String name = "Username";

        JsonPath response = RestAssured
                .given()
                .queryParam("name", name)
                .get("https://playground.learnqa.ru/ajax/api/hello")
                .jsonPath();
        String answer = response.getString("answer");
        assertEquals("Hello, " + name, answer, "The answer in not expected");
    }

    @Test
    public void testLengthString() {
        String name = ("qwertyuiop[]asg");
        assertTrue(name.length() <= 15, "Длинна строки больше 15 символов");
    }


}
