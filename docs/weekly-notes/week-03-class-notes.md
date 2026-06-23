# Week 03 Class Notes — SC-403 Web Application Development and Patterns

> These notes summarize the most relevant points discussed during the third class.  
> They are intended as weekly class notes, not as a full explanation of every concept.

---

## 📌 Class Context

This class focused on connecting the **Tienda** project to a database and creating the first database-backed feature of the course project.

The main work was centered on:

- Verifying that MySQL was running locally.
- Opening and using MySQL Workbench.
- Executing the `CreaTablas` SQL script.
- Creating the local database and tables.
- Adding database dependencies to `pom.xml`.
- Configuring `application.properties` for database access.
- Creating the `domain`, `repository`, `service`, and `controller` packages.
- Mapping the `categoria` table to a Java class.
- Creating a category listing page with Thymeleaf.
- Preparing a cloud database connection using Aiven.

> [!NOTE]
> After running the project, the web application can be opened in the browser using:
>
> ```text
> http://localhost
> ```
>
> This works because the project uses port `80`.

---

## 🛢️ MySQL Service Verification

Before working with the database, the professor explained that the MySQL service must be running on the computer.

On Windows, this can be verified from:

```text
Services
```

The MySQL service should appear in the list and must be running.

If the service is stopped, it can be started manually. The professor also mentioned that automatic startup depends on each computer and its available resources.

> [!IMPORTANT]
> Before continuing with the database setup, it is necessary to verify that:
>
> - MySQL is installed.
> - The MySQL service is running.
> - MySQL Workbench is installed.
> - The MySQL password is known and stored in a safe place.

---

## 🧰 MySQL Workbench

MySQL Workbench was used as the graphical tool to access the database.

The professor explained the difference between:

- The **MySQL service**, which is the database engine running in the background.
- **MySQL Workbench**, which is the graphical program used to connect to the database and execute SQL scripts.

The local connection should use a local host address and the correct MySQL port.

Common ports:

```text
3306
3307
```

> [!NOTE]
> The default MySQL port is usually `3306`, but some computers may use a different port.  
> The correct port must be checked in the local MySQL connection.

---

## 🧾 `CreaTablas` SQL Script

For this class, the professor used a resource file named:

```text
CreaTablas
```

This file contains the SQL instructions needed to create the database and the required tables for the course project.

The script was opened using MySQL Workbench and executed there.

The purpose of the script was to:

- Create the course database.
- Create the required tables.
- Insert initial sample data.
- Prepare the database so the Spring Boot project can read category information.

> [!NOTE]
> The `CreaTablas` file is not Java code.  
> It is a SQL script used to prepare the database.

The professor also mentioned that the file could be copied into the project as a backup, but it is executed from MySQL Workbench.

---

## 🗂️ Table Focus: `categoria`

The class focused mainly on the database table:

```text
categoria
```

This table is used to display the category listing in the web application.

The table includes fields such as:

| Field | Purpose |
|---|---|
| `id_categoria` | Category identifier |
| `descripcion` | Category name or description |
| `ruta_imagen` | Image path associated with the category |
| `activo` | Indicates whether the category is active |

The professor did not explain the full database structure in detail during this class. The focus was specifically on making the project read and display category records.

---

## 📦 `pom.xml` Database Dependencies

Before connecting the Spring Boot project to the database, new dependencies were added to `pom.xml`.

Main dependencies added or reviewed:

| Dependency | Purpose |
|---|---|
| `mysql-connector-j` | Allows the project to connect to MySQL |
| `spring-boot-starter-data-jpa` | Adds JPA and repository support |
| `spring-boot-starter-validation` | Adds validation annotations such as `@NotNull` and `@Size` |

Example:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

The JPA dependency is important because it allows the project to use Spring Data repositories, such as:

```java
JpaRepository
```

This avoids writing every basic SQL query manually.

---

## 🧩 IntelliJ Maven Note

The professor used NetBeans during class, but this project is being worked on with IntelliJ IDEA.

In IntelliJ, dependencies are not added exactly the same way as in NetBeans. They can be added manually in `pom.xml`, and then Maven must be reloaded.

The Maven reload option is:

```text
Maven panel → Reload All Maven Projects
```

> [!NOTE]
> `Reload All Maven Projects` only reloads the Maven configuration from `pom.xml`.  
> It does not perform a full clean and build.

To build the project from IntelliJ using Maven, the lifecycle options are:

```text
Maven panel → Lifecycle → clean
Maven panel → Lifecycle → package
```

From the terminal, the Maven Wrapper can be used:

```powershell
.\mvnw.cmd clean package
```

> [!WARNING]
> If the `clean` phase fails because Windows cannot delete the `target` folder, it may be because a process is locking generated files.  
> In that case, the application should be stopped and the `target` folder can be deleted manually before running the build again.

---

## ⚙️ `application.properties` Database Configuration

The `application.properties` file was updated to include the database connection.

The local database configuration follows this general structure:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/techshop
spring.datasource.username=database_user
spring.datasource.password=database_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

Purpose of each property:

| Property | Purpose |
|---|---|
| `spring.datasource.url` | Defines the database location |
| `spring.datasource.username` | Defines the database user |
| `spring.datasource.password` | Defines the database password |
| `spring.datasource.driver-class-name` | Defines the MySQL driver |
| `spring.jpa.database-platform` | Defines the Hibernate dialect for MySQL |

SQL logging was also configured for development:

```properties
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.type.descriptor.sql.BasicBinder=TRACE
```

This helps show SQL information in the application logs.

> [!WARNING]
> Real database passwords should not be committed to a public GitHub repository.  
> For public repositories or production environments, credentials should be handled with environment variables.

---

## 🧱 Project Layers Introduced

This class introduced four important project packages:

```text
domain
repository
service
controller
```

These packages organize the application by responsibility.

| Package | Purpose |
|---|---|
| `domain` | Contains classes that represent database tables |
| `repository` | Contains interfaces used to access database data |
| `service` | Contains business logic and intermediate operations |
| `controller` | Handles web requests and sends data to the views |

The flow created during this class can be understood as:

```text
Database table
→ Domain entity
→ Repository
→ Service
→ Controller
→ Thymeleaf view
```

---

## 🧬 `Categoria` Domain Entity

The first domain class created was:

```text
Categoria.java
```

Location:

```text
src/main/java/com/tienda/domain/Categoria.java
```

This class maps the database table:

```text
categoria
```

Purpose of this class:

- Represent one row from the `categoria` table.
- Define Java attributes that match the database columns.
- Use JPA annotations to connect the class with the database.
- Use validation annotations to define field restrictions.

Example structure:

```java
package com.tienda.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "categoria")
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "descripcion", unique = true, nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    private String descripcion;

    @Column(name = "ruta_imagen", length = 1024)
    @Size(max = 1024)
    private String rutaImagen;

    private boolean activo;
}
```

---

## 🏷️ Annotation Overview

Several annotations were used during this class.

| Annotation | Source | Purpose |
|---|---|---|
| `@Data` | Lombok | Generates getters, setters, `toString`, `equals`, and `hashCode` |
| `@Entity` | JPA / Jakarta Persistence | Marks the class as a database entity |
| `@Table` | JPA / Jakarta Persistence | Defines the database table mapped to the class |
| `@Id` | JPA / Jakarta Persistence | Marks the primary key |
| `@GeneratedValue` | JPA / Jakarta Persistence | Indicates that the ID is generated automatically |
| `@Column` | JPA / Jakarta Persistence | Defines column names and restrictions |
| `@NotNull` | Jakarta Validation | Validates that a value cannot be null |
| `@Size` | Jakarta Validation | Validates the maximum or minimum size of a value |
| `@Repository` | Spring | Marks the repository layer |
| `@Service` | Spring | Marks the service layer |
| `@Controller` | Spring MVC | Marks the web controller layer |
| `@RequestMapping` | Spring MVC | Maps a base URL path to a controller |
| `@GetMapping` | Spring MVC | Maps a GET request to a controller method |
| `@Transactional` | Spring Transaction Management | Defines transactional behavior |

> [!NOTE]
> The professor explained these annotations in a practical way while creating the project files.  
> The class did not go deeply into how annotations work internally in Java.

---

## 🧾 `Serializable` and `serialVersionUID`

The `Categoria` class implements:

```java
Serializable
```

This allows objects of the class to be serialized.

The class also includes:

```java
private static final long serialVersionUID = 1L;
```

The professor explained that this is commonly added to classes that implement serialization.

In this project, the important idea is that `Categoria` objects can be handled safely when they need to be transferred or processed internally by the framework.

---

## 🗃️ `CategoriaRepository`

The repository interface was created inside:

```text
src/main/java/com/tienda/repository/CategoriaRepository.java
```

The interface extends:

```java
JpaRepository<Categoria, Integer>
```

This gives the project access to common database operations, such as:

- `findAll()`
- `findById()`
- `save()`
- `delete()`

Example:

```java
package com.tienda.repository;

import com.tienda.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByActivoTrue();

}
```

The method:

```java
findByActivoTrue()
```

is a derived query method.

It returns only the categories where:

```text
activo = true
```

> [!NOTE]
> The professor mentioned that derived queries will be explained in more detail in Week 08.

---

## 🧠 `CategoriaService`

The service class was created inside:

```text
src/main/java/com/tienda/service/CategoriaService.java
```

The service uses the repository to recover category records from the database.

Example:

```java
package com.tienda.service;

import com.tienda.domain.Categoria;
import com.tienda.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        }
        return categoriaRepository.findAll();
    }
}
```

Purpose of this class:

- Connect the controller with the repository.
- Keep database access logic out of the controller.
- Decide whether all categories or only active categories should be returned.

The method:

```java
getCategorias(boolean activo)
```

works as follows:

| Parameter value | Result |
|---|---|
| `true` | Returns only active categories |
| `false` | Returns all categories |

The annotation:

```java
@Transactional(readOnly = true)
```

indicates that the method only reads data and does not modify the database.

---

## 🧭 `CategoriaController`

The controller was created inside:

```text
src/main/java/com/tienda/controller/CategoriaController.java
```

This controller handles requests that start with:

```text
/categoria
```

The listing method handles:

```text
/categoria/listado
```

Example:

```java
package com.tienda.controller;

import com.tienda.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        var categorias = categoriaService.getCategorias(false);

        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());

        return "categoria/listado";
    }
}
```

Purpose of this controller:

1. Receive the request from the browser.
2. Ask the service for the category list.
3. Add the category list to the model.
4. Add the total number of categories to the model.
5. Return the Thymeleaf view.

The model attributes added were:

| Attribute | Purpose |
|---|---|
| `categorias` | Sends the category list to the HTML page |
| `totalCategorias` | Sends the number of categories to the HTML page |

> [!IMPORTANT]
> The view returned by the controller must match the real template location.  
> For `return "categoria/listado";`, the file must exist at:
>
> ```text
> src/main/resources/templates/categoria/listado.html
> ```

---

## 🖼️ `listado.html`

The category listing page was created inside:

```text
src/main/resources/templates/categoria/listado.html
```

The professor created the `categoria` folder inside `templates`, copied the existing `index.html`, and renamed the copy to:

```text
listado.html
```

The page uses general layout fragments:

```html
<head th:replace="~{general/fragmentos :: head}"></head>
<header th:replace="~{general/fragmentos :: header}"></header>
<footer th:replace="~{general/fragmentos :: footer}"></footer>
```

It also uses category-specific fragments:

```html
<section th:replace="~{categoria/fragmentos :: agregar}"></section>
<section th:replace="~{categoria/fragmentos :: listado}"></section>
```

Purpose:

- Keep `listado.html` short.
- Reuse the general layout.
- Insert the category listing content from category fragments.
- Display the category data received from the controller.

---

## 🧩 Category Fragments

A new fragment file was created inside:

```text
src/main/resources/templates/categoria/fragmentos.html
```

This file contains reusable sections for category-related views.

The fragments created or included were:

| Fragment | Purpose |
|---|---|
| `agregar` | Shows the add category button and modal |
| `listado` | Shows the category table |
| `editar` | Prepares the edit category modal |
| `confirmarEliminar` | Prepares the delete confirmation modal |

The listing fragment uses Thymeleaf to loop through the category list:

```html
<tr th:each="c : ${categorias}">
    <td>[[${c.idCategoria}]]</td>
    <td>[[${c.descripcion}]]</td>
    <td><img th:src="@{${c.rutaImagen}}" alt="noData" height="75"/></td>
    <td th:text="${c.activo} ? 'Activa':'Inactiva'"></td>
</tr>
```

Meaning:

| Expression | Purpose |
|---|---|
| `th:each="c : ${categorias}"` | Iterates through the category list |
| `${c.idCategoria}` | Prints the category ID |
| `${c.descripcion}` | Prints the category description |
| `${c.rutaImagen}` | Reads the image path |
| `${c.activo}` | Checks if the category is active |

> [!NOTE]
> The add, edit, and delete fragments appear in the file, but their full functionality is developed later.

---

## 🔁 Thymeleaf Data Flow

The category listing works through this flow:

```text
Browser
→ /categoria/listado
→ CategoriaController
→ CategoriaService
→ CategoriaRepository
→ MySQL database
→ Model attributes
→ listado.html
→ categoria/fragmentos.html
```

This means the page is no longer only static HTML. It is now reading real records from the database.

The controller adds the data:

```java
model.addAttribute("categorias", categorias);
model.addAttribute("totalCategorias", categorias.size());
```

Then Thymeleaf reads that data from the HTML templates.

---

## 🌐 Local Execution

After running the project, the category listing can be opened at:

```text
http://localhost/categoria/listado
```

If the project is not using port `80`, then the URL would usually be:

```text
http://localhost:8080/categoria/listado
```

In this project, the expected local URL is:

```text
http://localhost/categoria/listado
```

because `server.port=80` is configured in `application.properties`.

---

## ⚠️ Template Resolution Error

During this work, an important Thymeleaf error can happen if the template path does not match the controller return value.

Example problem:

```text
Error resolving template [/categoria/listado]
```

This means Thymeleaf could not find the requested template.

The expected file location is:

```text
src/main/resources/templates/categoria/listado.html
```

The controller should return:

```java
return "categoria/listado";
```

> [!WARNING]
> The folder name matters.  
> `categoria` and `categorias` are different paths.

A similar error can happen with fragments.

Example:

```text
Error resolving fragment: "~{'general/fragmentos' :: agregar}"
```

This means Thymeleaf is looking for a fragment named `agregar` inside:

```text
templates/general/fragmentos.html
```

If the fragment was created inside:

```text
templates/categoria/fragmentos.html
```

then the reference must be:

```html
<section th:replace="~{categoria/fragmentos :: agregar}"></section>
```

---

## ☁️ Aiven Cloud Database

At the end of the class, the professor started the cloud database setup using Aiven.

The purpose of using Aiven is to have a database available outside the local computer.

This is important because deployment platforms such as Render cannot access a database that only exists on the student’s machine.

If the deployed application uses:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/techshop
```

then `localhost` means the Render server, not the student’s computer.

Therefore, the deployed application needs a database hosted in the cloud.

---

## 🛠️ Aiven Service Setup

The general Aiven process was:

1. Create or log in to an Aiven account.
2. Create a project.
3. Create a new database service.
4. Select MySQL.
5. Wait for the service to finish building.
6. Copy the connection information.
7. Create a connection in MySQL Workbench using the Aiven data.
8. Execute `CreaTablas` in the cloud database.
9. Update `application.properties` with the cloud database connection.

The connection data needed from Aiven includes:

| Aiven value | Used for |
|---|---|
| Host | Database server address |
| Port | Database port |
| User | Database username |
| Password | Database password |
| Database | Default database/schema |

> [!IMPORTANT]
> The connection data must come from the Aiven **MySQL** service.  
> Data from Kafka, Kafka REST, or Schema Registry is not valid for MySQL Workbench.

---

## ⚙️ Updating `application.properties` for Aiven

After creating the Aiven MySQL service, the database connection in `application.properties` must be updated.

The local connection:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/techshop
```

is replaced with a cloud connection using the Aiven host and port.

General structure:

```properties
spring.datasource.url=jdbc:mysql://aiven-host:aiven-port/database-name
spring.datasource.username=aiven-user
spring.datasource.password=aiven-password
```

> [!WARNING]
> Aiven credentials should be protected.  
> If the repository is public, the password should not be written directly in `application.properties`.

---

## 📄 Files Created or Modified

During this class, these files were created or modified:

| File | Purpose |
|---|---|
| `pom.xml` | Added database, JPA, and validation dependencies |
| `application.properties` | Added database connection configuration |
| `Categoria.java` | Created the entity that maps the `categoria` table |
| `CategoriaRepository.java` | Created the repository for category database access |
| `CategoriaService.java` | Created the service layer for category logic |
| `CategoriaController.java` | Created the controller for `/categoria/listado` |
| `templates/categoria/listado.html` | Created the category listing page |
| `templates/categoria/fragmentos.html` | Added category-specific fragments |

---

## 🧭 Commit Organization Note

The work from this class can be divided into logical commits.

Examples:

```text
⚙️ config(database): add MySQL connection and JPA dependencies
✨ feat(domain): add Categoria entity
✨ feat(repository): add Categoria repository
✨ feat(service): add Categoria service
✨ feat(controller): add Categoria listing controller
✨ feat(category): add category listing templates
⚙️ config(database): update datasource for Aiven MySQL
```

> [!NOTE]
> Commits should follow the format used by this repository, based on Conventional Commits and the project contribution rules.

---

## ✅ Class Summary

The third class focused on:

- Verifying that MySQL was installed and running.
- Using MySQL Workbench to connect to the database.
- Executing the `CreaTablas` SQL script.
- Creating the local database and category table data.
- Adding MySQL, JPA, and validation dependencies.
- Configuring `application.properties` for database access.
- Creating the `domain`, `repository`, `service`, and `controller` packages.
- Creating the `Categoria` entity.
- Creating the `CategoriaRepository` interface.
- Creating the `CategoriaService` class.
- Creating the `CategoriaController` class.
- Creating the category listing view with Thymeleaf.
- Using fragments to display the category listing.
- Connecting the project to a cloud database using Aiven.