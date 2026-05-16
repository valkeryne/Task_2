package stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import stellarburgers.api.OrderApi;
import stellarburgers.api.UserApi;
import stellarburgers.models.Order;
import stellarburgers.models.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderTest extends BaseTest {

    private String accessToken;
    private final List<String> ingredients = new ArrayList<>();
    private final OrderApi orderApi = new OrderApi();
    private final UserApi userApi = new UserApi();

    @Before
    public void setUp() {
        User user = generateUser();
        Response registerResponse = userApi.register(user);
        accessToken = registerResponse.path("accessToken");

        List<String> ingredientIds = orderApi.getIngredientsId();

        ingredients.add(ingredientIds.get(0));
        ingredients.add(ingredientIds.get(1));
        ingredients.add(ingredientIds.get(2));
    }

    @Test
    @DisplayName("Успешное создание заказа авторизованным пользователем")
    @Description("Ожидается 200 ответ. В теле должен быть success: true")
    public void createOrderTest() {
        Order order = new Order(ingredients);
        Response createOrderResponse = orderApi.create(order, accessToken);
        assertThat(createOrderResponse.getStatusCode())
                .as("Код ответа должен быть 200")
                .isEqualTo(200);
        assertThat(createOrderResponse.path("success").toString())
                .as("В теле ответа success должен быть true")
                .isEqualTo("true");
    }

    @Test
    @DisplayName("Успешное создание заказа не авторизованным пользователем")
    @Description("Ожидается 200 ответ. В теле должен быть success: true")
    public void createOrderWithoutAuthorizationTest() {
        Order order = new Order(ingredients);
        Response createOrderResponse = orderApi.create(order);

        assertThat(createOrderResponse.getStatusCode())
                .as("Код ответа должен быть 200")
                .isEqualTo(200);
        assertThat(createOrderResponse.path("success").toString())
                .as("В теле ответа success должен быть true")
                .isEqualTo("true");
    }

    @Test
    @DisplayName("Ошибка создания заказа без ингредиентов")
    @Description("Ожидается 400 ответ. В теле должен быть success: false")
    public void createOrderWithoutIngredientsTest() {
        Order order = new Order();
        Response createOrderResponse = orderApi.create(order);
        assertThat(createOrderResponse.getStatusCode())
                .as("Код ответа должен быть 400")
                .isEqualTo(400);
        assertThat(createOrderResponse.path("success").toString())
                .as("В теле ответа success должен быть false")
                .isEqualTo("false");
    }

    @Test
    @DisplayName("Ошибка создания заказа с невалидными ингредиентами")
    @Description("Ожидается 500 ответ")
    public void createOrderWithWrongIngredientsTest() {
        Order wrongOrder = new Order(List.of("wrongIngredient"));
        Response createOrderResponse = orderApi.create(wrongOrder);
        assertThat(createOrderResponse.getStatusCode())
                .as("Код ответа должен быть 500")
                .isEqualTo(500);
    }

    @Test
    @DisplayName("Успешно получить список заказов авторизованным пользователем")
    @Description("Ожидается 200 ответ. В теле ответа должен быть success: true. Должен быть не пустой список заказов")
    public void getOrdersWithAuthorizationTest() {
        Order order = new Order(ingredients);
        orderApi.create(order, accessToken);

        Response getOrdersResponse = orderApi.getOrder(accessToken);

        List<Object> orders = getOrdersResponse.path("orders");

        assertThat(getOrdersResponse.getStatusCode())
                .as("Код ответа должен быть 200")
                .isEqualTo(200);
        assertThat(getOrdersResponse.path("success").toString())
                .as("В теле ответа success должен быть true")
                .isEqualTo("true");
        assertThat(orders)
                .as("Список заказов не должен быть пустым")
                .isNotEmpty();
    }

    @Test
    @DisplayName("Ошибка получения заказов не авторизованным пользователем")
    @Description("Ожидается 401 ответ. В теле ответа должен быть success: false")
    public void getOrdersWithoutAuthorizationTest() {
        Response getOrdersResponse = orderApi.getOrder();

        assertThat(getOrdersResponse.getStatusCode())
                .as("Код ответа должен быть 401")
                .isEqualTo(401);
        assertThat(getOrdersResponse.path("success").toString())
                .as("В теле ответа success должен быть false")
                .isEqualTo("false");
    }

    @After
    public void cleanup() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }
}
