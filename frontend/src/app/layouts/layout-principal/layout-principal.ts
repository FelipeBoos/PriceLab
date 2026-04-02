import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from '../../shared/components/sidebar2/sidebar/sidebar';
import { Topbar } from '../../shared/components/topbar/topbar';

@Component({
  selector: 'app-layout-principal',
  imports: [ RouterOutlet, Sidebar, Topbar ],
  templateUrl: './layout-principal.html',
  styleUrl: './layout-principal.css',
})
export class LayoutPrincipal {}
