# ✅ GUÍA COMPLETA PARA RESOLVER EL TIMEOUT EN PEDIDOS

## Problema
El botón "Finalizar Pedido" se queda en "Procesando..." 30 segundos y luego muestra:
```
"Tiempo agotado. Verifica tu conexión e intenta de nuevo"
```

---

## Soluciones Implementadas en el Código

### 1. ✅ Logging Detallado Agregado
Se agregó logging en todos los pasos para identificar exactamente dónde se queda:
- En `CarritoViewModel.finalizarCompra()`
- En `PedidosRepository.crearPedido()`

### 2. ✅ Mejor Manejo de Errores
El código ahora diferencia entre:
- **Timeout:** "Tiempo agotado..."
- **Error Firebase:** Mensaje específico del error
- **Error Autenticación:** "Usuario no autenticado"

### 3. ✅ Logging para Debugging
```
D CarritoVM: finalizarCompra() llamado
D CarritoVM: Items en carrito: 2
D CarritoVM: Pedido creado localmente. Total: 15000
D CarritoVM: Llamando crearPedido()...
D PedidosRepository: UID del usuario: abc123...
D PedidosRepository: Preparando envío a Firebase...
D PedidosRepository: ✅ Pedido creado exitosamente
```

---

## Pasos para Diagnosticar el Problema

### PASO 1: Verificar Logs
```
1. Abre Android Studio
2. Ve a: View → Tool Windows → Logcat
3. Filtra por: "CarritoVM" o "PedidosRepository"
4. Intenta crear un pedido
5. Observa los logs:
   - Si ves "UID del usuario: null" → Problema de autenticación
   - Si ves "Preparando envío a Firebase..." pero no "✅ Pedido creado" → Problema de Firebase
   - Si no ves logs de Firebase → Problema de timeout
```

### PASO 2: Verificar Firebase

#### A. ¿Firestore habilitado?
```
1. Ve a Firebase Console (https://console.firebase.google.com)
2. Selecciona tu proyecto
3. Ve a Firestore Database
4. Verifica que diga "Cloud Firestore" (no deshabilitado)
```

#### B. ¿Reglas de seguridad correctas?
```
1. En Firestore → Rules
2. Verifica que permita escribir a usuarios autenticados:
   match /pedidos/{document=**} {
     allow read, write: if request.auth != null;
   }
3. Si cambias algo, publica: Publish
```

#### C. ¿Colección creada?
```
1. En Firestore → Collections
2. Verifica que existe la colección "pedidos"
3. Si no existe, se creará automáticamente al hacer el primer pedido
```

### PASO 3: Verificar Autenticación

#### ¿El usuario está autenticado?
```kotlin
// En Logcat, busca:
"UID del usuario: abc123xyz..." → ✅ Autenticado
"UID del usuario: null" → ❌ NO autenticado

// Si no está autenticado:
1. Asegúrate de haber iniciado sesión en la app
2. En Login, verifica que Firebase reciba las credenciales
3. Prueba con una nueva cuenta
```

### PASO 4: Verificar Conexión de Red

#### ¿Hay conexión a internet?
```
1. Abre Chrome en el dispositivo de prueba
2. Intenta acceder a google.com
3. Si carga → Conexión OK
4. Si no carga → Problema de red
```

#### ¿Firebase es alcanzable?
```
1. La app debe conectar a: firestore.googleapis.com
2. Verifica que no haya firewall bloqueando
3. En redes corporativas, podría estar bloqueado
```

---

## Soluciones Rápidas Según el Diagnóstico

### Si el UID es NULL
```
Problema: Usuario no autenticado

Solución:
1. Asegúrate de estar en la pantalla de login
2. Inicia sesión con Google o email
3. Verifica que el login sea exitoso
4. Intenta crear pedido de nuevo
```

### Si dice "Preparando envío a Firebase..." pero se queda
```
Problema: Firestore rechaza la escritura

Solución:
1. Ve a Firebase Console
2. Ve a Firestore → Rules
3. Cambia a permitir escritura:
   allow read, write: if request.auth != null;
4. Publica los cambios
5. Intenta de nuevo
```

### Si no hay conexión
```
Problema: Red lenta o sin conexión

Solución:
1. Cambia a WiFi de mejor señal
2. Reinicia el router
3. Apaga datos móviles y usa WiFi
4. Prueba en otra red
```

---

## Prueba Final

Una vez hayas verificado todo, sigue estos pasos:

```
1. Limpia la app: Ajustes → Apps → Mil Sabores → Almacenamiento → Limpiar
2. Reinicia la app
3. Inicia sesión de nuevo
4. Agrega productos al carrito
5. Haz clic en "Finalizar Pedido"
6. Observa los logs en Android Studio
7. Si todo está bien:
   - Debe completar en < 5 segundos
   - Debe navegar a "Mis Pedidos"
   - El pedido debe aparecer en Firestore
```

---

## Si Aún No Funciona

Si después de todo esto sigue dando timeout:

1. **Aumenta el timeout temporalmente** (de 30 a 60 segundos):
   - En `CarritoViewModel.kt`, línea ~135
   - Cambia: `withTimeoutOrNull(30000L)` → `withTimeoutOrNull(60000L)`

2. **Revisa los logs de Firebase**:
   - Firebase Console → Logs
   - Busca errores en la colección "pedidos"

3. **Considera estos problemas**:
   - Red muy lenta (fibra óptica de mala calidad)
   - Firestore está sobrecargado
   - Problema específico de tu dispositivo

---

## Archivo de Logs Útiles

Después de ejecutar el código actualizado, los logs te dirán:

```
✅ Si funciona:
D CarritoVM: ✅ Pedido creado exitosamente: abc123

❌ Si falla:
E CarritoVM: ❌ Error al crear pedido: Permission denied
E CarritoVM: ❌ TIMEOUT: No se recibió respuesta en 30 segundos
E CarritoVM: ❌ Excepción: Usuario no autenticado
```

---

## Resumen

| Síntoma | Causa | Solución |
|---------|-------|----------|
| "Tiempo agotado" siempre | Firestore rechaza escritura | Verificar reglas de seguridad |
| "Tiempo agotado" con conexión lenta | Red lenta | Usar mejor conexión |
| "Usuario no autenticado" | No hay sesión activa | Iniciar sesión primero |
| Funciona a veces | Conexión intermitente | Usar red más estable |

---

**Código actualizado:** ✅ Sí
**Logging agregado:** ✅ Sí
**Manejo de errores:** ✅ Mejorado
**Listo para debugging:** ✅ Sí

**Próximo paso:** Ejecutar con Android Studio abierto en Logcat y reportar los mensajes que ves.

