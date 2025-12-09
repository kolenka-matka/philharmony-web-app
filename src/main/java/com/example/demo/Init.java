package com.example.demo;

import com.example.demo.models.entities.Role;
import com.example.demo.models.entities.User;
import com.example.demo.models.enums.UserRoles;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class Init implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultPassword;

    public Init(UserRepository userRepository,
                UserRoleRepository userRoleRepository,
                PasswordEncoder passwordEncoder,
                @Value("${app.default.password}") String defaultPassword) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultPassword = defaultPassword;
        log.info("Init компонент инициализирован. defaultPassword = '{}'", defaultPassword);
    }

    @Override
    public void run(String... args) {
        log.info("=== НАЧАЛО ИНИЦИАЛИЗАЦИИ ===");

        try {
            initRoles();
            initUsers();
            log.info("=== ИНИЦИАЛИЗАЦИЯ УСПЕШНО ЗАВЕРШЕНА ===");
        } catch (Exception e) {
            log.error("=== ОШИБКА ПРИ ИНИЦИАЛИЗАЦИИ ===", e);
            throw e; // Перебрасываем исключение дальше
        }
    }

    private void initRoles() {
        log.info("Проверка ролей в базе... count = {}", userRoleRepository.count());

        if (userRoleRepository.count() == 0) {
            log.info("Создание базовых ролей...");

            Role adminRole = new Role(UserRoles.ADMIN);
            Role userRole = new Role(UserRoles.USER);

            userRoleRepository.saveAll(List.of(adminRole, userRole));
            log.info("Роли созданы: ADMIN, USER");

            // Проверяем, что сохранилось
            List<Role> allRoles = userRoleRepository.findAll();
            log.info("Всего ролей в базе: {}", allRoles.size());
            allRoles.forEach(role -> log.info("Роль: {}", role.getName()));
        } else {
            log.info("Роли уже существуют, пропуск инициализации");
            List<Role> existingRoles = userRoleRepository.findAll();
            existingRoles.forEach(role -> log.info("Существующая роль: {}", role.getName()));
        }
    }

    private void initUsers() {
        log.info("Проверка пользователей в базе... count = {}", userRepository.count());

        if (userRepository.count() == 0) {
            log.info("Создание пользователей по умолчанию...");

            initAdmin();
            initNormalUser();

            log.info("Пользователи по умолчанию созданы");
            log.info("Всего пользователей: {}", userRepository.count());
        } else {
            log.info("Пользователи уже существуют, пропуск инициализации");
        }
    }

    private void initAdmin() {
        log.info("Создание администратора...");

        var adminRole = userRoleRepository
                .findRoleByName(UserRoles.ADMIN)
                .orElseThrow(() -> {
                    log.error("Роль ADMIN не найдена!");
                    return new RuntimeException("Роль ADMIN не найдена в базе данных");
                });

        String encodedPassword = passwordEncoder.encode(defaultPassword);
        log.info("Пароль администратора закодирован: {}", encodedPassword.substring(0, Math.min(20, encodedPassword.length())) + "...");

        var adminUser = new User(
                "admin",
                encodedPassword,
                "admin@example.com",
                "Admin Adminovich",
                30
        );
        adminUser.setRoles(List.of(adminRole));

        userRepository.save(adminUser);
        log.info("Создан администратор: admin, email: {}, возраст: {}",
                adminUser.getEmail(), adminUser.getAge());
    }

    private void initNormalUser() {
        log.info("Создание обычного пользователя...");

        var userRole = userRoleRepository
                .findRoleByName(UserRoles.USER)
                .orElseThrow(() -> {
                    log.error("Роль USER не найдена!");
                    return new RuntimeException("Роль USER не найдена в базе данных");
                });

        String encodedPassword = passwordEncoder.encode(defaultPassword);
        log.info("Пароль пользователя закодирован: {}", encodedPassword.substring(0, Math.min(20, encodedPassword.length())) + "...");

        var normalUser = new User(
                "user",
                encodedPassword,
                "user@example.com",
                "User Userovich",
                22
        );
        normalUser.setRoles(List.of(userRole));

        userRepository.save(normalUser);
        log.info("Создан обычный пользователь: user, email: {}, возраст: {}",
                normalUser.getEmail(), normalUser.getAge());
    }
}