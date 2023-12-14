package ma.ac.uir.projets8.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.auth.AuthenticationRequest;
import ma.ac.uir.projets8.auth.AuthenticationResponse;
import ma.ac.uir.projets8.auth.RegisterRequest;
import ma.ac.uir.projets8.model.Account;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.model.enums.Role;
import ma.ac.uir.projets8.repository.AccountRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final AccountRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;



    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .id(user.getIdA())
                .roles(user.getRoles())
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            UserDetails userDetails = this.repository.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken , userDetails)) {
                var jwtToken = jwtService.generateToken(userDetails);
                // revoke old refresh token
                // save new refresh token
                var authResponse = AuthenticationResponse.builder()
                        .roles(userDetails.getAuthorities().stream().map(a -> Role.valueOf(a.getAuthority())).toList())
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
