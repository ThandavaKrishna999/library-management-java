# Library Management System

A console-based Library Management System built in Java, demonstrating core OOP principles, custom exception handling, input validation, and CSV-based file persistence.

## Features

- Add books to the library
- Register students and faculty members
- Lend and return books
- Search books by title or ISBN
- View all books and members
- Data persists across sessions via CSV files

## How to Run

**Compile:**
```
javac Main.java
```

**Run:**
```
java Main
```

No external dependencies. Requires Java 8 or above.

## Concepts Demonstrated

- **OOP** — Abstract classes (`Items`, `Members`), inheritance (`Books`, `Students`, `Faculty`), polymorphism (`borrowItem()`, `displayinfo()`)
- **Custom Exceptions** — `MemberNotFoundException`, `BookNotAvailableException`
- **Input Validation** — Empty field checks, type validation, `InputMismatchException` handling throughout all menu options
- **File Persistence** — CSV read/write using `FileWriter` and `BufferedReader`; data survives program restarts
- **Collections** — `ArrayList` for items, `HashMap` for member lookup by ID

## Project Structure

```
Main.java         - Entry point and menu loop
books.csv         - Auto-generated on first book add
members.csv       - Auto-generated on first member register
```

## What I'd Add Next

- **JDBC + MySQL** — Replace CSV with a relational database
- **Package structure** — Separate `model/`, `service/`, `exception/` packages
- **Maven** — Proper build tool setup
- **JUnit tests** — Unit tests for Library service methods
- **REST API** — Expose library operations via Spring Boot endpoints
