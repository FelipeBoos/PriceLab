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
  mensagemToastSucesso: string | null = null;
  private toastSucessoTimeoutId: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private estrategiaPrecoService: EstrategiaPrecoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarEstrategiasPreco();
    this.exibirToastSucessoAoAbrir();
  }

  private exibirToastSucessoAoAbrir(): void {
    const mensagem = sessionStorage.getItem('estrategiaPrecoToastSucesso');

    if (!mensagem) {
      return;
    }

    sessionStorage.removeItem('estrategiaPrecoToastSucesso');
    this.exibirToastSucesso(mensagem);
  }

  private exibirToastSucesso(mensagem: string): void {
    this.mensagemToastSucesso = mensagem;

    if (this.toastSucessoTimeoutId) {
      clearTimeout(this.toastSucessoTimeoutId);
    }

    this.toastSucessoTimeoutId = setTimeout(() => {
      this.mensagemToastSucesso = null;
      this.toastSucessoTimeoutId = null;
    }, 4000);
  }

  fecharToastSucesso(): void {
    this.mensagemToastSucesso = null;

    if (this.toastSucessoTimeoutId) {
      clearTimeout(this.toastSucessoTimeoutId);
      this.toastSucessoTimeoutId = null;
    }
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