package stellarburgers.tests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import io.qameta.allure.restassured.AllureRestAssured;
import stellarburgers.models.User;

import java.util.UUID;

public class BaseTest {

    @BeforeClass
    public static void setup() {
        AllureRestAssured allureFilter = new AllureRestAssured();
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addFilter(allureFilter)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    protected User generateUser() {
        String uid = UUID.randomUUID().toString().substring(0, 5);
        return new User(
                String.format("UserName_%s", uid),
                String.format("user_%s@yandex.ru", uid),
                String.format("Password_%s", uid)
        );
    }
}
