import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

}
