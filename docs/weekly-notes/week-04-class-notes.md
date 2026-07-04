notas rapidas de la clase 4

!nota
el profe esta explicando que vamos a usar un servicio en la nube apra las imagenes, en la plataforma de firebase, esto 
es para que las imagenes no esten quemadas, si usamos un url entones no es necesario pero la iamgen podria dejar de exisitr
por eso se usa firebase

paso 1 ir a firebase y hacer una cuenta
pas 2 ir a la consola
paso 3 crear un proeycto y ponerle nombre tienda

!improtante
aunque el nombre sea tienda firebase va ausar un nombre que muesta en pantalla en este caso: tienda-973a7 es un ifd unico
por que hay mil proyectos tienda.

pasoo 4 abilidar gemini si quieren pero si poner el analytics, dejar estado unidos en ubicacion y crear proyecto, le damos continar cuando esta terminado

!notas
firebase es mas que solo subir imagenes, esto es una facilidad de la nuba.
para este proyto nos interesa solo el storage, pero es muy potente.
en desaroolo movel funcina muy bien el autentificacion

paso 5 ingresar a storage y le damos a update, y cramos una cuenta de facturacion
seguimos los pasos que nos da la plataforma
el storage dejarlo en us east es solo donde queremos alojado el storage

paas 6 (super improtante)
ir al rules, en la linea 9 quitar el permiso para que quede: allow read, write;
paso 7, ir a settings, ir a general y guardar: ID del proyecto
tienda-973a7
paso 8, copiar el url qqe sale en storage gs://tienda-973a7.firebasestorage.app y guaralo tambein 
paso 9. regresar al setting, ir a services aount y generamos una llave privada 
paso 10., crear un nuevo directorio en main "firebase", aqui guardamos el arhivo de la llave privada

ya con esto la comunicacion entre el springboot y firebase va bien encaminada

paso 11 creamos una nueva dependecia en el pom
<dependency>
<groupId>com.google.firebase</groupId>
<artifactId>firebase-admin</artifactId>
<version>9.9.0</version>
</dependency>

recordar hacer clean and build

paso 12 añadimos un par de elemntos en el aplication properties (tener en cuenta que hay que actualiar el example)

#Esta información se utiliza para obtener acceso a su storage.
firebase.bucket.name=tienda-973a7.firebasestorage.app
firebase.storage.path=techshop
firebase.json.path=firebase
firebase.json.file=tienda-973a7-firebase-adminsdk-fbsvc-1540eed81f.json

hay que editar el proyecto id que se guardó y cambiamos tambien el nombre del archivo json llave privada
volver ha hacr clean and build

!nootaa, esto puede o debe ir en variables del entorno, por cuestiones de seguridad pero para este preycto no se hace.

paso 13, vamos a com.tienda y pegamos el archivo storage.config que nemos en los ecursos dle proeycto
estae archivo busca el jsonpath y el jsonfile, para ecir que el archivo el contrato esta en ste lugar y con esta llave, eso es todo, para opbtener las credenciaes
!nota esta calse es publca para poder usars en cualquier proyecto, no tn licencia, es generica

subimos el firebaseStorageSerice, que es talbien genecio y es para la coniguracion de las imagnes. (lo guardamos en service)

pase 14, vamos a incorporar el servicio en categoriaService
ahora este docuemto tambien va a guardar imagenes de las categorias
incomporando el servicio del storage, agregango private final FirebaseStorageService firebaseStorageService;
y arreglamos el contructor para que tenga ahora las 2 atributos de la clase

paso 15, vamos a agregar los metodos necesarios para que funcione

    // recupera 1 reguistro de categoria
    @Transactional(readOnly =true)
    public Optional<Categoria> getCategoria(Integer idCategoria){
        return categoriaRepository.findById(idCategoria);
    }

    // si  categoria tra un id categoria se atualia el regstro, sino se crea
    @Transactional
    public void save(Categoria categoria, MultipartFile imagenFile){ //Multipartfoile es el tipo de dato de la iamgen
        //se salva la categoria
        categoriaRepository.save(categoria); //o actualiza o inserta, hace al eccon en el objeto, si tiene id o si no tiene id
        if (imagenFile.isEmpty()){ // nsi no esta vaia el archivo, quiere decir que tra imagen para guardar
            try {
                String ruta = firebaseStorageService.uploadImage(
                        imagenFile,
                        "categoria",
                        categoria.getIdCategoria()
                        );
                categoria.setRutaImagen(ruta);
                categoriaRepository.save(categoria); //esto guarda la imagen
            } catch (IOException e) {

            }
        }

    }

    // si  id categoria existe se elimina si no tiene productos asociados
    @Transactional
    public void delete(Integer idCategoria){
//se valida que la categoria exista
if (!categoriaRepository.existsById(idCategoria)){ //sino existe
//ppenemos en multiusuarios, otra persona peude estar viendo lo mismo que otra persona y borrar lo mismo por eso se lanza la excpion
throw new IllegalArgumentException("La categoria no existe"); //trato de borrar algo que ya no existe

        }
        try {
            categoriaRepository.deleteById(idCategoria); //aqui busca el id y borra la categoria
        } catch (DataIntegrityViolationException e){ //esto es por si la categoria a eliminar tiene productos
            throw new IllegalArgumentException("La categoria no se puee elimiar, tiene productos asociados");
            
        }

paso  16, ir al controller para añadir metodos, esto simplemetne se hace cambiando el archhvo por otro nuevo que esta en los archivos de recusos
coin sto ya tenemos el srvicio y el controlador

paso 17, vamos a ahcer que en web cuando se selciona una imagen haga un previwe de la imagen subida, esto esta en fdragmentos, tenemos un onchange que ejecuta una fucnin, solo vamos a incorporarla en el proeyto
ahacemos un folder en static llamado js, aqui pegamos  l archivo rutinas.js que esta en los archivos del curso

paso 18
en fragmentos general linea 12 
agregamos
<script th:src="@{/webjars/jquery/jquery.min.js}"></script>
<script src="@{/js/rutinas.js"></script>

tambien tenemos que agregar un fragmeto que eta en los archivos del proeycto

<!-- Fragmentos #4 define una ventana tipo Toast para mostrar mensajes al usuario -->

vamos a listado.html que esat en categoria y añadimos unas lineas:

            <section th:replace="~{categoria/fragmentos :: confirmarEliminar}"></section>
            <section th:replace="~{general/fragmentos :: mostrarToast}"></section>

paso 19, dupicamos el archivo listado y le llamamos modifica.html

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity6">
        <head th:replace="~{general/fragmentos :: head}"></head>
        <body>
            <header th:replace="~{general/fragmentos :: header}"></header>

            <section th:replace="~{categoria/fragmentos :: editar}"></section>
            <section th:replace="~{general/fragmentos :: mostrarToast}"></section>


            <footer th:replace="~{general/fragmentos :: footer}"></footer>


        </body>
</html>














