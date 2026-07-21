import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastContainerComponent } from './core/toast/toast-container.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastContainerComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  // Temporary Sentry verification control; remove after the first frontend event is confirmed.
  throwTestError(): void {
    throw new Error('Sentry Test Error');
  }
}
