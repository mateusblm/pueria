import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import * as Sentry from '@sentry/angular';
import { ToastContainerComponent } from './core/toast/toast-container.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastContainerComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  // Temporary Sentry verification control; remove after the first frontend event is confirmed.
  async throwTestError(): Promise<void> {
    const error = new Error('Sentry Test Error');
    Sentry.captureException(error);
    await Sentry.flush(5000);
    throw error;
  }
}
