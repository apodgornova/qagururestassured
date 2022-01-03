package tests;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static filters.CustomLogFilter.customLogFilter;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

public class ListenerAndFilterTests {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com";
        Configuration.baseUrl = "http://demowebshop.tricentis.com";
    }

    @Test
    void okAuthUserTestWithListener() {
        step("Get cookie by api and set it to browser", () -> {
            String authorizationCookie =
                    given()
                            .filter(new AllureRestAssured())
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .formParam("Email", "nastya1@testemail.com")
                            .formParam("Password", "Testpass123")
                            .when()
                            .post("/login")
                            .then()
                            .statusCode(302)
                            .extract()
                            .cookie("NOPCOMMERCE.AUTH");

            step("Open minimal content, because cookie can be set when site is opened", () ->
                    //open("/Themes/DefaultClean/Content/images/logo.png"));
                    open(baseURI));

            step("Set cookie to to browser", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));
        });

        step("Open profile page", () ->
                open("/customer/info"));

        step("Verify First name is proper on profile page", () ->
                $("#FirstName").shouldHave(attribute("value", "Anastasia"))
        );

        step("Verify Last name is proper on profile page", () ->
                $("#LastName").shouldHave(attribute("value", "Podgornova"))
        );

    }

    @Test
    void addToWishlistWithTemplate() {

        String body = "addtocart_51.EnteredQuantity=1";

        step("Add product to wishlist", () -> {
            given()
                    .filter(customLogFilter().withCustomTemplates())
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .body(body)
                    .when()
                    .post("addproducttocart/details/51/2")
                    .then()
                    .statusCode(200)
                    .body("updatetopwishlistsectionhtml", is("(1)"))
                    .body("message", is("The product has been added to your <a href=\"/wishlist\">wishlist</a>"));
        });
    }

}

