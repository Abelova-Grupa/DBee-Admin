# DBee Admin 🐝
![Static Badge](https://img.shields.io/badge/Latest-0.1.0--SNAPSHOT-gold?style=flat)

Cross-platform visual database design and administration tool

## Requirements 📝
+ [Java Runtime Environment](https://www.java.com/en/download/manual.jsp) (*That you have already installed for Minecraft*)
+ [MySQL](https://www.mysql.com/) or [MariaDB](https://mariadb.org/)
+ [mysqldump](https://dev.mysql.com/doc/refman/8.4/en/mysqldump.html)

> Please note that your RDBMS must be configured with a privileged user.

## Tech Stack ⚙️
+ UI: **JavaFX**, **CSS** 
+ Logic: **Java**
+ Build: **Maven**

## Keyboard shortcuts ⌨

### Browser shortcuts️
| Command               |         Shortcut         |
|-----------------------|:------------------------:|
| Preview table content | `double-click` on table  |

### Editor shortcuts
| Command               | Shortcut |
|-----------------------|:--------:|
| Execute selected line |   `F5`   |
| Execute entire script |   `F7`   |

## ENV Configuration 💾
The configuration of the database connection can be accomplished through two primary methods: either by utilizing a form-based interface or by manually editing the `.env` file located in the root directory of the application. The `.env` file should include the following parameters to establish the connection:

| Parameter   | Default                       | Description                                   |
|-------------|-------------------------------|-----------------------------------------------|
| DB_URL      | `jdbc:mysql://localhost:3306` | Connection url for MySQL database (with JDBC) |
| DB_USERNAME | `root`                        | Username for elevated database user           |
| DB_PASSWORD |                               | Password for elevated database user           |

> These parameters **MUST** be correctly defined to ensure a successful connection to the database.
