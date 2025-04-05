//package com.webanhang.team_project.config.initializer;
//
//import com.webanhang.team_project.enums.UserRole;
//import com.webanhang.team_project.model.Role;
//import com.webanhang.team_project.repository.RoleRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class RoleInit implements CommandLineRunner {
//    private final RoleRepository roleRepository;
//
//    @Override
//    public void run(String... args) {
//        initRoles();
//    }
//
//    // khởi tạo role trong database
//    public void initRoles() {
//        List<UserRole> roles = Arrays.asList(UserRole.ADMIN, UserRole.SELLER, UserRole.CUSTOMER);
//        for (UserRole userRole : roles) {
//            if (roleRepository.findByName(userRole).isEmpty()) {
//                roleRepository.save(new Role(userRole));
//                log.info("Created role: {}", userRole);
//            }
//        }
//    }
//}
//
