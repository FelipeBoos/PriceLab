import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { EstrategiaPrecoService, EstrategiaPrecoResponse } from './services/estrategia-preco.service';

@Component({
  selector: 'app-estrategias-preco',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './estrategias-preco.html',
  styleUrls: ['./estrategias-preco.css'],
})
export class EstrategiasPreco implements OnInit {
  estrategiasPreco = signal<EstrategiaPrecoResponse[]>([]);

  constructor(
    private estrategiaPrecoService: EstrategiaPrecoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarEstrategiasPreco();
  }

  carregarEstrategiasPreco(): void {
    this.estrategiaPrecoService.listarEstrategiasPreco().subscribe({
      next: (resposta) => {
        console.log('Estratégias carregadas:', resposta);
        this.estrategiasPreco.set(resposta);
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao listar estratégias:', erro);
      }
    });
  }

  botaoFiltrar(): void {
    alert('Botão filtrar: ainda não implementado');
  }

  botaoSimular(): void {
    this.router.navigate(['/app/estrategias-preco/simular']);
  }

  excluirEstrategiaPreco(id: number): void {
    if (!confirm('Deseja realmente deletar essa estratégia de preço?')) {
      return;
    }

    this.estrategiaPrecoService.deletarEstrategiaPreco(id).subscribe({
      next: () => {
        console.log('Estratégia de preço deletada:', id);
        this.carregarEstrategiasPreco();
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao deletar estratégia de preço:', erro);

        if (erro.status === 404) {
          alert('Estratégia de preço não encontrada');
        }
      }
    });
  }
}