import { mkdirSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';

const apiUrl = process.env.PUERIA_API_URL?.trim() ?? '';
const sentryDsn = process.env.SENTRY_DSN?.trim() ?? '';
const destino = resolve('public/runtime-config.js');

mkdirSync(dirname(destino), { recursive: true });
writeFileSync(
  destino,
  `window.__PUERIA_CONFIG__ = ${JSON.stringify({ apiUrl, sentryDsn })};\n`,
  'utf8'
);
