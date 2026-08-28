This project contains a minimal chenup project.

1. Save this project in a convenient location. We will refer to this as simplechenup from this point on.
2. cd simplechenup
3. 5. Build the application: mvn clean package
4. Create a docker postgres container: docker run --name chenup-postgres -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres
5. Run the database initialization script: docker exec -i chenup-postgres psql -U postgres -d postgres < init.sql
6. Run the application: java -jar target/simplechenup-0.1.0-SNAPSHOT.jar
