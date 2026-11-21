# Katalog kursesh

Aplikacion web për lëndën Zhvillim i Web-it (Prof. Jon Kursani): një qendër trajnimi publikon kurset dhe instruktorët e vet.
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

Hyr te <http://localhost:8080/login> si `user` ose `admin`, me fjalëkalimet përkatëse nga `USER_PASSWORD` dhe `ADMIN_PASSWORD` në `.env`. Pas hyrjes hapet `/courses`. Përdoruesi `user` mund t'i shohë kurset dhe instruktorët, por nuk mund t'i shtojë, ndryshojë ose fshijë. Këto veprime lejohen vetëm për `admin`; kufizimi zbatohet edhe kur provohet hyrja direkte në URL. Nëse përdoruesi i kyçur hap `/login`, ridrejtohet te faqja e fundit e vizituar; forma e hyrjes shfaqet pas çkyçjes. Butoni "Dil" kryen çkyçjen.

## 4. Gabimet më të shpeshta

| Gabimi | Shkaku | Zgjidhja |
|---|---|---|
| `Connection to localhost:5433 refused` | Databaza nuk punon | `docker start katalog-db` |
| `password authentication failed for user "katalog"` | `DB_PASSWORD` te `.env` nuk përputhet me Docker-in | Rregullo `.env`, ose fshij kontejnerin dhe volume-in dhe krijoji sërish |
| `Could not resolve placeholder 'DB_PASSWORD'` | Mungon `.env`, ose aplikacioni nuk niset nga dosja e projektit | Krijo `.env` në root; në IntelliJ, Working directory = dosja e projektit |
| `Could not resolve placeholder 'USER_PASSWORD'` | `USER_PASSWORD` mungon te `.env` | Plotëso `USER_PASSWORD` dhe `ADMIN_PASSWORD` te `.env` |
| `403 Forbidden pas një forme` | Forma pa `th:action` nuk merr token-in CSRF nga Thymeleaf | Përdor `method="post"` dhe `th:action` në formë; mos e çaktivizo CSRF-in |
| `Port 8080 was already in use` | Aplikacioni po punon tashmë | Ndale ekzekutimin tjetër (■ në IntelliJ) |
| `release version 25 not supported` | IntelliJ ose Maven përdor JDK tjetër | Vendos JDK 25 te Project Structure dhe `JAVA_HOME` |
| `container name "/katalog-db" is already in use` | Kontejneri ekziston nga më parë | Përdor `docker start katalog-db` në vend të `docker run` |

## 5. Testet dhe kontrolli manual

Me JDK 25 dhe `JAVA_HOME` përkatës, në Windows ekzekuto `.\mvnw.cmd -q test`; në Linux/macOS `./mvnw -q test`.
Testet MVC përdorin fjalëkalime vetëm testuese dhe shërbime të simuluara; nuk lexojnë `.env` dhe nuk kërkojnë PostgreSQL. Ruajtja reale në databazë kontrollohet manualisht:

1. Plotëso `DB_PASSWORD`, `USER_PASSWORD` dhe `ADMIN_PASSWORD` te `.env`, nis PostgreSQL dhe aplikacionin.
2. Hap `/courses` pa login: duhet të kalojë te `/login`; CSS-i duhet të ngarkohet.
3. Provo fjalëkalim gabim: duhet "Përdoruesi ose fjalëkalimi është gabim."
4. Hyr si `user`: hapet `/courses`; navbar-i tregon `user` dhe "Dil".
5. Si `user`, kontrollo që listat dhe detajet hapen, por kontrollet e ndryshimit nuk shfaqen. Provo drejtpërdrejt `/courses/new` ose dërgimin e një kërkese për ndryshim: qasja duhet të refuzohet (403). Si `admin`, shto, ndrysho dhe fshij një kurs dhe instruktor pa kurse. Instruktori me kurse duhet të mbetet, me mesazhin përkatës.
6. Kliko "Dil": hapet `/login?logout` me "U çkyçët me sukses."; `/courses` kërkon përsëri login.
7. Hyr si `admin` dhe provo të njëjtat veprime.
8. I loguar, hap `/courses/999` nëse ky id nuk ekziston: duhet faqja 404.
9. Me `git status --short`, `git ls-files .env` dhe `git check-ignore .env`, kontrollo që `.env` mbetet i pagjurmuar dhe i përjashtuar nga Git.
