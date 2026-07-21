import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import * as Sentry from '@sentry/angular';

const sentryDsn = window.__PUERIA_CONFIG__?.sentryDsn?.trim();
const sentrySmokeTest = new URLSearchParams(window.location.search).get('sentryTest') === 'true';

if (sentryDsn) {
  Sentry.init({
    dsn: sentryDsn,
    environment: 'production',
    tracesSampleRate: 0.1,
    sendDefaultPii: false,
  });
}

bootstrapApplication(App, appConfig)
  .then(async () => {
    // Temporary test: remove after validating the first frontend event.
    if (sentryDsn && sentrySmokeTest) {
      Sentry.captureException(new Error('Sentry frontend smoke test: excecao intencional'));
      await Sentry.flush(5000);
    }
  })
  .catch((err) => console.error(err));
