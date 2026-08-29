package com.example;

import com.ahimsasystems.chenup.core.PersistenceInitializer;
import com.ahimsasystems.chenup.postgresdb.PostgresContext;
import com.ahimsasystems.chenup.postgresdb.PostgresPersistenceManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
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

        p1.setName(new PersonName("John", "Doe"));
        p1.setBirthDate(LocalDate.of(2000, 1, 1));

        persistenceManager.push(context);



        persistenceManager.push(context);

        UUID uuid = p1.getId();

        Person p1old = p1;




        p1 = (Person) persistenceManager.read(uuid, Person.class, context);

        System.out.println("p1old = p1 " + (p1old == p1));





        persistenceManager.flush(context);




    }
}