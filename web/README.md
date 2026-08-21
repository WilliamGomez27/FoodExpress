# FastFood Web API

Backend Node.js para la futura interfaz web de FastFoodApp.

## Inicio

```powershell
cd web
Copy-Item .env.example .env
npm install
npm run dev
```

Configura `web/.env` con el secreto JWT y las credenciales de MySQL antes de usar la API.

## Rutas iniciales

- `GET /api/health`: comprueba el estado de la API y la conexión a MySQL.
- `POST /api/auth/dev-token`: genera un token temporal para desarrollo.
- `GET /api/protected`: ejemplo de ruta protegida con `Authorization: Bearer <token>`.