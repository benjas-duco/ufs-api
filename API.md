# UFS API - guía para el equipo

Todas las rutas empiezan con `/api`. Las respuestas son JSON.

## Autenticación y roles

El login lo hace **Supabase Auth** desde la página. Para las rutas privadas, enviar el token que entrega Supabase:

```
Authorization: Bearer <access_token>
```

La API verifica que el token sea auténtico (firmado por Supabase) y que no esté vencido, saca de él el id del usuario y busca su **rol en la tabla `users`**. El rol nunca viene del frontend.
La primera vez que un usuario llama a una ruta privada se crea solo su fila en `users` (rol `USER`, nickname = su correo antes de la `@`).

Para cambiar el rol de alguien: editar la columna `role` de su fila en la tabla `users` (panel de Supabase). Aplica desde su siguiente petición.

| Rol | Puede |
|---|---|
| `USER` | Comprar: carrito, compras propias |
| `SHIPPER` | Todo lo de `USER` + ver y despachar pedidos |
| `ADMIN` | Todo lo de `SHIPPER` + manejar figuras y quitar pedidos de la cola |

## Rutas

| Método | Ruta | Quién | Qué hace |
|---|---|---|---|
| GET | `/api/figures` | público | Lista figuras (sin las `DELETED`). Filtros opcionales: `?brand=FIGMA&state=PRE_SALE&q=texto` |
| GET | `/api/figures/{id}` | público | Detalle de una figura |
| GET | `/api/tags` | público | Todos los tags que existen (lista de textos) |
| POST | `/api/figures` | ADMIN | Crea una figura |
| PUT | `/api/figures/{id}` | ADMIN | Edita una figura (se envía completa) |
| DELETE | `/api/figures/{id}` | ADMIN | Quita una figura (la pasa a `DELETED`) |
| GET | `/api/me` | logueado | Datos del usuario (`id`, `nickname`, `role`) |
| PUT | `/api/me` | logueado | Cambia el nickname `{ "nickname": "..." }` |
| GET | `/api/cart` | logueado | Carrito con subtotales y total |
| POST | `/api/cart/items` | logueado | Agrega al carrito `{ "figureId": 1, "units": 2 }` (suma si ya estaba) |
| PUT | `/api/cart/items/{figureId}` | logueado | Fija la cantidad `{ "units": 3 }` |
| DELETE | `/api/cart/items/{figureId}` | logueado | Saca una figura del carrito |
| DELETE | `/api/cart` | logueado | Vacía el carrito |
| POST | `/api/purchases` | logueado | **Compra todo el carrito** (el body son los datos de envío) |
| GET | `/api/purchases` | logueado | Mis compras |
| GET | `/api/purchases/{id}` | logueado | Una de mis compras |
| GET | `/api/admin/purchases` | SHIPPER | Todas las compras. `?pending=true` solo las que tienen ítems por enviar, `?pending=false` solo las completas |
| PATCH | `/api/admin/purchases/{purchaseId}/items/{figureId}/sent` | SHIPPER | Marca un ítem como enviado |
| PATCH | `/api/admin/purchases/{purchaseId}/tracking` | SHIPPER | Guarda el código de seguimiento `{ "trackingCode": "..." }` |
| DELETE | `/api/admin/purchases/{purchaseId}` | ADMIN | Quita una compra de la cola (solo si no tiene ítems enviados) |

Cada rol incluye lo de los anteriores: un ADMIN también puede lo de SHIPPER y USER.

## Ejemplos de body

Figura (POST / PUT):

```json
{
  "name": "Hatsune Miku",
  "brand": "POPUP_PARADE",
  "price": 29990,
  "state": "AVAILABLE",
  "tags": ["vocaloid", "Miku"],
  "units": 10,
  "imageUrl": "https://...",
  "description": "Texto largo"
}
```

Los tags se guardan en minúsculas. Los que todavía no existen se crean solos.

Comprar (POST /api/purchases):

```json
{
  "recipientName": "Nombre Apellido",
  "phone": "+56912345678",
  "region": "Región Metropolitana",
  "comuna": "Santiago",
  "street": "Av. Ejemplo",
  "streetNumber": "123",
  "apartment": "Depto 45",
  "reference": "Portón negro"
}
```

`apartment` y `reference` son opcionales.

## Valores permitidos

- `brand`: `POPUP_PARADE`, `NENDROID`, `FIGMA`, `SCALE_FIGURE`
- `state`: `AVAILABLE`, `UNAVAILABLE`, `ON_PRODUCTION`, `PRE_SALE`, `ON_SALE`, `DELETED`
- `role`: `USER`, `SHIPPER`, `ADMIN`

## Reglas

- Se puede agregar al carrito y comprar una figura en `AVAILABLE`, `ON_SALE` o `PRE_SALE`. `UNAVAILABLE`, `ON_PRODUCTION` y `DELETED` no.
- En `AVAILABLE` y `ON_SALE` la cantidad no puede superar el stock (`units`). En `PRE_SALE` no se revisa ni se descuenta stock.
- Al comprar se descuenta el stock; si llega a 0, la figura pasa a `UNAVAILABLE`. El carrito queda vacío.
- El total y los precios los calcula siempre el servidor. Cada ítem de la compra guarda el precio del momento (`unitPrice`).
- Cada ítem de una compra tiene su propio `sent`. Una compra está `completed` cuando todos sus ítems están enviados.
- Un ítem de una figura en `PRE_SALE` no se puede marcar como enviado hasta que la figura salga de preventa.
- Quitar una figura no borra la fila: la pasa a `DELETED`, desaparece del listado público y de los carritos. Un ADMIN puede verlas con `?state=DELETED`.
- Quitar una compra de la cola no devuelve el stock; si hace falta, se corrige editando la figura.

## Errores

Formato: `{ "status": 409, "message": "Solo quedan 2 unidades de 'Hatsune Miku'" }`

- 400: datos inválidos o carrito vacío
- 401: falta el token o es inválido
- 403: no tienes el rol necesario
- 404: no existe
- 409: no se puede por stock o estado

## Variables de entorno (Render)

Ya existentes: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. Nuevas:

- `SUPABASE_JWKS_URL`: `https://<tu-proyecto>.supabase.co/auth/v1/.well-known/jwks.json` (proyectos con claves asimétricas), **o**
- `SUPABASE_JWT_SECRET`: el JWT Secret del proyecto (proyectos con la clave legacy compartida)
- `CORS_ORIGINS` (opcional): dominios de la página separados por coma. Si no se define, se permite cualquiera.
