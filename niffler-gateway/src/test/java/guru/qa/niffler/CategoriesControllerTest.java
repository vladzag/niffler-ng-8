package guru.qa.niffler;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.jupiter.WiremockStubs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @WiremockStubs(value = {"getAllCategories.json"}, port = 8093)

    @Test
    void categoriesListShouldBeReturnedForCurrentUser() throws Exception {
        final String fixtureUser = "bee";

        mockMvc.perform(get("/api/categories/all")
                        .with(jwt().jwt(c -> c.claim("sub", fixtureUser)))
                        .param("excludeArchived", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(fixtureUser))
                .andExpect(jsonPath("$[0].name").value("Веселье"))
                .andExpect(jsonPath("$[0].archived").value(false))
                .andExpect(jsonPath("$[1].username").value(fixtureUser))
                .andExpect(jsonPath("$[1].name").value("Магазины"))
                .andExpect(jsonPath("$[1].archived").value(true))
                .andDo(print());;
    }

}
