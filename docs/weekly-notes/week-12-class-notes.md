# Week 12 — Shopping Cart

## Class objective

Week 12 implements the shopping cart and billing process.

The professor explains that the cart is not stored directly in a database table while the user is browsing. It is temporarily kept in a session variable as a list of `Item`.

The workflow developed in class is:

```text
Product
→ add to cart
→ cart in session
→ modify/delete quantities
→ bill
→ register Factura
→ register Venta for each product
→ decrease stock
→ clear cart
→ display invoice
→ print invoice
```

## Domain

The following classes are added:

```text
EstadoFactura.java
Factura.java
Venta.java
Item.java
```

`EstadoFactura` is an `enum` with the states:

```text
Activa
Pagada
Anulada
```

`Factura` represents the invoice header or master record.

`Venta` represents each invoice detail line.

`Item` is not a JPA entity. It is used temporarily inside the shopping cart and contains a `Producto`, the requested quantity, and the historical price.

## Repository

The following repositories are added:

```text
FacturaRepository.java
VentaRepository.java
```

`FacturaRepository` includes the JPQL query:

```java
findByIdFacturaConDetalle(...)
```

which retrieves in a single query:

```text
Factura
Usuario
Ventas
Productos
```

so the complete invoice can be displayed after the purchase is processed.

A `VentaService` is not created during this class.

## CarritoService

`CarritoService` manages a session variable named:

```text
carrito
```

The cart is:

```java
List<Item>
```

The service supports:

```text
obtenerCarrito
guardarCarrito
agregarProducto
buscarItem
eliminarItem
actualizarCantidad
contarUnidades
calcularTotal
limpiarCarrito
procesarCompra
```

The requested quantity is validated so that it does not exceed the available stock.

## Billing

`procesarCompra(...)`:

1. validates that the cart contains products;
2. creates the `Factura`;
3. registers one `Venta` for each `Item`;
4. keeps the historical price;
5. decreases the product stock;
6. returns the generated invoice.

The invoice is stored with the state:

```text
Pagada
```

## FacturaService

`FacturaService` uses:

```java
findByIdFacturaConDetalle(...)
```

to retrieve a processed invoice together with its user, sales records, and products.

## CarritoController

The following controller is created:

```text
src/main/java/com/tienda/controller/CarritoController.java
```

The routes developed in class are:

```text
GET  /carrito/listado
POST /carrito/agregar
POST /carrito/eliminar/{idProducto}
GET  /carrito/modificar/{idProducto}
POST /carrito/actualizar
GET  /facturar/carrito
GET  /carrito/verFactura
```

The route:

```text
/facturar/carrito
```

is separate from the `/carrito/**` routes because billing requires an authenticated user.

The temporary `System.out.println(...)` statements used for debugging are removed during the class.

## Cart templates

The professor creates:

```text
src/main/resources/templates/carrito/
```

and adds:

```text
fragmentos.html
listado.html
modifica.html
verFactura.html
```

`fragmentos.html` contains the fragments:

```text
verCarrito
listadoCarrito
modificaItem
detalleFactura
```

### Cart fragments

The resource:

```text
src/main/resources/templates/carrito/fragmentos.html
```

contains the four fragments used by the cart views:

```text
verCarrito
listadoCarrito
modificaItem
detalleFactura
```

The file is incorporated directly from the resource provided by the professor.

## JavaScript

In:

```text
static/js/rutinas.js
```

only the following function is added:

```javascript
addCart(formulario)
```

The function:

- obtains `idProducto`;
- uses `/carrito/agregar`;
- retrieves the CSRF token;
- performs the `POST` request through AJAX;
- places the response inside `#resultBlock`.

The existing `mostrarImagen(...)` function and previous routines are preserved.

## CSS

The professor instructs students to remove the old `estilos.css` used since the first weeks and replace it completely with the Week 12 resource.

The new CSS adds:

- a visual effect to cards;
- printing rules;
- hiding of elements marked with `d-print-none`.

The professor comments that the card zoom effect originally belonged to a previous challenge, but it is included in the resource delivered for this class.

## general/fragmentos.html

The following elements are added to the `head`:

```html
<link th:href="@{/css/estilos.css}" rel="stylesheet"/>
<meta name="_csrf" th:content="${_csrf.token}"/>
<meta name="_csrf_header" th:content="${_csrf.headerName}"/>
```

The existing `rutinas.js` reference is kept only once.

To prevent the header and footer from being printed, the following class is added:

```text
d-print-none
```

to both the `header` and the `footer`.

## consultas/fragmentos.html

The cart button form already existed from previous weeks.

During the class, the professor verifies that the button that executes:

```javascript
addCart(this.form)
```

uses:

```html
type="button"
```

instead of:

```html
type="submit"
```

because the request is handled through AJAX.

## Test performed in class

The professor signs in as:

```text
rebeca / 456
```

and tests:

```text
add products
increase quantities
reject quantities greater than available stock
view cart
delete
modify
bill
view invoice
print
```

Afterward, the professor checks the database to confirm that new records exist in:

```text
factura
venta
```

and that product stock has decreased.

## Printing

The invoice detail includes a button that uses:

```javascript
window.print()
```

The invoice buttons and all elements marked with:

```text
d-print-none
```

must not appear in the printed version.

The professor also shows that the browser can disable its own print headers and footers from the print settings.

## Database

During the class, the existing tables in the course schema are used:

```text
factura
venta
producto
usuario
```

No new tables are manually created as part of this class.

## Outside the Week 12 scope

PayPal is NOT implemented during this class.

The professor mentions that Week 13 contains an optional tutorial/challenge to incorporate PayPal in sandbox mode.

PayPal must not be added to the Week 12 result.

## Confirmed new files

```text
src/main/java/com/tienda/controller/CarritoController.java

src/main/java/com/tienda/domain/EstadoFactura.java
src/main/java/com/tienda/domain/Factura.java
src/main/java/com/tienda/domain/Item.java
src/main/java/com/tienda/domain/Venta.java

src/main/java/com/tienda/repository/FacturaRepository.java
src/main/java/com/tienda/repository/VentaRepository.java

src/main/java/com/tienda/service/CarritoService.java
src/main/java/com/tienda/service/FacturaService.java

src/main/resources/templates/carrito/listado.html
src/main/resources/templates/carrito/modifica.html
src/main/resources/templates/carrito/verFactura.html
```

The following file is also included:

```text
src/main/resources/templates/carrito/fragmentos.html
```

## Confirmed modified files

```text
src/main/resources/static/css/estilos.css
src/main/resources/static/js/rutinas.js
src/main/resources/templates/general/fragmentos.html
src/main/resources/templates/consultas/fragmentos.html
```

No Maven dependency is added in Week 12.

## Final checklist

- [ ] The application starts.
- [ ] Out-of-stock products keep the cart button disabled.
- [ ] An available product can be added without reloading the entire page.
- [ ] The View Cart button/total appears.
- [ ] `/carrito/listado` displays the selected products.
- [ ] A product can be removed from the cart.
- [ ] The quantity can be modified.
- [ ] The quantity cannot exceed available stock.
- [ ] The cart remains stored in the session.
- [ ] Billing requires authentication.
- [ ] The purchase creates a `Factura`.
- [ ] The `Venta` detail records are created.
- [ ] Product stock decreases.
- [ ] The cart is cleared after billing.
- [ ] The invoice detail is displayed.
- [ ] Printing works.
- [ ] The header and footer do not appear in the printed version.

## Commit used by the professor

At the end of the recording, the professor confirms the commit message:

```text
Semana 12, carrito de compras
```
