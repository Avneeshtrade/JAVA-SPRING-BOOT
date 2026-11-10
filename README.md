# 📚 Book API

Book API is a Spring Boot REST service for managing books with full CRUD operations and input validation. It uses SQLite for local development and PostgreSQL (Neon) in production via Spring profiles, and is containerized with Docker and deployed on Render.

**Live demo:** `https://<your-app-name>.onrender.com`

> The app runs on Render's free tier, so the first request after a period of inactivity may take up to a minute while the server wakes up.

---

## 🛠 Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3, Spring Web, Spring Data JPA, Hibernate
- **Validation:** Jakarta Bean Validation
- **Databases:** SQLite (development), PostgreSQL on Neon (production)
- **Deployment:** Docker, Render

---

## 📂 Project Structure

```
book-api/
├── Dockerfile
├── pom.xml
└── src/main/
    ├── java/com/example/bookapi/
    │   ├── BookApiApplication.java      # Entry point
    │   ├── config/DataSeeder.java       # Adds sample books on first run
    │   ├── controller/
    │   │   ├── BookController.java      # CRUD endpoints
    │   │   └── HelloController.java     # Health check and greeting
    │   ├── model/Book.java              # JPA entity
    │   └── repository/BookRepository.java
    └── resources/
        ├── application.properties       # Default profile (SQLite)
        └── application-prod.properties  # Production profile (PostgreSQL)
```

---

## 🔗 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Health check |
| GET | `/api/hello?name=Rahul` | Returns a greeting |
| GET | `/api/books` | Get all books |
| GET | `/api/books/{id}` | Get a book by ID |
| POST | `/api/books` | Create a new book |
| PUT | `/api/books/{id}` | Update an existing book |
| DELETE | `/api/books/{id}` | Delete a book |

### Book JSON format

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "price": 499.0
}
```

`title` and `author` are required, and `price` cannot be negative. Invalid input returns `400 Bad Request`, and a missing book returns `404 Not Found`.

---

## 🚀 Running Locally

### Prerequisites

- Java 17 or newer
- Maven 3.9+ (or an IDE with Maven built in, such as IntelliJ IDEA)

### Steps

```bash
git clone https://github.com/<your-username>/book-api.git
cd book-api
mvn spring-boot:run
```

The API starts at `http://localhost:8080`. A SQLite database file named `bookapi.db` is created automatically in the project folder, and two sample books are added on the first run. No database installation is needed.

### Example requests

```bash
# Get all books
curl http://localhost:8080/api/books

# Create a book
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Head First Java","author":"Kathy Sierra","price":550}'

# Update a book
curl -X PUT http://localhost:8080/api/books/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Code (2nd Ed)","author":"Robert C. Martin","price":599}'

# Delete a book
curl -X DELETE http://localhost:8080/api/books/2
```

To reset the local database, stop the app and delete `bookapi.db`.

---

## ⚙️ Configuration

The app uses Spring profiles to switch databases:

| Profile | Database | How it's activated |
|---------|----------|--------------------|
| default | SQLite (`bookapi.db`) | Runs automatically when no profile is set |
| `prod` | PostgreSQL (Neon) | Set `SPRING_PROFILES_ACTIVE=prod` |

The `prod` profile reads its connection details from these environment variables:

| Variable | Example |
|----------|---------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_URL` | `jdbc:postgresql://<host>/neondb?sslmode=require` |
| `DB_USERNAME` | `neondb_owner` |
| `DB_PASSWORD` | `your-password` |
| `PORT` | Set automatically by Render (defaults to `8080`) |

---

## 🐳 Running with Docker

```bash
docker build -t book-api .
docker run -p 8080:8080 book-api
```

To run against PostgreSQL instead of SQLite:

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL="jdbc:postgresql://<host>/neondb?sslmode=require" \
  -e DB_USERNAME="<username>" \
  -e DB_PASSWORD="<password>" \
  book-api
```

---

## ☁️ Deployment (Render + Neon)

1. Create a free PostgreSQL database on [Neon](https://neon.com) and convert its connection string to JDBC format.
2. Push the project to GitHub.
3. On [Render](https://render.com), create a new **Web Service** from the repository, choose **Docker** as the language and the **Free** instance type.
4. Add the environment variables listed in the Configuration section.
5. Deploy. Every push to `main` triggers an automatic redeploy.

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).