package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secretkey}")
    private String secretKey;

    @Value("${security.jwt.expired}")
    private  Long expired;

    private SecretKey getSignKey(){
        byte[] decode = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decode);
    }


    public String buildToken(Map<String, Object> claim, User user){
        claim.put("email", user.getEmail());
        claim.put("nik", user.getNik());
        return Jwts.builder()
                .signWith(this.getSignKey())
                .subject(user.getName())
                .claims(claim)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expired))
                .compact();
    }

    public Claims getAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaims(String token , Function<Claims ,T> claimsResolver){
        Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String getEmail(String token){
        return  getClaims(token, claims -> claims.get("email", String.class));
    }


}
