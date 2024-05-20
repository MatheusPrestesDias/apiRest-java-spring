package br.com.dias.apiRest.integrationtests.controller.yaml;

import br.com.dias.apiRest.configs.TestConfigs;
import br.com.dias.apiRest.integrationtests.DTO.AccountCredentialsDTO;
import br.com.dias.apiRest.integrationtests.DTO.TokenDTO;
import br.com.dias.apiRest.integrationtests.controller.yaml.mapper.YMLMapper;
import br.com.dias.apiRest.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerYamlTest extends AbstractIntegrationTest {

    private static TokenDTO tokenDTO;
    private static YMLMapper mapper;

    @BeforeAll
    public static void setup() {
        mapper = new YMLMapper();
    }

    @Test
    @Order(0)
    public void signIn() {

        AccountCredentialsDTO user = new AccountCredentialsDTO("matheus", "admin1234");

        tokenDTO = given()
                .config(RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
                )
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .basePath("/auth/authenticate")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .body(user, mapper)
                .when().
                post().then().statusCode(200)
                .extract().body().as(TokenDTO.class, mapper);

        Assertions.assertNotNull(tokenDTO.getAccessToken());
        Assertions.assertNotNull(tokenDTO.getRefreshToken());
    }

    @Test
    @Order(1)
    public void refreshToken() {

        AccountCredentialsDTO user = new AccountCredentialsDTO("matheus", "admin1234");

        var newToken = given()
                .config(RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT))
                )
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .basePath("/auth/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .pathParam("username", tokenDTO.getUsername())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getRefreshToken())
                .when().
                put("{username}").then().statusCode(200)
                .extract().body().as(TokenDTO.class, mapper);

        Assertions.assertNotNull(newToken.getAccessToken());
        Assertions.assertNotNull(newToken.getRefreshToken());
    }
}
