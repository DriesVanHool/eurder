package com.switchfully.eurder.services;

import com.switchfully.eurder.domain.User;
import com.switchfully.eurder.domain.exceptions.UnauthorizedException;
import com.switchfully.eurder.domain.exceptions.UnknownUserException;
import com.switchfully.eurder.domain.exceptions.WrongPasswordException;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.domain.security.Feature;
import com.switchfully.eurder.domain.security.UsernamePassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class SecurityService {
    private final Logger logger = LoggerFactory.getLogger(SecurityService.class);
    private final UserRepository repository;

    public SecurityService(UserRepository repository) {
        this.repository = repository;
    }

    public void validateAuthorisation(String authorization, Feature feature) throws RuntimeException {
        UsernamePassword usernamePassword = getUseramePassword(authorization);
        User user = repository.getUserById(usernamePassword.getUsername()).orElseThrow(UnknownUserException::new);

        if (!user.doesPasswordMatch(usernamePassword.getPassword())) {
            logger.info("Wrong password");
            throw new WrongPasswordException();
        }
        if (!user.hasAccessTo(feature)) {
            logger.info("This user doesn't have the correct role or access to features");
            throw new UnauthorizedException();
        }

    }

    private UsernamePassword getUseramePassword(String authorization) throws UnauthorizedException {
        try {
            String decodedToUsernameAndPassword = new String(Base64.getDecoder().decode(authorization.substring("Basic ".length())));
            String username = decodedToUsernameAndPassword.split(":")[0];
            String password = decodedToUsernameAndPassword.split(":")[1];
            return new UsernamePassword(username, password);
        }catch (RuntimeException ex){
            logger.info("Missing authorization value in header.");
            throw new UnauthorizedException();
        }
    }
}
