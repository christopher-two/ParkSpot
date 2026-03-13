# ParkSpot

ParkSpot es una app Kotlin Multiplatform para guardar la ubicacion de tu auto estacionado, consultar historial de lugares guardados y ver detalles de cada registro.

## Que hace el proyecto

- Onboarding inicial para configurar el flujo de uso.
- Seleccion y guardado de ubicaciones de estacionamiento.
- Historial de lugares guardados con pantalla de detalle.
- Mapa en Android con Mapbox.
- Soporte iOS para la app compartida (el mapa nativo en iOS aun no esta implementado).

## Stack tecnico

- Kotlin Multiplatform + Compose Multiplatform.
- Inyeccion de dependencias con Koin.
- Persistencia local con Room y DataStore.
- Networking con Ktor.
- Mapa en Android con Mapbox (`maps-compose`).

## Estructura del workspace

- `composeApp/`: logica compartida y features (`data`, `domain`, `presentation`).
- `androidapp/`: app Android (entry point y configuracion Android).
- `iosApp/`: app iOS (entry point en Xcode).

## Arquitectura (alto nivel)

El proyecto esta organizado por feature y capas:

- `feature/*/data`: repositorios e implementaciones de fuentes de datos.
- `feature/*/domain`: modelos y casos de uso.
- `feature/*/presentation`: `ViewModel` y UI en Compose.
- Modulos DI centralizados en `composeApp/src/commonMain/kotlin/org/christophertwo/car/di/FeaturesModules.kt`.

## Configuracion de Mapbox (Android)

No coloques el token en `strings.xml`.
El token se inyecta en build time desde `androidapp/build.gradle.kts` como `mapbox_access_token`.

Opciones de configuracion:

```ini
# local.properties (recomendado para desarrollo local)
MAPBOX_ACCESS_TOKEN=pk.your_token_here
```

O por CLI/CI:

```bash
./gradlew :androidapp:assembleDebug -PMAPBOX_ACCESS_TOKEN=pk.your_token_here
```

Tambien se mantiene compatibilidad con `MAPBOX_SECRET_TOKEN` como fallback.

## Ejecutar en Android

```bash
./gradlew :androidapp:assembleDebug
```

## Ejecutar en iOS

Abre `iosApp/` en Xcode y ejecuta el target `iosApp`.

## Notas

- `local.properties` no debe versionarse.
- Si un token fue expuesto previamente, debes rotarlo en Mapbox.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…