import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import * as Sentry from '@sentry/angular';

const sentryDsn = window.__PUERIA_CONFIG__?.sentryDsn?.trim();
if (sentryDsn) {
  Sentry.init({
    dsn: sentryDsn,
    environment: 'production',
    tracesSampleRate: 0.1,
    sendDefaultPii: false
  });
}

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
