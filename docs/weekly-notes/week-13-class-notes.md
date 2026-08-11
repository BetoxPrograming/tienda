# Week 13 — Constants and Permissions

## Objectives

Week 13 has two main objectives:

```text
1. Create the Constants CRUD and use its values from the code.
2. Allow an ADMIN to manage other users' roles.
```

The transcript begins by identifying exactly these two tasks.

---

## Part 1 — Constants CRUD

The standard course workflow is used:

```text
domain
repository
service
controller
templates
```

Files:

```text
src/main/java/com/tienda/domain/Constante.java
src/main/java/com/tienda/repository/ConstanteRepository.java
src/main/java/com/tienda/service/ConstanteService.java
src/main/java/com/tienda/controller/ConstanteController.java

src/main/resources/templates/constante/fragmentos.html
src/main/resources/templates/constante/listado.html
src/main/resources/templates/constante/modifica.html
```

The entity maps the table:

```text
constante
```

with the main fields:

```text
idConstante
atributo
valor
```

The purpose is for `atributo` to work as the identifier of a configuration value and for `valor` to contain the value that must be retrieved.

---

## Derived query by attribute

`ConstanteRepository` adds:

```java
findByAtributo(String atributo)
```

and `ConstanteService` exposes the same search operation.

This makes it possible to use attributes such as:

```text
dolar
servidor.http
```

without writing those values directly in the application code.

---

## CRUD test

The professor signs in as:

```text
juan / 123
```

and opens:

```text
Administration → Constants
```

From there, the professor verifies the constants list and modification process.

During the class, the value is changed to:

```text
dolar → 445
```

so it can be used in the shopping cart exercise.

---

## Part 2 — Cart total in dollars

The professor modifies:

```text
src/main/java/com/tienda/controller/CarritoController.java
```

`ConstanteService` is added to the controller.

Inside `agregar(...)`, the calculated total in colones is first stored in:

```java
BigDecimal totalColones = carritoService.calcularTotal(carrito);
```

and then the dollar equivalent is added to the model as:

```text
carritoTotalDolar
```

A private method is created:

```text
convierteDolares(...)
```

Its purpose is:

```text
search for "dolar"
→ obtain its String value
→ convert it to a numeric value
→ divide the total in colones by the exchange rate
```

During the class, the `BigDecimal` division produces an error when the result is not exact. After the break, rounding is added to the operation and the conversion works correctly.

---

## Cart fragment

Only the first fragment in:

```text
templates/carrito/fragmentos.html
```

is modified to display:

```text
total in colones / total in dollars
```

The professor decides not to apply `formatCurrency` to the dollar value and instead uses the `$` symbol directly.

---

## Part 3 — `servidor.http` from Constants

In Week 11, the server used for activation links was obtained from:

```text
application.properties
```

In Week 13, the professor modifies:

```text
RegistroService.java
```

to inject:

```text
ConstanteService
```

and retrieve:

```text
servidor.http
```

from the `constante` table.

The official resource keeps the value in:

```java
private final String servidor;
```

and initializes it in the constructor by searching for:

```text
servidor.http
```

The previous `@Value("${servidor.http}")` approach remains commented out.

The purpose explained in class is to switch between localhost and Render by modifying the constant, without recompiling only to change that URL.

---

# Part 4 — User permissions

The professor does not develop a Roles CRUD or a Routes CRUD.

Those two links are left as work that students may complete on their own because they are similar to the CRUD operations already developed in the course.

What is actually implemented in class is:

```text
Administration
→ Security
→ Permissions
```

---

## UsuarioService

At the end of:

```text
src/main/java/com/tienda/service/UsuarioService.java
```

two methods are added:

```text
getRolesNombres()
eliminarRol(...)
```

`getRolesNombres()` retrieves the names of all roles.

`eliminarRol(...)` searches for the user, removes the specified role from the user's role collection, and saves the user again.

The existing method:

```text
asignarRolPorUsername(...)
```

is reused to add permissions.

---

## UsuarioRolController

The following controller is created:

```text
src/main/java/com/tienda/controller/UsuarioRolController.java
```

Routes:

```text
GET /usuario_rol/mantenimiento
GET /usuario_rol/buscar
GET /usuario_rol/agregar
GET /usuario_rol/eliminar
```

The page allows an administrator to:

```text
search for a user by username
view assigned roles
view available roles
add a role
remove a role
```

---

## Permission templates

The following folder is created:

```text
src/main/resources/templates/usuario_rol/
```

with:

```text
fragmentos.html
mantenimiento.html
```

The professor clarifies that this must be a folder inside `templates`, not a Java package.

---

## Test performed in class

The professor signs in as Juan and searches for:

```text
rebeca
```

Rebeca initially has her normal permissions.

Juan adds:

```text
ADMIN
```

Then the session is closed and the professor signs in as Rebeca.

Rebeca can now see the Administration menu because her new role is being read from the database.

As a second demonstration, permissions are removed from Juan and Rebeca later restores them.

This confirms that permission management works dynamically.

---

## What is NOT implemented in Week 13

The recording does not implement:

```text
Roles CRUD
Routes CRUD
PayPal
```

These must not be added as part of the Week 13 class reproduction.

---

## New files

```text
src/main/java/com/tienda/domain/Constante.java
src/main/java/com/tienda/repository/ConstanteRepository.java
src/main/java/com/tienda/service/ConstanteService.java
src/main/java/com/tienda/controller/ConstanteController.java
src/main/java/com/tienda/controller/UsuarioRolController.java

src/main/resources/templates/constante/fragmentos.html
src/main/resources/templates/constante/listado.html
src/main/resources/templates/constante/modifica.html

src/main/resources/templates/usuario_rol/fragmentos.html
src/main/resources/templates/usuario_rol/mantenimiento.html
```

## Modified files

```text
src/main/java/com/tienda/controller/CarritoController.java
src/main/java/com/tienda/service/RegistroService.java
src/main/java/com/tienda/service/UsuarioService.java
src/main/resources/templates/carrito/fragmentos.html
```

No new Maven dependency is added during this class.

The current message files already contain the keys used by the supplied views, so the existing `messages.properties` file is not replaced with the complete resource.

---

## Final tests

```text
1. Sign in as Juan.
2. Open Administration → Constants.
3. Confirm that the Constants CRUD opens.
4. Confirm that the "dolar" constant exists.
5. Add a product to the cart.
6. Confirm that the cart button displays colones and dollars.
7. Open Administration → Security → Permissions.
8. Search for Rebeca.
9. Add ADMIN to Rebeca.
10. Sign out.
11. Sign in as Rebeca.
12. Confirm that Administration appears.
13. Restore the roles to the desired final state.
```

The following constant must also exist:

```text
servidor.http
```

in the `constante` table because `RegistroService` now retrieves it from there.

---

## End of class

The professor creates the commit using the text:

```text
Semana 13, constantes y permisos
```

Then the professor pushes the repository and explains how to use the GitHub commit history as portfolio evidence.

At the end of the recording, the professor also clarifies that Roles and Routes were not implemented during the class.
