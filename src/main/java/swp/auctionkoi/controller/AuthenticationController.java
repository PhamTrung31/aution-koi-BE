package swp.auctionkoi.controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.auctionkoi.dto.respone.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.auctionkoi.dto.request.LogoutRequest;
import swp.auctionkoi.dto.request.AuthenticationRequest;
import swp.auctionkoi.dto.request.IntrospectRequest;
import swp.auctionkoi.dto.respone.AuthenticationResponse;
import swp.auctionkoi.dto.respone.IntrospectResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.User;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.authentication.AuthenticationService;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;
    UserRepository userRepository;

    @Value("${jwt.signerKey}")

    @NonFinal
    protected String SIGNER_KEY;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        var result = authenticationService.authenticated(authenticationRequest);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest introspectRequest)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(introspectRequest);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request)
            throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " from the token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Validate the token
        try {
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Verify token
            boolean verified = signedJWT.verify(verifier);
            if (!verified) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Extract username from token
            String username = signedJWT.getJWTClaimsSet().getSubject();

            // Fetch user from repository
            User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

//    @PostMapping("/refresh")
//    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest authenticationRequest) throws ParseException, JOSEException {
//        var result = authenticationService.refreshToken(authenticationRequest);
//
//        return ApiResponse.<AuthenticationResponse>builder()
//                .result(result)
//                .build();
//    }




}
