package com.example.demo;

import com.example.demo.models.entities.*;
import com.example.demo.models.enums.EventType;
import com.example.demo.models.enums.UserRoles;
import com.example.demo.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class Init implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final GenreRepository genreRepository;
    private final HallRepository hallRepository;
    private final PerformerRepository performerRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultPassword;

    public Init(UserRepository userRepository,
                UserRoleRepository userRoleRepository,
                GenreRepository genreRepository,
                HallRepository hallRepository,
                PerformerRepository performerRepository,
                EventRepository eventRepository,
                PasswordEncoder passwordEncoder,
                @Value("${app.default.password}") String defaultPassword) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.genreRepository = genreRepository;
        this.hallRepository = hallRepository;
        this.performerRepository = performerRepository;
        this.eventRepository = eventRepository;
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
            initHalls();
            initGenres();
            initPerformers();
            initEvents();
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

    private void initHalls() {
        log.info("Проверка залов в базе... count = {}", hallRepository.count());

        if (hallRepository.count() == 0) {
            log.info("Создание залов...");

            Hall hall1 = new Hall();
            hall1.setName("Большой зал филармонии");
            hall1.setAddress("ул. Ленина, 1, Москва");
            hall1.setCapacity(1000);

            Hall hall2 = new Hall();
            hall2.setName("Малый камерный зал");
            hall2.setAddress("ул. Пушкина, 10, Москва");
            hall2.setCapacity(300);

            Hall hall3 = new Hall();
            hall3.setName("Органный зал");
            hall3.setAddress("ул. Гагарина, 5, Москва");
            hall3.setCapacity(500);

            Hall hall4 = new Hall();
            hall4.setName("Зал имени Чайковского");
            hall4.setAddress("ул. Музыкальная, 15, Москва");
            hall4.setCapacity(800);

            hallRepository.saveAll(List.of(hall1, hall2, hall3, hall4));
            log.info("Создано 4 зала");
        } else {
            log.info("Залы уже существуют, пропуск инициализации");
        }
    }

    private void initGenres() {
        log.info("Проверка жанров в базе... count = {}", genreRepository.count());

        if (genreRepository.count() == 0) {
            log.info("Создание жанров...");

            Genre genre1 = new Genre();
            genre1.setName("Классическая музыка");

            Genre genre2 = new Genre();
            genre2.setName("Джаз");

            Genre genre3 = new Genre();
            genre3.setName("Рок");

            Genre genre4 = new Genre();
            genre4.setName("Опера");

            Genre genre5 = new Genre();
            genre5.setName("Балет");

            Genre genre6 = new Genre();
            genre6.setName("Народная музыка");

            Genre genre7 = new Genre();
            genre7.setName("Электронная музыка");

            genreRepository.saveAll(List.of(genre1, genre2, genre3, genre4, genre5, genre6, genre7));
            log.info("Создано 7 жанров");
        } else {
            log.info("Жанры уже существуют, пропуск инициализации");
        }
    }

    private void initPerformers() {
        log.info("Проверка исполнителей в базе... count = {}", performerRepository.count());

        if (performerRepository.count() == 0) {
            log.info("Создание исполнителей...");

            // Сначала получаем жанры
            Genre genre1 = genreRepository.findByName("Классическая музыка")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Классическая музыка' не найден"));
            Genre genre2 = genreRepository.findByName("Джаз")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Джаз' не найден"));
            Genre genre3 = genreRepository.findByName("Рок")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Рок' не найден"));
            Genre genre4 = genreRepository.findByName("Опера")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Опера' не найден"));
            Genre genre5 = genreRepository.findByName("Балет")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Балет' не найден"));
            Genre genre6 = genreRepository.findByName("Народная музыка")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Народная музыка' не найден"));
            Genre genre7 = genreRepository.findByName("Электронная музыка")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Электронная музыка' не найден"));

            Performer perf1 = new Performer();
            perf1.setName("Московский симфонический оркестр");
            perf1.setDescription("Один из старейших и наиболее известных симфонических оркестров России. Основан в 1951 году.");
            perf1.setGenre(genre1);

            Performer perf2 = new Performer();
            perf2.setName("Джаз-бэнд \"Свинг\"");
            perf2.setDescription("Молодой, но уже известный джазовый коллектив, исполняющий как классические стандарты, так и собственные композиции.");
            perf2.setGenre(genre2);

            Performer perf3 = new Performer();
            perf3.setName("Группа \"Рок-Волна\"");
            perf3.setDescription("Легендарная рок-группа, выступающая с 1990-х годов. Известна своими энергетичными концертами.");
            perf3.setGenre(genre3);

            Performer perf4 = new Performer();
            perf4.setName("Солистка Анна Петрова (сопрано)");
            perf4.setDescription("Лауреат международных конкурсов, выпускница Московской консерватории.");
            perf4.setGenre(genre4);

            Performer perf5 = new Performer();
            perf5.setName("Балетная труппа \"Грация\"");
            perf5.setDescription("Молодой, но уже получивший признание коллектив под руководством народного артиста России.");
            perf5.setGenre(genre5);

            Performer perf6 = new Performer();
            perf6.setName("Ансамбль народной музыки \"Русские узоры\"");
            perf6.setDescription("Хранители традиций русской народной музыки.");
            perf6.setGenre(genre6);

            Performer perf7 = new Performer();
            perf7.setName("Ди-джей Alex Electro");
            perf7.setDescription("Известный исполнитель электронной музыки, участник международных фестивалей.");
            perf7.setGenre(genre7);

            performerRepository.saveAll(List.of(perf1, perf2, perf3, perf4, perf5, perf6, perf7));
            log.info("Создано 7 исполнителей");
        } else {
            log.info("Исполнители уже существуют, пропуск инициализации");
        }
    }
    private void initEvents() {
        log.info("Проверка мероприятий в базе... count = {}", eventRepository.count());

        if (eventRepository.count() == 0) {
            log.info("Создание мероприятий...");

            // Получаем залы
            Hall hall1 = hallRepository.findByName("Большой зал филармонии")
                    .orElseThrow(() -> new RuntimeException("Зал 'Большой зал филармонии' не найден"));
            Hall hall2 = hallRepository.findByName("Малый камерный зал")
                    .orElseThrow(() -> new RuntimeException("Зал 'Малый камерный зал' не найден"));
            Hall hall3 = hallRepository.findByName("Органный зал")
                    .orElseThrow(() -> new RuntimeException("Зал 'Органный зал' не найден"));
            Hall hall4 = hallRepository.findByName("Зал имени Чайковского")
                    .orElseThrow(() -> new RuntimeException("Зал 'Зал имени Чайковского' не найден"));

            // Получаем жанры
            Genre genre1 = genreRepository.findByName("Классическая музыка")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Классическая музыка' не найден"));
            Genre genre2 = genreRepository.findByName("Джаз")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Джаз' не найден"));
            Genre genre3 = genreRepository.findByName("Рок")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Рок' не найден"));
            Genre genre4 = genreRepository.findByName("Опера")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Опера' не найден"));
            Genre genre5 = genreRepository.findByName("Балет")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Балет' не найден"));
            Genre genre6 = genreRepository.findByName("Народная музыка")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Народная музыка' не найден"));
            Genre genre7 = genreRepository.findByName("Электронная музыка")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Электронная музыка' не найден"));

            // Получаем исполнителей
            Performer perf1 = performerRepository.findByName("Московский симфонический оркестр")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Московский симфонический оркестр' не найден"));
            Performer perf2 = performerRepository.findByName("Джаз-бэнд \"Свинг\"")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Джаз-бэнд \"Свинг\"' не найден"));
            Performer perf3 = performerRepository.findByName("Группа \"Рок-Волна\"")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Группа \"Рок-Волна\"' не найден"));
            Performer perf4 = performerRepository.findByName("Солистка Анна Петрова (сопрано)")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Солистка Анна Петрова (сопрано)' не найден"));
            Performer perf5 = performerRepository.findByName("Балетная труппа \"Грация\"")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Балетная труппа \"Грация\"' не найден"));
            Performer perf6 = performerRepository.findByName("Ансамбль народной музыки \"Русские узоры\"")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Ансамбль народной музыки \"Русские узоры\"' не найден"));
            Performer perf7 = performerRepository.findByName("Ди-джей Alex Electro")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Ди-джей Alex Electro' не найден"));

            // Создаем мероприятия (согласно data.sql)
            // Event 1: Вечер классической музыки - perf1
            Event event1 = new Event();
            event1.setTitle("Вечер классической музыки");
            event1.setDescription("В программе: симфонии Бетховена, произведения Чайковского и Моцарта. Исполняет Московский симфонический оркестр под управлением маэстро Иванова.");
            event1.setDateTime(LocalDateTime.of(2024, 12, 15, 19, 0));
            event1.setHall(hall1);
            event1.setAvailableSeats(150);
            event1.setEventType(EventType.CONCERT);
            event1.setGenre(genre1);
            event1.setImageUrl("https://www.mos.ru/upload/newsfeed/newsfeed/GL(101577).jpg");
            event1.setPerformers(new ArrayList<>(List.of(perf1)));

            // Event 2: Джазовая ночь - perf2
            Event event2 = new Event();
            event2.setTitle("Джазовая ночь");
            event2.setDescription("Лучшие джазовые стандарты в исполнении коллектива \"Свинг\". В программе произведения Луи Армстронга, Эллы Фицджеральд и современные аранжировки.");
            event2.setDateTime(LocalDateTime.of(2024, 12, 20, 20, 0));
            event2.setHall(hall2);
            event2.setAvailableSeats(50);
            event2.setEventType(EventType.CONCERT);
            event2.setGenre(genre2);
            event2.setImageUrl("https://www.tg-m.ru/img/news2020/emctol_09_10_01.jpg");
            event2.setPerformers(new ArrayList<>(List.of(perf2)));

            // Event 3: Рок-фестиваль - perf3
            Event event3 = new Event();
            event3.setTitle("Рок-фестиваль \"Звуки весны\"");
            event3.setDescription("Ежегодный фестиваль рок-музыки с участием лучших коллективов страны. Хэдлайнер: группа \"Рок-Волна\".");
            event3.setDateTime(LocalDateTime.of(2024, 12, 25, 18, 0));
            event3.setHall(hall1);
            event3.setAvailableSeats(200);
            event3.setEventType(EventType.FESTIVAL);
            event3.setGenre(genre3);
            event3.setImageUrl("https://www.interfax.ru/ftproot/textphotos/2023/06/02/en700.jpg");
            event3.setPerformers(new ArrayList<>(List.of(perf3)));

            // Event 4: Опера - perf4
            Event event4 = new Event();
            event4.setTitle("Опера \"Евгений Онегин\"");
            event4.setDescription("Постановка оперы П.И. Чайковского в 3 действиях. В главных партиях: Анна Петрова и приглашенные солисты Большого театра.");
            event4.setDateTime(LocalDateTime.of(2024, 12, 28, 18, 30));
            event4.setHall(hall3);
            event4.setAvailableSeats(120);
            event4.setEventType(EventType.THEATER);
            event4.setGenre(genre4);
            event4.setImageUrl("https://stanmus.ru/wp-content/uploads/2020/03/CHER3604-e1651934272807.jpg");
            event4.setPerformers(new ArrayList<>(List.of(perf4)));

            // Event 5: Балет - perf5
            Event event5 = new Event();
            event5.setTitle("Балет \"Лебединое озеро\"");
            event5.setDescription("Классическая постановка балета П.И. Чайковского в исполнении балетной труппы \"Грация\". Хореография Мариуса Петипа.");
            event5.setDateTime(LocalDateTime.of(2025, 1, 10, 19, 0));
            event5.setHall(hall1);
            event5.setAvailableSeats(80);
            event5.setEventType(EventType.THEATER);
            event5.setGenre(genre5);
            event5.setImageUrl("https://www.classicalmusicnews.ru/wp-content/uploads/2024/09/swan-lake.jpg");
            event5.setPerformers(new ArrayList<>(List.of(perf5)));

            // Event 6: Лекция - perf6
            Event event6 = new Event();
            event6.setTitle("Лекция \"История русской народной музыки\"");
            event6.setDescription("Встреча с музыковедом и концерт ансамбля \"Русские узоры\". В программе: народные песни и инструментальная музыка.");
            event6.setDateTime(LocalDateTime.of(2025, 1, 15, 17, 0));
            event6.setHall(hall2);
            event6.setAvailableSeats(100);
            event6.setEventType(EventType.CINEMA);
            event6.setGenre(genre6);
            event6.setImageUrl("https://rgub.ru/img/news/47158-2.jpg?t=1761739262");
            event6.setPerformers(new ArrayList<>(List.of(perf6)));

            // Event 7: Электронная вечеринка - perf7
            Event event7 = new Event();
            event7.setTitle("Электронная вечеринка \"Neon Nights\"");
            event7.setDescription("Ночная вечеринка с лучшими диджеями. Хэдлайнер: Alex Electro. Light show, современное звуковое оборудование.");
            event7.setDateTime(LocalDateTime.of(2025, 1, 20, 22, 0));
            event7.setHall(hall4);
            event7.setAvailableSeats(250);
            event7.setEventType(EventType.CONCERT);
            event7.setGenre(genre7);
            event7.setImageUrl("https://media.istockphoto.com/id/1157545996/ru/%D1%84%D0%BE%D1%82%D0%BE/dj-%D0%B8%D0%B3%D1%80%D0%B0%D1%82%D1%8C-%D0%B8-%D1%81%D0%BC%D0%B5%D1%88%D0%B8%D0%B2%D0%B0%D1%82%D1%8C-%D0%BC%D1%83%D0%B7%D1%8B%D0%BA%D1%83-%D0%BD%D0%B0-%D0%B2%D0%B5%D1%87%D0%B5%D1%80%D0%B8%D0%BD%D0%BA%D0%B5.jpg?s=612x612&w=0&k=20&c=613Z_85w1RPN3CIIosWfsRkJWBTfB0AC5RmqbJwu0G4=");
            event7.setPerformers(new ArrayList<>(List.of(perf7)));

            // Event 8: Мастер-класс - perf4
            Event event8 = new Event();
            event8.setTitle("Мастер-класс по оперному вокалу");
            event8.setDescription("Для начинающих и профессиональных певцов. Проводит Анна Петрова. Разбор техники дыхания, постановки голоса, работа над репертуаром.");
            event8.setDateTime(LocalDateTime.of(2025, 1, 25, 15, 0));
            event8.setHall(hall2);
            event8.setAvailableSeats(30);
            event8.setEventType(EventType.MASTERCLASS);
            event8.setGenre(genre4);
            event8.setImageUrl("https://voicestudio.ru/wp-content/uploads/2020/10/discipliny-05-e1648054692140-1280x675.png");
            event8.setPerformers(new ArrayList<>(List.of(perf4)));

            // Event 9: Концерт органной музыки - perf1
            Event event9 = new Event();
            event9.setTitle("Концерт органной музыки");
            event9.setDescription("Произведения И.С. Баха, В.А. Моцарта, С. Франка в исполнении лауреата международных конкурсов.");
            event9.setDateTime(LocalDateTime.of(2025, 2, 5, 19, 30));
            event9.setHall(hall3);
            event9.setAvailableSeats(90);
            event9.setEventType(EventType.CONCERT);
            event9.setGenre(genre1);
            event9.setImageUrl("https://cdn.culture.ru/images/98631b1b-ff32-5ee6-ad1e-b648a6d75eae");
            event9.setPerformers(new ArrayList<>(List.of(perf1)));

            // Event 10: Спектакль - perf1
            Event event10 = new Event();
            event10.setTitle("Спектакль \"Ревизор\"");
            event10.setDescription("Современная интерпретация классической комедии Н.В. Гоголя. Молодой режиссер, нестандартный подход.");
            event10.setDateTime(LocalDateTime.of(2025, 2, 12, 18, 0));
            event10.setHall(hall4);
            event10.setAvailableSeats(180);
            event10.setEventType(EventType.THEATER);
            event10.setGenre(null); // Без жанра как в SQL
            event10.setImageUrl("https://s1.afisha.ru/mediastorage/99/61/30360c73fc1944f8bfb29ecc6199.jpg");
            event10.setPerformers(new ArrayList<>(List.of(perf1)));

            // Event 11: Спортивное шоу - perf6
            Event event11 = new Event();
            event11.setTitle("Спортивное шоу \"Сила духа\"");
            event11.setDescription("Зрелищное спортивное представление с участием чемпионов мира по гимнастике и акробатике.");
            event11.setDateTime(LocalDateTime.of(2025, 2, 20, 19, 0));
            event11.setHall(hall1);
            event11.setAvailableSeats(220);
            event11.setEventType(EventType.SPORT);
            event11.setGenre(null); // Без жанра как в SQL
            event11.setImageUrl("https://s-cdn.sportbox.ru/images/styles/upload/fp_fotos/6a/a1/6ab815a126bd4128677d2383a1ae5b5d668644ba1d9b7123615716.jpg");
            event11.setPerformers(new ArrayList<>(List.of(perf6)));

            // Event 12: Кинопоказ - perf1
            Event event12 = new Event();
            event12.setTitle("Кинопоказ \"Великие композиторы\"");
            event12.setDescription("Документальный фильм о жизни и творчестве великих композиторов с обсуждением.");
            event12.setDateTime(LocalDateTime.of(2025, 3, 5, 18, 0));
            event12.setHall(hall2);
            event12.setAvailableSeats(120);
            event12.setEventType(EventType.CINEMA);
            event12.setGenre(genre1);
            event12.setImageUrl("https://mf.b37mrtl.ru/rbthmedia/images/2023.08/original/64ccc0a7fdff36569b4a4ca3.jpg");
            event12.setPerformers(new ArrayList<>(List.of(perf1)));

            // Сохраняем все мероприятия
            eventRepository.saveAll(List.of(event1, event2, event3, event4, event5, event6,
                    event7, event8, event9, event10, event11, event12));
            log.info("Создано 12 мероприятий");

        } else {
            log.info("Мероприятия уже существуют, пропуск инициализации");
        }
    }
}