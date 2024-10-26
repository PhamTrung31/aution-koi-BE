package swp.auctionkoi.service.authentication.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import swp.auctionkoi.dto.request.AuthenticationRequest;
import swp.auctionkoi.dto.request.IntrospectRequest;
import swp.auctionkoi.dto.request.LogoutRequest;
import swp.auctionkoi.dto.request.RefreshRequest;
import swp.auctionkoi.dto.respone.AuthenticationResponse;
import swp.auctionkoi.dto.respone.IntrospectResponse;
import swp.auctionkoi.exception.AppException;
import swp.auctionkoi.exception.ErrorCode;
import swp.auctionkoi.models.InvalidatedToken;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.InvalidatedTokenRepository;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.authentication.AuthenticationService;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @Override
    public AuthenticationResponse authenticated(AuthenticationRequest authenticationRequest) {

        var user = userRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(authenticated)
                .build();
    }


    private String generateToken(User user) {

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("http://localhost:8081/auctionkoi")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .claim("roles", user.getRole())

                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token = introspectRequest.getToken();

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }

    private String buildScope(User user) {
        Role role = user.getRole();
        if (role != null) {
            return role.name();
        }
        return "";
    }


    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

//        int jwtIdHash = signedJWT.getJWTClaimsSet().getJWTID().hashCode();
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();

        if (invalidatedTokenRepository.existsById(jwtId)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());

        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
        int jwtIdHash = signToken.getJWTClaimsSet().getJWTID().hashCode();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtIdHash)
                .expiryTime(expiryTime.toInstant())
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

//    @Override
//    public AuthenticationResponse refreshToken(RefreshRequest request)
//            throws ParseException, JOSEException {
//        var signedJWT = verifyToken(request.getToken());
//        // Sử dụng hash của JWT ID (jit) để làm unique key cho token
//        String jit = signedJWT.getJWTClaimsSet().getJWTID();
//        var expirytime = signedJWT.getJWTClaimsSet().getExpirationTime();
//
////        // Kiểm tra xem token này đã bị vô hiệu hóa hay chưa
//        if (invalidatedTokenRepository.existsById(jit)) {
//            // Nếu token đã bị làm mới (có trong bảng invalidated), trả về lỗi 401
//            throw new AppException(ErrorCode.UNAUTHENTICATED);
//        }
//
//        // Nếu token chưa bị vô hiệu hóa, lưu token này vào bảng invalidated tokens
//        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
//                .id(jit)
//                .expiryTime(expirytime.toInstant())
//                .build();
//
//        // Lưu token vào bảng invalidated để ngăn việc refresh lần nữa
//        invalidatedTokenRepository.save(invalidatedToken);
//
//        // Lấy thông tin người dùng từ token đã được xác thực
//        var username = signedJWT.getJWTClaimsSet().getSubject();
//        var user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        // Tạo token mới cho người dùng
//        var token = generateToken(user);
//
//        // Trả về token mới và thông tin xác thực
//        return AuthenticationResponse.builder()
//                .token(token)
//                .authenticated(true)
//                .build();
//    }


}
