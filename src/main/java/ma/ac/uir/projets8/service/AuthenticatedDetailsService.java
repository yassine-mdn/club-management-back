package ma.ac.uir.projets8.service;

import lombok.RequiredArgsConstructor;
import ma.ac.uir.projets8.exception.AccountNotFoundException;
import ma.ac.uir.projets8.model.Account;
import ma.ac.uir.projets8.repository.AccountRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Handles getting the authenticated user details
 */
@Service
@RequiredArgsConstructor
public class AuthenticatedDetailsService {

    private final AccountRepository repository;

    /**
     * Gets the authenticated user details
     * @return the authenticated user details
     */
    public Account getAuthenticatedAccount(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return repository.findByEmail(currentUserName).orElseThrow(AccountNotFoundException::new);
        }else{
            throw new RuntimeException("No User");
        }

    }
}
