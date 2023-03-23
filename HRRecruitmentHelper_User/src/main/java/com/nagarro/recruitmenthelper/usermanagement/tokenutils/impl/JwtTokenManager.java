package com.nagarro.recruitmenthelper.usermanagement.tokenutils.impl;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.tokenutils.TokenManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenManager implements TokenManager {
    @Value("${jwt.validity}")
    private long tokenValidity;
    @Value("${jwt.secret}")
    private String secretKeyword;

    @Override
    public Long getValidity() {
        return tokenValidity;
    }

    @Override
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDTO userDTO) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDTO.getRoleDTO().getName());
        return Jwts.builder().setClaims(claims)
                .setSubject(userDTO.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity * 1000))
                .signWith(SignatureAlgorithm.HS512, secretKeyword).compact();
    }

    @Override
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKeyword)
                .parseClaimsJws(token)
                .getBody();
    }
}
