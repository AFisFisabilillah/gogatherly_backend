package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    public JwtService jwtService;

    @Test
    void createToken() {
        User user = new User();
        user.setName("afis");
        user.setEmail("afis@gmail.com");
        user.setNik("321321321321");

        String token = jwtService.buildToken(new HashMap<>(), user);

        System.out.println(token);

    }

    @Test
    void validTest(){
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZmlzQGdtYWlsLmNvbSIsIm5payI6IjMyMTMyMTMyMTMyMSIsImVtYWlsIjoiYWZpc0BnbWFpbC5jb20iLCJpYXQiOjE3NDc2Mzc2NzksImV4cCI6MTc1MDI2NzQyNX0.pPGmjzWKWdq2rYriHbCV4Gn68Tl4IDXvjMIgdkljJUVWZl5oPEf1JOIy-683ywWFiHjjPQQZcq8vrI1vD1BYdQ";
        String NIK = jwtService.getClaims(token, claims -> {
            return claims.get("nik", String.class );
        });


        assertEquals("321321321321", NIK);
    }

}