import { Component, OnInit, signal } from '@angular/core';
import { ProdutoService, ProdutoResponse } from './services/produto.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-produtos',
  standalone: true,
  imports: [],
  templateUrl: './produtos.html',
  styleUrl: './produtos.css',
})
export class Produtos implements OnInit {

  produtos = signal<ProdutoResponse[]>([]);

  constructor(private produtoService: ProdutoService) {}

  ngOnInit(): void {
    console.log('NgOnInit da página produtos');
    this.carregarProdutos();
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


  incluirProduto() {
    alert("Incluir produto: Ainda não implementado");
  }

  filtrarProduto() {
    alert("Filtrar produto: Ainda não implementado");
  }

  editarProduto(id: number) {
    alert(`Editar produto: Ainda não implementado. Id selecionado: ${id}`);
  }

  deletarProduto(id: number) {
    alert(`Deletar produto: Ainda não implementado. Id selecionado: ${id}`);
  }
}
