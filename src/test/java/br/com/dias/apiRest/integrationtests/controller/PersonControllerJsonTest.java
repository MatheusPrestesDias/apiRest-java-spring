package br.com.dias.apiRest.integrationtests.controller;

import br.com.dias.apiRest.configs.TestConfigs;
import br.com.dias.apiRest.integrationtests.DTO.AccountCredentialsDTO;
import br.com.dias.apiRest.integrationtests.DTO.PersonDTO;
import br.com.dias.apiRest.integrationtests.DTO.TokenDTO;
import br.com.dias.apiRest.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {


    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static PersonDTO personDTO;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        personDTO = new PersonDTO();
    }

    @Test
    @Order(0)
    public void authorization() throws IOException {

        AccountCredentialsDTO user = new AccountCredentialsDTO("matheus", "admin123");

        var accessToken = given().basePath("/auth/authenticate")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(user)
                .when().
                post().then().statusCode(200)
                .extract().body().as(TokenDTO.class).getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    public void testCreate() throws IOException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_DIAS)
                .body(personDTO)
                .when()
                .post().then().statusCode(200)
                .extract().body().asString();

        PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
        personDTO = createdPerson;

        Assertions.assertNotNull(createdPerson.getId());
        Assertions.assertNotNull(createdPerson.getFirstName());
        Assertions.assertNotNull(createdPerson.getLastName());
        Assertions.assertNotNull(createdPerson.getAddress());
        Assertions.assertNotNull(createdPerson.getGender());

        Assertions.assertTrue(createdPerson.getId() > 0);

        Assertions.assertEquals("Richard",createdPerson.getFirstName());
        Assertions.assertEquals("Stallman",createdPerson.getLastName());
        Assertions.assertEquals("New York City, New York, US",createdPerson.getAddress());
        Assertions.assertEquals("Male",createdPerson.getGender());
    }

    @Test
    @Order(2)
    public void WithWrongOrigin() {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_PRESTES)
                .body(personDTO)
                .when()
                .post().then().statusCode(403)
                .extract().body().asString();


        Assertions.assertNotNull(content);
        Assertions.assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(3)
    public void testFindById() throws IOException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_DIAS)
                .pathParam("id", personDTO.getId())
                .when()
                .get("{id}").then().statusCode(200)
                .extract().body().asString();

        PersonDTO persistedPerson = objectMapper.readValue(content, PersonDTO.class);
        personDTO = persistedPerson;

        Assertions.assertNotNull(persistedPerson.getId());
        Assertions.assertNotNull(persistedPerson.getFirstName());
        Assertions.assertNotNull(persistedPerson.getLastName());
        Assertions.assertNotNull(persistedPerson.getAddress());
        Assertions.assertNotNull(persistedPerson.getGender());

        Assertions.assertTrue(persistedPerson.getId() > 0);

        Assertions.assertEquals("Richard",persistedPerson.getFirstName());
        Assertions.assertEquals("Stallman",persistedPerson.getLastName());
        Assertions.assertEquals("New York City, New York, US",persistedPerson.getAddress());
        Assertions.assertEquals("Male",persistedPerson.getGender());
    }

    @Test
    @Order(4)
    public void testFindByIdWithWrongOrigin() throws IOException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_PRESTES)
                .pathParam("id", personDTO.getId())
                .when()
                .get("{id}").then().statusCode(403)
                .extract().body().asString();

        Assertions.assertNotNull(content);
        Assertions.assertEquals("Invalid CORS request", content);
    }

    private void mockPerson() {
        personDTO.setFirstName("Richard");
        personDTO.setLastName("Stallman");
        personDTO.setAddress("New York City, New York, US");
        personDTO.setGender("Male");
        personDTO.setEnabled(true);
    }

}
