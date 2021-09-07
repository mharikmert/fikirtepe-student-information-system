package com.fikirtepe.app.web;

import com.fikirtepe.app.model.User;
import com.fikirtepe.app.security.JwtTokenUtil;
import com.fikirtepe.app.service.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    public void setAuthenticationService(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, JwtUserDetailsService jwtUserDetailsService){
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

//    @CrossOrigin(origins = "https://localhost:4200")
    @RequestMapping(value = "/api/auth", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthToken(@RequestBody User user) throws Exception{
        authenticate(user.getUsername(), user.getPassword());
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        logger.info(token);

        return ResponseEntity.ok(token);
    }

    private void authenticate(String username, String password) throws Exception{
        Objects.requireNonNull(username, password);
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }catch (DisabledException ex){
            throw new Exception("disabled user", ex);
        }catch (BadCredentialsException ex){
            throw new BadCredentialsException("invalid credentials", ex);
        }
    }
}
