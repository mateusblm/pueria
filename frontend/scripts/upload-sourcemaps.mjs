import { readdir, unlink } from 'node:fs/promises';
import { join } from 'node:path';
import { spawnSync } from 'node:child_process';

const outputDir = join(process.cwd(), 'dist', 'frontend', 'browser');
const cli = process.platform === 'win32' ? 'sentry-cli.cmd' : 'sentry-cli';
const { SENTRY_AUTH_TOKEN: token, SENTRY_ORG: org, SENTRY_PROJECT: project } = process.env;
const release = process.env.SENTRY_RELEASE || process.env.VERCEL_GIT_COMMIT_SHA;

async function findSourceMaps(directory) {
  const entries = await readdir(directory, { withFileTypes: true });
  const files = [];
  for (const entry of entries) {
    const path = join(directory, entry.name);
    if (entry.isDirectory()) files.push(...await findSourceMaps(path));
    else if (entry.name.endsWith('.map')) files.push(path);
  }
  return files;
}

async function removeSourceMaps() {
  for (const file of await findSourceMaps(outputDir)) await unlink(file);
}

if (!token || !org || !project) {
  console.warn('Sentry source maps: upload ignorado; configure SENTRY_AUTH_TOKEN, SENTRY_ORG e SENTRY_PROJECT.');
  await removeSourceMaps();
  process.exit(0);
}

const args = ['sourcemaps', 'upload', '--org', org, '--project', project, '--rewrite'];
if (release) args.push('--release', release);
args.push(outputDir);

const result = spawnSync(cli, args, { stdio: 'inherit', env: process.env });
if (result.error) {
  console.error(`Sentry source maps: falha ao executar ${cli}: ${result.error.message}`);
  process.exit(1);
}
if (result.status !== 0) process.exit(result.status ?? 1);

await removeSourceMaps();
console.log('Sentry source maps: upload concluído e mapas removidos do artefato público.');
