import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { CategoriaResponse, CategoriaService } from './services/categoria.service';

@Component({
  selector: 'app-categorias',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './categorias.html',
  styleUrl: './categorias.css',
})
export class Categorias implements OnInit {
  nome = '';
  descricao = '';
  categorias = signal<CategoriaResponse[]>([]);

  exibirFormulario = false;
  categoriaEmEdicaoId: number | null = null;

  constructor(private categoriaService: CategoriaService) {}

  ngOnInit(): void {
    console.log('ngOnInit da página Categorias');
    this.carregarCategorias();
  }

  salvarCategoria() {
    const categoria = {
      nome: this.nome,
      descricao: this.descricao
    };

    if (this.categoriaEmEdicaoId !== null) {
      this.categoriaService
        .atualizarCategoria(this.categoriaEmEdicaoId, categoria)
        .subscribe({
          next: () => {
            alert('Categoria atualizada com sucesso');
            this.resetarFormulario();
            this.carregarCategorias();
          },
          error: (erro: HttpErrorResponse) => {
            console.error('Erro ao atualizar categoria', erro);
          }
        });
    } else {
      this.categoriaService
        .cadastrarCategoria(categoria)
        .subscribe({
          next: () => {
            alert('Categoria cadastrada com sucesso');
            this.resetarFormulario();
            this.carregarCategorias();
          },
          error: (erro: HttpErrorResponse) => {
            console.error('Erro ao cadastrar categoria', erro);
          }
        });
    }
  }

  carregarCategorias() {
    this.categoriaService.listarCategorias().subscribe({
      next: (resposta) => {
        console.log('Categorias recebidas:', resposta);
        this.categorias.set(resposta);
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao listar categorias', erro);
      }
    });
  }

  deletarCategoria(id: number) {
    if (!confirm('Deseja realmente deletar esta categoria?')) {
      return;
    }

    this.categoriaService.deletarCategoria(id).subscribe({
      next: () => {
        console.log('Categoria deletada:', id);
        this.carregarCategorias();
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao deletar categoria:', erro);
      }
    });
  }

  incluirCategoria() {
    this.exibirFormulario = true;
    this.categoriaEmEdicaoId = null;
    this.nome = '';
    this.descricao = '';
  }

  editarCategoria(id: number) {
    const categoria = this.categorias().find((c: CategoriaResponse) => c.id === id);

    if (!categoria) return;

    this.nome = categoria.nome;
    this.descricao = categoria.descricao;
    this.exibirFormulario = true;
    this.categoriaEmEdicaoId = id;
  }

  filtrarCategoria() {
    console.log('Filtrar categoria: Ainda não implementado');
  }

  resetarFormulario() {
    this.nome = '';
    this.descricao = '';
    this.categoriaEmEdicaoId = null;
    this.exibirFormulario = false;
  }
}