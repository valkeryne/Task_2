package stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;

import io.restassured.response.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import stellarburgers.api.UserApi;
import stellarburgers.models.Login;
import stellarburgers.models.User;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginUserTest extends BaseTest {

    private User user;
    private String accessToken;
    private final UserApi userApi = new UserApi();

    @Before
    public void setUp() {
        user = generateUser();
        Response registerResponse = userApi.register(user);
        accessToken = registerResponse.path("accessToken");
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    @Description("Ожидается 200 ответ. В теле должен быть success: true, accessToken и refreshToken")
    public void loginExistingUserTest() {
        Login credentials = user.getCredentials();
        Response loginResponse = userApi.login(credentials);

        assertThat(loginResponse.getStatusCode())
                .as("Код ответа должен быть 200")
                .isEqualTo(200);
        assertThat(loginResponse.path("success").toString())
                .as("В теле ответа success должен быть true")
                .isEqualTo("true");
        assertThat(loginResponse.path("accessToken").toString())
                .as("В теле ответа должен быть accessToken")
                .isNotEmpty();
        assertThat(loginResponse.path("refreshToken").toString())
                .as("В теле ответа должен быть refreshToken")
                .isNotEmpty();
    }

    @Test
    public void loginNonExistingUserTest() {
        Login badCredentials = new Login(user.getEmail(), "badPassword");
        Response loginResponse = userApi.login(badCredentials);
        assertThat(loginResponse.getStatusCode())
                .as("Код ответа должен быть 401")
                .isEqualTo(401);
        assertThat(loginResponse.path("success").toString())
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
