package com.banking.employee_management_service.controller;

import com.banking.employee_management_service.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void clearDatabase() {
        employeeRepository.deleteAll();
    }

    private static String body(long id, String name, String department, String salary) {
        return """
                {"id":%d,"name":"%s","department":"%s","salary":%s}
                """.formatted(id, name, department, salary);
    }

    private ResultActions create(String path, String json) throws Exception {
        return mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private ResultActions create(String json) throws Exception {
        return create("/employees", json);
    }

    @Test
    void createsEmployee() throws Exception {
        create(body(1, "  Alice ", "IT", "5000.00"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void legacyCreatePathStillWorks() throws Exception {
        create("/employee/create-employee", body(1, "Alice", "IT", "5000"))
                .andExpect(status().isCreated());
    }

    @Test
    void rejectsDuplicateId() throws Exception {
        create(body(1, "Alice", "IT", "5000")).andExpect(status().isCreated());
        create(body(1, "Bob", "HR", "4000"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Employee with id 1 already exists"));
        mockMvc.perform(get("/employees"))
                .andExpect(jsonPath("$.content[0].name").value("Alice"));
    }

    @Test
    void rejectsInvalidInput() throws Exception {
        create(body(1, " ", "IT", "5000")).andExpect(status().isBadRequest());
        create(body(2, "Alice", "IT", "-1")).andExpect(status().isBadRequest());
        create(body(3, "Alice", "IT", "10.123")).andExpect(status().isBadRequest());
        create("{\"name\":\"Alice\",\"department\":\"IT\",\"salary\":100}").andExpect(status().isBadRequest());
    }

    @Test
    void pagesEmployeesOrderedById() throws Exception {
        create(body(3, "Carol", "Ops", "3000")).andExpect(status().isCreated());
        create(body(1, "Alice", "IT", "5000")).andExpect(status().isCreated());
        create(body(2, "Bob", "HR", "4000")).andExpect(status().isCreated());

        mockMvc.perform(get("/employees").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));

        mockMvc.perform(get("/employees").param("page", "1").param("size", "2"))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(3));
    }

    @Test
    void rejectsInvalidPaging() throws Exception {
        mockMvc.perform(get("/employees").param("size", "0")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/employees").param("size", "101")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/employees").param("page", "-1")).andExpect(status().isBadRequest());
    }

    @Test
    void legacyFetchAllReturnsFullList() throws Exception {
        create(body(2, "Bob", "HR", "4000")).andExpect(status().isCreated());
        create(body(1, "Alice", "IT", "5000")).andExpect(status().isCreated());
        mockMvc.perform(get("/employee/fetch-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }
}
