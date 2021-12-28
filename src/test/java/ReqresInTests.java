import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReqresInTests {
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://reqres.in/";
    }

    @Test
    void singleUserMethodOkTest() {
        /*
        GET https://reqres.in/api/users/2

        "data": {
        "id": 2,
        "email": "janet.weaver@reqres.in",
        "first_name": "Janet",
        "last_name": "Weaver",
        "avatar": "https://reqres.in/img/faces/2-image.jpg"
                },
        "support": {
        "url": "https://reqres.in/#support-heading",
        "text": "To keep ReqRes free, contributions towards server costs are appreciated!"
            }
        }
        */

        Response response = given()
                .when()
                .get("api/users/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        response.prettyPrint();
        String email = response.path("data.email");
        assertThat(email, is("janet.weaver@reqres.in"));
    }

    @Test
    void singleUserMethodUserNotFoundTest() {
        /*
         GET https://reqres.in/api/users/23

        {
        }
        */

        Response response = given()
                .when()
                .get("api/users/23")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("error", is(nullValue()))
                .extract()
                .response();

        response.prettyPrint();
    }

    @Test
    void createUserMethodOk() {

        /*
        POST https://reqres.in/api/users
        {
            "name": "morpheus",
            "job": "leader"
        }

        201
        {
            "name": "morpheus",
            "job": "leader",
            "id": "605",
            "createdAt": "2021-12-28T13:56:32.141Z"
        }
         */
        JSONObject requestBody = new JSONObject()
                .put("name", "newUserName")
                .put("job", "autoqa");


        Response response = given()
                .body(requestBody.toString())
                .when()
                .post("api/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        response.prettyPrint();
        String createdAt = response.path("createdAt");
        assertTrue(createdAt.contains(LocalDate.now().toString()));
    }

    @Test
    void updateUserMethodOk() {
        /*
        PUT https://reqres.in/api/users/2

        {
            "name": "morpheus",
            "job": "zion resident"
        }

        200
        {
            "name": "morpheus",
            "job": "zion resident",
            "updatedAt": "2021-12-28T14:14:45.392Z"
        }
         */
        JSONObject requestBody = new JSONObject()
                .put("name", "updatedUserName")
                .put("job", "updatedJob");

        Response response = given()
                .body(requestBody.toString())
                .when()
                .put("api/users/2")
                .then()
                .statusCode(200)
                .extract()
                .response();

        response.prettyPrint();
        String updatedAt = response.path("updatedAt");
        assertTrue(updatedAt.contains(LocalDate.now().toString()));
    }

    @Test
    void deleteUserMethodOk() {
        /*
        DELETE https://reqres.in/api/users/2

        204
        */

        Response response = given()
                .when()
                .delete("api/users/2")
                .then()
                .statusCode(204)
                .extract()
                .response();

        response.prettyPrint();
    }
}