import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CategoriaService } from './services/categoria.service';

@Component({
  selector: 'app-categorias',
  imports: [FormsModule],
  templateUrl: './categorias.html',
  styleUrl: './categorias.css',
})
export class Categorias {
  nome = '';
  descricao = '';

  constructor(private categoriaService: CategoriaService) {}

  salvarCategoria() {
    const categoria = {
      nome: this.nome,
      descricao: this.descricao
    };

    this.categoriaService.cadastrarCategoria(categoria).subscribe({
      next: (resposta) => {
        console.log('Categoria cadastrada com sucesso:', resposta);
        this.nome = '';
        this.descricao = '';
      },
      error: (erro) => {
        console.error('Erro ao cadastrar categoria:', erro);
      }
    })
  }
}
