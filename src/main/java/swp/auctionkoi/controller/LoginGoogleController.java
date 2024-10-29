package swp.auctionkoi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.auctionkoi.models.GoogleRoot;
import swp.auctionkoi.models.User;
import swp.auctionkoi.models.enums.Role;
import swp.auctionkoi.repository.UserRepository;
import swp.auctionkoi.service.user.UserService;
import swp.auctionkoi.service.user.impl.UserServiceImpl;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class LoginGoogleController {


    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    @GetMapping("/hello")
    public Map<String, Object> currentUser(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        Map<String, Object> attributes = oAuth2AuthenticationToken.getPrincipal().getAttributes();
        GoogleRoot googleUser = toPerson(attributes);
        String email = googleUser.getEmail();
        String avatarUrl = googleUser.getPicture();
        String name = googleUser.getName();
        // Kiểm tra nếu email đã tồn tại trong hệ thống
        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isEmpty()) {
            // Tạo mới user nếu chưa tồn tại
            User newUser = User.builder()
                    .username(email)       // Sử dụng email làm username
                    .password(null)        // Vì đăng nhập qua Google, password có thể để null
                    .fullname(name)
                    .avatarUrl(avatarUrl)  // Lấy avatar từ Google
                    .isActive(true)
                    .role(Role.MEMBER)     // Đặt role mặc định là MEMBER, có thể thay đổi tùy yêu cầu
                    .build();

            userRepository.save(newUser);
        }

        return attributes;
    }

    public GoogleRoot toPerson(Map<String, Object> map) {
        if (map == null){
            return null;
        }
        GoogleRoot googleRoot = new GoogleRoot();
        googleRoot.setEmail(map.get("email").toString());
        googleRoot.setName(map.get("name").toString());
        googleRoot.setPicture(map.get("picture").toString());
        return googleRoot;
    }
}
