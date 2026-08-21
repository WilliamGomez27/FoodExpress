import cors from 'cors';
import express from 'express';
import helmet from 'helmet';
import { env } from './config/env.js';
import { checkDatabaseConnection } from './config/database.js';
import { createToken, requireAuth } from './middleware/auth.js';

const app = express();

app.use(helmet());
app.use(cors());
app.use(express.json());

app.get('/api/health', async (_request, response) => {
  try {
    await checkDatabaseConnection();
    return response.json({ status: 'ok', database: 'connected' });
  } catch (error) {
    return response.status(503).json({
      status: 'degraded',
      database: 'unavailable',
      message: error.message
    });
  }
});

app.post('/api/auth/dev-token', (_request, response) => {
  const token = createToken({ sub: 'dev-user', role: 'admin' });
  return response.json({ token });
});

app.get('/api/protected', requireAuth, (request, response) => {
  return response.json({ message: 'Acceso autorizado', user: request.user });
});

app.use((_request, response) => {
  response.status(404).json({ message: 'Ruta no encontrada' });
});

app.listen(env.port, () => {
  console.log(`FastFood web API escuchando en http://localhost:${env.port}`);
});