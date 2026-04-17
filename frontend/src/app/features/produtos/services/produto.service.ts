import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'
import { CategoriaResponse } from '../../categorias/services/categoria.service';

export enum MoedaEnum {
  BRL = 'BRL',
  USD = 'USD',
  EUR = 'EUR'
}

export interface ProdutoRequest {
  nome: string,
  descricao: string,
  categoriaId: number,
  precoCusto: number,
  moeda?: MoedaEnum,
  importado: boolean,
  remessaConforme: boolean,
  freteInternacional: number,
  seguroInternacional: number,
  aliquotaIcmsImportacao: number,
  precoVenda: number,
  quantidadeEstoque: number,
  demandaBase?: number,
  fatorElasticidade?: number
}

export interface ProdutoCotacaoResponse {
  moeda: MoedaEnum;
  cotacao: number;
}

export interface ProdutoResponse {
  id: number,
  nome: string,
  descricao: string,
  categoriaNome: string,
  categoriaId: number,
  precoCusto: number;
  moeda?: MoedaEnum;
  cotacaoMoeda?: number;
  precoCustoEmReais?: number;
  importado: boolean;
  remessaConforme?: boolean;
  freteInternacional?: number;
  seguroInternacional?: number;
  aliquotaIcmsImportacao?: number;
  impostoImportacao?: number;
  icmsImportacao?: number;
  custoFinalAquisicao?: number;
  precoVenda: number;
  quantidadeEstoque: number;
  demandaBase?: number;
  fatorElasticidade?: number;
  categoria: CategoriaResponse;
}

@Injectable({
  providedIn: 'root',
})
export class ProdutoService {

  private apiUrl = 'http://localhost:8080/produtos';

  constructor(private http: HttpClient) {}

  listarProdutos() {
    return this.http.get<ProdutoResponse[]>(this.apiUrl);
  }

  cadastrarProduto(produto: ProdutoRequest) {
    return this.http.post<ProdutoResponse>(this.apiUrl, produto);
  }

  buscarCotacaoAtual(moeda: MoedaEnum) {
    return this.http.get<ProdutoCotacaoResponse>(`${this.apiUrl}/cotacao-atual`, {
      params: { moeda }
    });
  }

  atualizarProduto(id: number, produto: ProdutoRequest) {
    return this.http.put<void>(`${this.apiUrl}/${id}`, produto)
  }

  deletarProduto(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
  }
}
