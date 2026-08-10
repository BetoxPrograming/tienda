# Week 06 — Associations and Product Cards

## Class goals

During this class, the project begins using associations between `Producto` and `Categoria`.

The work completed during the recording was:

- Change the product/category relationship from a simple category ID to a JPA association.
- Add the inverse relationship from category to its products.
- Update the product forms so they work with `categoria.idCategoria`.
- Show the category description in the product list.
- Create `IndexController`.
- Load active products and active categories on the home page.
- Filter the home page products by category.
- Create the `consultas` template folder.
- Create the `tabs` fragment for the category navigation.
- Create the `tarjetas` fragment for the product cards.
- Add the two fragments to `index.html`.
- Build responsive Bootstrap product cards.
- Display image, description, detail, price, stock and availability.
- Add the visual cart button that will be used later in the course.
- Test the category filters and product cards.

---

## Before starting

Week 06 starts from the project completed in Week 05.

The product entity still contains the category ID directly:

```java
private Integer idCategoria;
```

and the product templates use:

```html
name="idCategoria"
```

The product list also displays:

```html
[[${p.idCategoria}]]
```

The first objective of Week 06 is to replace that direct ID handling with an association between the `Producto` and `Categoria` objects.

---

## Step 1 — Create the many-to-one association in `Producto`

Open:

```text
src/main/java/com/tienda/domain/Producto.java
```

The category ID that was used in Week 05 is commented:

```java
//private Integer idCategoria;
```

Then add the association:

```java
// Relación de muchos a uno con la clase Categoria
@ManyToOne
@JoinColumn(name = "id_categoria")
private Categoria categoria;
```

The important annotations are:

```java
@ManyToOne
@JoinColumn(name = "id_categoria")
```

`id_categoria` remains the foreign-key column in the `producto` table, but the Java entity now exposes a `Categoria` object.

The relationship is:

```text
many products → one category
```

---

## Step 2 — Update the Add Product category selector

Open:

```text
src/main/resources/templates/producto/fragmentos.html
```

The add form previously used:

```html
<select name="idCategoria"
```

Change it to:

```html
<select name="categoria.idCategoria"
```

The complete category selector keeps using the active categories that are already loaded by `ProductoController`:

```html
<select name="categoria.idCategoria"
        class="form-select"
        aria-label="Default select example">
    <option th:each="c : ${categorias}" th:value="${c.idCategoria}">
        [[${c.descripcion}]]
    </option>
</select>
```

The form now sends the selected ID inside the `categoria` object.

---

## Step 3 — Show the category description in the product list

In the same file, the Week 05 product list displayed:

```html
[[${p.idCategoria}]]
```

That direct field is no longer used.

Change it to:

```html
[[${p.categoria.descripcion}]]
```

The product list therefore displays the category name instead of only its numeric ID.

---

## Step 4 — Update the Edit Product category selector

The product edit fragment also needs to use the association.

Change:

```html
<select name="idCategoria" th:field="*{idCategoria}"
```

to:

```html
<select name="categoria.idCategoria"
        th:field="*{categoria.idCategoria}"
```

The options continue using:

```html
th:each="c : ${categorias}"
th:value="${c.idCategoria}"
```

This allows the current product category to be displayed and changed when a product is edited.

---

## Step 5 — Test the product association

Run the application and open the product list.

The category column should now show values such as category descriptions instead of category IDs.

The professor also tests modifying a product and changing its category.

At this point the product side of the association is working.

---

## Step 6 — Create the one-to-many association in `Categoria`

Open:

```text
src/main/java/com/tienda/domain/Categoria.java
```

Import:

```java
import java.util.List;
```

Then add:

```java
// Relación de uno a muchos con la clase Producto
@OneToMany(mappedBy = "categoria")
private List<Producto> productos;
```

The relationship from this side is:

```text
one category → many products
```

The value:

```java
mappedBy = "categoria"
```

refers to the `categoria` field created in `Producto`.

The two entities are now related in both directions:

```text
Categoria
    ↓
List<Producto>

Producto
    ↓
Categoria
```

---

## Step 7 — Create `IndexController`

The professor creates the new controller by copying:

```text
ProductoController
```

and naming the copy:

```text
IndexController
```

The new file is:

```text
src/main/java/com/tienda/controller/IndexController.java
```

The product-specific CRUD methods that are not needed on the home page are removed.

`IndexController` keeps access to:

```text
ProductoService
CategoriaService
```

The controller does not use a base `@RequestMapping`.

---

## Step 8 — Load the home page

The home route is:

```java
@GetMapping("/")
```

The method loads active products:

```java
var productos = productoService.getProductos(true);
model.addAttribute("productos", productos);
```

It also loads active categories:

```java
var categorias = categoriaService.getCategorias(true);
model.addAttribute("categorias", categorias);
```

and returns:

```java
return "/index";
```

Only active records are used on the home page.

---

## Step 9 — Create the category query route

The second route in `IndexController` is:

```java
@GetMapping("/consultas/{idCategoria}")
```

It receives the selected category ID:

```java
@PathVariable("idCategoria") Integer idCategoria
```

The category is searched with:

```java
Optional<Categoria> categoriaOpt = categoriaService.getCategoria(idCategoria);
```

If the category does not exist, an empty product list is sent to the view:

```java
model.addAttribute(
        "productos",
        java.util.Collections.emptyList()
);
```

If it exists, the products are obtained from the association:

```java
var categoria = categoriaOpt.get();
var productos = categoria.getProductos();
model.addAttribute("productos", productos);
```

The active category list is also loaded again:

```java
var categorias = categoriaService.getCategorias(true);
model.addAttribute("categorias", categorias);
```

The method finally returns:

```java
return "/index";
```

---

## Step 10 — Create the `consultas` folder

Inside:

```text
src/main/resources/templates
```

create:

```text
consultas
```

For this class, the folder contains only:

```text
fragmentos.html
```

The resulting path is:

```text
src/main/resources/templates/consultas/fragmentos.html
```

The professor starts from a copied fragment file and clears the previous sections so only the two new fragments remain.

---

## Step 11 — Create the `tabs` fragment

The first fragment is:

```html
<section th:fragment="tabs" class="py-3 mb-4">
```

It is used to display the category navigation.

The content is centered with Bootstrap:

```html
<div class="d-flex justify-content-center px-5">
```

and the navigation uses:

```html
<ul class="nav nav-underline">
```

---

## Step 12 — Add the “Todas” option

During the class, the professor sends this block through the class chat so students can copy it:

```html
<li class="nav-item">
    <a class="nav-link"
       aria-current="page"
       th:href="@{/}">
        [[#{pruebas.todas}]]
    </a>
</li>
```

The link:

```html
th:href="@{/}"
```

returns to the home route and therefore loads all active products.

The label:

```html
[[#{pruebas.todas}]]
```

comes from the existing message files.

No new message property is required because `pruebas.todas` already exists in the project.

---

## Step 13 — Create one tab for every active category

The category tabs are generated with:

```html
<li th:each="c : ${categorias}" class="nav-item">
```

Each category points to:

```html
@{/consultas/{id}(id=${c.idCategoria})}
```

and displays:

```html
[[${c.descripcion}]]
```

The result is a navigation bar similar to:

```text
Todas | Monitores | Teclados | Tarjetas madres | ...
```

Only active categories are displayed because `IndexController` loads them with:

```java
getCategorias(true)
```

---

## Step 14 — Add the query fragments to `index.html`

Open:

```text
src/main/resources/templates/index.html
```

Keep the existing general header and footer.

Between them add:

```html
<section th:replace="~{consultas/fragmentos :: tabs}"></section>
<section th:replace="~{consultas/fragmentos :: tarjetas}"></section>
```

The home page now has this structure:

```text
header
tabs
product cards
footer
```

---

## Step 15 — Create the `tarjetas` fragment

The second fragment begins with:

```html
<section th:fragment="tarjetas" class="container py-4">
```

The product cards are placed inside a responsive Bootstrap grid:

```html
<div class="row row-cols-1 row-cols-sm-2 row-cols-lg-3 row-cols-xl-4 g-4">
```

The grid uses:

```text
1 column  → very small screen
2 columns → small screen
3 columns → large screen
4 columns → extra-large screen
```

The spacing between cards is controlled with:

```text
g-4
```

---

## Step 16 — Repeat one card for every product

The products sent by `IndexController` are iterated with:

```html
<div th:each="p : ${productos}" class="col">
```

Each product creates one Bootstrap card:

```html
<div class="card h-100 shadow">
```

The same fragment works for:

- all active products from `/`;
- products associated with the category selected in `/consultas/{idCategoria}`.

---

## Step 17 — Display the product image

The card contains an image area:

```html
<div class="d-flex justify-content-center align-items-center bg-light p-3"
     style="height: 200px;">
```

The product image is displayed with:

```html
<img th:src="${p.rutaImagen}"
     class="img-fluid"
     th:alt="${p.descripcion}"
     style="max-height: 100%; object-fit: contain;"/>
```

The image is centered and constrained to the card area.

---

## Step 18 — Display the product description

The card header uses:

```html
<div class="card-header bg-white">
```

The product description is displayed as the card title:

```html
<h5 class="card-title text-center text-truncate mb-0">
    [[${p.descripcion}]]
</h5>
```

The title is:

- centered;
- truncated when it does not fit;
- displayed without an extra bottom margin.

---

## Step 19 — Display the product detail

The card body is:

```html
<div class="card-body d-flex flex-column">
```

The detail uses:

```html
<p class="card-text text-muted mb-auto text-truncate">
    [[${p.detalle}]]
</p>
```

The text is muted and truncated so cards remain visually aligned.

---

## Step 20 — Display the product price

The footer contains the price:

```html
<span class="text-success h5">
    [[${#numbers.formatCurrency(p.precio)}]]
</span>
```

The class uses the same Thymeleaf currency formatting already seen in the product list:

```text
#numbers.formatCurrency(...)
```

---

## Step 21 — Display available stock

When stock is greater than zero:

```html
<span th:if="${p.existencias > 0}"
      class="badge text-bg-success ms-2">
    [[${p.existencias}]] unidades
</span>
```

The stock is shown with a green Bootstrap badge.

---

## Step 22 — Display “Agotado”

When there is no stock:

```html
<span th:unless="${p.existencias > 0}"
      class="badge text-bg-danger ms-2">
    Agotado
</span>
```

The class therefore uses:

```text
th:if
th:unless
```

to display one state or the other.

---

## Step 23 — Prepare the cart form

The professor adds a form inside the card footer:

```html
<form th:action="@{/carrito/agregar}"
      th:method="POST"
      class="d-inline">
```

The product ID is stored in a hidden field:

```html
<input type="hidden"
       th:value="${p.idProducto}"
       name="idProducto"/>
```

> [!IMPORTANT]
> The `/carrito/agregar` process is **not implemented in Week 06**.
>
> The professor explains that this route will be implemented later in the course. During this class, only the card/form structure is prepared.

---

## Step 24 — Add the cart button

The card button is:

```html
<button type="submit"
        class="btn btn-sm btn-primary"
        th:disabled="${p.existencias == 0}"
        onclick="addCart(this.form)">
    <i class="fas fa-cart-plus"></i>
</button>
```

The button is disabled when:

```text
existencias == 0
```

so an out-of-stock product cannot use the button normally.

The icon is:

```html
<i class="fas fa-cart-plus"></i>
```

---

## Step 25 — Test the category filters

Run the application and open:

```text
/
```

The page should display:

- the category tabs;
- the product cards.

Selecting a category executes a route similar to:

```text
/consultas/1
```

and the cards change to the products associated with that category.

Selecting:

```text
Todas
```

returns to:

```text
/
```

and displays all active products again.

---

## Step 26 — Verify active records

The professor verifies that inactive categories do not appear in the tabs.

The home method uses:

```java
productoService.getProductos(true)
categoriaService.getCategorias(true)
```

so the normal home page uses active products and active categories.

---

## Files modified in Week 06

```text
src/main/java/com/tienda/domain/Producto.java
src/main/java/com/tienda/domain/Categoria.java
src/main/resources/templates/producto/fragmentos.html
src/main/resources/templates/index.html
```

---

## Files created in Week 06

```text
src/main/java/com/tienda/controller/IndexController.java
src/main/resources/templates/consultas/fragmentos.html
docs/weekly-notes/week-06-class-notes.md
```

---

## Week 06 project structure

The relevant structure after the class is:

```text
src/main/java/com/tienda/
├── controller/
│   ├── CategoriaController.java
│   ├── ProductoController.java
│   └── IndexController.java
├── domain/
│   ├── Categoria.java
│   └── Producto.java
├── repository/
│   ├── CategoriaRepository.java
│   └── ProductoRepository.java
└── service/
    ├── CategoriaService.java
    └── ProductoService.java
```

and:

```text
src/main/resources/templates/
├── categoria/
├── consultas/
│   └── fragmentos.html
├── general/
├── producto/
└── index.html
```

---

## Main concepts

### `@ManyToOne`

Used in `Producto` because many products can belong to one category:

```java
@ManyToOne
@JoinColumn(name = "id_categoria")
private Categoria categoria;
```

---

### `@OneToMany`

Used in `Categoria` because one category can contain many products:

```java
@OneToMany(mappedBy = "categoria")
private List<Producto> productos;
```

---

### Bidirectional association

The application can now navigate the relationship in both directions:

```text
producto.getCategoria()
```

and:

```text
categoria.getProductos()
```

This association is what allows `IndexController` to obtain the products for a selected category.

---

### Bootstrap responsive cards

The product grid changes its number of columns according to the screen size:

```html
row-cols-1
row-cols-sm-2
row-cols-lg-3
row-cols-xl-4
```

---

### Thymeleaf conditionals

The cards use:

```html
th:if
th:unless
th:disabled
```

to react to product stock.

---

## Week 06 scope limit

At the end of the class:

- The category/product JPA association exists.
- The home page displays category tabs.
- Products are displayed as cards.
- Category filtering works.
- The cart button is visually prepared.

The following is **not implemented yet**:

```text
Shopping cart processing
/carrito/agregar controller logic
Checkout
Payments
```

Do not add those features when reproducing Week 06.

---

## Final verification checklist

- [ ] `Producto` uses `@ManyToOne`.
- [ ] `Producto` uses `@JoinColumn(name = "id_categoria")`.
- [ ] The old `idCategoria` product field is commented.
- [ ] `Categoria` has `List<Producto> productos`.
- [ ] `Categoria` uses `@OneToMany(mappedBy = "categoria")`.
- [ ] The Add Product form uses `categoria.idCategoria`.
- [ ] The Edit Product form uses `categoria.idCategoria`.
- [ ] The product list displays `p.categoria.descripcion`.
- [ ] `IndexController` exists.
- [ ] `/` loads active products.
- [ ] `/` loads active categories.
- [ ] `/consultas/{idCategoria}` filters products using the category association.
- [ ] `templates/consultas/fragmentos.html` exists.
- [ ] The `tabs` fragment exists.
- [ ] The “Todas” link returns to `/`.
- [ ] Category tabs use the active category list.
- [ ] The `tarjetas` fragment exists.
- [ ] `index.html` loads both query fragments.
- [ ] Product cards display the product image.
- [ ] Product cards display description and detail.
- [ ] Product cards display formatted price.
- [ ] Product cards display stock or `Agotado`.
- [ ] The cart button is disabled when stock is zero.
- [ ] Product category editing still works.
- [ ] Product listing still works.
- [ ] Category CRUD still works.

---

## Week 06 result

At the end of Week 06, the project has moved from storing only a category ID inside a product to navigating a real object association:

```text
Categoria 1 ───── N Producto
```

That association is then used on the home page to filter and display products as responsive Bootstrap cards.
