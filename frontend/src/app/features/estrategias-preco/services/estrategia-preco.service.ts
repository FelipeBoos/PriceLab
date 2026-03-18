import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EstrategiaPrecoRequest {
  produtoId: number;
  margemLucro: number;
  percentualImposto: number;
}

export interface EstrategiaPrecoResponse {
  id: number;
  produtoId: number;
  produtoNome: string;
  margemLucro: number;
  percentualImposto: number;
  precoSugerido: number;
  lucroUnitario: number;
  demandaEstimada: number;
  lucroTotalEstimado: number;
}

@Injectable({
  providedIn: 'root',
})
export class EstrategiaPrecoService {

  private apiUrl = 'http://localhost:8080/estrategias-preco';

  constructor(private http: HttpClient) {}

  listarEstrategiasPreco() {
    return this.http.get<EstrategiaPrecoResponse[]>(this.apiUrl);
  }

  simularEstrategiaPreco(estrategiaPreco: EstrategiaPrecoRequest): Observable<EstrategiaPrecoResponse> {
    return this.http.post<EstrategiaPrecoResponse>(`${this.apiUrl}/simular`, estrategiaPreco);
  }

  salvarEstrategiaPreco(estrategiaPreco: EstrategiaPrecoRequest): Observable<EstrategiaPrecoResponse> {
    return this.http.post<EstrategiaPrecoResponse>(this.apiUrl, estrategiaPreco);
  }

  deletarEstrategiaPreco(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
