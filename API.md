# UFS API - guía para el equipo

Todas las rutas empiezan con `/api`. Las respuestas son JSON.

## Autenticación

El login lo hace **Supabase Auth** desde la página. Para las rutas privadas, enviar el token que entrega Supabase:

```
Authorization: Bearer <access_token>
```

La API saca el id del usuario del token. La primera vez que un usuario llama a una ruta privada, se crea solo su fila en la tabla `users` (con su correo antes de la `@` como nickname).
Para hacer admin a alguien: poner `admin = true` en su fila de la tabla `users` (desde el panel de Supabase).

## Rutas

| Método | Ruta | Quién | Qué hace |
|---|---|---|---|
| GET | `/api/figures` | público | Lista figuras. Filtros opcionales: `?brand=FIGMA&state=PRE_SALE&q=texto` |
| GET | `/api/figures/{id}` | público | Detalle de una figura |
| POST | `/api/figures` | admin | Crea una figura |
| PUT | `/api/figures/{id}` | admin | Edita una figura (se envía completa) |
| GET | `/api/me` | logueado | Datos del usuario (`id`, `nickname`, `admin`) |
| PUT | `/api/me` | logueado | Cambia el nickname `{ "nickname": "..." }` |
| GET | `/api/cart` | logueado | Carrito con subtotales y total |
| POST | `/api/cart/items` | logueado | Agrega al carrito `{ "figureId": 1, "units": 2 }` (suma si ya estaba) |
| PUT | `/api/cart/items/{figureId}` | logueado | Fija la cantidad `{ "units": 3 }` |
| DELETE | `/api/cart/items/{figureId}` | logueado | Saca una figura del carrito |
| DELETE | `/api/cart` | logueado | Vacía el carrito |
| POST | `/api/purchases` | logueado | **Compra todo el carrito** (el body son los datos de envío) |
| GET | `/api/purchases` | logueado | Mis compras |
| GET | `/api/purchases/{id}` | logueado | Una de mis compras |
| GET | `/api/admin/purchases` | admin | Todas las compras. `?pending=true` solo las que tienen ítems por enviar, `?pending=false` solo las completas |
| PATCH | `/api/admin/purchases/{purchaseId}/items/{figureId}/sent` | admin | Marca un ítem como enviado |
| PATCH | `/api/admin/purchases/{purchaseId}/tracking` | admin | Guarda el código de seguimiento `{ "trackingCode": "..." }` |

## Ejemplos de body

Figura (POST / PUT):

```json
{
  "name": "Hatsune Miku",
  "brand": "POPUP_PARADE",
  "price": 29990,
  "state": "AVAILABLE",
  "tags": ["vocaloid", "miku"],
  "units": 10,
  "imageUrl": "https://...",
  "description": "Texto largo"
}
```

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
- `state`: `AVAILABLE`, `UNAVAILABLE`, `ON_PRODUCTION`, `PRE_SALE`, `ON_SALE`

## Reglas

- Se puede agregar al carrito y comprar una figura en `AVAILABLE`, `ON_SALE` o `PRE_SALE`. `UNAVAILABLE` y `ON_PRODUCTION` no.
- En `AVAILABLE` y `ON_SALE` la cantidad no puede superar el stock (`units`). En `PRE_SALE` no se revisa ni se descuenta stock.
- Al comprar se descuenta el stock; si llega a 0, la figura pasa a `UNAVAILABLE`. El carrito queda vacío.
- El total y los precios los calcula siempre el servidor. Cada ítem de la compra guarda el precio del momento (`unitPrice`).
- Cada ítem de una compra tiene su propio `sent`. Una compra está `completed` cuando todos sus ítems están enviados.
- Un ítem de una figura en `PRE_SALE` no se puede marcar como enviado hasta que la figura salga de preventa.
- No hay ruta para borrar figuras: si una figura ya no se vende, pasarla a `UNAVAILABLE`.

## Errores

Formato: `{ "status": 409, "message": "Solo quedan 2 unidades de 'Hatsune Miku'" }`

- 400: datos inválidos o carrito vacío
- 401: falta el token o es inválido
- 403: la ruta es solo para admin
- 404: no existe
- 409: no se puede por stock o estado de la figura

## Variables de entorno (Render)

Ya existentes: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. Nuevas:

- `SUPABASE_JWKS_URL`: `https://<tu-proyecto>.supabase.co/auth/v1/.well-known/jwks.json` (proyectos con claves asimétricas), **o**
- `SUPABASE_JWT_SECRET`: el JWT Secret del proyecto (proyectos con la clave legacy compartida)
- `CORS_ORIGINS` (opcional): dominios de la página separados por coma. Si no se define, se permite cualquiera.
