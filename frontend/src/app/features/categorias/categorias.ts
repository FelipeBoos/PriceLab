import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { CategoriaResponse, CategoriaService } from './services/categoria.service';
import { Modal } from '../../shared/components/modal/modal/modal';

@Component({
  selector: 'app-categorias',
  standalone: true,
  imports: [FormsModule, Modal],
  templateUrl: './categorias.html',
  styleUrls: ['./categorias.css'],
})
export class Categorias implements OnInit {
  nome = '';
  descricao = '';
  categorias = signal<CategoriaResponse[]>([]);
  mensagemToast: string | null = null;
  private toastTimeoutId: ReturnType<typeof setTimeout> | null = null;

  exibirFormulario = false;
  categoriaEmEdicaoId: number | null = null;

  constructor(private categoriaService: CategoriaService) {}

  ngOnInit(): void {
    //console.log('ngOnInit da página Categorias');
    this.carregarCategorias();
  }

  private exibirToast(mensagem: string): void {
    this.mensagemToast = mensagem;

    if (this.toastTimeoutId) {
      clearTimeout(this.toastTimeoutId);
    }

    this.toastTimeoutId = setTimeout(() => {
      this.mensagemToast = null;
      this.toastTimeoutId = null;
    }, 4000);
  }

  fecharToast(): void {
    this.mensagemToast = null;

    if (this.toastTimeoutId) {
      clearTimeout(this.toastTimeoutId);
      this.toastTimeoutId = null;
    }
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
            this.exibirToast('Categoria atualizada com sucesso.');
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
            this.exibirToast('Categoria salva com sucesso.');
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

  deletarCategoria(id: number): void {
    if (!confirm('Deseja realmente deletar esta categoria?')) {
      return;
    }

    this.categoriaService.deletarCategoria(id).subscribe({
      next: () => {
        console.log('Categoria deletada:', id);
        this.exibirToast('Categoria deletada com sucesso.');
        this.carregarCategorias();
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao deletar categoria:', erro);
        console.error('Body do erro:', erro.error);

        const mensagemBackend =
          typeof erro.error === 'string'
            ? erro.error
            : erro.error?.message || '';

        if (
          erro.status === 400 &&
          (
            mensagemBackend.includes('produto vinculado') ||
            mensagemBackend.includes('produtos vinculados') ||
            mensagemBackend.includes('categoria se ja existe um produto')
          )
        ) {
          this.exibirToast('Não é possível excluir esta categoria, pois existe produto vinculado a ela.');
          return;
        }

        if (erro.status === 404) {
          this.exibirToast('Categoria não encontrada.');
          return;
        }

        this.exibirToast('Ocorreu um erro ao excluir a categoria.');
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

  fecharFormulario(): void {
    this.nome = '';
    this.descricao = '';
    this.categoriaEmEdicaoId = null;
    this.exibirFormulario = false;
  }

  resetarFormulario() {
    this.fecharFormulario();
  }
}