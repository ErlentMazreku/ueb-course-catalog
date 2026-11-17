# Katalog kursesh

Aplikacion web për lëndën Ueb: një qendër trajnimi publikon kurset dhe instruktorët e vet.
Teknologjitë: Java 25, Spring Boot 4.1.2, Spring MVC + Thymeleaf, Spring Data JPA, PostgreSQL në Docker.
Përshkrimi i plotë është te [SPEC.md](SPEC.md).

## 1. Nis databazën (Docker)

Herën e parë (zëvendëso `FJALEKALIMI` me fjalëkalimin që do të vendosësh te `.env`):

```bash
docker run -d --name katalog-db -e POSTGRES_DB=katalog -e POSTGRES_USER=katalog -e POSTGRES_PASSWORD=FJALEKALIMI -p 5433:5432 -v katalog-data:/var/lib/postgresql/data postgres:17
```

Herët tjera mjafton:

```bash
docker start katalog-db
```

Të dhënat ruhen në volume-in `katalog-data`, prandaj nuk humbin kur kontejneri ndalet.

## 2. Krijo `.env`

```bash
cp .env.example .env
```

Hape `.env` dhe plotëso vlerat. `DB_PASSWORD` duhet të jetë i njëjti si `POSTGRES_PASSWORD` te `docker run`.
`.env` nuk shkon në GitHub (është te `.gitignore`).

## 3. Hape dhe nise në IntelliJ

1. **File → Open** dhe zgjidh dosjen `katalog` (ajo me `pom.xml`). Prit sa Maven t'i shkarkojë varësitë.
2. **File → Project Structure → Project → SDK**: zgjidh JDK 25.
3. Hape `src/main/java/com/ueb/katalog/KatalogApplication.java` dhe kliko ▶ pranë `main`.
4. Hape në shfletues: <http://localhost:8080>

Nga terminali: `./mvnw spring-boot:run`

## 4. Gabimet më të shpeshta

| Gabimi | Shkaku | Zgjidhja |
|---|---|---|
| `Connection to localhost:5433 refused` | Databaza nuk punon | `docker start katalog-db` |
| `password authentication failed for user "katalog"` | `DB_PASSWORD` te `.env` nuk përputhet me Docker-in | Rregullo `.env`, ose fshij kontejnerin dhe volume-in dhe krijoji sërish |
| `Could not resolve placeholder 'DB_PASSWORD'` | Mungon `.env`, ose aplikacioni nuk niset nga dosja e projektit | Krijo `.env` në root; në IntelliJ, Working directory = dosja e projektit |
| `Port 8080 was already in use` | Aplikacioni po punon tashmë | Ndale ekzekutimin tjetër (■ në IntelliJ) |
| `release version 25 not supported` | IntelliJ ose Maven përdor JDK tjetër | Vendos JDK 25 te Project Structure dhe `JAVA_HOME` |
| `container name "/katalog-db" is already in use` | Kontejneri ekziston nga më parë | Përdor `docker start katalog-db` në vend të `docker run` |