# Week 08 — JPA Queries

## Class goals

During this class, the `tienda` project is extended with three ways to query products by a price range:

- Derived query.
- JPQL query.
- Native SQL query.

The professor implements the same filter three times only to demonstrate the three approaches. During the recording he explicitly explains that, in a normal project, the same requirement should not be duplicated with all three methods.

The work completed in class was:

- Add the three queries to `ProductoRepository`.
- Expose the three queries through `ProductoService`.
- Create `ConsultaController`.
- Create `/consultas/listado`.
- Create the three POST endpoints.
- Create `consultas/listado.html`.
- Add the `consultas` fragment.
- Create the reusable `formConsulta` fragment.
- Reuse the Week 06 product cards for the results.
- Test all three price-range queries.

---

## Before starting

Week 08 starts from the project completed in Week 06.

The project already contains:

```text
ProductoRepository
ProductoService
templates/consultas/fragmentos.html
templates/index.html
```

`consultas/fragmentos.html` already has the Week 06 fragments:

```text
tabs
tarjetas
```

These are preserved.

---

## Step 1 — Open `ProductoRepository`

Open:

```text
src/main/java/com/tienda/repository/ProductoRepository.java
```

The professor first points out that the existing method:

```java
findByActivoTrue()
```

is already an example of a derived query.

Week 08 adds another derived query and then the JPQL and SQL versions of the same requirement.

---

## Step 2 — Add `BigDecimal`

The product price uses `BigDecimal`.

Add:

```java
import java.math.BigDecimal;
```

The new query methods receive:

```text
precioInf
precioSup
```

---

## Step 3 — Derived query

Add:

```java
public List<Producto> findByPrecioBetweenOrderByPrecioAsc(
        BigDecimal precioInf,
        BigDecimal precioSup);
```

The method name defines the query:

```text
findBy
Precio
Between
OrderBy
Precio
Asc
```

This means:

```text
filter products between two prices
and order them by price ascending
```

No JPQL or SQL sentence is written.

---

## Step 4 — Import `@Query`

The next two queries require:

```java
import org.springframework.data.jpa.repository.Query;
```

---

## Step 5 — JPQL query

Add:

```java
@Query(value = "SELECT p FROM Producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
public List<Producto> consultaJPQL(
        BigDecimal precioInf,
        BigDecimal precioSup);
```

The professor explains that JPQL works with:

```text
Producto
```

because that is the Java entity.

It uses:

```text
p.precio
```

which is an entity attribute.

The parameters are:

```text
:precioInf
:precioSup
```

---

## Step 6 — Native SQL query

Add:

```java
@Query(
        nativeQuery = true,
        value = "SELECT * FROM producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
public List<Producto> consultaSQL(
        BigDecimal precioInf,
        BigDecimal precioSup);
```

The important distinction is:

```text
JPQL       → Producto
Native SQL → producto
```

JPQL works with the Java entity.

Native SQL works directly with the database table.

The SQL query uses:

```java
nativeQuery = true
```

---

## Step 7 — Open `ProductoService`

Open:

```text
src/main/java/com/tienda/service/ProductoService.java
```

Add:

```java
import java.math.BigDecimal;
```

The existing product service code remains unchanged.

Only three new methods are added.

---

## Step 8 — `consultaDerivada`

Add:

```java
@Transactional(readOnly = true)
public List<Producto> consultaDerivada(
        BigDecimal precioInf,
        BigDecimal precioSup) {

    return productoRepository
            .findByPrecioBetweenOrderByPrecioAsc(precioInf, precioSup);
}
```

---

## Step 9 — `consultaJPQL`

Duplicate the method and adjust it:

```java
@Transactional(readOnly = true)
public List<Producto> consultaJPQL(
        BigDecimal precioInf,
        BigDecimal precioSup) {

    return productoRepository.consultaJPQL(precioInf, precioSup);
}
```

---

## Step 10 — `consultaSQL`

Duplicate it again:

```java
@Transactional(readOnly = true)
public List<Producto> consultaSQL(
        BigDecimal precioInf,
        BigDecimal precioSup) {

    return productoRepository.consultaSQL(precioInf, precioSup);
}
```

The professor uses:

```java
@Transactional(readOnly = true)
```

because these methods only retrieve information.

---

## Step 11 — Create `ConsultaController`

Create:

```text
src/main/java/com/tienda/controller/ConsultaController.java
```

The class uses:

```java
@RequestMapping("/consultas")
```

and receives `ProductoService` through constructor injection.

---

## Step 12 — General consultations listing

Create:

```java
@GetMapping("/listado")
```

The method obtains all products:

```java
var productos = productoService.getProductos(false);
```

adds them to the model:

```java
model.addAttribute("productos", productos);
```

and returns:

```text
/consultas/listado
```

The URL is:

```text
/consultas/listado
```

---

## Step 13 — Derived-query endpoint

The first query method receives:

```text
precioInf
precioSup
```

with `@RequestParam`.

It calls:

```java
productoService.consultaDerivada(precioInf, precioSup)
```

and adds the result as:

```text
productos
```

The professor also adds the two prices back to the model:

```java
model.addAttribute("precioInf", precioInf);
model.addAttribute("precioSup", precioSup);
```

This allows the form to keep the values after submitting.

The final endpoint is:

```text
POST /consultas/consultaDerivada
```

---

## Step 14 — JPQL endpoint

Duplicate the previous method.

The final endpoint is:

```text
POST /consultas/consultaJPQL
```

It calls:

```java
productoService.consultaJPQL(precioInf, precioSup)
```

---

## Step 15 — SQL endpoint

Duplicate the method again.

The final endpoint is:

```text
POST /consultas/consultaSQL
```

It calls:

```java
productoService.consultaSQL(precioInf, precioSup)
```

> [!IMPORTANT]
> During testing, the professor notices that the three query endpoints had been created with `GET`.
>
> He corrects those three endpoints to `POST`.
>
> `/consultas/listado` remains a `GET`.

---

## Step 16 — Create `consultas/listado.html`

The professor copies:

```text
templates/index.html
```

into:

```text
templates/consultas/listado.html
```

The product cards remain:

```html
<section th:replace="~{consultas/fragmentos :: tarjetas}"/>
```

The category `tabs` call is changed to:

```html
<section th:replace="~{consultas/fragmentos :: consultas}"/>
```

The page therefore displays:

```text
query forms
+
product cards
```

---

## Step 17 — Add the `consultas` fragment

Open:

```text
src/main/resources/templates/consultas/fragmentos.html
```

The professor adds:

```html
<section th:fragment='consultas' class='row row-cols-4 p-2'>
```

The section contains four spaces:

```text
1. Derived query.
2. JPQL query.
3. Native SQL query.
4. Practice #2.
```

---

## Step 18 — First reusable form call

The professor builds the first call and also sends this line through the class chat:

```html
<div th:replace="~{consultas/fragmentos :: formConsulta('primary',#{consultas.derivadas},'/consultas/consultaDerivada')}"/>
```

The three parameters represent:

```text
primary
→ Bootstrap color

consultas.derivadas
→ form text

/consultas/consultaDerivada
→ endpoint
```

The closing parenthesis shown in the final code is important; during testing the professor corrects a missing parenthesis.

---

## Step 19 — JPQL form call

Duplicate the first call:

```html
<div th:replace="~{consultas/fragmentos :: formConsulta('warning',#{consultas.jpql},'/consultas/consultaJPQL')}"/>
```

---

## Step 20 — SQL form call

Duplicate it again:

```html
<div th:replace="~{consultas/fragmentos :: formConsulta('success',#{consultas.sql},'/consultas/consultaSQL')}"/>
```

---

## Step 21 — Practice #2 space

The fourth card remains:

```html
<div class='card'><p>Acá va su Práctica #2</p></div>
```

The professor does not implement Practice #2 during the class.

It remains a separate student assignment.

---

## Step 22 — Create `formConsulta`

Create the reusable fragment:

```html
<div th:fragment='formConsulta(colorForm, textoForm, urlForm)'>
```

It receives:

```text
colorForm
textoForm
urlForm
```

This allows the same form to be reused three times.

---

## Step 23 — Form configuration

The form uses:

```html
<form method="post"
      th:action='@{${urlForm}}'
      class='was-validated'>
```

The action depends on the URL parameter passed to the fragment.

---

## Step 24 — Dynamic card color

The header uses:

```html
th:classappend="${'bg-'+colorForm}"
```

Examples:

```text
primary → bg-primary
warning → bg-warning
success → bg-success
```

The title is:

```html
[[${textoForm}]]
```

---

## Step 25 — Lower price

Add:

```html
<label>[[#{consultas.precio.inferior}]]</label>

<input type="number"
       name='precioInf'
       th:value='${precioInf}'
       class='form-control'
       min="0"
       required/>
```

The name must be:

```text
precioInf
```

because it matches the controller parameter.

---

## Step 26 — Upper price

Add:

```html
<label>[[#{consultas.precio.superior}]]</label>

<input type="number"
       name='precioSup'
       th:value='${precioSup}'
       class='form-control'
       min="0"
       required/>
```

The name is:

```text
precioSup
```

---

## Step 27 — Submit button

The button uses:

```html
<button type='submit'
        class='btn text-white'
        th:classappend="${'btn-'+colorForm}">
```

The professor adds the magnifying-glass icon:

```html
<i class='fa-solid fa-magnifying-glass'></i>
```

and the text:

```html
[[${textoForm}]]
```

---

## Step 28 — Test the page

Run the application and open:

```text
/consultas/listado
```

The page should display:

```text
Derived Queries
JPQL Queries
Native Queries
Practice #2
```

The existing product cards appear below.

---

## Step 29 — Test all three queries

For each form:

1. Enter a lower price.
2. Enter a higher price.
3. Submit the form.
4. Verify that only products inside the range appear.
5. Verify that results are ordered by price ascending.
6. Verify that the entered values remain visible.

The three approaches should return equivalent results for the same range.

---

## Corrections made during testing

The recording includes several typing corrections.

The final implementation keeps the corrected versions:

- query endpoints use `@PostMapping`;
- `precioInf` is spelled correctly;
- `precioSup` is spelled correctly;
- both prices are added back to the model;
- JPQL uses `Producto`;
- native SQL uses `producto`;
- the alias `p` is lowercase;
- the reusable-fragment call has the correct closing parenthesis;
- the fragment parameter is `textoForm`;
- the form uses the URL supplied in `urlForm`.

Temporary typing mistakes are not part of the final result.

---

## Files created in Week 08

```text
src/main/java/com/tienda/controller/ConsultaController.java
src/main/resources/templates/consultas/listado.html
```

---

## Files modified in Week 08

```text
src/main/java/com/tienda/repository/ProductoRepository.java
src/main/java/com/tienda/service/ProductoService.java
src/main/resources/templates/consultas/fragmentos.html
```

---

## Final verification checklist

- [ ] The project starts without compilation errors.
- [ ] `findByActivoTrue()` still exists.
- [ ] The derived price-range query exists.
- [ ] The JPQL query exists.
- [ ] The native SQL query exists.
- [ ] Native SQL uses `nativeQuery = true`.
- [ ] `ProductoService` exposes the three queries.
- [ ] The three service methods are read-only.
- [ ] `ConsultaController` exists.
- [ ] `/consultas/listado` opens.
- [ ] `/consultas/listado` is a GET endpoint.
- [ ] The three query endpoints are POST.
- [ ] `consultas/listado.html` exists.
- [ ] The three forms are displayed.
- [ ] The Practice #2 card remains.
- [ ] `precioInf` is required.
- [ ] `precioSup` is required.
- [ ] Both inputs have `min="0"`.
- [ ] Derived query works.
- [ ] JPQL query works.
- [ ] Native SQL query works.
- [ ] Results are sorted by price ascending.
- [ ] Previous Week 06 product cards still work.
- [ ] Existing Category and Product CRUD still work.

---

## Week 08 result

At the end of the class, the application can filter products by a price range through:

```text
Derived Query
JPQL
Native SQL
```

All three follow the same project layers:

```text
repository
service
controller
templates
```

The three versions exist to demonstrate the alternatives. Practice #2 remains separate from the professor's Week 08 implementation.
