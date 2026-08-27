This project contains a minimal chenup project.

1. Create a docker postgres container: docker run --name chenup-postgres -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres
2. Save this project in a convenient location. We will refer to this as simplechenup from this point on.
3. cd simplechenup
4. Run the database initialization script: docker exec -i chenup-postgres psql -U postgres -d postgres < init.sql
5. Build the application: mvn clean package
6. Run the application: java -jar target/simplechenup-0.1.0-SNAPSHOT.jar
