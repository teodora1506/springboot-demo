# Online biblioteka - Spring Boot aplikacija

## Opis
Jednostavna REST API aplikacija za upravljanje autorima i knjigama.  
Koristi H2 bazu u memoriji i JPA za rad sa podacima.

## Pokretanje
- Pokreni aplikaciju preko IDE-a ili komandne linije sa:
mvn spring-boot:run

markdown
Copy
Edit
- Aplikacija radi na `http://localhost:8080`
- H2 konzola dostupna na `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: *(prazno)*

## API krajnje tačke (endpoints)
- `/authors` - CRUD operacije nad autorima
- `/books` - CRUD operacije nad knjigama

## Tehnologije
- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database
- Maven

