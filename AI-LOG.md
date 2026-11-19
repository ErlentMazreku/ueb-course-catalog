# AI-LOG

Çdo përdorim i AI-së që dha kod ose një zgjidhje shënohet këtu.

### 2026-11-12, Plani i projektit dhe drafti i SPEC.md, Erlent, Leuk, Hana, Adonis

- **Pyetja:** I dhashë AI-së (Claude, në chat) udhëzimet e fazës 1 dhe i kërkova një plan në faza dhe një draft të SPEC.md për temën Katalog kursesh.
- **Përgjigjja:** Plan në faza dhe draft i SPEC.md me Instructor 1 → N Course, URL-të, validimet dhe ndarjen e punës.
- **Çka bëmë:** E pranuam planin dhe e vendosëm SPEC.md në root të projektit.
- **Si e verifikuam:** <plotësohet pas rishikimit të SPEC.md me promptin e mësimdhënësit>

### 2026-11-17, Pjesa 1: themeli i projektit, Leuk

- **Pyetja:** I dhashë Claude Code (në terminalin e IntelliJ) detyrën e Pjesës 1 nga skedari prompt-faza1.txt.
- **Përgjigjja:** Claude Code tregoi planin, pastaj përditësoi tabelën e ndarjes së punës te SPEC.md (pjesa 8), plotësoi .gitignore dhe krijoi .env.example, application.properties, layout.html, fragments.html, error/404.html, style.css dhe README.md. Kontrolloi që projekti kompilohet me ./mvnw -q compile.
- **Çka bëmë:** E pranuam planin. Claude vuri re që pom.xml kishte java.version 27, ndërsa ne kemi JDK 25; e ndryshuam në 25 me miratimin tim. Promptin e dhamë nga një skedar, sepse terminali e priste tekstin e gjatë.
- **Si e verifikuam:** E nisëm aplikacionin me ./mvnw spring-boot:run. Në terminal dolën "HikariPool-1 - Start completed" (lidhja me PostgreSQL në Docker, porti 5433) dhe "Started KatalogApplication". Hapëm http://localhost:8080/abc dhe doli faqja jonë 404 me shiritin blu, pra layout, fragmentet dhe CSS punojnë. Kontrolluam që .env është në .gitignore.


### 2026-11-18, Pjesa 2: CRUD i instruktorëve, Erlent

- **Pyetja:** I dhashë Claude Code (aplikacioni desktop) detyrën e Pjesës 2 nga skedari prompt-faza2.txt, bashkë me SPEC.md.
- **Përgjigjja:** Claude Code tregoi planin, pastaj krijoi Instructor.java, InstructorRepository, InstructorService, InstructorController, instructors/list.html dhe instructors/form.html. Kontrolloi që projekti kompilohet me .\mvnw.cmd -q compile.
- **Çka bëmë:** E pranuam planin. Krijuam branch-in pjesa-2-instruktoret. Para kodit rregulluam mjedisin: Docker Desktop, kontejneri katalog-db, .env me DB_PASSWORD të njëjtë me Docker-in, dhe JAVA_HOME te JDK 25. Fusha courses nuk u shtua; vjen në Pjesën 3. Kontrolli "instruktori ka kurse" para fshirjes shtohet po ashtu në Pjesën 3.
- **Si e verifikuam:** E nisëm aplikacionin dhe provuam te /instructors: listën bosh, gabimet pranë fushave, ruajtjen me mesazhin e gjelbër, e-mailin e dyfishtë me shkronja të mëdha, ndryshimin pa rresht të ri, fshirjen dhe /instructors/999/edit që hap faqen 404.

### 2026-11-19, Pjesa 3: kurset dhe relacioni me instruktorin, Hana

- **Pyetja:** I dhashë Claude Code (aplikacioni desktop) detyrën e Pjesës 3, bashkë me SPEC.md, README.md dhe AI-LOG.md.
- **Përgjigjja:** Claude Code tregoi planin, pastaj krijoi CourseCategory, Course, CourseRepository, CourseService, CourseController, HomeController, InstructorConverter, DataLoader, messages.properties dhe faqet courses/list.html, detail.html e form.html. Shtoi fushën courses te Instructor, kontrollin "instruktori ka kurse" para fshirjes dhe kolonën me numrin e kurseve te instructors/list.html. Kontrolloi që projekti kompilohet me .\mvnw.cmd -q compile.
- **Çka bëmë:** E pranuam planin dhe punuam direkt në main, pa branch të ri. Për instruktorin në formë zgjodhëm një Converter të vogël (id -> Instructor) dhe th:selected që krahason id-të, që te ndryshimi instruktori aktual të dalë i zgjedhur. Validatori @ValidCourseSchedule nuk u shtua; mbetet për më vonë.
- **Si e verifikuam:** E nisëm aplikacionin dhe i provuam 9 hapat me kërkesa curl. Kontrolluam që DataLoader shtoi 3 instruktorë dhe 5 kurse dhe që pas rinisjes nuk i dyfishoi. Hapëm / dhe pamë që ridrejton te /courses. Provuam formularin bosh dhe vlerat e gabuara (çmim negativ, kapacitet 0 dhe 501, titull me 2 shkronja) dhe pamë që gabimet shfaqen pranë fushave. Ruajtëm një kurs të ri dhe kontrolluam që del në listë dhe te detajet. Kur e hapëm për ndryshim, kategoria dhe instruktori ishin të zgjedhur dhe ruajtja nuk krijoi rresht të ri. Kontrolluam që /courses/999 dhe /courses/999/edit hapin faqen 404. Në fund, provuam të fshinim një instruktor me kurs dhe fshirja u bllokua. Pasi e fshimë kursin, arritëm ta fshinim edhe instruktorin.

### 2026-11-19, Pjesa 4: Spring Security dhe kontrolli përfundimtar, Adonis

- **Pyetja:** I dhashë Codex detyrën e Pjesës 4, bashkë me SPEC.md, README.md dhe AI-LOG.md, duke kërkuar punë në faza dhe ndalim pas secilës fazë.
- **Përgjigjja:** Codex paraqiti planin dhe, pas miratimit, shtoi varësitë e kërkuara, SecurityConfig, LoginController dhe login.html; integroi navbar-in me Thymeleaf Security dhe shtoi teste MVC për login, logout, qasjen dhe CSRF-in. Përditësoi dokumentimin dhe krahasoi kërkesat e SPEC.md me implementimin.
- **Çka bëmë:** Punuam në pesë faza me miratim ndërmjet tyre. Përdoruesit user dhe admin mbetën in-memory, me fjalëkalime nga konfigurimi dhe PasswordEncoder; CSRF mbeti aktiv dhe çdo i loguar ka qasje të plotë në fazën 1. Testet përdorin vetëm vlera testuese, pa .env dhe pa PostgreSQL. Për ekzekutimin e testeve u përdor JDK 25 nga instalimi i IntelliJ. Te SPEC.md vendosëm Adonis për pjesën 4; kërkimi, REST-i, qasja sipas rolit dhe custom validator mbeten për fazën 2 të lëndës. Nuk ndryshuam kodin e pjesëve 2 dhe 3 gjatë auditimit.
- **Si e verifikuam:** <plotësohet nga Adonis>
