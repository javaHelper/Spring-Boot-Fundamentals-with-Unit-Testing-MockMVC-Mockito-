package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.entity.Library;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Slf4j
public class LibraryTestIT {
    private static final String GET_URL = "http://localhost:8080/api/library/authorName/Rahul";
    

    private static final String expected = """
    		[
			    {
			        "id": 1,
			        "bookName": "Cypress",
			        "isbn": "abcd",
			        "aisle": 4,
			        "author": "Rahul"
			    },
			    {
			        "id": 2,
			        "bookName": "DevOps",
			        "isbn": "xyz",
			        "aisle": 43,
			        "author": "Rahul"
			    }
			]
   """;

    @Test
    void testAuthorNameBookTest() throws JSONException {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(GET_URL, String.class);

        log.info("Response Status: {}", responseEntity.getStatusCode());
        log.info("Response Body : {}", responseEntity.getBody());
        
        JSONAssert.assertEquals(expected, responseEntity.getBody(), false);
    }

    @Test
    void createLibraryIntegrationTest(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String POST_URL = "http://localhost:8080/api/library";

        TestRestTemplate testRestTemplate = new TestRestTemplate();

        HttpEntity<Library> httpEntity = new HttpEntity<>(getLibrary(), headers);
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(POST_URL, httpEntity, String.class);
        log.info("Response : {}", responseEntity.getBody());

        String res = responseEntity.getBody();

        assertThat(responseEntity)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.CREATED);

        assertThat(Optional.ofNullable(JsonPath.read(res, "$.bookName")).get()).isEqualTo("JPA Persistence");
        assertThat(Optional.ofNullable(JsonPath.read(res, "$.isbn")).get()).isEqualTo("4XY");
        assertThat(Optional.ofNullable(JsonPath.read(res, "$.aisle")).get()).isEqualTo(40);
        assertThat(Optional.ofNullable(JsonPath.read(res, "$.author")).get()).isEqualTo("Rama");
    }

    private Library getLibrary(){
        Library library = new Library();
        library.setBookName("JPA Persistence");
        library.setAuthor("Rama");
        library.setAisle(40);
        library.setIsbn("4XY");
        return library;
    }
}