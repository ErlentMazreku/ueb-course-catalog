# AI-LOG

Çdo përdorim i AI-së që dha kod ose një zgjidhje shënohet këtu.

### 2026-11-12, Plani i projektit dhe drafti i SPEC.md, Leuk

- **Pyetja:** I dhashë AI-së (Claude, në chat) udhëzimet e fazës 1 dhe i kërkova një plan në faza dhe një draft të SPEC.md për temën Katalog kursesh.
- **Përgjigjja:** Plan në faza dhe draft i SPEC.md me Instructor 1 → N Course, URL-të, validimet dhe ndarjen e punës.
- **Çka bëmë:** E pranuam planin dhe e vendosëm SPEC.md në root të projektit.
- **Si e verifikuam:** <plotësohet pas rishikimit të SPEC.md me promptin e mësimdhënësit>

### 2026-11-17, Pjesa 1: themeli i projektit, Leuk

- **Pyetja:** I dhashë Claude Code (në terminalin e IntelliJ) detyrën e Pjesës 1 nga skedari prompt-faza1.txt.
- **Përgjigjja:** Claude Code tregoi planin, pastaj përditësoi tabelën e ndarjes së punës te SPEC.md (pjesa 8), plotësoi .gitignore dhe krijoi .env.example, application.properties, layout.html, fragments.html, error/404.html, style.css dhe README.md. Kontrolloi që projekti kompilohet me ./mvnw -q compile.
- **Çka bëmë:** E pranuam planin. Claude vuri re që pom.xml kishte java.version 27, ndërsa ne kemi JDK 25; e ndryshuam në 25 me miratimin tim. Promptin e dhamë nga një skedar, sepse terminali e priste tekstin e gjatë.
- **Si e verifikuam:** E nisëm aplikacionin me ./mvnw spring-boot:run. Në terminal dolën "HikariPool-1 - Start completed" (lidhja me PostgreSQL në Docker, porti 5433) dhe "Started KatalogApplication". Hapëm http://localhost:8080/abc dhe doli faqja jonë 404 me shiritin blu, pra layout, fragmentet dhe CSS punojnë. Kontrolluam që .env është në .gitignore.