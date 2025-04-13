package com.example.taskmanagementsystem;

import com.example.taskmanagementsystem.entities.RoleType;
import com.example.taskmanagementsystem.repositories.UserRepository;
import com.example.taskmanagementsystem.securities.UserService;
import com.example.taskmanagementsystem.web.models.AuthRequest;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashSet;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Testcontainers
public abstract class AbstractTest {
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:12.20"));

    @Container
    private static final RedisContainer redisContainer = new RedisContainer(
            DockerImageName.parse("redis:7.0.12"));

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.url",() -> jdbcUrl);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected MockMvc mockMvc;

    static {
        postgreSQLContainer.withReuse(true).start();
        redisContainer.withReuse(true).start();
    }

    @BeforeEach
    protected void setup() {
        userService.create(AuthRequest.builder().email("user@usa.net").password("54321")
                .roles(new HashSet<>() {{add(RoleType.ROLE_USER);}}).build());

        userService.create(AuthRequest.builder().email("admin@usa.net").password("12345")
                .roles(new HashSet<>() {{ add(RoleType.ROLE_ADMIN); }}).build());
    }

    @AfterEach
    protected void afterEach() {
        userRepository.deleteAll();
    }

    @AfterAll
    protected static void tearDown() {
        redisContainer.stop();
        postgreSQLContainer.stop();
    }
}
