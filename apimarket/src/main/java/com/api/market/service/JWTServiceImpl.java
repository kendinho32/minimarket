package com.api.market.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.jackson2.SimpleGrantedAuthorityMixin;
import org.springframework.stereotype.Component;

import com.api.market.entity.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JWTServiceImpl implements JWTService {
	
	private static final Logger logger = LoggerFactory.getLogger(JWTServiceImpl.class);
	
	@Value("${app.jwtSecret}")
    private String jwtSecret;
	
	public static final long EXPIRATION_DATE = 86400000L;
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	
	/**
     * Funcion que se encarga de crear el token utilizando el caracter secreto para 
     * crear el cuerpo y la cabecera del token
     * 
     * @param authentication Objeto que representa las caracteristicas principales del usuario autenticado
     * @return String que representa la cadena de caracteres creadas para ser el token del usuario
     */
	@Override
	public String create(Authentication auth) throws IOException {
		String username = ((User) auth.getPrincipal()).getUsername();
		
		Claims claims = Jwts.claims();
		return Jwts.builder().setClaims(claims).setSubject(username)
				.signWith(SignatureAlgorithm.HS512, jwtSecret).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DATE)).compact();
	}
	
	/**
     * Funcion que se encarga de crear el token utilizando el caracter secreto para 
     * crear el cuerpo y la cabecera del token
     * 
     * @param Usuario Objeto que representa las caracteristicas principales del usuario
     * @return String que representa la cadena de caracteres creadas para ser el token del usuario
     */
	@Override
	public String createTokenByUser(Usuario user) throws IOException {
		String username = user.getEmail();
		
		Claims claims = Jwts.claims();
		claims.put("authorities", new ObjectMapper().writeValueAsString(user.getRoles()));
		return Jwts.builder().setClaims(claims).setSubject(username)
				.signWith(SignatureAlgorithm.HS512, jwtSecret).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DATE)).compact();
	}

	@Override
	public boolean validate(String token) {
		boolean result = false;
		try {
			Claims claim = getClaims(token);
			if(claim != null) {
				result =  true;
			}
			return result;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}

	}

	@Override
	public Claims getClaims(String token) {
		Claims claim = null;
		try {
			claim = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token.substring(7, token.length())).getBody();
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return claim;
	}

	@Override
	public String getUsername(String token) {
		return getClaims(token).getSubject();
	}

	@Override
	public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException {
		Object roles = getClaims(token).get("authorities");

		return Arrays.asList(new ObjectMapper().addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class)
						.readValue(roles.toString().getBytes(), SimpleGrantedAuthority[].class));
	}

	@Override
	public String resolve(String token) {
		if (token != null && token.startsWith(TOKEN_PREFIX)) {
			return token.replace(TOKEN_PREFIX, "");
		}
		return null;
	}

}
