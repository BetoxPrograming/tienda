# Week 13 — Constantes y permisos

## Objetivos

La Semana 13 tiene dos objetivos principales:

```text
1. Crear el CRUD de constantes y utilizar sus valores desde el código.
2. Permitir que un ADMIN gestione los roles de otros usuarios.
```

La transcripción inicia precisamente indicando esas dos acciones.

---

## Parte 1 — CRUD de Constantes

Se crea el flujo normal del curso:

```text
domain
repository
service
controller
templates
```

Archivos:

```text
src/main/java/com/tienda/domain/Constante.java
src/main/java/com/tienda/repository/ConstanteRepository.java
src/main/java/com/tienda/service/ConstanteService.java
src/main/java/com/tienda/controller/ConstanteController.java

src/main/resources/templates/constante/fragmentos.html
src/main/resources/templates/constante/listado.html
src/main/resources/templates/constante/modifica.html
```

La entidad mapea la tabla:

```text
constante
```

con los campos principales:

```text
idConstante
atributo
valor
```

La idea es que `atributo` funcione como identificador de una configuración y
`valor` contenga el dato que se necesita recuperar.

---

## Consulta derivada por atributo

`ConstanteRepository` agrega:

```java
findByAtributo(String atributo)
```

y `ConstanteService` expone la misma búsqueda.

Esto permite utilizar expresiones como:

```text
dolar
servidor.http
```

sin escribir esos valores directamente dentro del código.

---

## Prueba del CRUD

El profesor entra como:

```text
juan / 123
```

y abre:

```text
Administración → Constantes
```

Desde ahí comprueba el listado y la modificación de constantes.

Durante la clase cambia:

```text
dolar → 445
```

para utilizarlo en el ejercicio del carrito.

---

## Parte 2 — Total del carrito en dólares

El profesor modifica:

```text
src/main/java/com/tienda/controller/CarritoController.java
```

Se agrega:

```text
ConstanteService
```

al controlador.

En `agregar(...)` primero se guarda el total calculado en colones:

```java
BigDecimal totalColones = carritoService.calcularTotal(carrito);
```

y después se agrega al modelo el equivalente en dólares:

```text
carritoTotalDolar
```

Se crea el método privado:

```text
convierteDolares(...)
```

Su objetivo es:

```text
buscar "dolar"
→ obtener su valor String
→ convertirlo a número
→ dividir el total en colones entre el tipo de cambio
```

Durante la clase la división con `BigDecimal` presenta un error cuando el
resultado no termina exactamente. Después del receso se agrega redondeo a la
operación y la conversión queda funcionando.

---

## Fragmento del carrito

Se modifica únicamente el primer fragmento de:

```text
templates/carrito/fragmentos.html
```

para mostrar:

```text
total en colones / total en dólares
```

El profesor decide no aplicar `formatCurrency` al valor en dólares y utiliza
el símbolo `$` directamente.

---

## Parte 3 — `servidor.http` desde Constantes

En Semana 11 el servidor utilizado para los enlaces de activación se obtenía
desde:

```text
application.properties
```

En Semana 13 el profesor modifica:

```text
RegistroService.java
```

para inyectar:

```text
ConstanteService
```

y obtener:

```text
servidor.http
```

desde la tabla `constante`.

El recurso oficial deja el valor en:

```java
private final String servidor;
```

y lo inicializa en el constructor consultando:

```text
servidor.http
```

La antigua lectura con `@Value("${servidor.http}")` queda comentada.

La finalidad explicada en clase es poder cambiar entre localhost y Render
modificando la constante, sin tener que recompilar solamente para cambiar esa URL.

---

# Parte 4 — Permisos de usuarios

El profesor no desarrolla CRUD de Roles ni CRUD de Rutas.

Esos dos enlaces quedan como trabajo que los estudiantes pueden completar
por su cuenta porque son CRUD similares a los anteriores.

Lo que sí se desarrolla en clase es:

```text
Administración
→ Seguridad
→ Permisos
```

---

## UsuarioService

Al final de:

```text
src/main/java/com/tienda/service/UsuarioService.java
```

se agregan dos métodos:

```text
getRolesNombres()
eliminarRol(...)
```

`getRolesNombres()` obtiene los nombres de todos los roles.

`eliminarRol(...)` busca el usuario, elimina de su colección el rol indicado y
guarda nuevamente el usuario.

El método ya existente:

```text
asignarRolPorUsername(...)
```

se reutiliza para agregar permisos.

---

## UsuarioRolController

Se crea:

```text
src/main/java/com/tienda/controller/UsuarioRolController.java
```

Rutas:

```text
GET /usuario_rol/mantenimiento
GET /usuario_rol/buscar
GET /usuario_rol/agregar
GET /usuario_rol/eliminar
```

La pantalla permite:

```text
buscar usuario por username
ver roles asignados
ver roles disponibles
agregar rol
eliminar rol
```

---

## Templates de permisos

Se crea la carpeta:

```text
src/main/resources/templates/usuario_rol/
```

con:

```text
fragmentos.html
mantenimiento.html
```

El profesor aclara que debe ser un folder dentro de `templates`, no un paquete Java.

---

## Prueba realizada

El profesor entra como Juan y busca:

```text
rebeca
```

Rebeca inicialmente tiene sus permisos normales.

Juan le agrega:

```text
ADMIN
```

Después se cierra la sesión y se entra como Rebeca.

Rebeca ahora puede ver el menú de Administración porque su nuevo rol se está
leyendo desde la base de datos.

Como segunda demostración se quitan permisos a Juan y posteriormente Rebeca
se los devuelve.

Esto demuestra que la gestión de permisos queda funcionando dinámicamente.

---

## Lo que NO se hace en Semana 13

No se implementa en la grabación:

```text
CRUD de Roles
CRUD de Rutas
PayPal
```

No deben agregarse como parte de la reproducción de esta clase.

---

## Archivos nuevos

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

## Archivos modificados

```text
src/main/java/com/tienda/controller/CarritoController.java
src/main/java/com/tienda/service/RegistroService.java
src/main/java/com/tienda/service/UsuarioService.java
src/main/resources/templates/carrito/fragmentos.html
```

No se agrega una dependencia nueva a Maven durante esta clase.

Los archivos de mensajes actuales ya contienen las llaves utilizadas por las
vistas suministradas, por lo que no se reemplaza el `messages.properties`
actual por el recurso completo.

---

## Pruebas finales

```text
1. Iniciar como Juan.
2. Administración → Constantes.
3. Confirmar que el CRUD de constantes abre.
4. Confirmar que existe la constante "dolar".
5. Agregar un producto al carrito.
6. Confirmar que el botón del carrito muestra colones y dólares.
7. Administración → Seguridad → Permisos.
8. Buscar a Rebeca.
9. Agregarle ADMIN.
10. Cerrar sesión.
11. Entrar como Rebeca.
12. Confirmar que aparece Administración.
13. Dejar nuevamente los roles como correspondan.
```

También debe existir:

```text
servidor.http
```

en la tabla `constante`, porque `RegistroService` ahora lo obtiene desde ahí.

---

## Cierre de la clase

El profesor realiza el commit con el texto:

```text
Semana 13, constantes y permisos
```

Después hace push y explica cómo utilizar el historial de GitHub como evidencia
del portafolio.

También aclara al final de la grabación que Roles y Rutas no fueron
implementados durante la clase.
