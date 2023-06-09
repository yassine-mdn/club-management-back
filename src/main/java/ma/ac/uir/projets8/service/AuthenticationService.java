package ma.ac.uir.projets8.service;


import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.auth.AuthenticationRequest;
import ma.ac.uir.projets8.auth.AuthenticationResponse;
import ma.ac.uir.projets8.auth.RegisterRequest;
import ma.ac.uir.projets8.model.Student;
import ma.ac.uir.projets8.repository.AccountRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
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
        return AuthenticationResponse.builder()
                .id(user.getIdA())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .token(jwtToken)
                .build();
    }
}
