package stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import stellarburgers.models.Login;
import stellarburgers.models.User;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserApi {

    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String USER_ENDPOINT = "/api/auth/user";

    @Step("Регистрация пользователя")
    public Response register(User user) {
        return given()
                .body(user)
                .when()
                .post(REGISTER_ENDPOINT);
    }

    @Step("Регистрация пользователя без обязательных параметров")
    public Response register(Map<String, String> userFields) {
        return given()
                .body(userFields)
                .when()
                .post(REGISTER_ENDPOINT);
    }

    @Step("Авторизация пользователя")
    public Response login(Login login) {
        return given()
                .body(login)
                .post(LOGIN_ENDPOINT);
    }

    @Step("Удаление пользователя")
    public void deleteUser(String accessToken) {
        given()
                .header("Authorization", accessToken)
                .when()
                .delete(USER_ENDPOINT)
                .then()
                .statusCode(202);
    }

    @Step("Получение информации о пользователе")
    public Response getUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get("api/auth/user");
    }

    @Step("Изменение пользовательских данных")
    public Response updateUser(User updatedUser, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .body(updatedUser)
                .when()
                .patch("api/auth/user");
    }
}
