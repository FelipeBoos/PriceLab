import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [],
  templateUrl: './modal.html',
  styleUrl: './modal.css',
})
export class Modal {
  @Input() aberto = false;
  @Input() titulo = '';
  @Input() fecharAoClicarFora = true;
  @Input() largura = 'min(720px, 100%)';

  @Output() fechar = new EventEmitter<void>();

  onOverlayClick(): void {
    if (this.fecharAoClicarFora) {
      this.fechar.emit();
    }
  }

  onFechar(): void {
    this.fechar.emit();
  }
}
