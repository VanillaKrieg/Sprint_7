import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.CourierGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;

public class CourierCreationTest {

    private CourierClient courierClient;
    private Courier courier;


    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new AllureRestAssured()
        );
    }

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @After
    public void clearData() throws NullPointerException {
        try {
            courier.setId(courierClient.login(courier).extract().path("id"));
            courierClient.delete(courier);
        } catch (NullPointerException ignored) {}
    }

    @Test
    @DisplayName("Курьер может быть создан с валидными данными")
    public void courierCanBeCreatedWithValidData() {
        courier = CourierGenerator.getRandom();

        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_CREATED)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Курьер не может быть создан с тем же логином")
    public void courierCanNotBeCreatedWithSameLogin() {
        courier = CourierGenerator.getRandom();

        courierClient.create(courier);
        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_CONFLICT)
                .body("message", is("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Курьер не может быть создан без логина")
    public void courierCanNotBeCreatedWithoutLogin() {
        courier = new Courier("", "password");

        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Курьер не может быть создан без пароля")
    public void courierCanNotBeCreatedWithoutPassword() {
        courier = new Courier("login", "");

        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }
}
