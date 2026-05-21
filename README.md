# Proyecto Vaadin Retail Management

## Descripción

Esta es una aplicación web basada en **Vaadin 24** y **Spring Boot 3** diseñada para la gestión de un negocio retail.  

- **Login** con usuario y contraseña estáticos (`admin` / `admin`).
- Interfaz moderna con **logo retail**, fondo rojo claro y paleta de colores (rojo, blanco y azul).
- Cinco vistas CRUD (Clientes, Productos, Proveedores, Facturas y Usuarios) que demuestran el uso de *Binder* y *DataProvider*.
- Seguridad básica que protege todas las vistas excepto la página de login.

El proyecto está configurado para compilar y ejecutarse con **Maven** y utiliza los recursos estáticos de Vaadin ubicados en `src/main/resources/META-INF/resources` (imágenes, estilos CSS).  

## Requisitos

- Java 17 (o superior)
- Maven 3.8+ 
- Git (para clonar el repositorio)

## Cómo ejecutar el proyecto

1. **Clonar el repositorio** (si aún no lo tienes):
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd WebApp-Vaadin
   ```

2. **Compilar el proyecto** (descarga de dependencias y generación de clases):
   ```bash
   mvn clean compile
   ```

3. **Iniciar la aplicación**:
   ```bash
   mvn spring-boot:run
   ```
   La aplicación se iniciará en `http://localhost:8080`.

4. **Acceder a la aplicación**:
   - Abra el navegador y vaya a `http://localhost:8080/login`.
   - Ingrese `admin` como usuario y `admin` como contraseña.
   - Tras el login será redirigido al dashboard principal.

## Estructura de directorios importante

- `src/main/java/com/retail/views/` – Vistas Vaadin (Login, CRUD, etc.).
- `src/main/resources/META-INF/resources/` – Recursos estáticos servidos por Vaadin:
  - `images/` → contiene `retail_logo.png`.
  - `styles.css` → estilos personalizados (fondo rojo, tipografía, etc.).
- `src/main/resources/application.properties` – Configuración de Spring Boot.
- `pom.xml` – Definiciones de dependencias y plugins Maven.

## Personalización

- **Cambiar el logo**: reemplace el archivo `src/main/resources/META-INF/resources/images/retail_logo.png` por su propio logo y mantenga el mismo nombre.
- **Colores**: modifique `styles.css` para adaptar la paleta de colores.
- **Credenciales**: en `LoginView.java` ajuste los valores `admin` según sea necesario.

---

¡Listo! Con estos pasos tendrás la aplicación en funcionamiento y podrás comenzar a extenderla para cubrir más funcionalidades de tu negocio retail.
