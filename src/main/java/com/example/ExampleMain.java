package com.example;

import com.ahimsasystems.chenup.core.PersistenceInitializer;
import com.ahimsasystems.chenup.postgresdb.PostgresContext;
import com.ahimsasystems.chenup.postgresdb.PostgresPersistenceManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class ExampleMain {


//    private static final String URL =
//            "jdbc:postgresql://10.211.55.20:5432/accounts";
//    private static final String USER = "postgres";
//    private static final String PASSWORD = "mysecretpassword";

    private static final String URL =
            "jdbc:postgresql://localhost:5432/postgres?currentSchema=chenup";
    private static final String USER = "postgres";
    private static final String PASSWORD = "mysecretpassword";

    public static void main(String[] args) throws Exception {

        System.out.println("Hello World!");

        PostgresPersistenceManager persistenceManager =
                new PostgresPersistenceManager();

//        var persistenceInitializer =
//                new MyPersistenceInitializer();


//        PersistenceInitializer persistenceInitializer =
//                ServiceLoader.load(PersistenceInitializer.class)
//                        .findFirst()
//                        .orElseThrow(() ->
//                                new IllegalStateException(
//                                        "No PersistenceInitializer found"));

        PersistenceInitializer persistenceInitializer =
                (PersistenceInitializer)
                        Class.forName("com.ahimsasystems.chenup.runtime.MyPersistenceInitializer")
                                .getDeclaredConstructor()
                                .newInstance();

        persistenceInitializer.registerAll(persistenceManager);

        try (Connection connection =
                     DriverManager.getConnection(URL, USER, PASSWORD)) {

            // We are managing transactions explicitly.
            connection.setAutoCommit(false);

            PostgresContext context =
                    new PostgresContext(connection, persistenceManager);

            try {

                runExample(persistenceManager, context);

                persistenceManager.flush(context);

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private static void runExample(
            PostgresPersistenceManager persistenceManager,
            PostgresContext context) throws SQLException {

        Person p1 = (Person) persistenceManager.create(Person.class);

        p1.setName("John Doe");
        p1.setBirthInstant(Instant.parse("2000-01-01T00:00:00Z"));
        p1.setSeparateName(new PersonName("John", "Doe"));

        persistenceManager.push(context);


        p1.setName("Jane Doe " + 1);

        persistenceManager.push(context);

        UUID uuid = p1.getId();

        Person p1old = p1;


        p1 = (Person) persistenceManager.read(uuid, Person.class, context);

        System.out.println("p1old = p1 " + (p1old == p1));


        for (int i = 0; i < 10; i++) {
            p1.setName("Jane Doe " + i);
            persistenceManager.push(context);
        }


        try {
            persistenceManager.flush(context);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


//
//        Person p2 = persistenceManager.read(
//                UUID.fromString(
//                        "0197832a-0751-7f6a-f3a0-a6b94281d055"),
//                Person.class,
//                context);
//
//        System.out.println("*** p2 = " + p2 + " ***");
//
//        System.out.println("*** p2.getName() = " + p2.getName() + " ***");
//
//        System.out.println("*** p2.getBirthInstant() = " + p2.getBirthInstant() + " ***");
//
//        System.out.println("*** p2.getSeparateName() = " + p2.getSeparateName() + " ***");
//
//
//        PersonImpl p2impl = (PersonImpl) p2;
//
//        System.out.println(
//                "version = " +
//                        p2impl.getMetaData().getVersion());
//
//        // p2.setBirthInstant(Instant.now());
//
//        String queryString =
//                """
//                SELECT e.id
//                FROM employment e
//                JOIN person p ON e.employee = p.id
//                WHERE (p.separate_name).sur_name = 'Lane'
//                  AND CURRENT_TIMESTAMP
//                      BETWEEN e.start_date AND e.end_date
//                """;
//
//        Vector<UUID> results =
//                null;
//        try {
//
//
//            results = persistenceManager.find(queryString, context);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        System.out.println("results = " + results);
//
//        if (!results.isEmpty()) {
//            UUID employmentId = results.getFirst();
//
//            Employment employment =
//                    persistenceManager.read(
//                            employmentId,
//                            Employment.class,
//                            context);
//
//            System.out.println("employment = " + employment);
//            System.out.println(
//                    "employee = " +
//                            employment.getEmployee().getId());
//        }
//
//        p2.setName(
//                "I was updated at " +
//                        java.time.LocalDateTime.now());
//
//        p2.setSeparateName(
//                new PersonName("Margo", "Lane"));
    }
}