import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { BrandMarkComponent } from '../../shared/components/brand-mark/brand-mark.component';

@Component({
  selector: 'app-auth-layout',
  imports: [RouterOutlet, BrandMarkComponent],
  templateUrl: './auth-layout.component.html',
  styleUrl: './auth-layout.component.scss'
})
export class AuthLayoutComponent {}
