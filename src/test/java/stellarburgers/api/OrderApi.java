package stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import stellarburgers.models.Order;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderApi {

    private static final String INGREDIENTS_ENDPOINT = "/api/ingredients";
    private static final String ORDER_ENDPOINT = "/api/orders";

    @Step("Получение списка ID доступных ингредиентов")
    public List<String> getIngredientsId() {
        Response ingredientResponse = given()
                .get(INGREDIENTS_ENDPOINT);
        return ingredientResponse.path("data._id");
    }

    @Step("Получение списка заказов авторизованным пользователем")
    public Response getOrder(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get("api/orders");
    }

    @Step("Получение списка заказов не авторизованным пользователем")
    public Response getOrder() {
        return given()
                .when()
                .get("api/orders");
    }

    @Step("Создание заказа авторизованным пользователем")
    public Response create(Order order, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .body(order)
                .post("api/orders");
    }

    @Step("Создание заказа не авторизованным пользователем")
    public Response create(Order order) {
        return given()
                .body(order)
                .post("api/orders");
    }
}
