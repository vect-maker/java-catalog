# Catálogo

Aplicación de catálogo de productos hecha con **Spring Boot**. Permite gestionar productos, categorías y usuarios, con autenticación mediante tokens JWT.

## Tecnologías

- **Spring Boot** – framework principal
- **SQLite** – base de datos ligera (archivo `catalog.db`)
- **JPA/Hibernate** – para trabajar con la base de datos usando objetos
- **Spring Security + JWT** – para autenticación y protección de rutas
- **Lombok** – para reducir código repetitivo

## Modelos principales

### Cuenta (`Account`)
Representa a un usuario del sistema.
- `email` – correo único
- `username` – nombre de usuario único
- `passwordHash` – contraseña encriptada
- `role` – puede ser `USER` (normal) o `ADMIN` (administrador)
- Soporta *eliminación suave* (soft delete): no se borra de la base de datos, solo se marca como eliminado

### Producto (`Product`)
Representa un artículo del catálogo.
- `title` – nombre del producto
- `description` – descripción
- `price` – precio
- `publishedBy` – usuario que lo publicó
- `categories` – categorías asociadas
- También soporta eliminación suave

### Categoría (`Category`)
Clasificación de productos.
- `name` – nombre único
- `description` – descripción opcional
- Solo los administradores pueden crear, editar o eliminar categorías

### Me gusta (`ProductLike`)
Permite que un usuario dé "me gusta" a un producto. Es una relación entre cuenta y producto.

## Autenticación

El sistema usa **JWT** (JSON Web Tokens). No mantiene sesiones en el servidor; cada petición debe llevar un token válido.

### Flujo de autenticación

1. **Registro** – `POST /auth/register`
   - Se envía email, username y password
   - Se crea la cuenta con rol `USER`
   - El email y username deben ser únicos

2. **Inicio de sesión** – `POST /auth/login`
   - Se envía email y password
   - Si son correctos, devuelve un **token JWT**

3. **Usar el token** – En cada petición protegida se debe enviar el header:
   ```
   Authorization: Bearer <token>
   ```

4. **Mi perfil** – `PATCH /auth/me` y `DELETE /auth/me`
   - Permite actualizar username/password o eliminar la propia cuenta

### Roles

- `USER`: puede crear productos, editar/eliminar solo los suyos, dar "me gusta" y ver el catálogo.
- `ADMIN`: además de lo anterior, puede gestionar categorías.

## Endpoints principales

| Método | Ruta | Descripción | Requiere token |
|--------|------|-------------|----------------|
| POST | `/auth/register` | Crear cuenta | No |
| POST | `/auth/login` | Iniciar sesión | No |
| PATCH | `/auth/me` | Actualizar perfil | Sí |
| DELETE | `/auth/me` | Eliminar cuenta | Sí |
| GET | `/products` | Listar productos | Sí |
| POST | `/products` | Crear producto | Sí |
| GET | `/products/{id}` | Ver producto | Sí |
| PUT | `/products/{id}` | Editar producto (solo dueño) | Sí |
| DELETE | `/products/{id}` | Eliminar producto (solo dueño) | Sí |
| POST | `/products/{id}/like` | Dar me gusta | Sí |
| DELETE | `/products/{id}/like` | Quitar me gusta | Sí |
| GET | `/products/{id}/likes` | Ver likes del producto | Sí |
| GET | `/categories` | Listar categorías | Sí |
| POST | `/categories` | Crear categoría | Sí (ADMIN) |
| PUT | `/categories/{id}` | Editar categoría | Sí (ADMIN) |
| DELETE | `/categories/{id}` | Eliminar categoría | Sí (ADMIN) |

## Configuración

Las propiedades principales están en `application.properties`:
- Base de datos SQLite local (`catalog.db`)
- Secreto JWT y tiempo de expiración (24 horas por defecto)

## Cómo ejecutar

```bash
./mvnw spring-boot:run
```

La aplicación se inicia y crea/actualiza las tablas automáticamente. También crea un usuario administrador por defecto:
- **Email:** `admin@catalog.local`
- **Contraseña:** `admin123`
