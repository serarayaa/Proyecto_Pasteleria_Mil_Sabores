# 📦 DOCUMENTACIÓN DE REPOSITORIOS - MIL SABORES v2.2.0

## 🏗️ Arquitectura de Repositorios

El proyecto utiliza el **Pattern Repository** para abstraer la lógica de acceso a datos. Esto permite separar la presentación de la lógica de negocio.

```
ViewModels
    ↓
Repositories ← Abstracción de datos
    ↓
Data Sources (Firebase, LocalStorage)
```

---

## 📋 LISTA DE REPOSITORIOS

### 1. CarritoRepository
### 2. PedidosRepository
### 3. PedidosLocalStorage
### 4. AuthRepository
### 5. FirebaseAuthDataSource
### 6. RecordatorioRepository
### 7. MediaRepository

---

## 1️⃣ CarritoRepository

**Ubicación:** `repository/CarritoRepository.kt`

### Responsabilidad
Gestionar el carrito de compras del usuario en memoria durante la sesión.

### Métodos Principales

```kotlin
class CarritoRepository {
    // Propiedades observables
    val items: StateFlow<List<CarritoItem>>
    val cantidadTotal: StateFlow<Int>
    
    // Operaciones
    fun agregarProducto(item: CarritoItem)
    fun removerProducto(productoId: String)
    fun actualizarCantidad(productoId: String, nuevaCantidad: Int)
    fun limpiarCarrito()
    fun actualizarProducto(item: CarritoItem)
}
```

### Funcionamiento

```
1. agregarProducto()
   ├─ Si producto existe: incrementar cantidad
   └─ Si no existe: agregar nuevo

2. removerProducto()
   └─ Filtrar y eliminar del carrito

3. actualizarCantidad()
   ├─ Si cantidad > 0: actualizar
   └─ Si cantidad <= 0: remover

4. limpiarCarrito()
   └─ Vaciar carrito completamente
```

### Datos Guardados

```kotlin
data class CarritoItem(
    val productoId: String,
    val titulo: String,
    val precio: Double,
    val imagen: String,
    val cantidad: Int = 1
)
```

### Uso en ViewModel

```kotlin
class CarritoViewModel(
    private val repo: CarritoRepository = CarritoRepository.getInstance()
) {
    val items = repo.items // Observar items del carrito
    
    fun agregarProducto(item: CarritoItem) {
        repo.agregarProducto(item)
    }
}
```

### Persistencia
🔴 **NO es persistente** - Se mantiene en memoria mientras la app está abierta.
Cuando cierras la app, el carrito se vacía.

---

## 2️⃣ PedidosRepository

**Ubicación:** `repository/PedidosRepository.kt`

### Responsabilidad
Gestionar pedidos en la base de datos Firestore (servidor).

### Métodos Principales

```kotlin
class PedidosRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    // Crear pedido en servidor
    suspend fun crearPedido(pedido: Pedido): Result<String>
    
    // Observar cambios en tiempo real
    fun observarPedidosUsuario(): Flow<List<Pedido>>
    
    // Obtener un pedido específico
    suspend fun obtenerPedido(pedidoId: String): Pedido?
    
    // Actualizar estado del pedido
    suspend fun actualizarPedido(pedido: Pedido)
}
```

### Funcionamiento

```
crearPedido(pedido):
1. Obtener UID del usuario autenticado
2. Generar ID único para el pedido
3. Crear estructura de datos con:
   - id, uid, fecha, productos, total, estado, observaciones
4. Guardar en Firestore: /pedidos/{pedidoId}
5. Retornar Result<String> con el ID

observarPedidosUsuario():
1. Crear listener en Firestore
2. Filtrar pedidos por uid actual
3. Ordenar por fecha descendente
4. Retornar Flow que notifica cambios en tiempo real
```

### Estructura en Firestore

```
/pedidos
  ├─ pedido_1
  │  ├─ id: "pedido_1"
  │  ├─ uid: "usuario_123"
  │  ├─ fecha: 1729000000000
  │  ├─ total: 15000
  │  ├─ estado: "PENDIENTE"
  │  ├─ observaciones: "Sin frutos secos"
  │  └─ productos:
  │     ├─ 0:
  │     │  ├─ nombre: "Pastel Chocolate"
  │     │  ├─ cantidad: 1
  │     │  └─ precio: 15000
  │     └─ ...
  └─ ...
```

### Persistencia
🟢 **PERSISTENTE EN SERVIDOR** - Se guarda en Firestore y es recuperable siempre.

---

## 3️⃣ PedidosLocalStorage

**Ubicación:** `data/local/PedidosLocalStorage.kt`

### Responsabilidad
Gestionar almacenamiento local de pedidos usando SharedPreferences + JSON.

### Métodos Principales

```kotlin
class PedidosLocalStorage(private val context: Context) {
    // Guardar pedido localmente
    fun guardarPedido(pedido: Pedido): Result<String>
    
    // Obtener todos los pedidos
    fun obtenerTodosPedidos(): List<Pedido>
    
    // Obtener pedidos de un usuario
    fun obtenerPedidosUsuario(uid: String): List<Pedido>
    
    // Actualizar pedido
    fun actualizarPedido(pedido: Pedido): Result<String>
    
    // Obtener por ID
    fun obtenerPedidoPorId(pedidoId: String): Pedido?
    
    // Eliminar pedido
    fun eliminarPedido(pedidoId: String): Result<Boolean>
    
    // Limpiar todo
    fun limpiarTodos(): Result<Boolean>
}
```

### Funcionamiento

```
guardarPedido(pedido):
1. Obtener lista de pedidos actual (JSON)
2. Agregar nuevo pedido
3. Serializar a JSON usando GSON
4. Guardar en SharedPreferences
5. Retornar Result<String>

obtenerTodosPedidos():
1. Leer JSON de SharedPreferences
2. Deserializar usando GSON
3. Retornar lista de Pedido
```

### Almacenamiento

```
SharedPreferences
├─ Key: "MilSaboresPedidos"
└─ Value: [JSON array de pedidos]

Ejemplo JSON:
[
  {
    "id": "PEDIDO_1729000000000",
    "uid": "usuario_123",
    "fecha": 1729000000000,
    "productos": [
      {"nombre": "Pastel Chocolate", "cantidad": 1, "precio": 15000}
    ],
    "total": 15000,
    "estado": "PENDIENTE",
    "observaciones": ""
  },
  ...
]
```

### Persistencia
🟢 **PERSISTENTE LOCALMENTE** - Se guarda en el dispositivo y persiste incluso al cerrar la app.

### Ventajas vs Firestore

| Aspecto | Firestore | LocalStorage |
|--------|-----------|--------------|
| Conexión | Requerida | No requerida |
| Velocidad | Segundos | Instantáneo |
| Sincronización | Automática | Manual |
| Compartir datos | Entre dispositivos | Solo local |

---

## 4️⃣ AuthRepository

**Ubicación:** `repository/auth/AuthRepository.kt`

### Responsabilidad
Gestionar autenticación del usuario con Firebase.

### Métodos Principales

```kotlin
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    // Iniciar sesión
    suspend fun login(email: String, pass: String): User?
    
    // Crear nueva cuenta
    suspend fun register(email: String, pass: String): User?
    
    // Enviar reset de contraseña
    suspend fun sendPasswordReset(email: String)
    
    // Obtener usuario actual
    fun currentUser(): FirebaseUser?
    
    // Cerrar sesión
    fun signOut()
}
```

### Funcionamiento

```
login(email, pass):
1. Llamar Firebase Authentication
2. Validar credenciales
3. Si éxito: retornar User con uid y email
4. Si fallo: lanzar excepción

register(email, pass):
1. Validar email y contraseña
2. Crear usuario en Firebase
3. Si éxito: retornar User
4. Si fallo: lanzar excepción

signOut():
1. Cerrar sesión en Firebase
2. Limpiar token de autenticación
```

### Usuario

```kotlin
data class User(
    val uid: String,      // Identificador único
    val email: String     // Email del usuario
)
```

### Seguridad

✅ **Firebase Authentication valida:**
- Email válido
- Contraseña mínimo 6 caracteres
- No almacena contraseña en texto plano
- Encriptación end-to-end

---

## 5️⃣ FirebaseAuthDataSource

**Ubicación:** `repository/auth/FirebaseAuthDataSource.kt`

### Responsabilidad
Fuente de datos específica para autenticación (abstracción de Firebase).

### Métodos Principales

```kotlin
class FirebaseAuthDataSource(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String): AuthResult
    suspend fun sendPasswordReset(email: String)
    fun logout()
}

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val exception: Exception) : AuthResult()
}
```

### Uso

```kotlin
// AuthRepository usa FirebaseAuthDataSource
class AuthRepository(
    private val dataSource: FirebaseAuthDataSource = FirebaseAuthDataSource()
) {
    suspend fun login(email: String, pass: String): User? {
        return when (val result = dataSource.login(email, pass)) {
            is AuthResult.Success -> result.user
            is AuthResult.Error -> throw result.exception
        }
    }
}
```

---

## 6️⃣ RecordatorioRepository

**Ubicación:** `repository/auth/RecordatorioRepository.kt`

### Responsabilidad
Gestionar recordatorios/notificaciones del usuario.

### Métodos Principales

```kotlin
class RecordatorioRepository {
    // Obtener todos los recordatorios
    fun obtenerRecordatorios(): Flow<List<Recordatorio>>
    
    // Crear nuevo recordatorio
    suspend fun crearRecordatorio(recordatorio: Recordatorio): Result<String>
    
    // Actualizar recordatorio
    suspend fun actualizarRecordatorio(recordatorio: Recordatorio)
    
    // Eliminar recordatorio
    suspend fun eliminarRecordatorio(id: String)
}

data class Recordatorio(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val fecha: Long,
    val activo: Boolean
)
```

### Funcionamiento

```
obtenerRecordatorios():
1. Conectar con Firebase
2. Obtener recordatorios del usuario
3. Retornar Flow que notifica cambios

crearRecordatorio():
1. Generar ID único
2. Guardar en Firestore
3. Programar notificación local
4. Retornar Result
```

---

## 7️⃣ MediaRepository

**Ubicación:** `data/media/MediaRepository.kt`

### Responsabilidad
Gestionar media (fotos, videos) del usuario.

### Métodos Principales

```kotlin
class MediaRepository {
    // Crear URI para imagen
    fun createImageUriForUser(context: Context, uid: String): Uri?
    
    // Guardar imagen
    suspend fun saveImageForUser(context: Context, uid: String, uri: Uri): Boolean
    
    // Obtener imagen del usuario
    fun getImageUriForUser(context: Context, uid: String): Uri?
    
    // Eliminar imagen
    fun deleteImageForUser(context: Context, uid: String): Boolean
}
```

### Funcionamiento

```
createImageUriForUser():
1. Crear ruta en almacenamiento interno
2. Generar nombre de archivo con UID
3. Crear URI válida para la imagen

saveImageForUser():
1. Validar que URI existe
2. Copiar archivo a almacenamiento interno
3. Guardar ruta en preferencias
4. Retornar Boolean de éxito

getImageUriForUser():
1. Recuperar URI de preferencias
2. Validar que archivo existe
3. Retornar URI o null
```

### Almacenamiento

```
/data/data/cl.duoc.milsabores/
├─ files/
│  └─ profile_photos/
│     ├─ usuario_1.jpg
│     ├─ usuario_2.jpg
│     └─ ...
└─ shared_prefs/
   └─ MilSaboresMediaPaths.xml
```

---

## 🔄 FLUJO COMPLETO: CREAR PEDIDO

```
Usuario → CarritoScreen
    ↓
Clic "Finalizar Pedido"
    ↓
CarritoViewModel.finalizarCompra()
    ├─ Validar carrito no vacío
    ├─ Convertir items a ProductoPedido
    └─ Crear objeto Pedido
        ↓
    PedidosLocalStorage.guardarPedido()
        ├─ Serializar a JSON
        ├─ Guardar en SharedPreferences
        └─ Retornar pedidoId
        ↓
    CarritoRepository.limpiarCarrito()
        └─ Vaciar carrito
        ↓
    Navegar a "Mis Pedidos"
        ↓
    PedidosLocalStorage.obtenerTodosPedidos()
        ├─ Leer JSON
        ├─ Deserializar
        └─ Mostrar en lista
```

---

## 🏗️ RELACIONES ENTRE REPOSITORIOS

```
CarritoViewModel
├─ CarritoRepository ─────→ items (StateFlow)
├─ PedidosLocalStorage ───→ guardarPedido()
└─ PedidosObserverService ─→ notificaciones

ProfileViewModel
├─ ProfilePhotoManager ───→ foto de perfil
├─ MediaRepository ───────→ almacenamiento media
└─ FirebaseAuth ──────────→ usuario actual

LoginViewModel
├─ AuthRepository ────────→ login/register
└─ FirebaseAuthDataSource → autenticación

PedidosViewModel
├─ PedidosRepository ─────→ pedidos servidor
├─ PedidosLocalStorage ───→ pedidos locales
└─ FirebaseAuth ──────────→ usuario actual
```

---

## 📊 MATRIZ DE PERSISTENCIA

| Repositorio | Persistencia | Ubicación | Acceso |
|-------------|-------------|-----------|--------|
| CarritoRepository | Memoria | RAM | Rápido |
| PedidosLocalStorage | Local | SharedPreferences | Muy Rápido |
| PedidosRepository | Servidor | Firestore | Necesita conexión |
| AuthRepository | Servidor | Firebase Auth | Necesita conexión |
| MediaRepository | Local | Almacenamiento interno | Rápido |
| RecordatorioRepository | Servidor | Firestore | Necesita conexión |

---

## 🔐 PATRONES DE SEGURIDAD

```
AuthRepository
├─ Valida credenciales con Firebase
├─ No almacena contraseñas
└─ Usa tokens de sesión seguros

PedidosRepository
├─ Solo accede a pedidos del usuario autenticado
├─ Firestore Rules valida acceso
└─ Datos encriptados en tránsito

PedidosLocalStorage
├─ Datos encriptados en dispositivo
├─ Acceso solo por la aplicación
└─ SharedPreferences privado

MediaRepository
├─ Almacenamiento interno (privado)
├─ No accesible por otras apps
└─ URI validadas antes de uso
```

---

## ✅ RESUMEN

| Repositorio | Tipo | Persistencia | Uso |
|-------------|------|-------------|-----|
| CarritoRepository | Local | Memoria | Carrito temporal |
| PedidosRepository | Servidor | Firestore | Pedidos permanentes |
| PedidosLocalStorage | Local | SharedPreferences | Pedidos offline |
| AuthRepository | Servidor | Firebase Auth | Autenticación |
| MediaRepository | Local | Almacenamiento | Fotos de usuario |
| RecordatorioRepository | Servidor | Firestore | Notificaciones |

**Arquitectura:** Clean Architecture con Repository Pattern
**Estado:** ✅ **COMPLETAMENTE IMPLEMENTADO**
**Versión:** 2.2.0
**Fecha:** Octubre 2024

