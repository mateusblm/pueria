import { Component, HostListener, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PrivacidadeDialogService } from '../../../core/privacidade/privacidade-dialog.service';

@Component({
  selector: 'app-privacidade-modal',
  imports: [RouterLink],
  templateUrl: './privacidade-modal.component.html',
  styleUrl: './privacidade-modal.component.scss'
})
export class PrivacidadeModalComponent {
  readonly dialog = inject(PrivacidadeDialogService);

  @HostListener('document:keydown.escape')
  fecharComEscape(): void {
    if (this.dialog.aberta()) this.dialog.fechar();
  }
}
