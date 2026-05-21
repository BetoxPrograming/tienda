# Week 02 Class Notes — SC-403 Web Application Development and Patterns

> These notes summarize the most relevant points discussed during the second class.  
> They are intended as weekly class notes, not as a full explanation of every concept.

---

## 📌 Class Context

This week formally starts the **Tienda** project.

The professor explained that most of the work from the previous class could be removed or ignored because Week 02 begins the real base structure for the store project.

The Tienda project is built week by week during class and will become part of the course portfolio.

---

## ⚙️ `application.properties`

The `application.properties` file is used to configure application-level properties.

The following settings were added or reviewed:

```properties
# para definir el puerto donde escucha el servidor
server.port=80

# se define el formato de despliegue de fecha/hora en el log
logging.pattern.dateformat=hh:mm

# para eliminar el banner de Spring al inicio
spring.main.banner-mode=off

# evitar el uso de cache en desarrollo... en produccion SI
spring.thymeleaf.cache=false
```

Purpose:

- `server.port=80` allows the project to run directly on `localhost`.
- `logging.pattern.dateformat=hh:mm` changes the time format shown in logs.
- `spring.main.banner-mode=off` removes the Spring Boot banner when the application starts.
- `spring.thymeleaf.cache=false` prevents Thymeleaf templates from being cached during development.

> [!NOTE]
> Cache should be disabled in development, but enabled in production.

---

## 📝 Personal Git Workflow Note

When a file is edited, it is useful to add it to staging with `git add`.

However, commits should be made when a **logical unit of work** is finished, not necessarily every time one file is edited.

Examples of logical units:

- Fixing a typo.
- Adding configuration.
- Implementing language switching.
- Replacing draft fragments with final fragments.
- Cleaning unused example files.

Commits should follow the rules defined in the repository `CONTRIBUTING.md`.

---

## 🧾 `pom.xml` Updates

The `pom.xml` file was updated to prepare the project for Bootstrap, frontend resources, and deployment.

Main changes:

- Spring Boot version was updated.
- `<finalName>app</finalName>` was added.
- Bootstrap-related dependencies were added.

The `finalName` setting defines the final generated name of the packaged application.

```xml
<finalName>app</finalName>
```

This makes Maven generate the final application as:

```text
app.jar
```

This is important for deployment because the Dockerfile expects that final file name.

---

## 🎨 Bootstrap Documentation

The professor introduced the Bootstrap documentation website.

Bootstrap documentation is useful for finding ready-made components, examples, layout structures, and navigation elements.

The professor explained that many Bootstrap elements can be copied and adapted to the project.

This is not new for the course background because Bootstrap and frontend templates were already used in previous web development coursework.

---

## 📦 Maven Dependencies Added

The following dependencies were added to the `pom.xml`:

| Dependency | Purpose |
|---|---|
| `bootstrap` | Bootstrap styles and components |
| `font-awesome` | Icons |
| `jquery` | JavaScript utility library |
| `popper.js` | Positioning support for Bootstrap elements |
| `webjars-locator-lite` | Helps locate WebJars resources |

After modifying the `pom.xml`, the project should be cleaned and built so Maven downloads the dependencies.

In IntelliJ, the equivalent of NetBeans “hammer and broom” is usually:

```bash
mvn clean package
```

---

## 🌐 Message Files and Internationalization

Files from the Week 02 campus resources were added to the project.

These files were placed in `src/main/resources`, together with `application.properties`.

The files include message resources for different languages, such as:

```text
messages.properties
messages_en.properties
messages_fr.properties
messages_pt.properties
```

These files store key-value text messages.

Example:

```properties
plantilla.suTienda=Su tienda en línea
```

The key is used inside Thymeleaf templates, and the value is displayed depending on the active language.

---

## 🗣️ Using Message Keys in Thymeleaf

The professor explained this Thymeleaf format:

```html
[[#{plantilla.suTienda}]]
```

Meaning:

- `[[...]]` prints a value in the HTML.
- `#{...}` looks for a message key inside the message files.
- `plantilla.suTienda` is the key being requested.

This allows text to be centralized and translated instead of being hardcoded directly into the HTML.

> [!NOTE]
> Before adding manual language switching, the displayed language can depend on the browser language.

---

## 🧹 Cleaning `index.html`

The old content from the previous class was removed from `index.html`.

The file was cleaned so the project could start using the real store structure.

The initial message key was added to test the internationalization setup.

---

## 🧩 Fragment Concept

A fragment is a reusable section of HTML that can be shared across multiple pages.

Short reason:

> Avoid repeating the same header, menu, or footer in every page.

---

## 📁 Fragment File Structure

A new folder was created inside templates:

```text
src/main/resources/templates/general
```

Inside that folder, a fragment file was created:

```text
fragmentos.html
```

This file contains reusable sections such as:

- `head`
- `header`
- `footer`

Example idea:

```html
<head th:fragment="head">
    ...
</head>

<header th:fragment="header">
    ...
</header>

<footer th:fragment="footer">
    ...
</footer>
```

---

## 🧱 Using Fragments from `index.html`

The `index.html` file was updated to use Thymeleaf fragments.

Example:

```html
<head th:replace="~{general/fragmentos :: head}"></head>

<header th:replace="~{general/fragmentos :: header}"></header>

<footer th:replace="~{general/fragmentos :: footer}"></footer>
```

Meaning:

- `general/fragmentos` points to the fragment file.
- `:: head`, `:: header`, and `:: footer` select the specific fragment.
- `th:replace` replaces the current tag with the selected fragment.

---

## 🏷️ HTML Namespaces

The professor added namespaces to the `html` tag so the pages can use extended tags and Thymeleaf attributes.

Example:

```html
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity6">
```

Purpose:

- `xmlns` enables XHTML-style extended HTML.
- `xmlns:th` enables Thymeleaf attributes such as `th:replace`.
- `xmlns:sec` prepares the project for Spring Security attributes used later in the course.

---

## 🧭 Bootstrap Navbar Fragment

After explaining how fragments work manually, the professor replaced the draft fragment file with a complete `fragmentos.html` file from the Week 02 resources.

That file already includes a more complete Bootstrap navigation bar.

This prepares the project for a reusable layout that will be shared across the application.

---

## 🌍 Language Switching Configuration

The professor added configuration functions to support language switching.

Main concepts:

- `LocaleResolver`
- `LocaleChangeInterceptor`
- Web MVC configuration for interceptors

The `LocaleResolver` stores the current language in the session.

The interceptor detects language change requests and applies the selected locale.

Important imports include Spring MVC classes such as:

```java
import org.springframework.web.servlet.LocaleResolver;
```

---

## 🚀 Render Deployment Notes

Before deploying to Render, verify:

- `pom.xml` uses Java 21.
- `pom.xml` includes:

```xml
<finalName>app</finalName>
```

- The Dockerfile uses JDK 21.
- The Dockerfile copies the generated `app.jar`.

Expected Dockerfile idea:

```dockerfile
COPY target/app.jar app.jar
```

---

## 🔗 Render and GitHub

To apply the project changes in Render:

1. Create a new Web Service.
2. Select Git provider.
3. Connect Render with GitHub.
4. Give Render permission to read the required repository.
5. Select the project repository.
6. Confirm that Render detects the Dockerfile.
7. Verify that the language/environment appears as Docker.

> [!NOTE]
> If Render does not have permission to read the GitHub account or repository, the repository will not appear in the list.

---

## ✅ Class Summary

The second class focused on:

- Starting the real Tienda project structure.
- Updating `application.properties`.
- Updating `pom.xml`.
- Adding Bootstrap and WebJars dependencies.
- Adding multilingual message files.
- Cleaning `index.html`.
- Introducing Thymeleaf message keys.
- Creating and using reusable fragments.
- Replacing draft fragments with the final provided fragment file.
- Configuring language switching.
- Reviewing Render deployment requirements.
