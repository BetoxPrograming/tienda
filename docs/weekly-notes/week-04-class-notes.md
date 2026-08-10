# Week 04 — Firebase Storage and Category CRUD

## Class goals

During this class, the `tienda` project was extended so that categories can be:

- Created.
- Updated.
- Deleted.
- Stored with an image in Firebase Storage.
- Previewed in the browser before the form is submitted.

The project already had category listing and database access from Week 03. Week 04 adds the remaining CRUD operations and cloud image storage.

---

## Before starting

Confirm that the Aiven MySQL service is running. If the service is stopped, the application will not be able to load the category list from the cloud database.

> [!NOTE]
> The images registered in the project will not be stored directly on the computer or inside the repository. Firebase Storage is used so that the image remains available from a controlled cloud location. A normal external URL could stop working if the original image is removed.

---

## Step 1 — Open Firebase

Go to Firebase and sign in with a Google account.

```text
https://console.firebase.google.com/
```

---

## Step 2 — Open the Firebase Console

From the Firebase page, enter the console to create and manage the project.

---

## Step 3 — Create the project

Create a new project and use the following project name:

```text
tienda
```

> [!IMPORTANT]
> Although the visible project name is `tienda`, Firebase assigns a unique internal project ID. In this project, the generated ID was:
>
> ```text
> tienda-973a7
> ```
>
> The unique ID is necessary because many Firebase projects can share the same visible name.

---

## Step 4 — Finish the initial project configuration

During project creation:

1. Gemini can be enabled if desired.
2. Enable Google Analytics.
3. Keep the Analytics location in the United States.
4. Create the project.
5. Select **Continue** when Firebase finishes the setup.

> [!NOTE]
> Firebase provides more than image storage. It includes services such as authentication, databases, hosting, analytics, and cloud tools. For this project, the main feature used in Week 04 is Firebase Storage.
>
> Firebase authentication is also especially useful in mobile development.

---

## Step 5 — Enable Firebase Storage

Inside the Firebase project:

1. Open **Storage**.
2. Select **Update project**.
3. Create or connect a billing account.
4. Follow the steps shown by Firebase.
5. Start Storage.
6. Select a no-cost location.
7. Keep the Storage location in the eastern United States when that option is available.

The selected region defines the datacenter location where the files are stored.

---

## Step 6 — Configure the Storage rules

Open the **Rules** tab in Firebase Storage.

The initial rule contains:

```text
allow read, write: if false;
```

For the class exercise, modify it so it becomes:

```text
allow read, write;
```

Publish the rule after making the change.

> [!IMPORTANT]
> This was a super important class step because the project needs permission to read and write images during the exercise.
>
> These public rules are only appropriate for the class development exercise. A production application should protect Storage with authentication and restricted access rules.

---

## Step 7 — Save the Firebase project ID

Go to:

```text
Project settings → General
```

Save the project ID:

```text
tienda-973a7
```

This value identifies the Firebase project used by the Spring Boot application.

---

## Step 8 — Save the Storage bucket reference

Open Firebase Storage and copy the bucket reference:

```text
gs://tienda-973a7.firebasestorage.app
```

The bucket name used by the project is:

```text
tienda-973a7.firebasestorage.app
```

---

## Step 9 — Generate the private key

Return to:

```text
Project settings → Service accounts
```

Generate a new private key and download the JSON file.

The private key allows the Spring Boot application to authenticate with Firebase and create the `Storage` client.

> [!IMPORTANT]
> The downloaded JSON file contains private credentials. It must not be published in GitHub.

---

## Step 10 — Create the Firebase resource directory

Create the following directory inside the project:

```text
src/main/resources/firebase
```

Place the downloaded private-key JSON file inside that folder.

Example filename used in this project:

```text
tienda-973a7-firebase-adminsdk-fbsvc-1540eed81f.json
```

The expected project structure is:

```text
src
└── main
    └── resources
        └── firebase
            └── tienda-973a7-firebase-adminsdk-fbsvc-1540eed81f.json
```

This location allows `StorageConfig` to find the file through the application classpath.

---

## Step 11 — Add the Firebase dependency

Add the Firebase Admin dependency to `pom.xml`:

```xml
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.9.0</version>
</dependency>
```

After modifying `pom.xml`, perform a clean build.

In the IntelliJ terminal on Windows:

```powershell
.\mvnw.cmd clean install
```

This removes the previous `target` directory, downloads the new dependency, compiles the project, runs the tests, and generates the application package.

---

## Step 12 — Add the Firebase properties

Add the following values to the private `application.properties` file:

```properties
#Esta información se utiliza para obtener acceso a su storage.
firebase.bucket.name=tienda-973a7.firebasestorage.app
firebase.storage.path=techshop
firebase.json.path=firebase
firebase.json.file=tienda-973a7-firebase-adminsdk-fbsvc-1540eed81f.json
```

The values have the following purpose:

| Property | Purpose |
|---|---|
| `firebase.bucket.name` | Identifies the Firebase Storage bucket. |
| `firebase.storage.path` | Defines the base folder used inside the bucket. |
| `firebase.json.path` | Defines the classpath folder containing the JSON key. |
| `firebase.json.file` | Defines the exact private-key filename. |

Update `application.properties.example` with generic placeholders so that the required configuration remains documented without publishing private information:

```properties
firebase.bucket.name=FIREBASE_BUCKET_NAME
firebase.storage.path=FIREBASE_STORAGE_PATH
firebase.json.path=FIREBASE_JSON_PATH
firebase.json.file=FIREBASE_JSON_FILE
```

Run the clean build again after updating the configuration:

```powershell
.\mvnw.cmd clean install
```

> [!NOTE]
> The class explained that these values can later be moved to environment variables as a better security practice. For the class project, they are read from `application.properties`.
>
> In this repository, `application.properties` and the Firebase private-key JSON file must remain private and ignored by Git.

---

## Step 13 — Add `StorageConfig` and `FirebaseStorageService`

Place the following class in the `com.tienda` package:

```text
StorageConfig.java
```

`StorageConfig` reads:

```properties
firebase.json.path
firebase.json.file
```

It locates the private-key file, creates the Google credentials, and exposes the Firebase `Storage` object as a Spring bean.

Relevant project structure:

```java
@Configuration
public class StorageConfig {

    @Value("${firebase.json.path}")
    private String jsonPath;

    @Value("${firebase.json.file}")
    private String jsonFile;

    @Bean
    public Storage storage() throws IOException {
        ClassPathResource resource =
                new ClassPathResource(jsonPath + File.separator + jsonFile);

        try (InputStream inputStream = resource.getInputStream()) {
            GoogleCredentials credentials =
                    GoogleCredentials.fromStream(inputStream);

            return StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
        }
    }
}
```

Also add:

```text
FirebaseStorageService.java
```

This service reads:

```properties
firebase.bucket.name
firebase.storage.path
```

Its responsibility is to receive a `MultipartFile`, create a consistent filename, upload the file to Firebase, and return the generated image URL.

> [!NOTE]
> `StorageConfig` and `FirebaseStorageService` are generic classes that can be reused in other projects that need to communicate with Firebase Storage.

---

## Step 14 — Inject Firebase into `CategoriaService`

Add the Firebase service as a dependency of `CategoriaService`:

```java
private final CategoriaRepository categoriaRepository;
private final FirebaseStorageService firebaseStorageService;
```

Update the constructor so that Spring injects both dependencies:

```java
public CategoriaService(
        CategoriaRepository categoriaRepository,
        FirebaseStorageService firebaseStorageService) {

    this.categoriaRepository = categoriaRepository;
    this.firebaseStorageService = firebaseStorageService;
}
```

This connects the category business logic with both persistence systems:

- MySQL stores category data.
- Firebase Storage stores category images.

---

## Step 15 — Add `getCategoria`, `save`, and `delete`

### Retrieve one category

```java
// recupera 1 reguistro de categoria
@Transactional(readOnly = true)
public Optional getCategoria(Integer idCategoria) {
    return categoriaRepository.findById(idCategoria);
}
```

This method is used when the application needs to open an existing category for modification.

### Save or update a category

```java
// si categoria tra un id categoria se atualia el regstro, sino se crea
@Transactional
public void save(Categoria categoria, MultipartFile imagenFile) {

    //Multipartfoile es el tipo de dato de la iamgen
    //se salva la categoria
    categoriaRepository.save(categoria);

    //o actualiza o inserta, hace al eccon en el objeto,
    //si tiene id o si no tiene id
    if (!imagenFile.isEmpty()) {
        try {
            String ruta = firebaseStorageService.uploadImage(
                    imagenFile,
                    "categoria",
                    categoria.getIdCategoria()
            );

            categoria.setRutaImagen(ruta);
            categoriaRepository.save(categoria);

        } catch (IOException e) {
        }
    }
}
```

The category is saved first so that it has an `idCategoria`. That ID is then used to generate a consistent image filename in Firebase.

If the category already has an ID, JPA updates the existing record. If it does not have an ID, JPA inserts a new record.

### Delete a category

```java
// si id categoria existe se elimina si no tiene productos asociados
@Transactional
public void delete(Integer idCategoria) {

    //se valida que la categoria exista
    if (!categoriaRepository.existsById(idCategoria)) {

        //ppenemos en multiusuarios, otra persona peude estar viendo
        //lo mismo que otra persona y borrar lo mismo
        throw new IllegalArgumentException("La categoria no existe");
    }

    try {
        categoriaRepository.deleteById(idCategoria);

    } catch (DataIntegrityViolationException e) {

        //esto es por si la categoria a eliminar tiene productos
        throw new IllegalStateException(
                "La categoria no se puee elimiar, tiene productos asociados"
        );
    }
}
```

The first validation handles the case where another user already deleted the same category. The database-integrity exception handles the case where the category cannot be deleted because it has associated products.

---

## Step 16 — Update `CategoriaController`

Replace or update the controller with the version provided in the Week 04 resources.

The controller now manages these operations:

| Route | Method | Purpose |
|---|---|---|
| `/categoria/listado` | `GET` | Displays all categories. |
| `/categoria/guardar` | `POST` | Creates or updates a category and receives `imagenFile`. |
| `/categoria/eliminar` | `POST` | Deletes a category and returns a result message. |
| `/categoria/modificar/{idCategoria}` | `GET` | Loads one category in the modification form. |

The save method receives both the category and the selected file:

```java
@PostMapping("/guardar")
public String guardar(
        @Valid Categoria categoria,
        @RequestParam MultipartFile imagenFile,
        RedirectAttributes redirectAttributes) {

    categoriaService.save(categoria, imagenFile);

    redirectAttributes.addFlashAttribute(
            "todoOk",
            messageSource.getMessage(
                    "mensaje.actualizado",
                    null,
                    Locale.getDefault()
            )
    );

    return "redirect:/categoria/listado";
}
```

The delete method uses `RedirectAttributes` to send success or error messages after redirecting back to the list.

---

## Step 17 — Add the JavaScript image preview

Create the following directory:

```text
src/main/resources/static/js
```

Copy the course resource into it:

```text
rutinas.js
```

The `mostrarImagen(input)` function:

1. Reads the selected file.
2. Validates that it does not exceed 512 KB.
3. Uses `FileReader`.
4. Places the image in the element with the ID `blah`.
5. Displays the preview with a height of 200 pixels.

The same file also:

- Sends category data to the confirmation modal.
- Automatically hides Toast messages after the configured time.

---

## Step 18 — Load `rutinas.js` and update the category list

In `general/fragmentos.html`, load Bootstrap, jQuery, and the project JavaScript file:

```html
<script th:src="@{/webjars/bootstrap/js/bootstrap.bundle.min.js}"></script>
<script th:src="@{/webjars/jquery/jquery.min.js}"></script>
<script th:src="@{/js/rutinas.js}"></script>
```

> [!IMPORTANT]
> The `rutinas.js` path must use `th:src` and the Thymeleaf expression must close correctly:
>
> ```html
> <script th:src="@{/js/rutinas.js}"></script>
> ```
>
> If this file does not load, neither the image preview nor the category information passed to the delete modal will work.

Add the fragment supplied in the course resources for:

- The category form.
- The delete-confirmation modal.
- Success and error Toast messages.

Update:

```text
src/main/resources/templates/categoria/listado.html
```

The list must provide:

- A button to create a category.
- A button to modify each category.
- A button to open the deletion modal.
- `data-bs-id` with the category ID.
- `data-bs-descripcion` with the category description.

The JavaScript modal listener places that data into:

```text
modalId
modalDescripcion
```

---

## Step 19 — Create `modifica.html`

Duplicate the category page structure and create:

```text
src/main/resources/templates/categoria/modifica.html
```

This template is used for both creating and updating categories.

The form must:

- Send data to `/categoria/guardar`.
- Use `POST`.
- Use `multipart/form-data`.
- Bind the `Categoria` object.
- Receive the file as `imagenFile`.
- Call `mostrarImagen(this)` when the selected file changes.
- Display the preview in an image element with `id="blah"`.
- Preserve the current `rutaImagen` when editing an existing category.

Important form attributes:

```html
<form th:action="@{/categoria/guardar}"
      method="post"
      enctype="multipart/form-data"
      th:object="${categoria}">
```

Image input:

```html
<input type="file"
       name="imagenFile"
       onchange="mostrarImagen(this)">
```

Preview target:

```html
<img id="blah"
     th:src="${categoria.rutaImagen}">
```

---

## CRUD flow completed in Week 04

### Create

```text
modifica.html
    → CategoriaController.guardar()
    → CategoriaService.save()
    → categoriaRepository.save()
    → FirebaseStorageService.uploadImage()
```

### Read

```text
CategoriaController.listado()
    → CategoriaService.getCategorias()
    → CategoriaRepository
    → listado.html
```

### Update

```text
/categoria/modificar/{idCategoria}
    → CategoriaService.getCategoria()
    → modifica.html
    → /categoria/guardar
    → CategoriaService.save()
```

### Delete

```text
Delete button
    → confirmation modal
    → /categoria/eliminar
    → CategoriaService.delete()
    → redirect:/categoria/listado
```

---

## Main concepts

### `MultipartFile`

`MultipartFile` is the Spring type used to receive an uploaded file from a form that uses:

```html
enctype="multipart/form-data"
```

### Dependency injection

`CategoriaService` does not manually create `CategoriaRepository` or `FirebaseStorageService`. Spring injects both dependencies through the constructor.

### Spring bean

`StorageConfig` creates a `Storage` bean. Spring can then inject the same configured Firebase client into `FirebaseStorageService`.

### Cloud Storage

The database stores the image URL in `rutaImagen`, while the actual image file is stored in Firebase Storage.

### `RedirectAttributes`

`RedirectAttributes` keeps a success or error message available after a redirect. The message is then displayed through a Toast component.

### Transaction

`@Transactional` groups database operations into a transaction. `readOnly = true` is used for methods that only retrieve data.

---

## Final verification checklist

- [ ] Aiven is running.
- [ ] The application connects to the `techshop` database.
- [ ] Firebase Storage is enabled.
- [ ] The Firebase rules were published for the class exercise.
- [ ] The private-key JSON exists in `src/main/resources/firebase`.
- [ ] The JSON filename matches `firebase.json.file`.
- [ ] The Firebase dependency exists in `pom.xml`.
- [ ] The project was cleaned and built.
- [ ] `StorageConfig` creates the `Storage` bean.
- [ ] `FirebaseStorageService` uploads images.
- [ ] `CategoriaService` includes `getCategoria`, `save`, and `delete`.
- [ ] `CategoriaController` includes save, delete, and modify routes.
- [ ] `rutinas.js` exists under `static/js`.
- [ ] `fragmentos.html` loads `rutinas.js` with `th:src`.
- [ ] The image preview works.
- [ ] Categories can be created.
- [ ] Categories can be modified.
- [ ] Categories can be deleted when they have no associated products.
- [ ] Success and error Toast messages appear.
