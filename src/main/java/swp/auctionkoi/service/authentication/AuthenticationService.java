package swp.auctionkoi.service.authentication;

import com.nimbusds.jose.JOSEException;
import org.springframework.stereotype.Service;
import swp.auctionkoi.dto.request.AuthenticationRequest;
import swp.auctionkoi.dto.request.IntrospectRequest;
import swp.auctionkoi.dto.respone.AuthenticationResponse;
import swp.auctionkoi.dto.respone.IntrospectResponse;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticated(AuthenticationRequest authenticationRequest);
    IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException;
}
