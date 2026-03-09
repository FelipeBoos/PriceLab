import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
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
  categorias: CategoriaResponse[] = [];

  constructor(
    private categoriaService: CategoriaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('ngOnInt da página Categorias')
    this.carregarCategorias();
  }

  salvarCategoria() {
    const categoria = {
      nome: this.nome,
      descricao: this.descricao
    };

    this.categoriaService.cadastrarCategoria(categoria).subscribe({
      next: (resposta) => {
        alert("Categoria cadastrada com sucesso");
        console.log('Categoria cadastrada com sucesso:', resposta);
        this.nome = '';
        this.descricao = '';
      },
      error: (erro) => {
        console.error('Erro ao cadastrar categoria:', erro);
      }
    });
  }
  carregarCategorias() {
    this.categoriaService.listarCategorias().subscribe({
      next: (resposta) => {
        console.log('Categorias recebidas:', resposta)
        this.categorias = resposta;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao listar categorias', erro);
      }
    });
  }
}

