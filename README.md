# Online biblioteka - Spring Boot aplikacija

## Opis
Jednostavna aplikacija za upravljanje autorima i knjigama sa REST API-jem.  
Koristi H2 bazu u memoriji i JPA za rad sa podacima.

## Pokretanje
- Pokreni `DemoApplication` u IDE-u ili sa `mvn spring-boot:run`  
- Aplikacija je na `http://localhost:8080`  
- H2 konzola: `http://localhost:8080/h2-console`  
  - JDBC URL: `jdbc:h2:mem:testdb`  
  - User: `sa`  
  - Password: *(prazno)*

## API
- `/authors` - CRUD operacije nad autorima  
- `/books` - CRUD operacije nad knjigama

