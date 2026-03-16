import { Component } from '@angular/core';

@Component({
  selector: 'app-estrategias-preco',
  imports: [],
  templateUrl: './estrategias-preco.html',
  styleUrl: './estrategias-preco.css',
})
export class EstrategiasPreco {
  mostrarModalSimulacao = false;

  botaoSimular(): void {
    this.abrirSimulacao();
  }

  botaoFiltrar(): void {
    alert('Botão filtrar: ainda não implementado');
  }

  abrirSimulacao(): void {
    this.mostrarModalSimulacao = true;
  }

  fecharSimulacao(): void {
    this.mostrarModalSimulacao = false;
  }
}
