# Week 11 — User Registration and Email Activation

## Class goals

Week 11 completes the security section by adding user management and the user registration process.

The professor works in two main parts:

1. User CRUD.
2. Registration, activation and account recovery by email.

The complete registration flow is:

```text
Visitor
↓
Register
↓
Enter username, name, last names and email
↓
System creates an inactive user with a temporary key
↓
System sends an activation email
↓
User opens the activation link
↓
User completes the account information
↓
Account becomes active
↓
Password is encrypted
↓
Default USER role is assigned
↓
User can authenticate
```

---

## Part 1 — User CRUD

### Step 1 — Extend `UsuarioRepository`

The repository created in Week 10 only had:

```java
findByUsernameAndActivoTrue(...)
```

Week 11 adds the queries required by user management and registration:

```java
findByActivoTrue()
findByUsername(...)
findByUsernameAndPassword(...)
findByUsernameOrCorreo(...)
existsByUsernameOrCorreo(...)
```

The professor explains the purpose of each query.

`findByUsernameAndPassword(...)` is used during the activation process because the activation URL contains the username and the temporary key.

`findByUsernameOrCorreo(...)` is used by the account recovery process.

`existsByUsernameOrCorreo(...)` is used to avoid duplicate usernames or email addresses.

---

### Step 2 — Create `RolRepository`

Create:

```text
src/main/java/com/tienda/repository/RolRepository.java
```

It includes:

```java
public Optional<Rol> findByRol(String rol);
```

This repository is used when a new account receives its default role.

---

### Step 3 — Create `UsuarioService`

Create:

```text
src/main/java/com/tienda/service/UsuarioService.java
```

It contains the same CRUD pattern already used with Category and Product:

```text
getUsuarios(...)
getUsuario(...)
save(...)
delete(...)
```

and additional operations required for registration:

```text
getUsuarioPorUsername(...)
getUsuarioPorUsernameYPassword(...)
getUsuarioPorUsernameOCorreo(...)
existeUsuarioPorUsernameOCorreo(...)
asignarRolPorUsername(...)
```

The save method also receives:

```java
boolean encriptaClave
```

because the temporary activation key is initially stored without encryption, while the final password is stored encrypted.

Firebase is reused for the user photograph.

---

### Step 4 — Default role

When a new user is created, the service assigns the default role:

```text
USER
```

The role is obtained with `RolRepository` and then added to the user's role set.

---

### Step 5 — Create `UsuarioController`

Create:

```text
src/main/java/com/tienda/controller/UsuarioController.java
```

The controller provides the normal user CRUD:

```text
/usuario/listado
/usuario/guardar
/usuario/eliminar
/usuario/modificar/{idUsuario}
```

The professor describes it as the same CRUD pattern already used for Category and Product.

---

### Step 6 — Create the `usuario` templates

Create:

```text
src/main/resources/templates/usuario/
```

and copy the supplied resources:

```text
fragmentos.html
listado.html
modifica.html
```

The page allows the administrator to:

```text
list users
create users
update users
delete users
```

The professor runs the project and verifies the user list before continuing with registration.

---

## Part 2 — Email support

### Step 7 — Add Spring Boot Mail

Open:

```text
pom.xml
```

Add the dependency sent by the professor through the class chat:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

After adding it, the professor stops the application and performs Clean & Build so Maven downloads the dependency.

---

### Step 8 — Create `CorreoService`

Create:

```text
src/main/java/com/tienda/service/CorreoService.java
```

The service receives:

```java
JavaMailSender
```

and creates:

```java
enviarCorreoHtml(...)
```

The method receives:

```text
recipient
subject
HTML content
```

The email is created with:

```java
MimeMessage
MimeMessageHelper
```

and sent with:

```java
mailSender.send(...)
```

---

## Gmail configuration

### Step 9 — Create a Gmail application password

During class, the professor opens the Google account configuration.

A Gmail account with two-factor authentication is used.

An application password is generated for TechShop.

The generated application password is different for every student.

> [!IMPORTANT]
> The application password must not be the normal Gmail password.
>
> Each student uses their own account and their own generated application password.

---

### Step 10 — Configure `application.properties`

The professor adds:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<GMAIL ACCOUNT>
spring.mail.password=<APPLICATION PASSWORD>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

The application password is entered as one continuous string, without spaces.

The class also adds:

```properties
servidor.http=http://localhost
```

This value is used when constructing the activation link inside the email.

While testing locally:

```text
servidor.http=http://localhost
```

is correct.

Before publishing, the professor replaces localhost with the public Render URL.

---

## Part 3 — Registration service and controller

### Step 11 — Create `RegistroService`

Create:

```text
src/main/java/com/tienda/service/RegistroService.java
```

It uses:

```text
CorreoService
UsuarioService
MessageSource
```

---

### Step 12 — Generate a temporary key

The method:

```java
demeClave()
```

creates a random 40-character temporary key.

It uses letters and numbers.

The key is temporarily stored as the user's password during the first registration stage.

---

### Step 13 — Create an inactive user

`crearUsuario(...)`:

1. Generates the temporary key.
2. Sets it as the password.
3. Sets:

```java
usuario.setActivo(false);
```

4. Saves the user.
5. Sends the activation email.

At this stage the account is not yet ready for login.

---

### Step 14 — Build the activation email

The email content comes from the existing message key:

```text
registro.correo.activar
```

The formatted message receives:

```text
name
last names
server URL
username
temporary key
```

The resulting activation URL follows this structure:

```text
<server>/registro/activacion/<username>/<temporary-key>
```

---

### Step 15 — Create `RegistroController`

Create:

```text
src/main/java/com/tienda/controller/RegistroController.java
```

The controller includes:

```text
GET  /registro/nuevo
GET  /registro/recordar
POST /registro/crearUsuario
GET  /registro/activacion/{usuario}/{id}
POST /registro/activar
POST /registro/recordarUsuario
```

---

## Part 4 — Registration templates

### Step 16 — Create the `registro` folder

Create:

```text
src/main/resources/templates/registro/
```

The supplied pages are:

```text
fragmentos.html
nuevo.html
activa.html
recordar.html
salida.html
```

During the recording, the professor removes the unnecessary `<body>` and closing `</body>` from the registration fragment template and formats the file.

---

### Step 17 — New user form

The `nuevoUsuario` fragment asks for:

```text
username
name
last names
email
```

The form posts to:

```text
/registro/crearUsuario
```

The class notes that only the email is currently marked required in the supplied form, although the professor comments that username should also be required as an improvement.

That improvement is not implemented during the class.

---

### Step 18 — Activation email test

The professor creates a new test user.

After submitting the first registration form:

- An inactive row is created in `usuario`.
- The temporary password is visible as a non-BCrypt value.
- The activation email arrives.
- The activation link points back to localhost while testing locally.

---

### Step 19 — Activation page

Opening the email link executes:

```text
/registro/activacion/{usuario}/{id}
```

The service searches the user using:

```text
username
temporary password
```

If found, the user information is sent to:

```text
registro/activa
```

---

### Step 20 — Complete activation

The activation form allows the user to finish the account:

```text
username
name
last names
email
phone
password
image
```

Submitting the form:

- marks the account active;
- saves the final password encrypted;
- optionally uploads the profile image;
- assigns the default USER role;
- returns to the site.

---

## Corrections during testing

### Step 21 — Nullable phone and image

Some students had `@NotNull` validations on fields that can be empty during the first registration step.

The professor removes `@NotNull` from:

```text
telefono
rutaImagen
```

because these values are not supplied in the first registration form.

The Week 10 `Usuario.java` used as the base project does not contain those incorrect `@NotNull` annotations, so no additional annotation removal is necessary there.

---

### Step 22 — Initialize the roles collection

During another student's activation test, the role set is null.

The professor corrects `Usuario.java` by initializing it:

```java
private Set<Rol> roles = new HashSet<>();
```

and imports:

```java
java.util.HashSet
```

This correction is part of the final Week 11 state.

---

## Account recovery

### Step 23 — Remember user

The login page already contains the link:

```text
/registro/recordar
```

The page asks for:

```text
username
email
```

The service searches with:

```java
findByUsernameOrCorreo(...)
```

If the user is found:

1. A new temporary key is generated.
2. The account becomes inactive temporarily.
3. The temporary key is saved.
4. A recovery email is sent.
5. The user follows the activation flow again.

The professor explains that the recovery process is practically the same as the registration activation process.

---

## Render

### Step 24 — Replace localhost before deployment

While working locally:

```properties
servidor.http=http://localhost
```

At the end of class, the professor opens Render and copies the public application URL.

Before publishing, `servidor.http` is changed to that URL.

This is necessary because an activation email containing `localhost` would only work on the computer running the application.

No new Render file is created.

---

## Files created in Week 11

```text
src/main/java/com/tienda/repository/RolRepository.java

src/main/java/com/tienda/service/UsuarioService.java
src/main/java/com/tienda/service/CorreoService.java
src/main/java/com/tienda/service/RegistroService.java

src/main/java/com/tienda/controller/UsuarioController.java
src/main/java/com/tienda/controller/RegistroController.java

src/main/resources/templates/usuario/fragmentos.html
src/main/resources/templates/usuario/listado.html
src/main/resources/templates/usuario/modifica.html

src/main/resources/templates/registro/fragmentos.html
src/main/resources/templates/registro/nuevo.html
src/main/resources/templates/registro/activa.html
src/main/resources/templates/registro/recordar.html
src/main/resources/templates/registro/salida.html
```

---

## Files modified in Week 11

```text
pom.xml
src/main/resources/application.properties
src/main/java/com/tienda/domain/Usuario.java
src/main/java/com/tienda/repository/UsuarioRepository.java
```

The existing message files already contain the `registro.*` and `usuario.*` keys required by the supplied resources, so they do not need a Week 11 modification in this project state.

---

## Manual configuration required

Before testing email, replace these two values in:

```text
src/main/resources/application.properties
```

```properties
spring.mail.username=TU_CORREO_GMAIL
spring.mail.password=TU_CLAVE_DE_APLICACION
```

Use the Gmail account configured for the project and its Google application password.

Do not use the normal Gmail account password.

For local testing keep:

```properties
servidor.http=http://localhost
```

Before a Render deployment, replace it with the project's own public Render URL.

---

## Final verification checklist

- [ ] Maven recognizes `spring-boot-starter-mail`.
- [ ] The project starts without compilation errors.
- [ ] Admin → Users opens.
- [ ] The user list is displayed.
- [ ] User create/update/delete functions are available to ADMIN.
- [ ] `/login` still opens.
- [ ] Register opens `/registro/nuevo`.
- [ ] Remember opens `/registro/recordar`.
- [ ] A new registration creates an inactive database user.
- [ ] An activation email arrives.
- [ ] The email contains the activation link.
- [ ] The activation link opens the activation form.
- [ ] The account can be completed with a final password.
- [ ] The account becomes active.
- [ ] The final password is encrypted.
- [ ] The user receives the default USER role.
- [ ] The newly activated user can log in.
- [ ] Remember User sends the recovery email.
- [ ] Category, Product and Queries still work.
- [ ] Existing security restrictions still work.

---

## Week 11 result

At the end of Week 11, the application supports:

```text
User CRUD
User self-registration
Email activation
Account recovery
Profile image during activation
Default USER role
BCrypt final password
```

The professor's final commit message in the recording is:

```text
Semana 11. Registro de usuarios
```
