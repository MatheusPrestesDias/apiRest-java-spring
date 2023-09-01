package br.com.dias.apiRest.service;

import br.com.dias.apiRest.data.dto.v1.security.AccountCredentialsDTO;
import br.com.dias.apiRest.data.dto.v1.security.TokenDTO;
import br.com.dias.apiRest.repository.UserRepository;
import br.com.dias.apiRest.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    public TokenDTO authenticate(AccountCredentialsDTO data) {
        var username = data.getUsername();
        try {
            authenticate(username, data.getPassword());
            var user = userRepository.findByUserName(username);

            if (user != null) {
                return jwtTokenProvider.createAccessToken(username, user.getRoles());
            } else {
                throw new UsernameNotFoundException("Username " + username + " not found!");
            }
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username/password supplied!");
        }
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
