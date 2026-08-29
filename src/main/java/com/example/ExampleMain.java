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

    private static final String URL =
            "jdbc:postgresql://localhost:5432/postgres?currentSchema=chenup";
    private static final String USER = "postgres";
    private static final String PASSWORD = "mysecretpassword";

    public static void main(String[] args) throws Exception {

        PostgresPersistenceManager persistenceManager =
                new PostgresPersistenceManager();

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

        p1.setName(new PersonName("John", "Smith"));
        p1.setBirthDate(LocalDate.of(2000, 1, 1));

        Organization org = (Organization) persistenceManager.create(Organization.class);
        org.setName("ACME, Inc.");

        // Push to the database. Note that this does not flush the cache or commit the transaction.
        persistenceManager.push(context);

        // Note that in 0.1.0, it is safest to push new entities before creating associations.
        // This is listed as a bug which is planned to be fixed in 0.1.1.

        persistenceManager.push(context);

        Employment employment = (Employment) persistenceManager.create(Employment.class);
        employment.setEmployee(p1);
        employment.setEmployer(org);
        employment.setStartDate(LocalDate.of(2010, 1, 1));
        employment.setEndDate(LocalDate.of(2015, 1, 1));

        // Push to the database. Note that this does not flush the cache or commit the transaction.
        // You must push to the database for the find method to work.
        persistenceManager.push(context);

        // Currently, queries are required to return a list of IDs.
        // Obviously, such query strings should not be taken directly from user input without sanitization.
        String queryString =
            """
            SELECT e.id
            FROM employment e
            JOIN person p ON e.employee = p.id
            WHERE (p.name).sur_name = 'Smith'
            """;

        var results = persistenceManager.find(queryString, context);

        System.out.println("results = " + results);
        for (var result: results){
            var employmentRead = (Employment) persistenceManager.read(result, Employment.class, context);
            System.out.println("employment id = " + employmentRead.getId());
            System.out.println("employment employee = " + employmentRead.getEmployee().getName());
            System.out.println("employment employer = " + employmentRead.getEmployer().getName());
            System.out.println("employment employee name = " + employmentRead.getEmployee().getName());
            System.out.println("employment employee id = " + employmentRead.getEmployee().getId());

        }

        // Update employment to show that the version of the association is incremented, but not of the person or organization.
        employment.setEndDate(LocalDate.of(2025, 1, 1));

        persistenceManager.push(context);
        System.out.println("p1 version = " + ((PersonImpl)p1).getMetaData().getVersion());
        System.out.println("org version = " + ((OrganizationImpl)org).getMetaData().getVersion());


        System.out.println("employment version = " + ((EmploymentImpl)employment).getMetaData().getVersion());









    }
}