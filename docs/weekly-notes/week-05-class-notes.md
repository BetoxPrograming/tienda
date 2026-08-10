# Week 05 — Product CRUD

## Class goals

During this class, the `tienda` project was extended so the same structure already used for categories could also be used for products.

The main work completed during the recording was:

- Create the `Producto` domain class from `Categoria`.
- Map the fields of the `producto` table.
- Create `ProductoRepository`.
- Create `ProductoService`.
- Create `ProductoController`.
- Create the `templates/producto` folder.
- Copy the three category templates into the product folder.
- Use `Ctrl + H` with **Preserve Case** to convert the copied category files into product files.
- Load active categories from `ProductoController`.
- Add a category selector to the product form.
- Add detail, price, and stock fields.
- Complete the product edit form.
- Complete the product listing with price, stock, total, image, category ID, and active state.
- Commit and push the completed Week 05 product work.

---

## Before starting

The project already contains the category CRUD created in the previous classes.

The class starts from the existing category structure:

```text
src/main/java/com/tienda/
├── controller/
│   └── CategoriaController.java
├── domain/
│   └── Categoria.java
├── repository/
│   └── CategoriaRepository.java
└── service/
    └── CategoriaService.java
```

and:

```text
src/main/resources/templates/categoria/
├── fragmentos.html
├── listado.html
└── modifica.html
```

The objective of the class is to reuse that structure for products instead of creating the whole CRUD again from zero.

---

## Step 1 — Create `Producto.java`

The professor begins by copying:

```text
Categoria.java
```

and pasting it in the same package with the name:

```text
Producto.java
```

The file is located at:

```text
src/main/java/com/tienda/domain/Producto.java
```

The table name is changed to:

```java
@Table(name = "producto")
```

The primary key becomes:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_producto")
private Integer idProducto;
```

The product also keeps the category identifier:

```java
private Integer idCategoria;
```

---

## Step 2 — Map the product fields

The professor reviews the `producto` table and adds the fields needed by the entity.

The resulting Week 05 structure is:

```java
package com.tienda.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name = "producto")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;
    private Integer idCategoria;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "La descripción no puede estar vacía.")
    @Size(max = 50, message = "La descripción no puede tener más de 50 caracteres.")
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String detalle;

    @Column(precision = 12, scale = 2)
    @NotNull(message = "El precio no puede estar vacío.")
    @DecimalMin(value = "0.01", inclusive = true, message = "El precio debe ser mayor a 0.")
    private BigDecimal precio;

    @NotNull(message = "El campo de existencias no puede estar vacío.")
    @Min(value = 0, message = "Las existencias deben ser un número mayor o igual a 0.")
    private Integer existencias;

    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;
    private boolean activo;

}
```

### Description

The description receives validation messages:

```java
@NotBlank(message = "La descripción no puede estar vacía.")
@Size(max = 50, message = "La descripción no puede tener más de 50 caracteres.")
```

### Detail

The detail can contain longer text:

```java
@Column(columnDefinition = "TEXT")
private String detalle;
```

### Price

The price uses:

```java
BigDecimal
```

with:

```java
@Column(precision = 12, scale = 2)
```

and decimal validation.

### Stock

Stock is stored as an integer:

```java
private Integer existencias;
```

and cannot be negative.

---

## Step 3 — Create the product template folder

Inside:

```text
src/main/resources/templates
```

create:

```text
producto
```

The folder name is lowercase.

Copy the three files from:

```text
templates/categoria
```

into:

```text
templates/producto
```

The result is:

```text
src/main/resources/templates/producto/
├── fragmentos.html
├── listado.html
└── modifica.html
```

During the class, the professor repeatedly verifies that this folder and the copied files are in the correct location before continuing.

---

## Step 4 — Create `ProductoRepository`

Copy:

```text
CategoriaRepository.java
```

and rename the copy to:

```text
ProductoRepository.java
```

The new file is located at:

```text
src/main/java/com/tienda/repository/ProductoRepository.java
```

The professor uses the existing category repository instead of writing a new repository from zero.

---

## Step 5 — Create `ProductoService`

Copy:

```text
CategoriaService.java
```

and rename the copy to:

```text
ProductoService.java
```

The new file is located at:

```text
src/main/java/com/tienda/service/ProductoService.java
```

The service therefore keeps the same CRUD structure already working for categories, now applied to products.

---

## Step 6 — Create `ProductoController`

Copy:

```text
CategoriaController.java
```

and rename the copy to:

```text
ProductoController.java
```

The new file is located at:

```text
src/main/java/com/tienda/controller/ProductoController.java
```

The base route becomes:

```java
@RequestMapping("/producto")
```

---

## Step 7 — Use `Ctrl + H`

After all the copies exist, the professor opens **Replace** with:

```text
Ctrl + H
```

The search value is:

```text
Categoria
```

and the replacement value is:

```text
Producto
```

The professor specifically enables:

```text
Preserve Case
```

and uses:

```text
Replace All
```

The replacement is performed in the new product files:

```text
ProductoRepository.java
ProductoService.java
ProductoController.java
producto/fragmentos.html
producto/listado.html
producto/modifica.html
```

> [!IMPORTANT]
> The original category files are not the files that should be replaced. The replacement is performed on the copies created for product.

The professor also points out later that the replacement even changes comments in the copied files.

---

## Step 8 — Run the first product listing

After the initial replacement, the project is run.

The product option now opens a product listing and the professor confirms that the existing product records from the database are being loaded.

At this moment the product section is already functional at a basic level, but it still only contains the fields inherited from the copied category HTML.

The rest of the class is used to complete the product-specific fields.

---

## Step 9 — Review the product fragments

The professor opens:

```text
src/main/resources/templates/producto/fragmentos.html
```

and explains that the file contains different fragments used by the product pages.

The first area modified is the fragment used to add a product.

The existing description block is used as the starting point for the new controls.

---

## Step 10 — Add the category field

Before the product description, a new field is created for the category.

Initially, the professor demonstrates the field with an input, but then replaces it with a dropdown so the user does not have to type the category ID manually.

The final selector uses:

```html
<select name="idCategoria"
        class="form-select"
        aria-label="Default select example">
    <option th:each="c : ${categorias}"
            th:value="${c.idCategoria}">
        [[${c.descripcion}]]
    </option>
</select>
```

The important values are:

```text
idCategoria
categorias
c.idCategoria
c.descripcion
```

The visible text is the category description, while the value sent by the form is the category ID.

---

## Step 11 — Add `CategoriaService` to `ProductoController`

The category dropdown needs category data.

The professor opens:

```text
ProductoController.java
```

duplicates the service field, changes the new field to:

```java
CategoriaService
```

and rebuilds the constructor so both services are available:

```text
ProductoService
CategoriaService
```

The required import for `CategoriaService` is also added.

---

## Step 12 — Load active categories in the product listing

Inside the product listing method, the professor obtains only active categories:

```java
var categorias = categoriaService.getCategorias(true);
```

and sends them to Thymeleaf:

```java
model.addAttribute("categorias", categorias);
```

The product listing method therefore sends both products and categories to the page.

The `true` value is kept because only active categories should be available when creating a product.

---

## Step 13 — Load categories when modifying a product

The same two lines are copied into the product modification method:

```java
var categorias = categoriaService.getCategorias(true);
model.addAttribute("categorias", categorias);
```

This allows the category selector to work on the update form as well.

---

## Step 14 — Complete the Add Product form

After the category selector, the product form is expanded with the fields that were missing.

The form order becomes:

```text
Category
Description
Detail
Price
Stock
Active
Image
```

---

## Step 15 — Limit the description length

The professor adds:

```html
maxlength="50"
```

to the product description input.

This keeps the HTML field consistent with the maximum size of the product description.

---

## Step 16 — Add the detail field

The detail uses a textarea:

```html
<textarea class="form-control"
          name="detalle"
          rows="3"></textarea>
```

The professor explains that a `textarea` is used instead of a normal single-line input.

---

## Step 17 — Add the price field

The price field is numeric:

```html
<input type="number"
       class="form-control"
       name="precio"
       min="0"
       required="true"/>
```

---

## Step 18 — Add the stock field

The stock field is also numeric:

```html
<input type="number"
       class="form-control"
       name="existencias"
       min="0"
       required="true"/>
```

After these changes, the professor runs the page and verifies that the Add Product modal shows the new controls.

---

## Step 19 — Complete the Edit Product fragment

After the break, the professor goes to the third product fragment:

```text
editar
```

Instead of rewriting the controls, the blocks already created in the Add Product modal are copied into the edit form.

The category selector is placed before description.

The detail, price, and stock fields are placed after description.

---

## Step 20 — Add `th:field` to the edit controls

The copied controls need to load the values from the product being modified.

The professor uses the existing description field as the example and adds `th:field` to the new controls.

### Category

```html
th:field="*{idCategoria}"
```

### Detail

```html
th:field="*{detalle}"
```

### Price

```html
th:field="*{precio}"
```

### Stock

```html
th:field="*{existencias}"
```

The professor emphasizes that the value in `th:field` must match the corresponding `name`.

---

## Step 21 — Test product modification

The professor opens an existing product, changes one of its values, saves it, and verifies that the modified value appears in the product listing.

This confirms that the copied CRUD structure and the new product fields are working together.

---

## Step 22 — Complete the product listing headers

The professor returns to the second fragment:

```text
listado
```

and adds the missing product columns.

The listing now includes:

```text
Description
Price
Stock
Total
Image
Category
Active
```

The corresponding message expressions include:

```html
[[#{producto.descripcion}]]
[[#{producto.precio}]]
[[#{producto.existencias}]]
[[#{producto.total}]]
[[#{producto.imagen}]]
[[#{producto.categoria}]]
[[#{producto.activo}]]
```

---

## Step 23 — Use `p` for product in the table

The copied category table originally uses a short variable based on category.

The professor changes the iteration variable to:

```text
p
```

for product.

He explains that students can keep the previous short variable if they want, but he personally changes it to `p`.

The product row therefore uses values such as:

```html
[[${p.idProducto}]]
[[${p.descripcion}]]
```

---

## Step 24 — Format the price as currency

The professor first displays the price directly and then improves its presentation.

The table cell is aligned to the right:

```html
class="text-end"
```

and the value is formatted with:

```html
#numbers.formatCurrency(...)
```

The final expression is:

```html
[[${#numbers.formatCurrency(p.precio)}]]
```

---

## Step 25 — Display stock centered

The stock value is displayed with:

```html
[[${p.existencias}]]
```

and centered with:

```html
class="text-center"
```

---

## Step 26 — Calculate the product total

The price cell is duplicated and adapted to calculate:

```text
price × stock
```

The expression is:

```html
[[${#numbers.formatCurrency(p.precio*p.existencias)}]]
```

The total is also displayed using currency formatting.

---

## Step 27 — Display the category ID

The last missing value added to the table is the category.

During Week 05, the value displayed is:

```html
[[${p.idCategoria}]]
```

The professor leaves that value as the category ID at the end of the class and indicates that it will be changed in the following week.

---

## Step 28 — Final product table

At the end of the listing work, each product row contains the values used during the class:

```html
<td>[[${p.idProducto}]]</td>
<td>[[${p.descripcion}]]</td>
<td class="text-end">[[${#numbers.formatCurrency(p.precio)}]]</td>
<td class="text-center">[[${p.existencias}]]</td>
<td class="text-end">[[${#numbers.formatCurrency(p.precio*p.existencias)}]]</td>
<td><img th:src="@{${p.rutaImagen}}" alt="noData" height="75"/></td>
<td>[[${p.idCategoria}]]</td>
<td th:text="${p.activo} ? 'Activa':'Inactiva'"></td>
```

The existing update and delete controls remain part of the product CRUD copied from category.

---

## Step 29 — Final class commit

Once the professor finishes the product listing, he creates one Git commit for the class.

The commit message spoken during the recording is:

```text
Semana 5. Productos completo.
```

After the commit, the professor performs the remote push.

---

## Files created during Week 05

```text
src/main/java/com/tienda/domain/Producto.java
src/main/java/com/tienda/repository/ProductoRepository.java
src/main/java/com/tienda/service/ProductoService.java
src/main/java/com/tienda/controller/ProductoController.java
src/main/resources/templates/producto/fragmentos.html
src/main/resources/templates/producto/listado.html
src/main/resources/templates/producto/modifica.html
```

---

## Week 05 structure

After the class, the relevant structure is:

```text
src/main/java/com/tienda/
├── controller/
│   ├── CategoriaController.java
│   └── ProductoController.java
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
│   ├── fragmentos.html
│   ├── listado.html
│   └── modifica.html
└── producto/
    ├── fragmentos.html
    ├── listado.html
    └── modifica.html
```

---

## Final verification checklist

Before considering Week 05 complete, verify:

- [ ] `Producto.java` exists.
- [ ] `Producto.java` maps `idProducto`, `idCategoria`, description, detail, price, stock, image path, and active state.
- [ ] `ProductoRepository.java` exists.
- [ ] `ProductoService.java` exists.
- [ ] `ProductoController.java` exists.
- [ ] `ProductoController` has access to `CategoriaService`.
- [ ] Active categories are added to the model in the product listing.
- [ ] Active categories are added to the model in product modification.
- [ ] `templates/producto` exists.
- [ ] `producto/fragmentos.html` exists.
- [ ] `producto/listado.html` exists.
- [ ] `producto/modifica.html` exists.
- [ ] The Add Product modal contains a category dropdown.
- [ ] The Add Product modal contains detail, price, and stock.
- [ ] The edit form contains the same product fields.
- [ ] The edit fields use `th:field`.
- [ ] The product table displays price.
- [ ] The product table displays stock.
- [ ] The product table calculates price × stock.
- [ ] The product table displays `idCategoria`.
- [ ] Product modification works.
- [ ] The product listing loads correctly.
- [ ] The original category files continue working.

---

## Week 05 result

At the end of the recorded class, the product section reuses the CRUD structure already created for categories and adds the fields specific to products.

The most important practical idea of the class is that the existing structure can be duplicated and adapted efficiently with:

```text
Ctrl + H
Replace All
Preserve Case
```

After the copied files are adapted, the product-specific HTML is completed manually and the category list is supplied from `ProductoController`.
