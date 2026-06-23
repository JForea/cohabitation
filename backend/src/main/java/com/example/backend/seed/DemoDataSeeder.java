package com.example.backend.seed;

import com.example.backend.entities.*;
import com.example.backend.repositories.*;
import com.example.backend.types.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@org.springframework.context.annotation.Profile("demo")
public class DemoDataSeeder implements CommandLineRunner {

    private static final String DEMO_APARTMENT_NAME = "Демо-квартира";

    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;
    private final ProfileRepository profileRepository;
    private final ExpenseRepository expenseRepository;
    private final TaskRepository taskRepository;
    private final BuyingRepository buyingRepository;
    private final EventRepository eventRepository;
    private final RuleRepository ruleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DemoDataSeeder(
            UserRepository userRepository,
            ApartmentRepository apartmentRepository,
            ProfileRepository profileRepository,
            ExpenseRepository expenseRepository,
            TaskRepository taskRepository,
            BuyingRepository buyingRepository,
            EventRepository eventRepository,
            RuleRepository ruleRepository,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate
    ) {
        this.userRepository = userRepository;
        this.apartmentRepository = apartmentRepository;
        this.profileRepository = profileRepository;
        this.expenseRepository = expenseRepository;
        this.taskRepository = taskRepository;
        this.buyingRepository = buyingRepository;
        this.eventRepository = eventRepository;
        this.ruleRepository = ruleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (apartmentRepository.existsByName(DEMO_APARTMENT_NAME)) {
            return;
        }

        Apartment apartment = createApartment();
        updateApartmentCreatedAt(
                apartment.getId(),
                Instant.now().minus(180, ChronoUnit.DAYS)
        );

        List<User> users = createUsers();
        List<Profile> profiles = createProfiles(users, apartment);

        createRules(apartment);
        createExpenses(apartment, profiles);
        createTasks(profiles);
        createBuyings(profiles);
        createEvents(profiles);

        System.out.println("Demo db created.");
    }

    private Apartment createApartment() {
        Apartment apartment = new Apartment(
                DEMO_APARTMENT_NAME,
                "ул. Демонстрационная, 10",
                (short) 180
        );

        apartment.setBudget(50000);
        apartment.setInviteCode("DEMO2026");

        return apartmentRepository.save(apartment);
    }

    private List<User> createUsers() {
        List<User> users = List.of(
                new User("rodion.demo@gmail.com", passwordEncoder.encode("password"), "Родион", false, color(0)),
                new User("ivan.demo@gmail.com", passwordEncoder.encode("password"), "Иван", true, color(1)),
                new User("maria.demo@gmail.com", passwordEncoder.encode("password"), "Мария", false, color(2)),
                new User("dmitry.demo@gmail.com", passwordEncoder.encode("password"), "Дмитрий", true, color(3)),
                new User("kate.demo@gmail.com", passwordEncoder.encode("password"), "Екатерина", false, color(4))
        );

        return userRepository.saveAll(users);
    }

    private List<Profile> createProfiles(List<User> users, Apartment apartment) {
        int[] points = {210, 145, 90, 175, 55};

        List<Profile> profiles = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            Profile profile = new Profile(users.get(i), apartment, i == 0);

            profile.addPoints((short) points[i]);

            if (i == 1) {
                profile.setRole(Role.ADMIN);
            }

            profiles.add(profile);
        }

        profiles = profileRepository.saveAll(profiles);

        for (int i = 0; i < users.size(); i++) {
            users.get(i).setCurrentProfile(profiles.get(i));
        }

        userRepository.saveAll(users);

        return profiles;
    }

    private void createRules(Apartment apartment) {
        ruleRepository.saveAll(List.of(
                new Rule(apartment, "После 23:00 соблюдать тишину"),
                new Rule(apartment, "Мыть за собой посуду"),
                new Rule(apartment, "Сообщать о крупных расходах заранее"),
                new Rule(apartment, "Не оставлять личные вещи в коридоре")
        ));
    }

    private void createExpenses(Apartment apartment, List<Profile> profiles) {
        Random random = new Random(42);

        YearMonth currentMonth = YearMonth.now();
        LocalDate today = LocalDate.now();

        for (int monthOffset = 5; monthOffset >= 0; monthOffset--) {
            YearMonth month = currentMonth.minusMonths(monthOffset);
            boolean isCurrentMonth = month.equals(currentMonth);

            int fullMonthExpensesCount = random.nextInt(11) + 20;
            int fullMonthTotal = random.nextInt(30001) + 30000;

            int expensesCount;
            int monthlyTotal;

            if (isCurrentMonth) {
                int daysPassed = today.getDayOfMonth() - 1;
                int daysInMonth = today.lengthOfMonth();

                double monthProgress = (double) daysPassed / daysInMonth;

                expensesCount = Math.max(
                        1,
                        (int) Math.round(fullMonthExpensesCount * monthProgress)
                );

                monthlyTotal = Math.max(
                        1000,
                        (int) Math.round(fullMonthTotal * monthProgress)
                );
            } else {
                expensesCount = fullMonthExpensesCount;
                monthlyTotal = fullMonthTotal;
            }

            int remaining = monthlyTotal;

            for (int i = 0; i < expensesCount; i++) {
                int amount = generateExpenseAmount(
                        remaining,
                        expensesCount - i,
                        random
                );

                remaining -= amount;

                Profile createdBy = randomProfile(profiles, random);
                ExpenseCategory category = randomExpenseCategory(random);

                Instant createdAt = isCurrentMonth
                        ? randomInstantInCurrentMonthBeforeToday(today, random)
                        : randomInstantInMonth(month, random);

                Expense expense = new Expense(
                        apartment,
                        randomExpenseName(category, random),
                        amount,
                        category,
                        null,
                        createdBy
                );

                expense = expenseRepository.save(expense);
                updateExpenseCreatedAt(expense.getId(), createdAt);
            }
        }
    }

    private void createTasks(List<Profile> profiles) {
        Random random = new Random(43);

        String[] names = {
                "Вынести мусор",
                "Помыть посуду",
                "Пропылесосить комнату",
                "Убраться на кухне",
                "Помыть ванную",
                "Полить растения",
                "Разобрать холодильник",
                "Проверить счётчики",
                "Протереть пыль",
                "Заказать воду"
        };

        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            Profile creator = randomProfile(profiles, random);
            Profile assignedTo = randomProfile(profiles, random);

            Task task = new Task(
                    creator,
                    assignedTo,
                    names[i % names.length],
                    "Задача для проверки работы приложения",
                    randomEnum(Room.class, random),
                    randomEnum(TaskPriority.class, random),
                    (short) (random.nextInt(8) + 3),
                    LocalDate.now().plusDays(random.nextInt(20) + 1),
                    null
            );

            if (i % 3 == 0) {
                task.setCompletedBy(assignedTo);
                task.setCompletedAt(
                        Instant.now().minusSeconds(random.nextInt(7 * 24 * 60 * 60))
                );
            }

            tasks.add(task);
        }

        taskRepository.saveAll(tasks);
    }

    private void createBuyings(List<Profile> profiles) {
        Random random = new Random(44);

        String[] names = {
                "Молоко", "Хлеб", "Яйца", "Сыр", "Курица",
                "Макароны", "Рис", "Гречка", "Картофель", "Лук",
                "Яблоки", "Бананы", "Чай", "Кофе", "Сахар",
                "Соль", "Средство для посуды", "Губки", "Порошок", "Мыло"
        };

        List<Buying> buyings = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            Profile creator = randomProfile(profiles, random);
            Profile assignedTo = randomProfile(profiles, random);

            Buying buying = new Buying(
                    creator,
                    assignedTo,
                    names[i % names.length],
                    String.valueOf(random.nextInt(3) + 1),
                    buyingCategoryByName(names[i % names.length]),
                    true
            );

            if (i % 4 == 0) {
                buying.setCompletedBy(assignedTo);
                buying.setCompletedAt(
                        Instant.now().minusSeconds(random.nextInt(10 * 24 * 60 * 60))
                );
            }

            buyings.add(buying);
        }

        buyingRepository.saveAll(buyings);
    }

    private void createEvents(List<Profile> profiles) {
        LocalDate today = LocalDate.now();
        LocalDate sameDate = futureDateInCurrentMonth(today, 3);

        List<Event> events = List.of(
                new Event(
                        profiles.get(0),
                        "Оплата коммунальных услуг",
                        sameDate,
                        LocalTime.of(10, 0),
                        "Не забыть оплатить счета"
                ),
                new Event(
                        profiles.get(1),
                        "Генеральная уборка",
                        sameDate,
                        LocalTime.of(15, 0),
                        "Общая уборка квартиры"
                ),
                new Event(
                        profiles.get(2),
                        "Собрание жильцов",
                        futureDateInCurrentMonth(today, 7),
                        LocalTime.of(19, 30),
                        "Обсудить правила проживания"
                ),
                new Event(
                        profiles.get(3),
                        "Покупка продуктов",
                        futureDateInCurrentMonth(today, 12),
                        LocalTime.of(18, 0),
                        "Закупить продукты на неделю"
                ),
                new Event(
                        profiles.get(4),
                        "Проверка счётчиков",
                        futureDateInCurrentMonth(today, 20),
                        LocalTime.of(12, 0),
                        "Передать показания"
                )
        );

        eventRepository.saveAll(events);
    }

    private int generateExpenseAmount(
            int remaining,
            int expensesLeft,
            Random random
    ) {
        if (expensesLeft == 1) {
            return remaining;
        }

        int minAmount = 300;
        int maxAmount = remaining - ((expensesLeft - 1) * minAmount);

        int average = remaining / expensesLeft;
        int softMax = Math.min(maxAmount, average * 2);

        if (softMax < minAmount) {
            return minAmount;
        }

        return random.nextInt(softMax - minAmount + 1) + minAmount;
    }

    private void updateApartmentCreatedAt(Integer apartmentId, Instant createdAt) {
        jdbcTemplate.update(
                "UPDATE apartment SET created_at = ? WHERE id = ?",
                Timestamp.from(createdAt),
                apartmentId
        );
    }

    private void updateExpenseCreatedAt(Long expenseId, Instant createdAt) {
        jdbcTemplate.update(
                "UPDATE expense SET created_at = ? WHERE id = ?",
                Timestamp.from(createdAt),
                expenseId
        );
    }

    private Instant randomInstantInMonth(YearMonth month, Random random) {
        int day = random.nextInt(month.lengthOfMonth()) + 1;
        int hour = random.nextInt(12) + 8;
        int minute = random.nextInt(60);

        return LocalDateTime
                .of(month.getYear(), month.getMonth(), day, hour, minute)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    private Instant randomInstantInCurrentMonthBeforeToday(
            LocalDate today,
            Random random
    ) {
        int daysPassed = today.getDayOfMonth() - 1;

        if (daysPassed <= 0) {
            return today
                    .atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
        }

        int day = random.nextInt(daysPassed) + 1;
        int hour = random.nextInt(12) + 8;
        int minute = random.nextInt(60);

        return LocalDateTime
                .of(today.getYear(), today.getMonth(), day, hour, minute)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    private String randomExpenseName(
            ExpenseCategory category,
            Random random
    ) {
        return switch (category) {
            case PRODUCTS -> random(
                    random,
                    "Пятёрочка",
                    "Перекрёсток",
                    "Лента",
                    "Закупка продуктов"
            );

            case HOUSING_AND_COMMUNAL_SERVICES -> random(
                    random,
                    "Электричество",
                    "Водоснабжение",
                    "Газ",
                    "Коммунальные услуги"
            );

            case SERVICES -> random(
                    random,
                    "Интернет",
                    "Домофон",
                    "Уборка",
                    "Подписка на сервис"
            );

            case HOUSEHOLD_GOODS -> random(
                    random,
                    "Средство для посуды",
                    "Стиральный порошок",
                    "Мешки для мусора",
                    "Бытовая химия"
            );

            case RENT -> "Аренда квартиры";

            case OTHER -> random(
                    random,
                    "Прочие расходы",
                    "Незапланированная покупка"
            );
        };
    }

    private ExpenseCategory randomExpenseCategory(Random random) {
        ExpenseCategory[] categories = {
                ExpenseCategory.PRODUCTS,
                ExpenseCategory.HOUSING_AND_COMMUNAL_SERVICES,
                ExpenseCategory.SERVICES,
                ExpenseCategory.HOUSEHOLD_GOODS,
                ExpenseCategory.RENT,
                ExpenseCategory.OTHER
        };

        return categories[random.nextInt(categories.length)];
    }

    private BuyingCategory buyingCategoryByName(String name) {
        return switch (name) {
            case "Хлеб" -> BuyingCategory.BAKERY;
            case "Молоко", "Яйца", "Сыр" -> BuyingCategory.DAIRY;
            case "Макароны", "Рис", "Гречка" -> BuyingCategory.CEREALS;
            case "Картофель", "Лук" -> BuyingCategory.VEGETABLES;
            case "Яблоки", "Бананы" -> BuyingCategory.FRUITS;
            case "Курица" -> BuyingCategory.MEAT;
            case "Чай", "Кофе" -> BuyingCategory.DRINKS;
            case "Средство для посуды", "Губки", "Порошок", "Мыло" -> BuyingCategory.HOUSEHOLD;
            default -> BuyingCategory.OTHER;
        };
    }

    private LocalDate futureDateInCurrentMonth(LocalDate today, int plusDays) {
        LocalDate date = today.plusDays(plusDays);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        if (date.isAfter(lastDayOfMonth)) {
            return lastDayOfMonth;
        }

        return date;
    }

    private Profile randomProfile(List<Profile> profiles, Random random) {
        return profiles.get(random.nextInt(profiles.size()));
    }

    private String random(Random random, String... values) {
        return values[random.nextInt(values.length)];
    }

    private Color color(int index) {
        Color[] values = Color.values();
        return values[index % values.length];
    }

    private <T extends Enum<T>> T randomEnum(Class<T> enumClass, Random random) {
        T[] values = enumClass.getEnumConstants();
        return values[random.nextInt(values.length)];
    }
}