package org.ohdsi.gisservice.security.sso;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SsoProvider {

    @Value("${security.userInfoUri}")
    private String userInfoUri;

    @Autowired
    private RestTemplate restTemplate;

    public Authentication getAuthentication(String token) {

//        RestTemplate restTemplate = new RestTemplate();
        HttpEntity request = new HttpEntity<>(resolveRequestHeaders(token));
        ResponseEntity<JsonNode> response;

        try {
            response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, JsonNode.class);
        } catch (HttpStatusCodeException ex) {
            throw new InvalidSsoTokenException(ex.getMessage());
        }

        JsonNode responseJson = response.getBody();

        String username = resolvePrincipal(responseJson);
        List<String> permissions = resolvePermissions(responseJson);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authenticationToken.setDetails(permissions);

        return authenticationToken;
    }

    protected HttpHeaders resolveRequestHeaders(String authToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, authToken);
        headers.add(HttpHeaders.CONTENT_ENCODING, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
        return headers;
    }

    protected String resolvePrincipal(JsonNode responseJson) {

        return responseJson.get("login").asText();
    }

    protected List<String> resolvePermissions(JsonNode responseJson) {

        return Lists.newArrayList(responseJson.get("permissions").elements())
                .stream()
                .map(permNode -> permNode.get("permission").asText())
                .collect(Collectors.toList());
    }
}
