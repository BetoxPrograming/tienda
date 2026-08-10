# Week 09 — Introduction to Spring Security

## Class goals

Week 09 introduces Spring Security into the `tienda` project.

During the recording, the professor works with three roles:

```text
ADMIN
VENDEDOR
USUARIO
```

and three temporary in-memory users:

```text
juan   → ADMIN
rebeca → VENDEDOR
pedro  → USUARIO
```

The main work completed in class was:

- Add Spring Security to the project.
- Add the Thymeleaf integration for Spring Security.
- Create `SecurityConfig`.
- Define public and role-restricted URLs.
- Configure login.
- Configure logout.
- Configure the access-denied page.
- Limit active sessions.
- Create three temporary users in memory.
- Add `login.html`.
- Add `acceso_denegado.html`.
- Display Login when nobody is authenticated.
- Display the authenticated username.
- Add Logout.
- Hide the Administration menu for non-admin users.
- Hide the Category and Product action buttons for non-admin users.
- Hide the main management menu from the normal `USUARIO`.
- Test the three roles.

> [!IMPORTANT]
> The users created in Week 09 are temporary and live only in memory.
>
> During the recording, the professor explicitly explains that this part will be replaced in Week 10 with users obtained from the database.

---

## Step 1 — Add Spring Security

Open:

```text
pom.xml
```

At the end of the dependencies, add:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

This dependency introduces Spring Security into the application.

---

## Step 2 — Add Thymeleaf Extras Spring Security 6

The professor then adds a second dependency:

```xml
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

This dependency is required for Thymeleaf security expressions such as:

```html
sec:authorize
sec:authentication
```

After adding both dependencies, the professor runs Clean & Build so the new classes are available.

---

## Step 3 — Create `SecurityConfig`

Create:

```text
src/main/java/com/tienda/SecurityConfig.java
```

The professor creates this as a new configuration class.

```java
@Configuration
public class SecurityConfig {
```

Week 09 keeps the security configuration separate from the existing `ProjectConfig`.

---

## Step 4 — Define public routes

The professor creates a group of routes that can be accessed without authentication.

Examples used in class include:

```text
/
 /index
/fav/**
/carrito/**
/consultas/**
/js/**
/webjars/**
/login
/acceso_denegado
```

The static resources are public so Bootstrap, JavaScript and the favicon can load before authentication.

The consultations are also kept public.

---

## Step 5 — Define routes for `USUARIO`

The normal customer role is:

```text
USUARIO
```

The route prepared for this profile is related to checkout:

```text
/facturar/carrito
```

At this point in the course, the complete checkout implementation is not yet developed.

---

## Step 6 — Define routes for `ADMIN` or `VENDEDOR`

The professor allows administrators and sellers to access listing pages:

```text
/producto/listado
/categoria/listado
/usuario/listado
```

A seller can consult information but should not create, update or delete records.

---

## Step 7 — Define routes for `ADMIN`

The administrator receives access to the modification processes for:

```text
producto
categoria
usuario
```

This includes operations such as:

```text
guardar
modificar
eliminar
nuevo
```

The administrator therefore has the highest level of access.

---

## Step 8 — Create `securityFilterChain`

Create a bean:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
```

The request rules use:

```java
requestMatchers(...)
permitAll()
hasRole(...)
hasAnyRole(...)
authenticated()
```

The final fallback is:

```java
.anyRequest().authenticated()
```

Any request that is not explicitly listed must therefore come from an authenticated user.

---

## Step 9 — Configure login

The professor configures a custom login page:

```text
/login
```

The login process also uses:

```text
/login
```

When authentication succeeds, the user is sent to:

```text
/
```

If authentication fails, the user returns to:

```text
/login?error=true
```

The login process itself is provided by Spring Security.

---

## Step 10 — Configure logout

The logout process uses:

```text
/logout
```

After logout, the application returns to:

```text
/login?logout=true
```

The professor also configures:

```text
invalidateHttpSession(true)
deleteCookies("JSESSIONID")
```

so the current session information is removed.

---

## Step 11 — Configure access denied

If an authenticated user attempts to access a route that does not belong to their role, Spring Security sends the user to:

```text
/acceso_denegado
```

This is configured with:

```java
accessDeniedPage("/acceso_denegado")
```

---

## Step 12 — Configure sessions

The professor limits the number of sessions to:

```text
1
```

using:

```java
maximumSessions(1)
```

and allows a new login to replace the previous session:

```java
maxSessionsPreventsLogin(false)
```

---

## Step 13 — Create the password encoder

Add:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

The passwords used by the temporary users are encoded with BCrypt.

---

## Step 14 — Create temporary users in memory

Week 09 creates three users directly in Java.

### Juan

```text
username: juan
password: 123
role: ADMIN
```

### Rebeca

```text
username: rebeca
password: 456
role: VENDEDOR
```

### Pedro

```text
username: pedro
password: 789
role: USUARIO
```

They are returned through:

```java
InMemoryUserDetailsManager
```

> [!IMPORTANT]
> These users are not loaded from MySQL in Week 09.
>
> The professor says during the class that this method will be replaced the following week.

---

## Step 15 — Add `login.html`

The professor provides the completed resource:

```text
src/main/resources/templates/login.html
```

The form uses:

```html
<form method="POST" th:action="@{/login}">
```

The fields are:

```text
username
password
```

The authentication process is handled by Spring Security.

If the URL contains:

```text
?error=true
```

the page displays the login error message.

---

## Step 16 — Add `acceso_denegado.html`

The professor also provides:

```text
src/main/resources/templates/acceso_denegado.html
```

This page informs the user that they do not have permission to access the requested resource or process.

---

## Step 17 — Register the access-denied view

At the end of the class, the professor opens:

```text
ProjectConfig.java
```

and registers:

```java
registry.addViewController("/acceso_denegado")
        .setViewName("acceso_denegado");
```

The old example view-controller entries are no longer needed.

---

## Step 18 — Add Login to the general header

Open:

```text
src/main/resources/templates/general/fragmentos.html
```

When the visitor is not authenticated, generate the login block with:

```html
sec:authorize="!isAuthenticated()"
```

The link points to:

```text
/login
```

The professor keeps the visible text as:

```text
Login
```

instead of internationalizing it during this class.

---

## Step 19 — Display the authenticated user

Create another block that is only displayed when somebody is authenticated:

```html
sec:authorize="isAuthenticated()"
```

The username is obtained with:

```html
sec:authentication="name"
```

This allows the header to display:

```text
juan
rebeca
pedro
```

depending on the active session.

---

## Step 20 — Add Logout

Inside the authenticated block, create a POST form:

```html
<form method="post" th:action="@{/logout}">
```

The button submits the logout request.

The professor tests the process by entering and leaving sessions with different users.

---

## Step 21 — Restrict the Administration menu

The Administration dropdown must only be generated for an administrator.

Add:

```html
sec:authorize="hasRole('ROLE_ADMIN')"
```

The professor tests:

```text
juan   → sees Administration
rebeca → does not see Administration
```

---

## Step 22 — Restrict Category action buttons

The Add Category button already had an administrator restriction from earlier work.

During Week 09, the professor copies the same security expression to the table cell that contains:

```text
Delete
Update
```

using:

```html
sec:authorize="hasRole('ROLE_ADMIN')"
```

The result is:

```text
juan   → sees action buttons
rebeca → does not see action buttons
```

---

## Step 23 — Restrict Product action buttons

The professor repeats the same change in:

```text
templates/producto/fragmentos.html
```

The table cell containing Delete and Update becomes administrator-only.

---

## Step 24 — Restrict the main management menu

The normal customer `USUARIO` should not see:

```text
Categories
Products
Queries
Administration
```

The professor applies the security expression to the main menu:

```html
sec:authorize="hasRole('ROLE_VENDEDOR') or hasRole('ROLE_ADMIN')"
```

The language selector stays outside this restriction.

The expected result is:

```text
juan   → full management menu
rebeca → management menu without Administration
pedro  → no management menu
```

---

## Step 25 — Test access by URL

The professor performs one final security test.

He logs in as an administrator and copies the URL of a protected modification page.

Then he logs in as:

```text
pedro
```

and manually tries to open the administrator URL.

Spring Security blocks the request and displays:

```text
acceso_denegado.html
```

This confirms that hiding buttons is not the only protection.

The server-side route restrictions also prevent unauthorized access.

---

## Week 09 roles

### ADMIN — Juan

```text
username: juan
password: 123
```

Can:

- Open management listings.
- Access the Administration menu.
- Add information.
- Update information.
- Delete information.

### VENDEDOR — Rebeca

```text
username: rebeca
password: 456
```

Can:

- Open the management listings.
- Consult information.

Cannot:

- See the Administration menu.
- Add records.
- Update records.
- Delete records.

### USUARIO — Pedro

```text
username: pedro
password: 789
```

Can use the public/customer portion of the site.

The management menu is hidden.

---

## Files created in Week 09

```text
src/main/java/com/tienda/SecurityConfig.java
src/main/resources/templates/login.html
src/main/resources/templates/acceso_denegado.html
```

---

## Files modified in Week 09

```text
pom.xml
src/main/java/com/tienda/ProjectConfig.java
src/main/resources/templates/general/fragmentos.html
src/main/resources/templates/categoria/fragmentos.html
src/main/resources/templates/producto/fragmentos.html
```

---

## Final verification checklist

- [ ] The project compiles after adding both security dependencies.
- [ ] The application opens at `/`.
- [ ] A visitor can see the public home page.
- [ ] Attempting to open `/categoria/listado` while logged out shows the login page.
- [ ] The custom `login.html` is displayed.
- [ ] Invalid credentials display the login error.
- [ ] `juan / 123` can authenticate.
- [ ] `rebeca / 456` can authenticate.
- [ ] `pedro / 789` can authenticate.
- [ ] The authenticated username appears in the header.
- [ ] Logout works.
- [ ] Juan sees the Administration menu.
- [ ] Rebeca does not see the Administration menu.
- [ ] Pedro does not see the management menu.
- [ ] Juan sees Add/Delete/Update controls in Categories.
- [ ] Juan sees Add/Delete/Update controls in Products.
- [ ] Rebeca does not see Add/Delete/Update controls.
- [ ] Rebeca can still open Category and Product listings.
- [ ] Pedro cannot manually open an administrator modification URL.
- [ ] Unauthorized access displays the access-denied page.
- [ ] Queries remain publicly accessible.
- [ ] Existing Category, Product and Week 08 query functionality still works.

---

## Class result

At the end of Week 09, `tienda` has its first role-based security implementation.

The class introduces:

```text
authentication
authorization
roles
login
logout
session control
access denied
conditional Thymeleaf rendering
```

The user source is still temporary:

```text
InMemoryUserDetailsManager
```

Database-backed authentication is intentionally left for the following class.

The professor closes the class with the commit:

```text
Semana 9. Intro a seguridad.
```
