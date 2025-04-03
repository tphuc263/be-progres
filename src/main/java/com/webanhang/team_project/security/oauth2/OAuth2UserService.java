package com.webanhang.team_project.security.oauth2;

import com.webanhang.team_project.enums.UserRole;
import com.webanhang.team_project.model.Role;
import com.webanhang.team_project.model.User;
import com.webanhang.team_project.repository.RoleRepository;
import com.webanhang.team_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Xử lý thông tin người dùng sau khi đăng nhập OAuth2 thành công
 * Hỗ trợ đăng nhập qua Google và GitHub
 */
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // Lấy registrationId từ userRequest
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();

        OAuth2UserInfo userInfo;
        if ("google".equals(registrationId)) {
            userInfo = new GoogleOAuth2UserInfo(attributes);
        } else if ("github".equals(registrationId)) {
            userInfo = new GitHubOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("Sorry! Login with " + registrationId + " is not supported yet.");
        }

        return processOAuth2User(userInfo, oauth2User);
    }

    /**
     * Xử lý thông tin người dùng OAuth2
     * Tạo tài khoản mới nếu chưa tồn tại trong hệ thống
     */
    private OAuth2User processOAuth2User(OAuth2UserInfo userInfo, OAuth2User oauth2User) {
        // Check if user exists
        User user = userRepository.findByEmail(userInfo.getEmail());

        if (user == null) {
            // Create new user
            user = new User();
            user.setEmail(userInfo.getEmail());
            user.setFirstName(userInfo.getName());
            user.setPassword("OAUTH2_USER"); // You might want to generate a random password

            // Set default role
            Role userRole = roleRepository.findByName(UserRole.CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            user.setRole(userRole);

            user = userRepository.save(user);
        }

        return oauth2User;
    }
}