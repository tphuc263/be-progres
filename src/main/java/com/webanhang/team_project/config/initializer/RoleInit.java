package com.webanhang.team_project.config.initializer;

import com.webanhang.team_project.enums.UserRole;
import com.webanhang.team_project.model.Role;
import com.webanhang.team_project.model.User;
import com.webanhang.team_project.repository.RoleRepository;
import com.webanhang.team_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleInit implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initRoles();
        initAdminAccount();
    }

    // khởi tạo role trong database
    public void initRoles() {
        List<UserRole> roles = Arrays.asList(UserRole.ADMIN, UserRole.SELLER, UserRole.CUSTOMER);
        for (UserRole userRole : roles) {
            if (roleRepository.findByName(userRole).isEmpty()) {
                roleRepository.save(new Role(userRole));
                log.info("Created role: {}", userRole);
            }
        }
    }


    // khởi tạo tài khoản admin
    private void initAdminAccount() {
        if (!userRepository.existsByEmail("duongtriphucpd@gmail.com")) {
            Role adminRole = roleRepository.findByName(UserRole.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User admin = new User(
                    "Admin",
                    "User",
                    "duongtriphucpd@gmail.com",
                    passwordEncoder.encode("admin123"),
                    adminRole
            );
            admin.setActive(true);
            User savedAdmin = userRepository.save(admin);

            log.info("Created admin account with email: {}", "duongtriphucpd@gmail.com");
        } else {
            log.info("Admin account already exists");
        }
    }
}

