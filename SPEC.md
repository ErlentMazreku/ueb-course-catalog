# SPEC: Katalog kursesh

## 1. Përshkrimi
Katalog kursesh është një aplikacion web ku një qendër trajnimi publikon kurset e veta dhe instruktorët që i japin. Përdoruesit shohin kurset dhe kërkojnë sipas titullit dhe kategorisë; administratori shton, ndryshon dhe fshin kurse dhe instruktorë.

## 2. Rolet
- **USER**: sheh listën e kurseve dhe të instruktorëve, hap detajet e një kursi, kërkon kurse (faza 2).
- **ADMIN**: çdo gjë që bën USER-i, plus krijon, ndryshon dhe fshin kurse dhe instruktorë.
- Përdoruesit janë in-memory: `user` (USER) dhe `admin` (ADMIN, USER). Fjalëkalimet lexohen nga skedari `.env`, që nuk shkon në GitHub.
- Faza 1: çdo përdorues i loguar i sheh dhe i përdor të gjitha faqet. Faza 2: qasja sipas rolit, si në tabelën e pjesës 5.

## 3. Entitetet

### Instructor
| Fusha | Tipi | Validimi |
|---|---|---|
| id | Long | gjenerohet automatikisht |
| firstName | String | @NotBlank, @Size(max = 50) |
| lastName | String | @NotBlank, @Size(max = 50) |
| email | String | @NotBlank, @Email, @Size(max = 100), unik (kontrollohet në formë dhe në databazë) |
| expertise | String | @Size(max = 100), opsionale |
| courses | List&lt;Course&gt; | ana tjetër e relacionit, pa validim |

### Course (entiteti kryesor)
| Fusha | Tipi | Validimi |
|---|---|---|
| id | Long | gjenerohet automatikisht |
| title | String | @NotBlank, @Size(min = 3, max = 100) |
| description | String | @Size(max = 1000), opsionale |
| category | CourseCategory (enum) | @NotNull |
| price | BigDecimal | @NotNull, @DecimalMin("0.0"), @Digits(integer = 6, fraction = 2) |
| capacity | Integer | @NotNull, @Min(1), @Max(500) |
| startDate | LocalDate | @NotNull |
| endDate | LocalDate | @NotNull |
| instructor | Instructor | @NotNull (zgjidhet nga lista) |

CourseCategory: PROGRAMMING, DESIGN, BUSINESS, LANGUAGES, DATA. Në faqe shfaqen me emra shqip (Programim, Dizajn, Biznes, Gjuhë të huaja, Shkenca e të dhënave).

Të dhëna testuese: kur databaza është bosh, `DataLoader` shton 3 instruktorë dhe 5 kurse.

## 4. Relacioni
Instructor 1 → N Course: një instruktor jep shumë kurse, një kurs ka saktësisht një instruktor.
- Course: `@ManyToOne(optional = false)` me `@JoinColumn(name = "instructor_id")`. Kolona e lidhjes është në tabelën `course`.
- Instructor: `@OneToMany(mappedBy = "instructor")`, vetëm për të lexuar kurset e instruktorit (p.sh. numri i kurseve në listë).
- Pse jo ManyToMany: në këtë katalog çdo kurs e jep një instruktor i vetëm.
- Fshirja: instruktori që ka kurse nuk fshihet; faqja tregon mesazhin "Ky instruktor ka kurse. Fshijini ose ndërroni instruktorin e tyre fillimisht."

## 5. Faqet dhe URL-të
| URL | Metoda | Faqja ose veprimi | Qasja (faza 1) | Qasja (faza 2) |
|---|---|---|---|---|
| /login | GET | login.html | të gjithë | të gjithë |
| /login | POST | Spring Security kontrollon emrin dhe fjalëkalimin | të gjithë | të gjithë |
| /logout | POST | çkyçja, kthen në /login?logout | i loguar | i loguar |
| / | GET | ridrejton në /courses | i loguar | USER, ADMIN |
| /courses | GET | courses/list.html | i loguar | USER, ADMIN |
| /courses/{id} | GET | courses/detail.html | i loguar | USER, ADMIN |
| /courses/new | GET | courses/form.html (bosh) | i loguar | ADMIN |
| /courses | POST | ruan kursin e ri, pastaj /courses | i loguar | ADMIN |
| /courses/{id}/edit | GET | courses/form.html (e mbushur) | i loguar | ADMIN |
| /courses/{id}/edit | POST | ruan ndryshimet, pastaj /courses | i loguar | ADMIN |
| /courses/{id}/delete | POST | fshin kursin, pastaj /courses | i loguar | ADMIN |
| /instructors | GET | instructors/list.html | i loguar | USER, ADMIN |
| /instructors/new | GET | instructors/form.html (bosh) | i loguar | ADMIN |
| /instructors | POST | ruan instruktorin e ri, pastaj /instructors | i loguar | ADMIN |
| /instructors/{id}/edit | GET | instructors/form.html (e mbushur) | i loguar | ADMIN |
| /instructors/{id}/edit | POST | ruan ndryshimet, pastaj /instructors | i loguar | ADMIN |
| /instructors/{id}/delete | POST | fshin, nëse instruktori s'ka kurse | i loguar | ADMIN |
| /courses?q=...&category=... | GET | lista e filtruar | – | USER, ADMIN |
| /api/courses, /api/courses/{id} | GET | JSON (pjesa 7) | – | USER, ADMIN |

Të gjitha faqet përdorin `layout.html` dhe `fragments.html`. Nëse një id nuk ekziston, shfaqet `error/404.html`.

## 6. Custom validator
- Emri: `@ValidCourseSchedule`, mbi klasën `Course`; logjika në `CourseScheduleValidator`.
- Çka kontrollon: (1) `endDate` është pas `startDate`; (2) kursi nuk zgjat më shumë se 12 muaj.
- Nëse njëra datë mungon, validatori e lë kalimin; gabimin e jep `@NotNull`.
- Mesazhi: "Data e mbarimit duhet të jetë pas datës së fillimit dhe kursi nuk mund të zgjasë më shumë se 12 muaj."
- Gabimi shfaqet pranë fushës `endDate`.

## 7. Kërkimi dhe REST
Kërkimi, në faqen /courses:
- Kriteri 1: fjalë nga titulli (parametri `q`), pa dallim shkronjash të mëdha e të vogla.
- Kriteri 2: kategoria (parametri `category`), nga një listë.
- Të dy janë opsionalë dhe kombinohen me "dhe"; pa kritere shfaqen të gjitha kurset.

REST:
- `GET /api/courses` kthen listën e kurseve në JSON; pranon të njëjtat `q` dhe `category`.
- `GET /api/courses/{id}` kthen një kurs, ose 404 nëse nuk ekziston.
- Shembull: `{"id": 1, "title": "Java për fillestarë", "category": "PROGRAMMING", "price": 120.00, "capacity": 25, "startDate": "2026-12-01", "endDate": "2027-02-28", "instructorName": "Arta Krasniqi"}`
- Kush e thërret: përdoruesit e loguar (USER, ADMIN), p.sh. nga shfletuesi pas login-it. Vetëm lexim.
- JSON kthehet nga një `CourseDto`, jo nga vetë entiteti, që Instructor dhe Course të mos e thërrasin njëri-tjetrin pa fund.

## 8. Ndarja e punës
Faza 1 e lëndës u nda në 4 pjesë, një për secilin anëtar, të bëra me radhë.

| Pjesa | Anëtari        | Çka bën |
|---|----------------|---|
| 1 | Leuk           | projekti, Docker, .env.example, application.properties, layout.html, fragments.html, style.css, error/404.html, README.md, SPEC.md |
| 2 | Erlent         | Instructor: entiteti, repository, service, controller, instructors/list.html dhe form.html |
| 3 | Hana           | Course: enum-i, entiteti, relacioni me Instructor, repository, service, controller, faqet e kurseve, HomeController, DataLoader |
| 4 | Adonis         | Spring Security: SecurityConfig, LoginController, login.html, logout; kontrolli përfundimtar i SPEC.md me kodin |

## 9. Jashtë fushës
- Regjistrimi i studentëve në kurse dhe pagesat.
- Krijimi i llogarive nga faqja; përdoruesit janë vetëm in-memory.
- Ngarkimi i fotove ose i dokumenteve.
- Dërgimi i e-mail-ave.
- Më shumë se një instruktor për kurs.
- Paginimi i listave.
- Faqja në disa gjuhë dhe publikimi online.
