package br.com.dias.apiRest.integrationtests.controller.xml;

import br.com.dias.apiRest.configs.TestConfigs;
import br.com.dias.apiRest.integrationtests.DTO.AccountCredentialsDTO;
import br.com.dias.apiRest.integrationtests.DTO.TokenDTO;
import br.com.dias.apiRest.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerXmlTest extends AbstractIntegrationTest {

    private static TokenDTO tokenDTO;

    @Test
    @Order(0)
    public void signIn() {

        AccountCredentialsDTO user = new AccountCredentialsDTO("matheus", "admin1234");

        tokenDTO = given().basePath("/auth/authenticate")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .body(user)
                .when().
                post().then().statusCode(200)
                .extract().body().as(TokenDTO.class);

        Assertions.assertNotNull(tokenDTO.getAccessToken());
        Assertions.assertNotNull(tokenDTO.getRefreshToken());
    }

    @Test
    @Order(1)
    public void refreshToken() {

        AccountCredentialsDTO user = new AccountCredentialsDTO("matheus", "admin1234");

        var newToken = given().basePath("/auth/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .pathParam("username", tokenDTO.getUsername())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getRefreshToken())
                .when().
                put("{username}").then().statusCode(200)
                .extract().body().as(TokenDTO.class);

        Assertions.assertNotNull(newToken.getAccessToken());
        Assertions.assertNotNull(newToken.getRefreshToken());
    }
}
