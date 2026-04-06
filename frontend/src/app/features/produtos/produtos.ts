import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProdutoService, ProdutoResponse, MoedaEnum } from './services/produto.service';
import { HttpErrorResponse } from '@angular/common/http';
import { CategoriaResponse, CategoriaService } from '../categorias/services/categoria.service';
import { Modal } from '../../shared/components/modal/modal/modal';

@Component({
  selector: 'app-produtos',
  standalone: true,
  imports: [FormsModule, Modal],
  templateUrl: './produtos.html',
  styleUrl: './produtos.css',
})
export class Produtos implements OnInit {
  nome = '';
  descricao = '';
  categoriaId: number | null = null;
  categorias =  signal<CategoriaResponse[]>([]);
  precoCusto: number | null = null;
  moeda: MoedaEnum = MoedaEnum.BRL;
  precoVenda: number | null = null;
  quantidadeEstoque: number | null = null;
  demandaBase: number | null = null;
  fatorElasticidade: number | null = null;

  produtos = signal<ProdutoResponse[]>([]);

  exibirFormulario = false;
  produtoEmEdicaoId: number | null = null;

  constructor(private produtoService: ProdutoService, private categoriaService: CategoriaService) {}

  ngOnInit(): void {
    console.log('NgOnInit da página produtos');
    this.carregarProdutos();
    this.carregarCategorias();
  }

  carregarProdutos() {
    this.produtoService.listarProdutos().subscribe({
      next: (resposta) => {
        console.log('Produtos carregados:', resposta);
        this.produtos.set(resposta);
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao listar produtos', erro);
      }
    });
  }

  botaoSalvar() {
    if (
      this.categoriaId === null ||
      this.precoCusto === null ||
      this.precoVenda === null ||
      this.quantidadeEstoque === null ||
      this.demandaBase === null ||
      this.fatorElasticidade === null
    ) {
      alert("Preencha todos os campos");
      return;
    }

    const produto = {
      nome: this.nome,
      descricao: this.descricao,
      categoriaId: this.categoriaId,
      precoCusto: this.precoCusto,
      moeda: this.moeda,
      precoVenda: this.precoVenda,
      quantidadeEstoque: this.quantidadeEstoque,
      demandaBase: this.demandaBase,
      fatorElasticidade: this.fatorElasticidade
    };

    if (this.produtoEmEdicaoId === null) {
      //salvarProduto()
      this.produtoService
        .cadastrarProduto(produto)
        .subscribe({
          next: () => {
            alert('Produto cadastrado com sucesso');
            this.resetarFormulario();
            this.carregarProdutos();
          }
        })
    } else {
      //atualizarProduto()
      this.produtoService
        .atualizarProduto(this.produtoEmEdicaoId, produto)
        .subscribe({
          next: () => {
            alert('Produto atualizado com sucesso');
            this.resetarFormulario();
            this.carregarProdutos();  
          }
        })
    }
  }

  incluirProduto() {
    this.resetarFormulario();
    this.exibirFormulario = true;
  }

  filtrarProduto() {
    alert("Filtrar produto: Ainda não implementado");
  }

  editarProduto(id: number) {
    const produto = this.produtos().find((p: ProdutoResponse) => p.id === id);

    if (!produto) {
      return;
    }

    this.nome = produto.nome;
    this.descricao = produto.descricao;
    this.categoriaId = produto.categoriaId;
    this.precoCusto = produto.precoCusto;
    this.moeda = produto.moeda ?? MoedaEnum.BRL;
    this.precoVenda = produto.precoVenda;
    this.quantidadeEstoque = produto.quantidadeEstoque;
    this.demandaBase = produto.demandaBase ?? null;
    this.fatorElasticidade = produto.fatorElasticidade ?? null;

    this.exibirFormulario = true;
    this.produtoEmEdicaoId = id;
  }

  deletarProduto(id: number): void {
    if (!confirm('Deseja realmente excluir este produto?')){
      return;
    }

    this.produtoService.deletarProduto(id).subscribe({
      next: () => {
        console.log('Produto deletado: ', id);
        alert('Produto deletado com sucesso');
        this.carregarProdutos();
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao excluir produto:', erro);

        if (erro.status === 404) {
          alert('Produto não encontrado');
          return;
        }

        alert('Ocorreu um erro ao excluir o produto');
      }
    });
  }

  resetarFormulario() {
    this.nome = '';
    this.descricao = '';
    this.categoriaId = null;
    this.precoCusto = null;
    this.moeda = MoedaEnum.BRL;
    this.precoVenda = null;
    this.quantidadeEstoque = null;
    this.demandaBase = null;
    this.fatorElasticidade = null;
    this.produtoEmEdicaoId = null;
    this.exibirFormulario = false;
  }

  carregarCategorias(): void {
    this.categoriaService.listarCategorias().subscribe({
      next: (resposta) => {
        this.categorias.set(resposta);
      },
      error: (erro) => {
        console.error('Erro ao carregar categorias:', erro);
      }
    })
  }
}
