import { Component, ElementRef, Input, OnChanges, inject } from '@angular/core';
import {
  Apple,
  Baby,
  Brain,
  ChartNoAxesCombined,
  Check,
  ChevronRight,
  CircleHelp,
  createElement,
  HeartPulse,
  Heart,
  House,
  Eye,
  EyeOff,
  LogOut,
  Menu,
  MessageCircle,
  MoonStar,
  Plus,
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
  check: Check,
  chevronRight: ChevronRight,
  help: CircleHelp,
  heartPulse: HeartPulse,
  heart: Heart,
  home: House,
  eye: Eye,
  eyeOff: EyeOff,
  logout: LogOut,
  menu: Menu,
  message: MessageCircle,
  moon: MoonStar,
  plus: Plus,
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
