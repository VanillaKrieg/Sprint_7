package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.client.base.ScooterRestClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends ScooterRestClient {

    private static final String ORDERS_URI = "orders/";
    private static final String ACCEPT_ORDER_URI = ORDERS_URI + "accept/";
    private static final String GET_ORDER_URI = ORDERS_URI + "track";

    @Step("Create {order}")
    public ValidatableResponse create(Order order) {
        return given()
                .spec(getBaseReqSpec())
                .body(order)
                .when()
                .post(ORDERS_URI)
                .then();
    }

    @Step("Get Order list")
    public ValidatableResponse getList() {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(ORDERS_URI)
                .then();
    }

    @Step("Accept {order}")
    public ValidatableResponse accept(Courier courier, Order order) {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .queryParam("courierId", courier.getId() == null ? "" : courier.getId())
                .put(ACCEPT_ORDER_URI + (order.getId() == null ? "" : order.getId()))
                .then();
    }

    @Step("Get {order}")
    public ValidatableResponse get(Order order) {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .queryParam("t", order.getTrackId() == null ? "" : order.getTrackId())
                .get(GET_ORDER_URI)
                .then();
    }
}
