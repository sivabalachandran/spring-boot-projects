package com.ebi.assessment.person;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")
    );

    @Test
    public void shouldReturnTheAllTheUsersAfterInserting() throws Exception {
        List<PersonDto> persons = new ArrayList<>();
        PersonDto person1 = PersonDto.builder()
                .withFirstName("siva")
                .withLastName("Balachandran")
                .withAge(30)
                .withFavoriteColor("Blue")
                .build();

        PersonDto person2 = PersonDto.builder()
                .withFirstName("Test")
                .withLastName("User")
                .withAge(33)
                .withFavoriteColor("Red")
                .build();

        persons.add(person1);
        persons.add(person2);

        final String request = objectMapper.writeValueAsString(persons);
        this.mockMvc.perform(post("/persons")
                .contentType(APPLICATION_JSON_UTF8)
                .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(notNullValue())))
                .andExpect(jsonPath("$[0].firstName", is("siva")))
                .andExpect(jsonPath("$[0].lastName", is("Balachandran")))
                .andExpect(jsonPath("$[0].age", is(30)))
                .andExpect(jsonPath("$[0].favoriteColor", is("Blue")));
    }

    @Test
    public void getRequestShouldReturnTheAllTheUsers() throws Exception {

        this.mockMvc.perform(get("/persons"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(notNullValue())))
                .andExpect(jsonPath("$[0].firstName", is("siva")))
                .andExpect(jsonPath("$[0].lastName", is("Balachandran")))
                .andExpect(jsonPath("$[0].age", is(30)))
                .andExpect(jsonPath("$[0].favoriteColor", is("Blue")));
    }
}
