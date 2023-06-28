package com.ecommerce.userservice.util;

import com.ecommerce.userservice.exception.TokenNotValidException;
import com.ecommerce.userservice.model.UserPrincipal;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ecommerce.userservice.constant.SecurityConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;

@Component
@Slf4j
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    public String generateAccessToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(SecurityConstant.Company_LLC)
                .withAudience(SecurityConstant.Company_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(SecurityConstant.AUTHORITIES, claims)
                .withClaim("userId",userPrincipal.getUserId().toString())
                .withClaim("firstName",userPrincipal.getFirstName())
                .withClaim("lastName",userPrincipal.getLastName())
                .withClaim("email",userPrincipal.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME))
                .sign(HMAC512(secret.getBytes()));
    }

    public String generateRefreshToken(UserPrincipal userPrincipal) {
        return JWT.create()
                .withIssuer(SecurityConstant.Company_LLC)
                .withAudience(SecurityConstant.Company_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUserId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstant.REFRESH_TOKEN_EXP))
                .sign(HMAC256(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(DecodedJWT decodedJWT) {
        String[] claims = decodedJWT.getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public Authentication getAuthentication(String email, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPasswordAuthToken = new
                UsernamePasswordAuthenticationToken(email, null, authorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }


    public DecodedJWT decodeToken(String value) {
        if (isNull(value)){
            throw new TokenNotValidException("Token has not been provided");
        }

        DecodedJWT decodedJWT = JWT.decode(value);
        log.info("Token decoded successfully");
        return decodedJWT;
    }


    private String[] getClaimsFromUser(UserPrincipal user) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : user.getAuthorities()){
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }
}
