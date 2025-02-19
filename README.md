# DBee Admin 🐝
Cross-platform visual database design and administration tool 

## Tech Stack ⚙️
+ UI: **JavaFX**, **CSS** 
+ Logic: **Java (Maven)**

## ENV Configuration ⚙️
The configuration of the database connection can be accomplished through two primary methods: either by utilizing a form-based interface or by manually editing the `.env` file located in the root directory of the application. The `.env` file should include the following parameters to establish the connection:

| Parameter | Default | Description |
|-----------|---------|-------------|
| DB_URL | `jdbc:mysql://localhost:3306` | Connection url for MySQL database (with JDBC) |
| DB_USERNAME | `root` | Username for elevated database user |
| DB_PASSWORD |  | Password for elevated database user |

> These parameters **MUST** be correctly defined to ensure a successful connection to the database.