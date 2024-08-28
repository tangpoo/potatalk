package com.potatalk.repository;

import com.potatalk.memberservice.MemberServiceApplication;
import com.potatalk.memberservice.domain.Member;
import com.potatalk.memberservice.dto.MemberCreateDto;
import com.potatalk.memberservice.repository.MemberRepository;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Testcontainers
@SpringBootTest(classes = MemberServiceApplication.class)
public class MemberRepositoryTests {

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Container
    private static final MySQLContainer<?> mySQLContainer =
        new MySQLContainer<>("mysql:8.0.32")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private MemberRepository memberRepository;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:mysql://" + mySQLContainer.getHost() + ":" + mySQLContainer.getMappedPort(3306) + "/" + mySQLContainer.getDatabaseName());
        registry.add("spring.r2dbc.username", mySQLContainer::getUsername);
        registry.add("spring.r2dbc.password", mySQLContainer::getPassword);
    }

    @BeforeEach
    void setup() {
        DatabaseClient client = DatabaseClient.create(connectionFactory());
        client.sql("CREATE TABLE IF NOT EXISTS members (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(255) NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "nick_name VARCHAR(255) NOT NULL)")
            .fetch()
            .rowsUpdated()
            .block();
    }
    private ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
            .option(ConnectionFactoryOptions.DRIVER, "mysql")
            .option(ConnectionFactoryOptions.HOST, mySQLContainer.getHost())
            .option(ConnectionFactoryOptions.PORT, mySQLContainer.getMappedPort(3306))
            .option(ConnectionFactoryOptions.USER, mySQLContainer.getUsername())
            .option(ConnectionFactoryOptions.PASSWORD, mySQLContainer.getPassword())
            .option(ConnectionFactoryOptions.DATABASE, mySQLContainer.getDatabaseName())
            .build());
    }
    @AfterEach
    void tearDown() {
        memberRepository.deleteAll().block();
    }

    @Test
    void save_and_find_member() {
        // Arrange
        final MemberCreateDto request = new MemberCreateDto("username", "password", "nickName");
        final Member member = Member.createMember(request, passwordEncoder);

        memberRepository.save(member).block();

        // Act
        memberRepository.findAll();

        // Assert
//        StepVerifier.create(result)
//            .expectNextMatches(
//                res -> res.getUsername().equals(request.getUsername())
//            )
//            .verifyComplete();
    }
}
