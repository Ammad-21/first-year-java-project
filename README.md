# First Year Java Project (FoodDash)

FoodDash is a Java Swing desktop application that simulates a simple “order food from an institution” flow.
It uses an SQLite database (`UMS.db`) and the SQLite JDBC driver (`sqlite-jdbc-3.48.0.0.jar`).

## What the app does
1. **Login / Sign up** (`src/testing/Login.java`)
2. **Browse institutions** from the database (`src/testing/InstitutionHomePage.java`)
3. **Pick foods** (random selection from the FOODS table) and add to a basket (`src/testing/FoodsPage.java`)
4. **Generate an invoice** from the basket (`src/testing/Invoice.java`)
5. **Enter payment details** and save a “payment record” into SQLite (`src/testing/PaymentPage.java`)

## How it works (code + database)
- **UI:** Java Swing (multiple `JFrame` pages).
- **Navigation flow:** `Login` → `InstitutionHomePage` → `FoodsPage` → `Invoice` → `PaymentPage`.
- **Database access:** `src/testing/Database.java` connects to `UMS.db` using `jdbc:sqlite:UMS.db`.
- **Main tables used:**
  - `INSTITUTION` (used for listing/searching institutions)
  - `FOODS` (used to display foods; app selects 12 random foods)
  - `USERS` (used for login/signup)
  - `Payments` (created/used by `PaymentPage` to store submitted payment info)

## Run (Eclipse)
1. Import this folder as an existing Eclipse project.
2. Ensure `sqlite-jdbc-3.48.0.0.jar` is on the build path (already referenced in `.classpath`).
3. Run `src/testing/Login.java` (contains `public static void main`).

### Demo login
- Username: `admin`
- Password: `12345`

## Notes / disclaimer
- This is a **student project demo** and **does not process real payments**.
- For safety on a public repo, `UMS.db` is a **sanitized** database (no saved payment rows). A private copy is kept locally as `UMS_private.db` and is ignored by git.
