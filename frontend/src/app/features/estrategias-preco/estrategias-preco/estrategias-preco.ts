import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Modal } from '../../../shared/components/modal/modal/modal';
import { EstrategiaPrecoService, EstrategiaPrecoRequest, EstrategiaPrecoResponse } from '../services/estrategia-preco.service';
import { ProdutoService, ProdutoResponse } from '../../produtos/services/produto.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-estrategias-preco',
  imports: [FormsModule, Modal],
  templateUrl: './estrategias-preco.html',
  styleUrl: './estrategias-preco.css',
})
export class EstrategiasPreco implements OnInit {
  exibirFormulario = false;
  estrategiasPreco = signal<EstrategiaPrecoResponse[]>([]);

  produtos = signal<ProdutoResponse[]>([]);

  constructor(private estrategiaPrecoService: EstrategiaPrecoService, private produtoService: ProdutoService) {}

  ngOnInit(): void {
    this.carregarProdutos();
    this.carregarEstrategiasPreco();
  }

  carregarEstrategiasPreco(): void {
    this.estrategiaPrecoService.listarEstrategiasPreco().subscribe({
      next: (resposta) => {
        console.log('Estrategias carregadas: ', resposta);
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao listar estrategias: ', erro);
      }
    });
  }

  botaoSimular(): void {
    this.abrirSimulacao();
  }

  botaoFiltrar(): void {
    alert('Botão filtrar: ainda não implementado');
  }

  abrirSimulacao(): void {
    this.exibirFormulario = true;
  }

  fecharSimulacao(): void {
    this.exibirFormulario = false;
  }

  resetarFormulario(): void {

  }

  carregarProdutos(): void {
    this.produtoService.listarProdutos().subscribe({
      next: (resposta) => {
        //console.log('Produtos carregados:', resposta);
        this.produtos.set(resposta);
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao carregar produtos:', erro);
      }
    })
  }

  testarSimulacao() {
    const request = {
      produtoId: 8,
      margemLucro: 0.2,
      percentualImposto: 0.1
    };

    this.estrategiaPrecoService.simularEstrategiaPreco(request).subscribe({
      next: (resposta) => {
        console.log('Resultado da simulação: ', resposta);
      },
      error: (erro) => {
        console.error('Erro na simulação: ', erro);
      }
    });
  }

  excluirEstrategiaPreco(id: number): void {
    if (!confirm('Deseja realmente deletar essa estratégia de preço?')) {
      return;
    }

    this.estrategiaPrecoService.deletarEstrategiaPreco(id).subscribe({
      next: () => {
        console.log('Estrategia de preco deletada: ', id);
        this.carregarEstrategiasPreco();
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao deletar estratégia de preço: ', erro);

        if (erro.status === 404) {
          alert('Estrategia de preço não encontrado');
          return;
        }
      }
    });
  }
}
