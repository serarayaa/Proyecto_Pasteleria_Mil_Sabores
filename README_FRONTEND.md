# Aplicación Android "Pastelería Mil Sabores"

## Resumen del proyecto

*Pastelería Mil Sabores* es una aplicación móvil para Android escrita en **Kotlin** que permite a los clientes navegar por el catálogo de productos, gestionar un carrito de compras y realizar pedidos en línea. La interfaz se desarrolla con **Jetpack Compose** e implementa el patrón **MVVM** para separar la lógica de negocio de la interfaz gráfica. El proyecto consume servicios REST para autenticación y operaciones de productos, y utiliza **Firebase** para la autenticación y sincronización【204530804535123†L4-L7】.  

La app está preparada para funcionar con los microservicios de autenticación y productos desplegados por separado. Para la autenticación, se apoya en tokens JWT generados por el backend. Los pedidos se almacenan localmente en SQLite mediante **Room** y se sincronizan con el servidor cuando hay conectividad.

## Arquitectura técnica

La aplicación sigue una arquitectura en capas inspirada en **Clean Architecture**. Las capas principales son【88169254099683†L20-L83】:

- **UI (Presentación)**: Interfaz declarativa con Jetpack Compose. Cada pantalla se encapsula en un componente Compose, y la navegación se gestiona a través de `AppNavHost`. La actividad principal (`MainActivity`) inicializa Firebase, crea el canal de notificaciones y establece el contenido de Compose【737762684225106†L32-L85】.
- **Dominio / ViewModels**: Contiene las reglas de negocio y los `ViewModel` que exponen estados inmutables a la interfaz. Los ViewModels se comunican con los repositorios para acceder a los datos y gestionan las operaciones asíncronas.
- **Repositorios**: Abstracciones que intermedian entre las capas de dominio y los orígenes de datos. Implementan la lógica para decidir si un dato debe leerse de la red o de la base local.
- **Fuentes de datos (Data Sources)**: Componentes que obtienen datos ya sea de la red (servicios REST mediante **Retrofit**) o de la base local (**Room**). Cada llamada de red se configura con interceptores para añadir el token JWT a las peticiones.  

La siguiente figura muestra una vista general simplificada de la integración entre la aplicación y los microservicios de backend. El dibujo está inspirado en el diagrama generado para este proyecto. El dispositivo Android se comunica con dos servicios: `auth-service` para autenticación (registro y login) y `product-service` para catalogar productos; ambos servicios consultan una base de datos Oracle.

![Diagrama de arquitectura]({{file:file-3qhk9LqmNoBhxhRxTqN5AM}})

## Estructura del código

El proyecto está organizado en directorios que siguen la arquitectura MVVM. A continuación se presenta una tabla con los paquetes principales y su responsabilidad, basada en la documentación de archivos【819904675997940†L22-L63】:

| Directorio/Archivo | Descripción breve |
|-------------------|------------------|
| `app/src/main/java/com/milsabores` | Directorio raíz del código fuente de la app. |
| `ui/` | Contiene las pantallas Compose; cada pantalla es un `Composable` con estado asociado. |
| `ui/nav/` | Define las rutas de navegación y el `NavHost`. |
| `viewmodel/` | ViewModels que encapsulan la lógica de negocio y exponen estados a la UI. |
| `data/local/` | Implementación de la base de datos Room y DAOs. |
| `data/remote/` | Servicios Retrofit para consumir los microservicios (auth y product). |
| `repository/` | Repositorios que coordinan datos entre fuentes locales y remotas. |
| `model/` | Clases de datos compartidas (DTOs, entidades). |
| `util/` | Clases de soporte (por ejemplo, `Constants`, manejadores de errores, validaciones). |

## Tecnologías y dependencias principales

| Herramienta/Librería | Uso |
|----------------------|-----|
| **Kotlin** | Lenguaje principal de desarrollo. |
| **Jetpack Compose** | Framework declarativo de UI para construir las pantallas. |
| **Room** | Persistencia local utilizando SQLite. |
| **Retrofit y OkHttp** | Cliente HTTP para consumir los microservicios con interceptores JWT. |
| **Firebase Authentication** | Registro y login de usuarios; se complementa con el backend para generar tokens JWT. |
| **Hilt/Dagger** | Inyección de dependencias (si se integra en futuras versiones). |
| **JUnit / Espresso** | Pruebas unitarias y de interfaz (recomendado; actualmente no implementadas). |

## Cómo ejecutar en local

Para ejecutar la aplicación en un entorno de desarrollo local se recomienda usar **Android Studio** o el wrapper de Gradle. Siga estos pasos:

1. **Clonar el repositorio**:

   ```bash
   git clone https://github.com/serarayaa/Proyecto_Pasteleria_Mil_Sabores.git
   cd Proyecto_Pasteleria_Mil_Sabores
   ```

2. **Configuración de entorno**: asegúrese de tener **JDK 17** y **Android Studio Flamingo** (o superior) instalados. Configure el archivo `local.properties` con la ruta al SDK de Android.

3. **Construir y ejecutar**: desde Android Studio, abra el proyecto y ejecute la aplicación en un emulador o dispositivo físico. También puede compilar desde la línea de comandos【204530804535123†L31-L60】:

   ```bash
   ./gradlew clean assembleDebug
   # Instalar en un dispositivo conectado
   ./gradlew installDebug
   ```

4. **Variables de configuración**: modifique la constante `BASE_URL` en el servicio Retrofit (`data/remote`) para apuntar a la dirección IP o dominio donde se ejecuta el backend. Para pruebas locales, utilice `10.0.2.2` (Android Emulator) o la IP de su computadora.

## Cómo ejecutar en AWS EC2

La aplicación Android no se ejecuta directamente en EC2, pero puede automatizar la construcción y distribución de la APK y hospedar los servicios de backend en la nube. Para desplegar los microservicios Spring Boot en una instancia EC2 y conectarlos a la app:

1. **Crear una instancia EC2** (Amazon Linux 2 o Ubuntu) con un grupo de seguridad que permita tráfico en los puertos 8085 y 8087.
2. **Instalar Java** en la instancia (`sudo yum install java-17-openjdk` en Amazon Linux).  
3. **Copiar los JARs** de `auth-service` y `product-service` compilados con Maven al servidor (por ejemplo, usando `scp`).  
4. **Ejecutar los servicios**: use `nohup java -jar auth-service.jar &` y `nohup java -jar product-service.jar &`.   
5. **Actualizar la app**: configure `BASE_URL` en Retrofit con la IP pública o dominio de la instancia y pruebe la conexión.   
6. **Firmar y distribuir la APK**: genere una versión de producción con `./gradlew assembleRelease`, firme el APK con una llave de firma y súbalo a un almacenamiento como Amazon S3 o distribúyalo directamente.

Los pasos 1–4 se basan en la guía general de despliegue de Spring Boot en EC2【143898066960138†L63-L77】.

## Flujo de autenticación

La autenticación se implementa con Firebase y JWT:

1. **Registro**: la app envía las credenciales de usuario (nombre, email, contraseña y RUT) al endpoint `/auth/register` del backend. El backend crea un usuario con contraseña cifrada y devuelve un token JWT【792214114687694†L30-L97】.  
2. **Login**: el usuario inicia sesión con `/auth/login` enviando email y contraseña. El backend valida y genera un token JWT para consumir los servicios protegidos【792214114687694†L98-L123】.  
3. **Uso del token**: el token JWT se guarda de manera segura (por ejemplo, en `DataStore`) y se incluye en el encabezado `Authorization: Bearer <token>` de cada petición a `product-service`.  
4. **Autorización**: en caso de expirar el token (24 horas por defecto【485311177202626†L41-L77】), la app debe solicitar un nuevo login.

## Instalación y distribución (APK)

Para generar y distribuir la APK:

1. **Generar versión de lanzamiento**:

   ```bash
   ./gradlew clean assembleRelease
   ```

2. **Firmar la APK**: cree un *keystore* si aún no existe y configure los parámetros de firma en `app/build.gradle`. Una opción rápida es utilizar la herramienta de firma de Android Studio.

3. **Probar la APK**: instale la versión `release` en un dispositivo real para verificar que las dependencias se incluyan correctamente y que la comunicación con el backend funcione.

4. **Distribuir**: puede publicar la APK en Google Play, en un portal interno o almacenarla en un servicio como **Amazon S3** para descarga directa. Incluya instrucciones para permitir la instalación desde orígenes desconocidos en dispositivos Android.

## Conexión Backend ↔ Android

Para que la aplicación móvil consuma los microservicios:

1. **Configurar `BASE_URL`**: ajuste la URL base de Retrofit en `data/remote` para apuntar al dominio o IP pública del backend (por ejemplo, `http://ec2-xx-xx-xx-xx.compute-1.amazonaws.com:8087/`). Para pruebas locales, use `http://10.0.2.2:8087/`.
2. **Enviar el token JWT**: incluya un interceptor de OkHttp que agregue `Authorization: Bearer <token>` a cada petición. Si el token está expirado, gestione la renovación mediante un flujo de login.
3. **Manejar errores de red**: implemente manejo de excepciones para mostrar mensajes apropiados al usuario cuando el servidor no esté disponible o la autenticación sea inválida.

## Puntos débiles detectados y mejoras recomendadas

- **Pruebas automáticas**: actualmente no hay pruebas unitarias ni de UI. Se recomienda añadir pruebas de ViewModel, repositorios y pantallas para mejorar la confiabilidad.  
- **CI/CD**: integrar un flujo de integración continua (GitHub Actions) para compilar el proyecto, ejecutar pruebas y publicar artefactos.  
- **Badges y documentación**: añadir insignias de estado de compilación, cobertura de código y licencia en la cabecera del README.  
- **Internacionalización**: centralizar los textos en archivos de recursos para soportar varios idiomas.  
- **Accesibilidad**: seguir las guías de Material Design para tamaños de fuente y contraste.  
- **Diagrama de navegación**: generar un diagrama visual de rutas para facilitar la comprensión.

## Notas finales y créditos

Este proyecto fue desarrollado como parte del curso de desarrollo móvil y arquitecturas de microservicios. Los autores agradecen a los tutores y compañeros que apoyaron en su construcción.  

**Colaboradores**: @serarayaa y equipo Mil Sabores.  
**Licencia**: MIT (especificar en el repositorio).  
**Contacto**: Para sugerencias o contribuciones, abrir un *issue* o realizar un *pull request*.
