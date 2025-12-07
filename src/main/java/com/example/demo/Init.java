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
import java.util.List;

@Slf4j
@Component
public class Init implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final HallRepository hallRepository;
    private final GenreRepository genreRepository;
    private final PerformerRepository performerRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default.password:123456}")
    private String defaultPassword;

    public Init(RoleRepository roleRepository,
                UserRepository userRepository,
                HallRepository hallRepository,
                GenreRepository genreRepository,
                PerformerRepository performerRepository,
                EventRepository eventRepository,
                PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.hallRepository = hallRepository;
        this.genreRepository = genreRepository;
        this.performerRepository = performerRepository;
        this.eventRepository = eventRepository;
        this.passwordEncoder = passwordEncoder;
        log.info("Init компонент инициализирован");
    }

    @Override
    public void run(String... args) {
        log.info("Запуск инициализации начальных данных");
        initRoles();
        initUsers();
        initHalls();
        initGenres();
        initPerformers();
        initEvents();
        log.info("Инициализация начальных данных завершена");
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            log.info("Создание базовых ролей...");
            roleRepository.saveAll(List.of(
                    new Role(UserRoles.USER),
                    new Role(UserRoles.ADMIN)
            ));
            log.info("Роли созданы: USER, ADMIN");
        } else {
            log.debug("Роли уже существуют, пропуск инициализации");
        }
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            log.info("Создание пользователей по умолчанию...");

            Role userRole = roleRepository.findByName(UserRoles.USER)
                    .orElseThrow(() -> new RuntimeException("Роль USER не найдена"));
            Role adminRole = roleRepository.findByName(UserRoles.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Роль ADMIN не найдена"));

            // Администратор
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // или defaultPassword
            admin.setEmail("admin@philharmonic.ru");
            admin.setFullName("Администратор Системы");
            admin.setRoles(List.of(adminRole));
            userRepository.save(admin);
            log.info("Создан администратор: admin");

            // Обычный пользователь 1
            User user1 = new User();
            user1.setUsername("ivanov");
            user1.setPassword(passwordEncoder.encode("user123"));
            user1.setEmail("ivanov@mail.ru");
            user1.setFullName("Иванов Иван Иванович");
            user1.setRoles(List.of(userRole));
            userRepository.save(user1);
            log.info("Создан пользователь: ivanov");

            // Обычный пользователь 2
            User user2 = new User();
            user2.setUsername("petrov");
            user2.setPassword(passwordEncoder.encode("user123"));
            user2.setEmail("petrov@mail.ru");
            user2.setFullName("Петров Петр Петрович");
            user2.setRoles(List.of(userRole));
            userRepository.save(user2);
            log.info("Создан пользователь: petrov");

            log.info("Пользователи по умолчанию созданы");
        } else {
            log.debug("Пользователи уже существуют, пропуск инициализации");
        }
    }

    private void initHalls() {
        if (hallRepository.count() == 0) {
            log.info("Создание концертных залов...");

            hallRepository.saveAll(List.of(
                    createHall("Большой зал филармонии", "ул. Ленина, 1, Москва", 1000),
                    createHall("Малый камерный зал", "ул. Пушкина, 10, Москва", 300),
                    createHall("Органный зал", "ул. Гагарина, 5, Москва", 500),
                    createHall("Зал имени Чайковского", "ул. Музыкальная, 15, Москва", 800)
            ));
            log.info("Создано {} залов", hallRepository.count());
        } else {
            log.debug("Залы уже существуют, пропуск инициализации");
        }
    }

    private Hall createHall(String name, String address, int capacity) {
        Hall hall = new Hall();
        hall.setName(name);
        hall.setAddress(address);
        hall.setCapacity(capacity);
        return hall;
    }

    private void initGenres() {
        if (genreRepository.count() == 0) {
            log.info("Создание музыкальных жанров...");

            genreRepository.saveAll(List.of(
                    createGenre("Классическая музыка"),
                    createGenre("Джаз"),
                    createGenre("Рок"),
                    createGenre("Опера"),
                    createGenre("Балет"),
                    createGenre("Народная музыка"),
                    createGenre("Электронная музыка")
            ));
            log.info("Создано {} жанров", genreRepository.count());
        } else {
            log.debug("Жанры уже существуют, пропуск инициализации");
        }
    }

    private Genre createGenre(String name) {
        Genre genre = new Genre();
        genre.setName(name);
        return genre;
    }

    private void initPerformers() {
        if (performerRepository.count() == 0) {
            log.info("Создание исполнителей...");

            Genre classical = genreRepository.findByName("Классическая музыка")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Классическая музыка' не найден"));
            Genre jazz = genreRepository.findByName("Джаз")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Джаз' не найден"));
            Genre rock = genreRepository.findByName("Рок")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Рок' не найден"));
            Genre opera = genreRepository.findByName("Опера")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Опера' не найден"));
            Genre ballet = genreRepository.findByName("Балет")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Балет' не найден"));
            Genre folk = genreRepository.findByName("Народная музыка")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Народная музыка' не найден"));
            Genre electronic = genreRepository.findByName("Электронная музыка")
                    .orElseThrow(() -> new RuntimeException("Жанр 'Электронная музыка' не найден"));

            performerRepository.saveAll(List.of(
                    createPerformer("Московский симфонический оркестр",
                            "Один из старейших и наиболее известных симфонических оркестров России. Основан в 1951 году.",
                            classical),
                    createPerformer("Джаз-бэнд \"Свинг\"",
                            "Молодой, но уже известный джазовый коллектив, исполняющий как классические стандарты, так и собственные композиции.",
                            jazz),
                    createPerformer("Группа \"Рок-Волна\"",
                            "Легендарная рок-группа, выступающая с 1990-х годов. Известна своими энергетичными концертами.",
                            rock),
                    createPerformer("Солистка Анна Петрова (сопрано)",
                            "Лауреат международных конкурсов, выпускница Московской консерватории.",
                            opera),
                    createPerformer("Балетная труппа \"Грация\"",
                            "Молодой, но уже получивший признание коллектив под руководством народного артиста России.",
                            ballet),
                    createPerformer("Ансамбль народной музыки \"Русские узоры\"",
                            "Хранители традиций русской народной музыки.",
                            folk),
                    createPerformer("Ди-джей Alex Electro",
                            "Известный исполнитель электронной музыки, участник международных фестивалей.",
                            electronic)
            ));
            log.info("Создано {} исполнителей", performerRepository.count());
        } else {
            log.debug("Исполнители уже существуют, пропуск инициализации");
        }
    }

    private Performer createPerformer(String name, String description, Genre genre) {
        Performer performer = new Performer();
        performer.setName(name);
        performer.setDescription(description);
        performer.setGenre(genre);
        return performer;
    }

    private void initEvents() {
        if (eventRepository.count() == 0) {
            log.info("Создание мероприятий...");

            Hall bigHall = hallRepository.findByName("Большой зал филармонии")
                    .orElseThrow(() -> new RuntimeException("Зал 'Большой зал филармонии' не найден"));
            Hall smallHall = hallRepository.findByName("Малый камерный зал")
                    .orElseThrow(() -> new RuntimeException("Зал 'Малый камерный зал' не найден"));
            Hall organHall = hallRepository.findByName("Органный зал")
                    .orElseThrow(() -> new RuntimeException("Зал 'Органный зал' не найден"));
            Hall tchaikovskyHall = hallRepository.findByName("Зал имени Чайковского")
                    .orElseThrow(() -> new RuntimeException("Зал 'Зал имени Чайковского' не найден"));

            Performer symphonyOrchestra = performerRepository.findByName("Московский симфонический оркестр")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Московский симфонический оркестр' не найден"));
            Performer jazzBand = performerRepository.findByName("Джаз-бэнд \"Свинг\"")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Джаз-бэнд \"Свинг\"' не найден"));
            Performer rockBand = performerRepository.findByName("Группа \"Рок-Волна\"")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Группа \"Рок-Волна\"' не найден"));
            Performer operaSinger = performerRepository.findByName("Солистка Анна Петрова (сопрано)")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Солистка Анна Петрова (сопрано)' не найден"));
            Performer balletTroupe = performerRepository.findByName("Балетная труппа \"Грация\"")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Балетная труппа \"Грация\"' не найден"));
            Performer folkEnsemble = performerRepository.findByName("Ансамбль народной музыки \"Русские узоры\"")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Ансамбль народной музыки \"Русские узоры\"' не найден"));
            Performer dj = performerRepository.findByName("Ди-джей Alex Electro")
                    .orElseThrow(() -> new RuntimeException("Исполнитель 'Ди-джей Alex Electro' не найден"));

            eventRepository.saveAll(List.of(
                    createEvent("Вечер классической музыки",
                            "В программе: симфонии Бетховена, произведения Чайковского и Моцарта. Исполняет Московский симфонический оркестр под управлением маэстро Иванова.",
                            LocalDateTime.of(2024, 12, 15, 19, 0),
                            bigHall,
                            150,
                            EventType.CONCERT,
                            "https://www.mos.ru/upload/newsfeed/newsfeed/GL(101577).jpg",
                            List.of(symphonyOrchestra)),

                    createEvent("Джазовая ночь",
                            "Лучшие джазовые стандарты в исполнении коллектива \"Свинг\". В программе произведения Луи Армстронга, Эллы Фицджеральд и современные аранжировки.",
                            LocalDateTime.of(2024, 12, 20, 20, 0),
                            smallHall,
                            50,
                            EventType.CONCERT,
                            "https://www.tg-m.ru/img/news2020/emctol_09_10_01.jpg",
                            List.of(jazzBand)),

                    createEvent("Рок-фестиваль \"Звуки весны\"",
                            "Ежегодный фестиваль рок-музыки с участием лучших коллективов страны. Хэдлайнер: группа \"Рок-Волна\".",
                            LocalDateTime.of(2024, 12, 25, 18, 0),
                            bigHall,
                            200,
                            EventType.FESTIVAL,
                            "https://www.interfax.ru/ftproot/textphotos/2023/06/02/en700.jpg",
                            List.of(rockBand)),

                    createEvent("Опера \"Евгений Онегин\"",
                            "Постановка оперы П.И. Чайковского в 3 действиях. В главных партиях: Анна Петрова и приглашенные солисты Большого театра.",
                            LocalDateTime.of(2024, 12, 28, 18, 30),
                            organHall,
                            120,
                            EventType.THEATER,
                            "https://stanmus.ru/wp-content/uploads/2020/03/CHER3604-e1651934272807.jpg",
                            List.of(operaSinger)),

                    createEvent("Балет \"Лебединое озеро\"",
                            "Классическая постановка балета П.И. Чайковского в исполнении балетной труппы \"Грация\". Хореография Мариуса Петипа.",
                            LocalDateTime.of(2025, 1, 10, 19, 0),
                            bigHall,
                            80,
                            EventType.THEATER,
                            "https://www.classicalmusicnews.ru/wp-content/uploads/2024/09/swan-lake.jpg",
                            List.of(balletTroupe)),

                    createEvent("Лекция \"История русской народной музыки\"",
                            "Встреча с музыковедом и концерт ансамбля \"Русские узоры\". В программе: народные песни и инструментальная музыка.",
                            LocalDateTime.of(2025, 1, 15, 17, 0),
                            smallHall,
                            100,
                            EventType.CINEMA,
                            "https://rgub.ru/img/news/47158-2.jpg?t=1761739262",
                            List.of(folkEnsemble)),

                    createEvent("Электронная вечеринка \"Neon Nights\"",
                            "Ночная вечеринка с лучшими диджеями. Хэдлайнер: Alex Electro. Light show, современное звуковое оборудование.",
                            LocalDateTime.of(2025, 1, 20, 22, 0),
                            tchaikovskyHall,
                            250,
                            EventType.CONCERT,
                            "https://media.istockphoto.com/id/1157545996/ru/%D1%84%D0%BE%D1%82%D0%BE/dj-%D0%B8%D0%B3%D1%80%D0%B0%D1%82%D1%8C-%D0%B8-%D1%81%D0%BC%D0%B5%D1%88%D0%B8%D0%B2%D0%B0%D1%82%D1%8C-%D0%BC%D1%83%D0%B7%D1%8B%D0%BA%D1%83-%D0%BD%D0%B0-%D0%B2%D0%B5%D1%87%D0%B5%D1%80%D0%B8%D0%BD%D0%BA%D0%B5.jpg?s=612x612&w=0&k=20&c=613Z_85w1RPN3CIIosWfsRkJWBTfB0AC5RmqbJwu0G4=",
                            List.of(dj))
            ));
            log.info("Создано {} мероприятий", eventRepository.count());
        } else {
            log.debug("Мероприятия уже существуют, пропуск инициализации");
        }
    }

    private Event createEvent(String title, String description, LocalDateTime dateTime,
                              Hall hall, int availableSeats, EventType eventType,
                              String imageUrl, List<Performer> performers) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setDateTime(dateTime);
        event.setHall(hall);
        event.setAvailableSeats(availableSeats);
        event.setEventType(eventType);
        event.setImageUrl(imageUrl);
        event.setPerformers(performers);
        return event;
    }
}