package com.example.backend.seed;

import com.example.backend.entities.*;
import com.example.backend.types.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@org.springframework.context.annotation.Profile("load-test")
public class LoadTestDataSeeder implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.load-test.seed.apartments:100}")
    private int apartmentsCount;

    @Value("${app.load-test.seed.users-per-apartment:4}")
    private int usersPerApartment;

    @Value("${app.load-test.seed.tasks-per-apartment:30}")
    private int tasksPerApartment;

    @Value("${app.load-test.seed.buyings-per-apartment:60}")
    private int buyingsPerApartment;

    @Value("${app.load-test.seed.expenses-per-apartment:150}")
    private int expensesPerApartment;

    @Value("${app.load-test.seed.events-per-apartment:10}")
    private int eventsPerApartment;

    @Value("${app.load-test.seed.notifications-per-apartment:50}")
    private int notificationsPerApartment;

    public LoadTestDataSeeder(ObjectProvider<PasswordEncoder> passwordEncoderProvider) {
        this.passwordEncoder = passwordEncoderProvider.getIfAvailable(BCryptPasswordEncoder::new);
    }

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("LOAD TEST SEEDER STARTED");

        if (alreadySeeded()) {
            System.out.println("[LoadTestDataSeeder] Load-test data already exists. Seeder skipped.");
            return;
        }

        System.out.println("[LoadTestDataSeeder] Seeding database...");

        List<Apartment> apartments = new ArrayList<>();
        List<User> users = new ArrayList<>();
        List<Profile> profiles = new ArrayList<>();
        List<List<Profile>> profilesByApartment = new ArrayList<>();

        createApartments(apartments);
        System.out.println("[LoadTestDataSeeder] Apartments seeded...");

        createUsersAndProfiles(apartments, users, profiles, profilesByApartment);
        System.out.println("[LoadTestDataSeeder] Users and profiles seeded...");

        createDeviceTokens(users);
        System.out.println("[LoadTestDataSeeder] Devices and tokens seeded...");

        createRules(apartments);
        System.out.println("[LoadTestDataSeeder] Rules seeded...");

        createTasks(profilesByApartment);
        System.out.println("[LoadTestDataSeeder] Tasks seeded...");

        createBuyings(profilesByApartment);
        System.out.println("[LoadTestDataSeeder] Buyings seeded...");

        createExpenses(apartments, profilesByApartment);
        System.out.println("[LoadTestDataSeeder] Expenses seeded...");

        createEvents(profilesByApartment);
        System.out.println("[LoadTestDataSeeder] Events seeded...");

        createNotifications(profilesByApartment);
        System.out.println("[LoadTestDataSeeder] Notifications seeded...");

        entityManager.flush();

        System.out.printf(
                "[LoadTestDataSeeder] Done. Apartments=%d, Users=%d, Profiles=%d%n",
                apartments.size(),
                users.size(),
                profiles.size()
        );
    }

    private boolean alreadySeeded() {
        Number count = (Number) entityManager
                .createNativeQuery("SELECT COUNT(*) FROM user_ WHERE email LIKE 'loadtest%@example.local'")
                .getSingleResult();

        return count.longValue() > 0;
    }

    private void createApartments(List<Apartment> apartments) {
        for (int i = 1; i <= apartmentsCount; i++) {
            Apartment apartment = new Apartment(
                    "Квартира " + i,
                    "ул. Тестовая, д. " + i,
                    randomMinutesOffset()
            );

            apartment.setInviteCode(String.format("LT%06d", i));
            apartment.setBudget(randomInt(30000, 120000));

            entityManager.persist(apartment);
            apartments.add(apartment);
        }

        entityManager.flush();
    }

    private void createUsersAndProfiles(
            List<Apartment> apartments,
            List<User> users,
            List<Profile> profiles,
            List<List<Profile>> profilesByApartment
    ) {
        String encodedPassword = passwordEncoder.encode("password");

        int globalUserIndex = 1;

        for (int apartmentIndex = 0; apartmentIndex < apartments.size(); apartmentIndex++) {
            Apartment apartment = apartments.get(apartmentIndex);
            List<Profile> apartmentProfiles = new ArrayList<>();

            for (int j = 1; j <= usersPerApartment; j++) {
                boolean male = j % 2 == 0;

                User user = new User(
                        "loadtest" + globalUserIndex + "@example.local",
                        encodedPassword,
                        randomUserName(globalUserIndex, male),
                        male,
                        randomEnum(Color.class)
                );

                entityManager.persist(user);
                users.add(user);

                Profile profile = new Profile(user, apartment, j == 1);

                entityManager.persist(profile);

                user.setCurrentProfile(profile);

                profiles.add(profile);
                apartmentProfiles.add(profile);

                globalUserIndex++;
            }

            profilesByApartment.add(apartmentProfiles);
        }

        entityManager.flush();
    }

    private void createDeviceTokens(List<User> users) {
        Platform[] platforms = Platform.values();

        for (User user : users) {
            int tokensCount = randomInt(1, 2);

            for (int i = 1; i <= tokensCount; i++) {
                DeviceToken token = new DeviceToken(
                        user,
                        "device-" + user.getId() + "-" + i,
                        UUID.randomUUID().toString(),
                        platforms[(user.getId() + i) % platforms.length]
                );

                token.setUpdatedAt(Instant.now().minus(Duration.ofDays(randomInt(0, 30))));
                entityManager.persist(token);
            }
        }
    }

    private void createRules(List<Apartment> apartments) {
        String[] rules = {
                "Не шуметь после 23:00",
                "Выносить мусор по очереди",
                "Убирать кухню после готовки",
                "Предупреждать гостей заранее",
                "Не оставлять вещи в общем коридоре",
                "Закрывать входную дверь",
                "Оплачивать общие расходы вовремя"
        };

        for (Apartment apartment : apartments) {
            int rulesCount = randomInt(3, 6);

            for (int i = 0; i < rulesCount; i++) {
                entityManager.persist(new Rule(apartment, rules[i % rules.length]));
            }
        }
    }

    private void createTasks(List<List<Profile>> profilesByApartment) {
        String[] taskNames = {
                "Помыть посуду",
                "Пропылесосить комнату",
                "Вынести мусор",
                "Убрать ванную",
                "Купить бытовую химию",
                "Протереть пыль",
                "Помыть пол",
                "Разобрать общую полку",
                "Проверить оплату счетов",
                "Убрать кухню"
        };

        String[] descriptions = {
                "Плановая задача по квартире",
                "Нужно выполнить до конца недели",
                "Задача для поддержания порядка",
                "Можно выполнить в любое удобное время",
                "Желательно не откладывать"
        };

        for (List<Profile> apartmentProfiles : profilesByApartment) {
            for (int i = 0; i < tasksPerApartment; i++) {
                Profile createdBy = randomFrom(apartmentProfiles);
                Profile assignedTo = randomBoolean(85) ? randomFrom(apartmentProfiles) : null;

                Task task = new Task(
                        createdBy,
                        assignedTo,
                        taskNames[i % taskNames.length],
                        descriptions[i % descriptions.length],
                        randomEnum(Room.class),
                        randomEnum(TaskPriority.class),
                        (short) randomInt(1, 15),
                        LocalDate.now().plusDays(randomInt(-10, 30)),
                        null
                );

                if (randomBoolean(40)) {
                    Profile completedBy = assignedTo != null ? assignedTo : randomFrom(apartmentProfiles);
                    task.setCompletedBy(completedBy);
                    task.setCompletedAt(Instant.now().minus(Duration.ofDays(randomInt(0, 20))));
                    completedBy.addPoints(task.getPoints());
                }

                if (randomBoolean(20)) {
                    task.setLastReminderDate(LocalDate.now().minusDays(randomInt(0, 5)));
                }

                entityManager.persist(task);
            }
        }
    }

    private void createBuyings(List<List<Profile>> profilesByApartment) {
        String[] buyingNames = {
                "Молоко",
                "Хлеб",
                "Яйца",
                "Кофе",
                "Чай",
                "Сахар",
                "Пакеты для мусора",
                "Губки",
                "Средство для посуды",
                "Туалетная бумага",
                "Салфетки",
                "Соль"
        };

        String[] quantities = {
                "1 шт.",
                "2 шт.",
                "1 уп.",
                "2 уп.",
                "500 г",
                "1 кг",
                "3 л"
        };

        for (List<Profile> apartmentProfiles : profilesByApartment) {
            for (int i = 0; i < buyingsPerApartment; i++) {
                Profile createdBy = randomFrom(apartmentProfiles);
                Profile assignedTo = randomBoolean(75) ? randomFrom(apartmentProfiles) : null;

                Buying buying = new Buying(
                        createdBy,
                        assignedTo,
                        buyingNames[i % buyingNames.length],
                        quantities[i % quantities.length],
                        randomEnum(BuyingCategory.class),
                        randomBoolean(65)
                );

                if (randomBoolean(35)) {
                    Profile completedBy = assignedTo != null ? assignedTo : randomFrom(apartmentProfiles);
                    buying.setCompletedBy(completedBy);
                    buying.setCompletedAt(Instant.now().minus(Duration.ofDays(randomInt(0, 14))));
                }

                entityManager.persist(buying);
            }
        }
    }

    private void createExpenses(List<Apartment> apartments, List<List<Profile>> profilesByApartment) {
        String[] expenseNames = {
                "Коммунальные услуги",
                "Интернет",
                "Бытовая химия",
                "Продукты",
                "Ремонт",
                "Общие покупки",
                "Вода",
                "Электричество",
                "Газ",
                "Уборка"
        };

        for (int apartmentIndex = 0; apartmentIndex < apartments.size(); apartmentIndex++) {
            Apartment apartment = apartments.get(apartmentIndex);
            List<Profile> apartmentProfiles = profilesByApartment.get(apartmentIndex);

            for (int i = 0; i < expensesPerApartment; i++) {
                Expense expense = new Expense(
                        apartment,
                        expenseNames[i % expenseNames.length],
                        randomInt(100, 10000),
                        randomEnum(ExpenseCategory.class),
                        null,
                        randomFrom(apartmentProfiles)
                );

                entityManager.persist(expense);
            }
        }
    }

    private void createEvents(List<List<Profile>> profilesByApartment) {
        String[] eventNames = {
                "Общее собрание",
                "Генеральная уборка",
                "Оплата счетов",
                "Обсуждение правил",
                "Покупка продуктов",
                "Планирование бюджета",
                "Проверка задач"
        };

        String[] descriptions = {
                "Событие для жильцов квартиры",
                "Нужно участие всех по возможности",
                "Короткое обсуждение бытовых вопросов",
                "Плановое мероприятие"
        };

        for (List<Profile> apartmentProfiles : profilesByApartment) {
            for (int i = 0; i < eventsPerApartment; i++) {
                Event event = new Event(
                        randomFrom(apartmentProfiles),
                        eventNames[i % eventNames.length],
                        LocalDate.now().plusDays(randomInt(-15, 45)),
                        randomBoolean(80)
                                ? LocalTime.of(randomInt(8, 22), randomFrom(List.of(0, 15, 30, 45)))
                                : null,
                        descriptions[i % descriptions.length]
                );

                entityManager.persist(event);
            }
        }
    }

    private void createNotifications(List<List<Profile>> profilesByApartment) {
        for (List<Profile> apartmentProfiles : profilesByApartment) {
            for (int i = 0; i < notificationsPerApartment; i++) {
                Profile actor = randomFrom(apartmentProfiles);

                Notification notification = new Notification(
                        actor,
                        randomEnum(NotificationType.class),
                        randomEnum(EntityType.class),
                        Map.of(
                                "title", "Тестовое уведомление " + i,
                                "message", "Сгенерировано для нагрузочного тестирования",
                                "entityId", i + 1
                        )
                );

                entityManager.persist(notification);

                for (Profile recipient : apartmentProfiles) {
                    ProfileNotification profileNotification = new ProfileNotification(
                            recipient,
                            notification
                    );

                    if (randomBoolean(60)) {
                        profileNotification.setRead(true);
                    }

                    entityManager.persist(profileNotification);
                }
            }
        }
    }

    private short randomMinutesOffset() {
        int[] offsets = {
                -720, -660, -600, -480, -300, -180, 0,
                60, 120, 180, 240, 300, 330, 360, 420, 480
        };

        return (short) offsets[randomInt(0, offsets.length - 1)];
    }

    private String randomUserName(int index, boolean male) {
        String[] maleNames = {
                "Алексей", "Иван", "Дмитрий", "Максим", "Артём", "Кирилл", "Никита"
        };

        String[] femaleNames = {
                "Анна", "Мария", "Екатерина", "Дарья", "София", "Полина", "Алина"
        };

        String baseName = male
                ? maleNames[index % maleNames.length]
                : femaleNames[index % femaleNames.length];

        return baseName + " " + index;
    }

    private int randomInt(int minInclusive, int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }

    private boolean randomBoolean(int trueProbabilityPercent) {
        return randomInt(1, 100) <= trueProbabilityPercent;
    }

    private <T> T randomFrom(List<T> values) {
        return values.get(randomInt(0, values.size() - 1));
    }

    private <E extends Enum<E>> E randomEnum(Class<E> enumClass) {
        E[] values = enumClass.getEnumConstants();

        if (values == null || values.length == 0) {
            throw new IllegalStateException("Enum has no values: " + enumClass.getName());
        }

        return values[randomInt(0, values.length - 1)];
    }
}