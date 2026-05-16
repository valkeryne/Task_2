package stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;

import io.restassured.response.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import stellarburgers.api.UserApi;
import stellarburgers.models.User;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateUserTest extends BaseTest {

    private String accessToken;
    private final UserApi userApi = new UserApi();

    @Before
    public void setUp() {
        User user = generateUser();
        Response registerResponse = userApi.register(user);
        accessToken = registerResponse.path("accessToken");
    }

    @Test
    @DisplayName("Успешное обновление пользовательских данных")
    @Description("Ожидается 200 ответ. В теле ответа должен быть success: true. При запросе в api на получение пользовательской информации, должны прийти обновленные данные")
    public void updateAuthorizedUserTest() {
        User updatedUser = generateUser();
        Response updateUserResponse = userApi.updateUser(updatedUser, accessToken);

        Response updatedUserInfoResponse = userApi.getUser(accessToken);

        assertThat(updateUserResponse.getStatusCode())
                .as("Код ответа должен быть 200")
                .isEqualTo(200);
        assertThat(updateUserResponse.path("success").toString())
                .as("В теле ответа success должен быть true")
                .isEqualTo("true");
        assertThat(updatedUserInfoResponse.path("user.email").toString())
                .as("Email пользователя должен быть обновлён")
                .isEqualTo(updatedUser.getEmail());
        assertThat(updatedUserInfoResponse.path("user.name").toString())
                .as("Имя пользователя должно быть обновлено")
                .isEqualTo(updatedUser.getName());
    }

    @Test
    @DisplayName("Ошибка обновления пользовательских данных не авторизованным пользователем")
    @Description("Код ответа должен быть 401. В теле ответа должен быть success: false")
    public void updateUnauthorizedUserTest() {
        User updatedUser = generateUser();
        Response updateUserResponse = userApi.updateUser(updatedUser, "");
        assertThat(updateUserResponse.getStatusCode())
                .as("Код ответа при изменении данных без токена должен быть 401")
                .isEqualTo(401);

        assertThat(updateUserResponse.path("success").toString())
                .as("В теле ответа success должен быть false")
                .isEqualTo("false");

        assertThat(updateUserResponse.path("message").toString())
                .as("Сообщение об ошибке должно уведомлять о необходимости авторизации")
                .isEqualTo("You should be authorised");
    }

    @After
    public void cleanup() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }
}
