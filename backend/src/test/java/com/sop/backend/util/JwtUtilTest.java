    package com.sop.backend.util;

    import com.nimbusds.jose.JOSEException;
    import com.sop.backend.models.User;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;

    import java.lang.reflect.Field;

    import static org.junit.jupiter.api.Assertions.*;

    @SpringBootTest(classes = JwtUtil.class)
    class JwtUtilTest {

        @Autowired
        private JwtUtil jwtUtil;

        private User user;

        @BeforeEach
        void setUp() throws IllegalAccessException, NoSuchFieldException {
            jwtUtil = new JwtUtil();
            jwtUtil.setSecret("12345678901234567890123456789012");
            jwtUtil.setExpirationMs(3600000);
            user = new User();
            user.setName("John Doe");
            user.setEmail("johndoe@email.test");
            user.setPassword("password");

            Field idField = User.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, 1L);
        }

        @Test
        void testGenerateAndValidateToken() throws Exception {
            String token = jwtUtil.generateToken(user);
            assertNotNull(token, "Token should not be null");

            String subject = jwtUtil.validateToken("Bearer " + token);
            assertEquals(user.getId().toString(), subject, "The subject should match the username");
        }

        @Test
        void testValidateExpiredToken() throws JOSEException, InterruptedException {
            jwtUtil = new JwtUtil();
            jwtUtil.setSecret("12345678901234567890123456789012");
            jwtUtil.setExpirationMs(1);

            String token = jwtUtil.generateToken(user);

            Thread.sleep(10);

            Exception exception = assertThrows(Exception.class, () -> jwtUtil.validateToken("Bearer " + token));
            assertTrue(exception.getMessage().contains("Login session has expired"));
        }

        @Test
        void testValidateInvalidToken() {
            String invalidToken = "invalid.jwt.token";

            Exception exception = assertThrows(Exception.class, () -> jwtUtil.validateToken("Bearer " + invalidToken));
            assertTrue(exception.getMessage().contains("Token is not valid") || exception.getMessage().contains("Invalid"));
        }
    }
