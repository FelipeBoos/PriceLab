import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface CategoriaRequest {
  nome: string;
  descricao: string;
}

export interface CategoriaResponse {
  id: number;
  nome: string;
  descricao: string;
}

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {

  private apiUrl = 'http://localhost:8080/categorias';

  constructor(private http: HttpClient) {}

  listarCategorias() {
    return this.http.get<CategoriaResponse[]>(this.apiUrl);
  }

  cadastrarCategoria(categoria: CategoriaRequest) {
    return this.http.post<CategoriaResponse>(this.apiUrl, categoria);
  }

  atualizarCategoria(id: number, categoria: CategoriaRequest) {
    return this.http.put<void>(`${this.apiUrl}/${id}`, categoria);
  }

  deletarCategoria(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
