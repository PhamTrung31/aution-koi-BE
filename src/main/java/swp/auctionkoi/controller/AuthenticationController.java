package swp.auctionkoi.controller;

import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.auctionkoi.dto.request.ApiResponse;
import swp.auctionkoi.dto.request.AuthenticationRequest;
import swp.auctionkoi.dto.request.IntrospectRequest;
import swp.auctionkoi.dto.respone.AuthenticationResponse;
import swp.auctionkoi.dto.respone.IntrospectResponse;
import swp.auctionkoi.service.authentication.AuthenticationService;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        var result = authenticationService.authenticated(authenticationRequest);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        var result = authenticationService.introspect(introspectRequest);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

}
