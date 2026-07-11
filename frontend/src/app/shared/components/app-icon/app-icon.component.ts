import { Component, ElementRef, Input, OnChanges, inject } from '@angular/core';
import {
  Apple,
  Baby,
  Brain,
  ChartNoAxesCombined,
  ChevronRight,
  createElement,
  HeartPulse,
  Heart,
  LogOut,
  Menu,
  MessageCircle,
  MoonStar,
  Ruler,
  Salad,
  Smartphone,
  Sparkles,
  Stethoscope,
  Toilet,
  Footprints,
  UserRound,
  UsersRound,
  X,
  type IconNode
} from 'lucide';

const ICONS = {
  apple: Apple,
  baby: Baby,
  brain: Brain,
  chart: ChartNoAxesCombined,
  chevronRight: ChevronRight,
  heartPulse: HeartPulse,
  heart: Heart,
  logout: LogOut,
  menu: Menu,
  message: MessageCircle,
  moon: MoonStar,
  ruler: Ruler,
  salad: Salad,
  smartphone: Smartphone,
  sparkles: Sparkles,
  stethoscope: Stethoscope,
  toilet: Toilet,
  footprints: Footprints,
  user: UserRound,
  users: UsersRound,
  close: X
} satisfies Record<string, IconNode>;

export type AppIconName = keyof typeof ICONS;

@Component({
  selector: 'app-icon',
  template: '',
  styles: [':host { display: inline-flex; flex: 0 0 auto; line-height: 0; }'],
  host: {
    'aria-hidden': 'true'
  }
})
export class AppIconComponent implements OnChanges {
  private readonly element = inject<ElementRef<HTMLElement>>(ElementRef);

  @Input({ required: true }) name!: AppIconName;
  @Input() size = 20;
  @Input() strokeWidth = 2;

  ngOnChanges(): void {
    const host = this.element.nativeElement;
    host.replaceChildren(createElement(ICONS[this.name], {
      width: this.size,
      height: this.size,
      'stroke-width': this.strokeWidth,
      'aria-hidden': 'true'
    }));
  }
}
