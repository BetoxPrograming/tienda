# Week 12 — Carrito de compras

## Objetivo de la clase

La Semana 12 implementa el carrito de compras y el proceso de facturación.

El profesor explica que el carrito no se guarda directamente en una tabla de base de datos mientras el usuario navega. Se mantiene temporalmente en una variable de sesión como una lista de `Item`.

El flujo trabajado en clase es:

```text
Producto
→ agregar al carrito
→ carrito en sesión
→ modificar/eliminar cantidades
→ facturar
→ registrar Factura
→ registrar Venta por cada producto
→ descontar existencias
→ limpiar carrito
→ mostrar factura
→ imprimir factura
```

## Domain

Se agregan:

```text
EstadoFactura.java
Factura.java
Venta.java
Item.java
```

`EstadoFactura` es un `enum` con los estados:

```text
Activa
Pagada
Anulada
```

`Factura` representa el encabezado o maestro de la facturación.

`Venta` representa cada línea de detalle de una factura.

`Item` no es una entidad JPA. Se utiliza temporalmente dentro del carrito y contiene un `Producto`, la cantidad deseada y el precio histórico.

## Repository

Se agregan:

```text
FacturaRepository.java
VentaRepository.java
```

`FacturaRepository` incorpora la consulta JPQL:

```java
findByIdFacturaConDetalle(...)
```

que recupera en una sola consulta:

```text
Factura
Usuario
Ventas
Productos
```

para poder mostrar la factura completa después de procesarla.

No se crea `VentaService` durante esta clase.

## CarritoService

`CarritoService` administra una variable de sesión llamada:

```text
carrito
```

El carrito es:

```java
List<Item>
```

El servicio permite:

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

Se valida que la cantidad solicitada no supere las existencias del producto.

## Facturación

`procesarCompra(...)`:

1. valida que el carrito tenga productos;
2. crea la `Factura`;
3. registra una `Venta` por cada `Item`;
4. conserva el precio histórico;
5. descuenta las existencias del producto;
6. devuelve la factura generada.

La factura queda con estado:

```text
Pagada
```

## FacturaService

`FacturaService` utiliza:

```java
findByIdFacturaConDetalle(...)
```

para recuperar una factura ya procesada con su usuario, ventas y productos.

## CarritoController

Se crea:

```text
src/main/java/com/tienda/controller/CarritoController.java
```

Las rutas trabajadas son:

```text
GET  /carrito/listado
POST /carrito/agregar
POST /carrito/eliminar/{idProducto}
GET  /carrito/modificar/{idProducto}
POST /carrito/actualizar
GET  /facturar/carrito
GET  /carrito/verFactura
```

La ruta:

```text
/facturar/carrito
```

está separada de las rutas `/carrito/**` porque la facturación requiere un usuario autenticado.

Los `System.out.println(...)` utilizados temporalmente para depuración se eliminan durante la clase.

## Templates del carrito

El profesor crea:

```text
src/main/resources/templates/carrito/
```

y coloca:

```text
fragmentos.html
listado.html
modifica.html
verFactura.html
```

`fragmentos.html` contiene los fragmentos:

```text
verCarrito
listadoCarrito
modificaItem
detalleFactura
```

### Fragmentos del carrito

El recurso:

```text
src/main/resources/templates/carrito/fragmentos.html
```

contiene los cuatro fragmentos utilizados por las vistas del carrito:

```text
verCarrito
listadoCarrito
modificaItem
detalleFactura
```

El archivo se incorporó directamente desde el recurso proporcionado por el profesor.

## JavaScript

En:

```text
static/js/rutinas.js
```

se agrega únicamente:

```javascript
addCart(formulario)
```

La función:

- obtiene `idProducto`;
- utiliza `/carrito/agregar`;
- recupera el token CSRF;
- hace el `POST` por AJAX;
- coloca la respuesta en `#resultBlock`.

La función `mostrarImagen(...)` y las rutinas existentes se conservan.

## CSS

El profesor indica retirar el `estilos.css` viejo utilizado desde las primeras semanas y sustituirlo completamente por el recurso de Semana 12.

El nuevo CSS agrega:

- efecto visual en las tarjetas;
- reglas para impresión;
- ocultamiento de elementos `d-print-none`.

El profesor comenta que el efecto de zoom de las tarjetas pertenecía a un reto anterior, pero al entregar este recurso ya queda incorporado.

## general/fragmentos.html

Se agregan al `head`:

```html
<link th:href="@{/css/estilos.css}" rel="stylesheet"/>
<meta name="_csrf" th:content="${_csrf.token}"/>
<meta name="_csrf_header" th:content="${_csrf.headerName}"/>
```

El `rutinas.js` ya existente se mantiene una sola vez.

Para evitar imprimir encabezado y pie de página se agrega:

```text
d-print-none
```

al `header` y al `footer`.

## consultas/fragmentos.html

El formulario del botón del carrito ya existía desde semanas anteriores.

Durante la clase se verifica que el botón que ejecuta:

```javascript
addCart(this.form)
```

sea:

```html
type="button"
```

y no:

```html
type="submit"
```

porque el envío se realiza mediante AJAX.

## Prueba realizada en clase

El profesor inicia sesión como:

```text
rebeca / 456
```

y prueba:

```text
agregar productos
aumentar cantidades
rechazar cantidades mayores al inventario
ver carrito
eliminar
modificar
facturar
ver factura
imprimir
```

Después revisa en la base de datos que existan los registros nuevos en:

```text
factura
venta
```

y que las existencias de los productos hayan disminuido.

## Impresión

El detalle de factura incluye un botón que utiliza:

```javascript
window.print()
```

Los botones de la factura y los elementos marcados con:

```text
d-print-none
```

no deben aparecer en la impresión.

El profesor también muestra que el navegador permite desactivar encabezados y pies de impresión desde la configuración de impresión.

## Base de datos

Durante la clase se utilizan las tablas que ya existen en el esquema del curso:

```text
factura
venta
producto
usuario
```

No se realiza una creación manual nueva de tablas como paso de la clase.

## Fuera del límite de Semana 12

PayPal NO se implementa durante esta clase.

El profesor indica que en Semana 13 hay un tutorial/reto opcional para incorporar PayPal en sandbox.

No debe agregarse PayPal al resultado de Semana 12.

## Archivos nuevos confirmados

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

También se incluye:

```text
src/main/resources/templates/carrito/fragmentos.html
```

## Archivos modificados confirmados

```text
src/main/resources/static/css/estilos.css
src/main/resources/static/js/rutinas.js
src/main/resources/templates/general/fragmentos.html
src/main/resources/templates/consultas/fragmentos.html
```

No se agrega dependencia Maven en Semana 12.

## Checklist final

- [ ] La aplicación inicia.
- [ ] Los productos agotados mantienen deshabilitado el botón del carrito.
- [ ] Un producto disponible se agrega sin recargar toda la página.
- [ ] Aparece el botón/total de Ver Carrito.
- [ ] `/carrito/listado` muestra los productos seleccionados.
- [ ] Se puede eliminar un producto del carrito.
- [ ] Se puede modificar la cantidad.
- [ ] No se permite superar las existencias.
- [ ] El carrito se mantiene en la sesión.
- [ ] Facturar requiere autenticación.
- [ ] La compra crea una factura.
- [ ] Se crean las líneas de venta.
- [ ] El inventario disminuye.
- [ ] El carrito queda vacío después de facturar.
- [ ] Se muestra el detalle de la factura.
- [ ] La impresión funciona.
- [ ] Header y footer no aparecen en la impresión.

## Commit usado por el profesor

Al final de la grabación el profesor confirma el nombre:

```text
Semana 12, carrito de compras
```
