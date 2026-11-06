# 🎓 Student Course Registration System

> A full-stack web application for managing student course registrations, built with **Spring Boot**, **Thymeleaf** and **PostgreSQL** (hosted free on **Neon**).

**🌐 Live demo:** `https://abdullah-alhar.github.io/<your-repo-name>/` *(replace after deploying)*
**⚙️ Backend:** `https://<your-render-service>.onrender.com` *(replace after deploying)*

> ⏳ The backend runs on Render's free plan, which **sleeps after 15 minutes** without visitors.
> The first visit shows *"Server is waking up, please wait..."* for up to a minute. That is normal.

---

## 📋 Table of Contents

- [About the Project](#-about-the-project)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [How It Is Hosted](#-how-it-is-hosted)
- [Project Structure](#-project-structure)
- [Run It on Your Computer](#-run-it-on-your-computer)
- [Deploy It Online (Neon + Render + GitHub Pages)](#-deploy-it-online-neon--render--github-pages)
- [Import the Old .txt Data](#-import-the-old-txt-data-one-time)
- [Environment Variables](#-environment-variables)
- [Troubleshooting](#-troubleshooting)
- [OOP Concepts Used](#-oop-concepts-used)
- [User Roles](#-user-roles)

---

## 📖 About the Project

The **Student Course Registration System** is a web platform for managing students, lecturers, courses and registrations in an academic institution. **Admins**, **Students** and **Lecturers** each get their own screens and permissions.

All data (including uploaded receipts and course materials) is stored in a **PostgreSQL database**, so nothing is lost when the free server restarts. Passwords are stored as **BCrypt hashes**, never as plain text.

---

## ✨ Features

### 👨‍💼 Admin
- Manage students, lecturers and admins
- Approve or reject course registrations
- Verify and process student payments
- Manage course categories, rooms and schedules
- Upload and manage course materials

### 👨‍🎓 Student
- Register for available courses
- View registration status (Pending / Approved / Rejected)
- Make payments and upload payment receipts (PDF)
- View course schedules and materials
- Update personal profile

### 👨‍🏫 Lecturer
- View assigned courses and schedules
- Upload course materials
- View student feedback

### 🔐 Security
- Session-based login with role-based access (custom `AuthInterceptor`)
- Passwords hashed with **BCrypt** (`BCryptPasswordEncoder`)
- Database password and URLs read from **environment variables**, never written in the code

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4, Spring MVC, Thymeleaf |
| Database | PostgreSQL on **Neon** (free), accessed with Spring `JdbcTemplate` + HikariCP |
| Passwords | Spring Security Crypto (`BCryptPasswordEncoder`) |
| Frontend landing page | Static HTML/JS on **GitHub Pages** |
| Backend hosting | **Render** (free plan, Docker) |
| CI/CD | GitHub Actions (deploys the frontend to Pages) |
| Build | Apache Maven |

---

## 🏗 How It Is Hosted

```
  Visitor
     │
     ▼
 GitHub Pages  (abdullah-alhar.github.io/<your-repo-name>)
  frontend/index.html  ──►  checks  GET /health  every 3 s
     │                       ("Server is waking up, please wait...")
     │  when the server answers
     ▼
 Render  (Docker, Spring Boot app: all pages, login, uploads)
     │
     ▼
 Neon  (PostgreSQL: all data + uploaded files)
```

- **GitHub Pages** hosts a small static page. It wakes the backend and then sends the visitor to the login page.
- **Render** runs the full Spring Boot app (all 14 pages, login, file uploads).
- **Neon** keeps the data safe. Free hosts wipe their disk on restart, so nothing is kept in files.

---

## 📁 Project Structure

```
student-registration/
├── .github/workflows/deploy-frontend.yml   # Publishes frontend/ to GitHub Pages
├── frontend/                               # Static site for GitHub Pages
│   ├── index.html                          #   "Server is waking up..." page
│   └── config.js                           #   ← the ONE place to set the backend URL
├── src/main/java/com/example/Student_Course_Registration_System/
│   ├── config/
│   │   ├── DatabaseConfig.java             # Reads DATABASE_URL, creates tables
│   │   ├── PasswordHasher.java             # BCrypt hashing / checking
│   │   └── WebConfig.java                  # Login guard + CORS
│   ├── controller/                         # Web pages (+ HealthController: /health)
│   ├── service/                            # Business rules
│   ├── repository/                         # ALL database code (SQL) lives here
│   ├── importer/LegacyTxtImporter.java     # One-time .txt → database import
│   ├── model/  enums/  interceptor/
├── src/main/resources/
│   ├── schema.sql                          # Database tables
│   ├── application.properties              # Settings (secrets come from env vars)
│   ├── templates/                          # Thymeleaf pages
│   └── static/                             # CSS / JS
├── Dockerfile                              # Backend image for Render
├── .env.example                            # Template for your local .env
└── pom.xml
```

---

## 💻 Run It on Your Computer

**You need:** Java 21+, Maven 3.9+, and a Neon account (free). Node.js is optional (only for the Neon CLI).

1. **Clone the project**
   ```bash
   git clone https://github.com/abdullah-alhar/<your-repo-name>.git
   cd <your-repo-name>
   ```

2. **Give the app your database address.** Pick one option:
   - **Option A: Neon CLI** (writes `.env.local` for you):
     ```bash
     npx neon@latest login
     npx neon@latest env pull
     ```
   - **Option B: by hand:** copy the template and paste your Neon connection strings into it:
     ```bash
     cp .env.example .env
     ```

3. **Start the app**
   ```bash
   mvn spring-boot:run
   ```
   On first start the app creates all tables automatically (from `schema.sql`).

4. Open **http://localhost:8080** and log in.

> `.env` and `.env.local` are in `.gitignore`. Never commit them.
> (The `mvnw` wrapper in this repo is incomplete, so use `mvn` directly.)

---

## 🚀 Deploy It Online (Neon + Render + GitHub Pages)

Do the three parts **in this order**.

### Part 1: Neon (database)

1. Sign up at **https://neon.tech** (free) and create a project. *(Already done for this project: `still-dream-99108855`.)*
2. In the Neon dashboard click **Connect**.
3. Copy **two** connection strings:
   - **Pooled**: with *Connection pooling* **ON** (the host contains `-pooler`). This becomes `DATABASE_URL`.
   - **Direct**: with *Connection pooling* **OFF** (no `-pooler`). This becomes `DATABASE_URL_UNPOOLED`.

   They look like: `postgresql://neondb_owner:xxxx@ep-xxxx-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require`
4. Keep them somewhere private. You need them in Part 2.

> Why two? The running app uses the **pooled** one. Creating tables and importing data use the **direct** one, which is what Neon recommends.

### Part 2: Render (backend)

1. Push this project to GitHub (without `.env` files: they are ignored automatically).
2. Sign up at **https://render.com** with your GitHub account.
3. Click **New → Web Service** and pick this repository.
4. Fill in:
   | Setting | Value |
   |---|---|
   | Language / Runtime | **Docker** (it finds the `Dockerfile`) |
   | Branch | `main` |
   | Instance type | **Free** |
   | Health Check Path | `/health` *(under "Advanced")* |
5. Under **Environment Variables** add:
   | Key | Value |
   |---|---|
   | `DATABASE_URL` | your **pooled** Neon string |
   | `DATABASE_URL_UNPOOLED` | your **direct** Neon string |
   | `ALLOWED_ORIGINS` | `https://abdullah-alhar.github.io` |
   | `SESSION_COOKIE_SECURE` | `true` |

   *(Don't set `PORT`: Render sets it for you.)*
6. Click **Create Web Service**. The first build takes ~5–10 minutes.
7. When it says **Live**, open `https://<your-service>.onrender.com/health`. You should see `{"status":"UP"}`.
   Then open `/health/db`. You should see `"database":"UP"`.

### Part 3: GitHub Pages (frontend)

1. Open **`frontend/config.js`** and put in your Render URL (no `/` at the end):
   ```js
   window.APP_CONFIG = { API_URL: "https://<your-service>.onrender.com" };
   ```
2. Commit and push to `main`.
3. On GitHub: **Settings → Pages → Build and deployment → Source: "GitHub Actions"**.
4. Go to the **Actions** tab. The workflow *"Deploy frontend to GitHub Pages"* runs (or click **Run workflow**).
5. Visit **`https://abdullah-alhar.github.io/<your-repo-name>/`**. You'll see *"Server is waking up, please wait..."*, and then the login page opens.
6. Put both links at the top of this README.

---

## 📥 Import the Old .txt Data (one time)

The old data files were moved to `legacy-data/` (git-ignored because they contain the old plain-text passwords).
The importer copies them into the database and **hashes every password with BCrypt**.

```bash
# make sure .env / .env.local points to the database you want to fill, then:
mvn spring-boot:run -Dspring-boot.run.profiles=import
```

- It prints how many rows were added per table, then stops.
- Running it twice is safe: rows that already exist are skipped.
- It's all-or-nothing: if anything fails, nothing is saved.
- Everyone can then log in with their **old** password.
- Using the Neon CLI? Run `npx neon@latest checkout production` first so `.env.local` points to the production branch.

After a successful import you can delete `legacy-data/` (keep a private backup if you like).

---

## 🔑 Environment Variables

| Name | Needed? | What it is |
|---|---|---|
| `DATABASE_URL` | **Yes** | Neon **pooled** connection string (used by the app) |
| `DATABASE_URL_UNPOOLED` | Recommended | Neon **direct** connection string (creating tables, import). Falls back to `DATABASE_URL`. |
| `ALLOWED_ORIGINS` | No | Websites allowed to call the backend. Default `https://abdullah-alhar.github.io` |
| `SESSION_COOKIE_SECURE` | On Render | `true` = login cookie only over HTTPS. Leave unset locally. |
| `PORT` | No | Set by Render automatically. Default `8080`. |
| `DB_USERNAME` / `DB_PASSWORD` | Only for `jdbc:` URLs | For a local PostgreSQL with a `jdbc:postgresql://...` URL |
| `JAVA_OPTS` | No | Override Java memory flags (see `Dockerfile`) |

---

## 🩺 Troubleshooting

| Problem | Fix |
|---|---|
| Page stays on *"Server is waking up"* for minutes | Open the Render dashboard → **Logs**. Usually a wrong `DATABASE_URL`. |
| `/health/db` says `DOWN` | Check both Neon strings on Render (no spaces, full string including `?sslmode=require...`). |
| App says `DATABASE_URL is not set` | Create `.env` from `.env.example`, or run `npx neon@latest env pull`. |
| Can't log in after import | Use the **old** password; the import hashed it, it did not change it. |
| GitHub Pages shows 404 | Settings → Pages → Source must be **GitHub Actions**; check the Actions tab for errors. |
| *"Backend address is not set yet"* | Edit `frontend/config.js` and push again. |
| Render runs out of memory | Memory is already limited in the `Dockerfile` (`-XX:MaxRAMPercentage=75`). Check the Render logs. |

---

## 🧠 OOP Concepts Used

| Concept | Implementation |
|---|---|
| **Abstraction** | `Person` is an abstract class with `getDetails()` as an abstract method |
| **Inheritance** | `Student`, `Lecturer` and `Admin` extend `Person` |
| **Encapsulation** | All fields are `private`; exposed only through controlled getters/setters |
| **Polymorphism** | `getDetails()` is overridden in each subclass |
| **Composition** | `Course` owns a `CourseCategory`; `Registration` links `Student` and `Course` |

---

## 👥 User Roles

| Role | Access |
|---|---|
| **Admin** | Full access: manage everything, approve registrations, verify payments |
| **Lecturer** | View courses and schedules, upload materials, view feedback |
| **Student** | Register for courses, make payments, view schedule, update profile |

Unauthenticated users are automatically redirected to the login page.

---

## 📄 License

This project was developed for academic purposes as part of a university assignment.
