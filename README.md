# 🍔 FoodExpress (v1.1.1)

**FoodExpress** es una aplicación multiplataforma (Kotlin Multiplatform / Compose Multiplatform) diseñada para la gestión integral de un negocio de comida rápida, permitiendo operar tanto en dispositivos **Android** como en equipos de escritorio (**Desktop**).

---

## 🚀 Características Principales (v1.1.1)

1. **Catálogo de Menú y Vista Cliente (`MenuScreen`)**:
   - Visualización moderna tipo tarjeta de todos los productos disponibles.
   - Carga automática de imágenes locales optimizada mediante **Coil 3** (con respaldo de iconos con iniciales).
   - **Vista Previa con Zoom**: Al hacer clic en la imagen de cualquier producto, se despliega una vista ampliada al 50% (modal), la cual se cierra al volver a presionarla.

2. **Punto de Venta (`VentasScreen`)**:
   - Catálogo interactivo de productos con tarjetas *touch-friendly*.
   - **Agregado Rápido**: Con solo presionar el cuerpo de la tarjeta del producto, este se añade inmediatamente al carrito de compras.
   - Gestión completa de cantidades, subtotales y cobro directo conectado a la base de datos.

3. **Gestión de Inventario y Bodega (`PantallaInventario`)**:
   - Control total de stock actual y stock mínimo con alertas visuales.
   - Tarjetas de resumen métrico en tiempo real: *Total Productos*, *Stock Bajo* y *Valor Total Estimado*.
   - Módulo de registro de movimientos (Entradas, Ajustes, Mermas y Devoluciones).
   - Opciones para **crear, editar (modificar) y eliminar** productos directamente desde la interfaz.

4. **Recetas de Producción (`PantallaRecetas`)**:
   - Vinculación de materias primas con productos terminados.
   - Interfaz renovada por tarjetas para seleccionar productos y configurar sus ingredientes.
   - Gestión completa de ingredientes en las recetas (agregar, editar cantidades y eliminar).

5. **Seguridad y Conectividad**:
   - Conexión a base de datos **MySQL** optimizada por transacciones bajo demanda (prevención de desconexiones por inactividad).
   - Bypass temporal de inicio de sesión habilitado para agilizar la etapa de desarrollo y pruebas.

---

## 🛠️ Tecnologías Utilizadas
- **Kotlin Multiplatform (KMP)**
- **Jetpack Compose / Compose Multiplatform** (UI declarativa y responsiva)
- **Coil 3** (Carga asíncrona de imágenes locales y remotas)
- **MySQL / JDBC Driver 5.1.49** (Compatibilidad multiplataforma garantizada)
- **Gradle Kotlin DSL**

---

## 📦 Configuración y Ejecución

1. **Base de Datos**:
   - Ejecuta los scripts de migración ubicados en la carpeta `database/` (incluyendo la adición de la columna `imagen_url` en la tabla `productos_venta`).
   - Configura tus credenciales de conexión en los archivos `db.properties` (tanto para Android en `assets/` como para Desktop en `resources/`).

2. **Compilación y Pruebas**:
   - Para compilar y ejecutar en Android: `./gradlew assembleDebug`
   - Para compilar y ejecutar en Desktop: `./gradlew run`
