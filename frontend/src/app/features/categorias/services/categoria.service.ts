import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
  providedIn: 'root',
})
export class CategoriaService {

  private readonly apiUrl = 'http://localhost:8080/categorias';

  constructor(private http: HttpClient) {}

  cadastrarCategoria(categoria: CategoriaRequest): Observable<CategoriaResponse> {
    return this.http.post<CategoriaResponse>(this.apiUrl, categoria);
  } 

  listarCategorias(): Observable<CategoriaResponse[]> {
    return this.http.get<CategoriaResponse[]>(this.apiUrl);
  }

  deletarCategoria(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
  }
}
