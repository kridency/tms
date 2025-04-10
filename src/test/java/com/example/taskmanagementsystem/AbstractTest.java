package com.example.taskmanagementsystem;

import com.example.taskmanagementsystem.entities.RoleType;
import com.example.taskmanagementsystem.repositories.UserRepository;
import com.example.taskmanagementsystem.securities.SecurityService;
import com.example.taskmanagementsystem.web.models.CreateUserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashSet;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Testcontainers
public class AbstractTest {
    protected static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:12.20"));

    static { postgreSQLContainer.withReuse(true).start(); }

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.url",() -> jdbcUrl);
        registry.add("spring.datasource.hikari.schema",() -> "task_management_schema");
        registry.add("spring.jpa.properties.hibernate.jakarta.persistence.create-database-schemas",() -> "true");
    }

    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        securityService.register(CreateUserRequest.builder().email("user@usa.net").password("54321")
                .roles(new HashSet<>() {{add(RoleType.ROLE_EXECUTOR);}}).build());

        securityService.register(CreateUserRequest.builder().email("admin@usa.net").password("12345")
                .roles(new HashSet<>() {{add(RoleType.ROLE_AUTHOR); add(RoleType.ROLE_EXECUTOR);}}).build());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }
}
