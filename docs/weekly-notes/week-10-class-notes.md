# Week 10 — Security with Database

## Class goals

During Week 10, the security configuration created in Week 09 stops depending on users and URL arrays written directly in Java.

The professor moves two parts of the security process to the database:

```text
Users and roles
Security routes and required roles
```

The class works with four existing database tables:

```text
usuario
rol
usuario_rol
ruta
```

The main goals are:

- Map the `rol` table.
- Map the `usuario` table.
- Map the many-to-many relationship between users and roles.
- Create `UsuarioRepository`.
- Create `UsuarioDetailsService`.
- Authenticate users from the database.
- Save the authenticated user's image URL in the HTTP session.
- Display the user's image in the general header.
- Remove the in-memory users from `SecurityConfig`.
- Map the `ruta` table.
- Create `RutaRepository`.
- Create `RutaService`.
- Stop using the URL arrays written directly in `SecurityConfig`.
- Load route permissions from the database.
- Verify that login and role restrictions still work.
- Verify that the deployed application in Render continues working.

> [!IMPORTANT]
> During the class the professor opens MySQL Workbench only to explain the existing tables and data.
>
> Students are not instructed to create or manually modify those tables during this class.

---

## Starting point

Week 09 already has:

```text
Spring Security
Login
Logout
ADMIN
VENDEDOR
USUARIO
Access denied
```

However, the three users are still created in memory:

```text
juan
rebeca
pedro
```

and the allowed/protected routes are still defined with Java arrays inside `SecurityConfig`.

Week 10 replaces both mechanisms with database information.

---

# Part 1 — Users and roles from the database

## Step 1 — Review the database structure

The professor shows the database structure in Workbench.

The security process uses:

```text
usuario
rol
usuario_rol
```

`usuario_rol` is the intermediate table that associates users with roles.

This allows:

```text
One user → multiple roles
One role → multiple users
```

---

## Step 2 — Create `Rol`

Create:

```text
src/main/java/com/tienda/domain/Rol.java
```

The class maps:

```text
rol
```

and contains:

```text
idRol
rol
```

The professor starts from an existing domain class only to reuse its basic structure.

---

## Step 3 — Create `Usuario`

Create:

```text
src/main/java/com/tienda/domain/Usuario.java
```

The class maps:

```text
usuario
```

The fields used are:

```text
idUsuario
username
password
nombre
apellidos
correo
telefono
rutaImagen
activo
roles
```

---

## Step 4 — Map the user-role relationship

`Usuario` uses:

```java
@ManyToMany(fetch = FetchType.LAZY)
```

The intermediate table is:

```text
usuario_rol
```

with:

```java
@JoinTable(
    name = "usuario_rol",
    joinColumns = @JoinColumn(name = "id_usuario"),
    inverseJoinColumns = @JoinColumn(name = "id_rol")
)
```

The roles are stored in:

```java
private Set<Rol> roles;
```

This relationship is what later allows Spring Security to obtain the roles belonging to the authenticated user.

---

## Step 5 — Create `UsuarioRepository`

Create:

```text
src/main/java/com/tienda/repository/UsuarioRepository.java
```

The professor creates a derived query:

```java
public Optional<Usuario> findByUsernameAndActivoTrue(String username);
```

The query searches for:

```text
username entered in the login form
+
an active user
```

---

## Step 6 — Create `UsuarioDetailsService`

Create:

```text
src/main/java/com/tienda/service/UsuarioDetailsService.java
```

Unlike the CRUD services from previous classes, this service implements:

```java
UserDetailsService
```

The service is registered as:

```java
@Service("userDetailsService")
```

It receives:

```text
UsuarioRepository
HttpSession
```

through constructor injection.

---

## Step 7 — Implement `loadUserByUsername`

Spring Security calls:

```java
loadUserByUsername(String username)
```

when somebody attempts to authenticate.

The professor uses:

```java
usuarioRepository.findByUsernameAndActivoTrue(username)
```

If the user does not exist, the service throws:

```java
UsernameNotFoundException
```

---

## Step 8 — Save the user's image in session

After finding the user, the service removes the previous session value:

```java
session.removeAttribute("imagenUsuario");
```

and stores the current user's image URL:

```java
session.setAttribute("imagenUsuario", usuario.getRutaImagen());
```

The session attribute is named:

```text
imagenUsuario
```

This is used later by the general header.

---

## Step 9 — Convert database roles into Spring Security roles

The roles belonging to the user are transformed into:

```java
SimpleGrantedAuthority
```

The professor adds the prefix:

```text
ROLE_
```

to every role stored in the database.

Conceptually:

```text
ADMIN
↓
ROLE_ADMIN
```

The roles are collected into a `Set`.

---

## Step 10 — Return the Spring Security user

The service returns:

```java
new User(
    usuario.getUsername(),
    usuario.getPassword(),
    roles
)
```

The username and encoded password come from the database.

The permissions come from the user's related roles.

---

## Step 11 — Replace in-memory authentication

Open:

```text
src/main/java/com/tienda/SecurityConfig.java
```

The method that created:

```text
juan
rebeca
pedro
```

with `InMemoryUserDetailsManager` is removed.

The professor explains that those users are no longer needed because authentication now comes from the database.

---

## Step 12 — Configure `AuthenticationManagerBuilder`

Add:

```java
@Autowired
public void configurerGlobal(
        AuthenticationManagerBuilder build,
        @Lazy PasswordEncoder passwordEncoder,
        @Lazy UserDetailsService userDetailsService) throws Exception {

    build.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
}
```

Spring Security now uses the new `UsuarioDetailsService`.

The `PasswordEncoder` from Week 09 remains:

```java
BCryptPasswordEncoder
```

---

## Step 13 — Display the authenticated user's image

Open:

```text
src/main/resources/templates/general/fragmentos.html
```

In Week 09, the authenticated user was represented by a generic Font Awesome user icon.

The professor replaces that icon with:

```html
<img th:src="@{${session.imagenUsuario}}"
     alt="your image"
     height="40"
     class="rounded-circle"/>
```

The username remains beside the image:

```html
<span sec:authentication="name" class="ms-1"></span>
```

The Login block used during the class is:

```html
<ul class="navbar-nav" sec:authorize="!isAuthenticated()">
    <li class="nav-item">
        <a class="nav-link" th:href="@{/login}">
            <i class="fas fa-sign-in-alt"></i>
            <span class="ms-1">Login</span>
        </a>
    </li>
</ul>
```

---

## Step 14 — Test users from the database

The professor runs the application and tests the same users used previously:

```text
rebeca / 456
juan / 123
pedro / 789
```

The important difference is that they are no longer created in Java.

They are now retrieved from the database.

For users with an image URL, the image appears in the navigation bar.

The professor notes that Pedro does not have a valid image and therefore a broken image can appear for that user.

---

# Part 2 — Security routes from the database

## Step 15 — Review the `ruta` table

The second half of the class removes the URL arrays from `SecurityConfig`.

The professor reviews the existing:

```text
ruta
```

table.

A route contains:

```text
idRuta
ruta
requiereRol
rol
```

Some routes are public.

Other routes require a role.

---

## Step 16 — Create `Ruta`

Create:

```text
src/main/java/com/tienda/domain/Ruta.java
```

The relationship with `Rol` is:

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "id_rol")
private Rol rol;
```

This allows each protected route to identify the role required to access it.

---

## Step 17 — Create `RutaRepository`

Create:

```text
src/main/java/com/tienda/repository/RutaRepository.java
```

The professor adds the derived query:

```java
public List<Ruta> findAllByOrderByRequiereRolAsc();
```

The attribute name must match:

```text
requiereRol
```

Several errors corrected during the class were caused by spelling this attribute incorrectly.

---

## Step 18 — Create `RutaService`

Create:

```text
src/main/java/com/tienda/service/RutaService.java
```

The service only needs to retrieve the routes:

```java
@Transactional(readOnly=true)
public List<Ruta> getRutas() {
    return rutaRepository.findAllByOrderByRequiereRolAsc();
}
```

No CRUD for routes is implemented during Week 10.

---

## Step 19 — Remove the URL arrays

Back in:

```text
SecurityConfig.java
```

the arrays created during Week 09 are removed.

The professor's objective is to stop maintaining route permissions directly in Java.

---

## Step 20 — Inject `RutaService`

The `SecurityFilterChain` receives:

```java
@Lazy RutaService rutaService
```

Before configuring authorization, obtain the database routes:

```java
var rutas = rutaService.getRutas();
```

---

## Step 21 — Build authorization rules dynamically

The professor loops through every route:

```java
for (Ruta ruta : rutas)
```

If:

```java
ruta.isRequiereRol()
```

the route requires its associated role:

```java
requests.requestMatchers(ruta.getRuta())
        .hasRole(ruta.getRol().getRol());
```

Otherwise:

```java
requests.requestMatchers(ruta.getRuta())
        .permitAll();
```

After processing the database routes:

```java
requests.anyRequest().authenticated();
```

remains as the final rule.

---

## Step 22 — Keep the remaining Week 09 security configuration

The following processes remain:

```text
Custom login
Logout
Access denied
One active session
BCrypt password encoder
```

Only the source of:

```text
users
routes
```

changes.

---

## Step 23 — Test role restrictions

The professor logs in as:

```text
rebeca / 456
```

and manually attempts to open a category modification URL.

Rebeca does not have the required administrator role, so the application denies access.

This confirms that:

```text
routes
+
required roles
```

are now being loaded from the database.

---

# Render verification

At the end of the class, after committing and pushing his project, the professor reminds students to verify Render.

The reason is that the database is already hosted remotely, so the deployed application should continue to authenticate against it.

The professor opens his Render deployment and confirms that the application remains functional.

No new Render configuration file is created during this class.

---

# Files created in Week 10

```text
src/main/java/com/tienda/domain/Rol.java
src/main/java/com/tienda/domain/Usuario.java
src/main/java/com/tienda/domain/Ruta.java

src/main/java/com/tienda/repository/UsuarioRepository.java
src/main/java/com/tienda/repository/RutaRepository.java

src/main/java/com/tienda/service/UsuarioDetailsService.java
src/main/java/com/tienda/service/RutaService.java
```

---

# Files modified in Week 10

```text
src/main/java/com/tienda/SecurityConfig.java
src/main/resources/templates/general/fragmentos.html
```

No dependency is added during Week 10.

---

# What is not implemented in Week 10

The professor does **not** implement:

```text
User registration
Email activation
User CRUD
Route CRUD
```

Registration and activation are announced for the following week.

The route CRUD is left as a challenge.

---

# Final verification checklist

Before considering Week 10 complete, verify:

- [ ] The application starts without compilation errors.
- [ ] `Rol.java` exists.
- [ ] `Usuario.java` exists.
- [ ] `Ruta.java` exists.
- [ ] `UsuarioRepository` exists.
- [ ] `RutaRepository` exists.
- [ ] `UsuarioDetailsService` exists.
- [ ] `RutaService` exists.
- [ ] `Usuario` has the `ManyToMany` relationship with `Rol`.
- [ ] `Ruta` has the `ManyToOne` relationship with `Rol`.
- [ ] The in-memory users are no longer created in `SecurityConfig`.
- [ ] `UsuarioDetailsService` searches by username and active status.
- [ ] The authenticated user's image URL is stored as `imagenUsuario`.
- [ ] The general header reads `session.imagenUsuario`.
- [ ] `SecurityConfig` gets routes through `RutaService`.
- [ ] The route arrays from Week 09 are no longer used.
- [ ] Public routes continue opening without login.
- [ ] `juan / 123` authenticates from the database.
- [ ] `rebeca / 456` authenticates from the database.
- [ ] `pedro / 789` authenticates from the database.
- [ ] Juan keeps administrator access.
- [ ] Rebeca cannot open administrator modification routes.
- [ ] Access denied still works.
- [ ] Logout still works.
- [ ] Week 08 queries still work.
- [ ] Category and Product functionality still works.

---

# Week 10 result

At the end of Week 10, Spring Security is no longer based on security information written directly in Java.

The main flow becomes:

```text
Login form
   ↓
UsuarioDetailsService
   ↓
UsuarioRepository
   ↓
usuario + usuario_rol + rol
   ↓
Spring Security
```

Route authorization becomes:

```text
SecurityConfig
   ↓
RutaService
   ↓
RutaRepository
   ↓
ruta + rol
   ↓
requestMatchers
```

The commit made by the professor at the end of the recording is:

```text
Semana 10 seguridad con base datos
```
