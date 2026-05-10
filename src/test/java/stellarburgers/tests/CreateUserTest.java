package stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;

import io.restassured.response.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import stellarburgers.api.UserApi;
import stellarburgers.models.User;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class CreateUserTest extends BaseTest {

    private User user;
    private String accessToken;
    private final UserApi userApi = new UserApi();

    @Before
    public void setUp() {
        user = generateUser();
    }

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    @Description("Ожидается 200 ответ. В теле должен быть success: true")
    public void createUserTest() {
        Response createUserResponse = userApi.register(user);
        assertThat(createUserResponse.getStatusCode())
                .as("Код ответа должен быть 200")
                .isEqualTo(200);
        assertThat(createUserResponse.path("success").toString())
                .as("В теле ответа success должен быть true")
                .isEqualTo("true");
        accessToken = createUserResponse.path("accessToken");
    }

    @Test
    @DisplayName("Ошибка регистрации уже существующего пользователя")
    @Description("Ожидается 403 ответ. В теле ответа должен быть success: false")
    public void createExistingUserTest() {
        Response createUserResponse = userApi.register(user);
        accessToken = createUserResponse.path("accessToken");

        Response createDuplicateUserResponse = userApi.register(user);
        assertThat(createDuplicateUserResponse.getStatusCode())
                .as("Код ответа должен быть 403")
                .isEqualTo(403);
        assertThat(createDuplicateUserResponse.path("success").toString())
                .as("В теле ответа success должен быть false")
                .isEqualTo("false");
    }

    @Test
    @DisplayName("Ошибка регистрации пользователя из-за отсутствия обязательных полей")
    @Description("Ожидается 403 ответ. В теле ответа должен быть success: false")
    public void createUserWithoutFieldsTest() {
        String uid = UUID.randomUUID().toString();
        Map<String, String> userWithoutEmail = Map.of(
                "name", String.format("UserName_%s", uid),
                "password", String.format("Password_%s", uid)
        );
        Response createUserResponse = userApi.register(userWithoutEmail);
        assertThat(createUserResponse.getStatusCode())
                .as("Код ответа должен быть 403")
                .isEqualTo(403);
        assertThat(createUserResponse.path("success").toString())
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
