#

#

````
curl --location 'http://localhost:8080/api/library/Rahul'
````

# Controller Test cases

```java
package com.example;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.controller.LibraryController;
import com.example.entity.Library;
import com.example.service.LibraryService;

@WebMvcTest(controllers = {LibraryController.class})
class SpringBootRestServiceApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    //@Autowired
    LibraryService libraryService;

    @Test
    public void testFindByAuthor() throws Exception {
        List<Library> libraries = new ArrayList<>();
        libraries.add(this.getLibrary(1L, "Rahul", "abcd", "DevOps", 4));
        libraries.add(this.getLibrary(2L, "Rahul", "xyz", "Cypress", 43));

        when(libraryService.findAllByAuthor("Rahul")).thenReturn(libraries);

        this.mockMvc.perform(get("/api/library/authorName/Rahul"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))

                .andExpect(jsonPath("$.[0].id").value(1L))
                .andExpect(jsonPath("$.[0].author").value("Rahul"))
                .andExpect(jsonPath("$.[0].isbn").value("abcd"))
                .andExpect(jsonPath("$.[0].bookName").value("DevOps"))
                .andExpect(jsonPath("$.[0].aisle").value(4))

                .andExpect(jsonPath("$.[1].id").value(2L))
                .andExpect(jsonPath("$.[1].author").value("Rahul"))
                .andExpect(jsonPath("$.[1].isbn").value("xyz"))
                .andExpect(jsonPath("$.[1].bookName").value("Cypress"))
                .andExpect(jsonPath("$.[1].aisle").value(43));
    }

    @Test
    void testFindLibraryById() throws Exception {
        Library library = getLibrary(1L, "Sumita", "14V", "Spring Boot in Action", 12);
        when(libraryService.findLibraryById(any())).thenReturn(library);

        this.mockMvc.perform(get("/api/library/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))  // Library class has 5 fields
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.author").value("Sumita"))
                .andExpect(jsonPath("$.isbn").value("14V"))
                .andExpect(jsonPath("$.bookName").value("Spring Boot in Action"))
                .andExpect(jsonPath("$.aisle").value(12));
    }

    @Test
    void testUpdateLibrary() throws Exception {
        ObjectMapper map = new ObjectMapper();

        Library updatedLibrary = getLibrary(1L, "John", "1XB", "Microservices", 1);
        String libraryJson = map.writeValueAsString(updatedLibrary);
        System.out.println(libraryJson);

        // Mock service layer behavior
        when(libraryService.updateLibrary(any(Library.class))).thenReturn(updatedLibrary);

        mockMvc.perform(put("/api/library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(libraryJson))
                .andExpect(status().isOk()) // HTTP 200 OK
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.author").value("John"))
                .andExpect(jsonPath("$.isbn").value("1XB"))
                .andExpect(jsonPath("$.bookName").value("Microservices"))
                .andExpect(jsonPath("$.aisle").value(1));

        // Verify that the service method was called once
        verify(libraryService, times(1)).updateLibrary(any(Library.class));
    }

    @Test
    void deleteLibrary() throws Exception {
        this.mockMvc.perform(delete("/api/library/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    private Library getLibrary(Long id, String author, String isbn, String bookName, int aisle) {
        Library library = new Library();
        library.setId(id);
        library.setAuthor(author);
        library.setIsbn(isbn);
        library.setBookName(bookName);
        library.setAisle(aisle);
        return library;
    }
}
```

-------

# Integration Test cases

```java
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
```


