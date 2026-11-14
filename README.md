# Matxx Items (Fabric, Minecraft 1.21 / 1.21.1)

Con el inventario abierto, mantené **Shift izquierdo** y pasá el mouse por los ítems:
- **Tótem** de tu mochila/hotbar -> va a la **segunda mano**.
- **Cualquier otro ítem** -> se mueve como un shift+click (mochila <-> hotbar, o hacia/desde un cofre).

## Compilar (opción A: IntelliJ IDEA)
1. Instalá Java 21 y abrí esta carpeta con IntelliJ. Esperá a que Gradle sincronice.
2. Gradle > Tasks > build > build.
3. El mod es `build/libs/MatxxItems-1.0.1.jar`.
   **No uses** el que termina en `-dev.jar` ni `-sources.jar`: ésos no los lee Lunar.

## Compilar (opción B: sin instalar nada, con GitHub)
1. Subí esta carpeta a un repositorio nuevo de GitHub (con todo, incluida `.github`).
2. En la pestaña **Actions** esperá a que termine el build.
3. Entrá al build terminado y bajá el artefacto **MatxxItems** (adentro está el .jar listo).

## Instalar en Lunar Client
1. En el launcher elegí la versión **1.21.1**.
2. Tocá el engranaje al lado de "Launch" y abrí la carpeta de mods (o arrastrá el .jar al launcher).
3. Dejá `MatxxItems-1.0.1.jar` en esa carpeta y abrí el juego.

## Si Lunar no lo muestra
- Borrá cualquier `MatxxItems-...jar` viejo de la carpeta de mods (dos jars del mismo mod chocan).
- Cerrá Lunar del todo y abrilo de nuevo.
- Fijate que el jar sea el que NO termina en `-dev` ni `-sources`.
- Para probar que anda sin mirar ninguna lista: entrá a un mundo, abrí el inventario, mantené Shift izquierdo y pasá el mouse por un ítem.
