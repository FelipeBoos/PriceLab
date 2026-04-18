import { ChangeDetectorRef, Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProdutoService, ProdutoResponse, MoedaEnum } from './services/produto.service';
import { HttpErrorResponse } from '@angular/common/http';
import { CategoriaResponse, CategoriaService } from '../categorias/services/categoria.service';
import { Modal } from '../../shared/components/modal/modal/modal';
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'app-produtos',
  standalone: true,
  imports: [FormsModule, Modal, NgxMaskDirective],
  templateUrl: './produtos.html',
  styleUrl: './produtos.css',
})
export class Produtos implements OnInit {
  nome = '';
  descricao = '';
  categoriaId: number | null = null;
  categorias =  signal<CategoriaResponse[]>([]);
  precoCusto: number | null = null;
  precoCustoInput = '';
  moeda: MoedaEnum = MoedaEnum.BRL;
  importado = false;
  remessaConforme = false;
  freteInternacional = 0;
  freteInternacionalInput = '';
  seguroInternacional = 0;
  seguroInternacionalInput = '';
  aliquotaIcmsImportacao = 0;
  precoVenda: number | null = null;
  precoVendaInput = '';
  quantidadeEstoque: number | null = null;
  demandaBase: number | null = null;
  fatorElasticidade: number | null = null;
  cotacaoAtual: number | null = null;
  custoEmBrlCalculado: number | null = null;
  carregandoCotacao = false;

  produtos = signal<ProdutoResponse[]>([]);

  exibirFormulario = false;
  produtoEmEdicaoId: number | null = null;
  mensagemToastSucesso: string | null = null;
  private toastSucessoTimeoutId: ReturnType<typeof setTimeout> | null = null;

  get produtoImportado(): boolean {
    return this.importado;
  }

  get badgeModalTipoProduto(): 'NACIONAL' | 'IMPORTADO' {
    return this.produtoImportado ? 'IMPORTADO' : 'NACIONAL';
  }

  get moedaEstrangeiraSelecionada(): boolean {
    return this.moeda === MoedaEnum.USD || this.moeda === MoedaEnum.EUR;
  }

  get codigoMoedaSelecionada(): string {
    return this.moeda ?? MoedaEnum.BRL;
  }

  get simboloMoedaSelecionada(): string {
    if (this.moeda === MoedaEnum.USD) {
      return '$';
    }

    if (this.moeda === MoedaEnum.EUR) {
      return '€';
    }

    return 'R$';
  }

  get labelValorMoedaSelecionada(): string {
    return `Valor em ${this.codigoMoedaSelecionada}`;
  }

  get cotacaoAtualFormatada(): string {
    if (this.cotacaoAtual === null) {
      return '--';
    }

    return this.cotacaoAtual.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 6,
    });
  }

  get custoEmBrlCalculadoFormatado(): string {
    if (this.custoEmBrlCalculado === null) {
      return '';
    }

    return this.formatarMoeda(this.custoEmBrlCalculado, false);
  }

  get impostoImportacaoCalculado(): number {
    if (!this.importado || this.custoEmBrlCalculado === null) {
      return 0;
    }

    return this.arredondarMoeda(this.baseCalculoImportacao * 0.6);
  }

  get icmsImportacaoCalculado(): number {
    if (!this.importado || this.custoEmBrlCalculado === null) {
      return 0;
    }

    const aliquotaFracao = this.normalizarNumero(this.aliquotaIcmsImportacao) / 100;
    return this.arredondarMoeda((this.baseCalculoImportacao + this.impostoImportacaoCalculado) * aliquotaFracao);
  }

  get custoFinalAquisicaoCalculado(): number {
    if (this.custoEmBrlCalculado === null) {
      return 0;
    }

    if (!this.importado) {
      return this.arredondarMoeda(this.custoEmBrlCalculado);
    }

    return this.arredondarMoeda(this.baseCalculoImportacao + this.impostoImportacaoCalculado + this.icmsImportacaoCalculado);
  }

  get impostoImportacaoCalculadoFormatado(): string {
    return this.formatarMoeda(this.impostoImportacaoCalculado, false);
  }

  get icmsImportacaoCalculadoFormatado(): string {
    return this.formatarMoeda(this.icmsImportacaoCalculado, false);
  }

  get custoFinalAquisicaoCalculadoFormatado(): string {
    return this.formatarMoeda(this.custoFinalAquisicaoCalculado, false);
  }

  get labelCustoFinalAquisicao(): string {
    return this.importado
      ? 'Custo final de aquisição (com importação)'
      : 'Custo final de aquisição';
  }

  private exibirToastSucesso(mensagem: string): void {
    this.mensagemToastSucesso = mensagem;

    if (this.toastSucessoTimeoutId) {
      clearTimeout(this.toastSucessoTimeoutId);
    }

    this.toastSucessoTimeoutId = setTimeout(() => {
      this.mensagemToastSucesso = null;
      this.toastSucessoTimeoutId = null;
    }, 4000);
  }

  fecharToastSucesso(): void {
    this.mensagemToastSucesso = null;

    if (this.toastSucessoTimeoutId) {
      clearTimeout(this.toastSucessoTimeoutId);
      this.toastSucessoTimeoutId = null;
    }
  }

  formatarCustoOrigem(produto: ProdutoResponse): string {
    const valor = produto.precoCusto ?? 0;
    const moeda = produto.moeda ?? MoedaEnum.BRL;

    if (moeda === MoedaEnum.USD) {
      return `US$ ${valor.toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      })}`;
    }

    if (moeda === MoedaEnum.EUR) {
      return `€ ${valor.toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      })}`;
    }

    return `R$ ${valor.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
  }

  formatarCustoFinalAquisicao(produto: ProdutoResponse): string {
    const valor = produto.custoFinalAquisicao ?? produto.precoCustoEmReais ?? 0;

    return `R$ ${valor.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
  }

  formatarPrecoVenda(valor: number | null | undefined): string {
    const valorTratado = valor ?? 0;

    return `R$ ${valorTratado.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
  }

  private get baseCalculoImportacao(): number {
    if (this.custoEmBrlCalculado === null) {
      return 0;
    }

    return this.arredondarMoeda(
      this.custoEmBrlCalculado + this.normalizarNumero(this.freteInternacional) + this.normalizarNumero(this.seguroInternacional)
    );
  }

  private arredondarMoeda(valor: number): number {
    return Math.round((valor + Number.EPSILON) * 100) / 100;
  }

  private normalizarNumero(valor: number | null | undefined): number {
    return Number.isFinite(valor as number) ? Math.max(valor as number, 0) : 0;
  }

  onImportadoChange(): void {
    this.aliquotaIcmsImportacao = this.normalizarNumero(this.aliquotaIcmsImportacao);
    this.cdr.detectChanges();
  }

  onFreteInternacionalInputChange(valor: string): void {
    this.freteInternacionalInput = valor ?? '';
    const numero = this.parseValorMonetarioMascara(this.freteInternacionalInput, false);
    this.freteInternacional = numero ?? 0;
  }

  onSeguroInternacionalInputChange(valor: string): void {
    this.seguroInternacionalInput = valor ?? '';
    const numero = this.parseValorMonetarioMascara(this.seguroInternacionalInput, false);
    this.seguroInternacional = numero ?? 0;
  }

  onAliquotaIcmsImportacaoChange(valor: number | null): void {
    this.aliquotaIcmsImportacao = this.normalizarNumero(valor);
  }

  onRemessaConformeChange(): void {
    this.cdr.detectChanges();
  }

  private formatarValorMonetarioEntrada(valor: number): string {
    if (!valor || valor <= 0) {
      return '';
    }

    return this.formatarMoeda(valor, false);
  }

  private sincronizarCamposImportacao(): void {
    this.freteInternacionalInput = this.formatarValorMonetarioEntrada(this.freteInternacional);
    this.seguroInternacionalInput = this.formatarValorMonetarioEntrada(this.seguroInternacional);
  }

  private limparImportacaoVisual(): void {
    this.freteInternacionalInput = '';
    this.seguroInternacionalInput = '';
  }

  private formatarMoeda(valor: number, formatoEstrangeiro: boolean): string {
    const locale = formatoEstrangeiro ? 'en-US' : 'pt-BR';
    return valor.toLocaleString(locale, {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
  }

  constructor(
    private produtoService: ProdutoService,
    private categoriaService: CategoriaService,
    private cdr: ChangeDetectorRef
  ) {}

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

  onMoedaChange(): void {
    this.sincronizarPrecoCustoInput();
    this.atualizarCotacaoPreview();
  }

  onPrecoCustoInputChange(valor: string): void {
    this.precoCustoInput = valor ?? '';
    this.precoCusto = this.parseValorMonetarioMascara(this.precoCustoInput, this.moedaEstrangeiraSelecionada);
    this.atualizarCustoEmBrlCalculado();
  }

  onPrecoVendaInputChange(valor: string): void {
    this.precoVendaInput = valor ?? '';
    this.precoVenda = this.parseValorMonetarioMascara(this.precoVendaInput, false);
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
      importado: this.importado,
      remessaConforme: this.remessaConforme,
      freteInternacional: this.freteInternacional,
      seguroInternacional: this.seguroInternacional,
      aliquotaIcmsImportacao: this.aliquotaIcmsImportacao,
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
            this.exibirToastSucesso('Produto cadastrado com sucesso.');
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
    this.importado = produto.importado ?? false;
    this.remessaConforme = produto.remessaConforme ?? false;
    this.freteInternacional = produto.freteInternacional ?? 0;
    this.seguroInternacional = produto.seguroInternacional ?? 0;
    this.aliquotaIcmsImportacao = produto.aliquotaIcmsImportacao ?? 0;
    this.precoVenda = produto.precoVenda;
    this.quantidadeEstoque = produto.quantidadeEstoque;
    this.demandaBase = produto.demandaBase ?? null;
    this.fatorElasticidade = produto.fatorElasticidade ?? null;
    this.cotacaoAtual = produto.cotacaoMoeda ?? (this.moeda === MoedaEnum.BRL ? 1 : null);
    this.custoEmBrlCalculado = produto.precoCustoEmReais ?? null;
    this.sincronizarCamposMonetarios();
    this.sincronizarCamposImportacao();

    this.atualizarCotacaoPreview();

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
    this.precoCustoInput = '';
    this.moeda = MoedaEnum.BRL;
    this.importado = false;
    this.remessaConforme = false;
    this.freteInternacional = 0;
    this.seguroInternacional = 0;
    this.aliquotaIcmsImportacao = 0;
    this.precoVenda = null;
    this.precoVendaInput = '';
    this.quantidadeEstoque = null;
    this.demandaBase = null;
    this.fatorElasticidade = null;
    this.cotacaoAtual = null;
    this.custoEmBrlCalculado = null;
    this.carregandoCotacao = false;
    this.limparImportacaoVisual();
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

  private atualizarCotacaoPreview(): void {
    if (!this.moedaEstrangeiraSelecionada) {
      this.cotacaoAtual = 1;
      this.carregandoCotacao = false;
      this.atualizarCustoEmBrlCalculado();
      this.cdr.detectChanges();
      return;
    }

    this.carregandoCotacao = true;
    this.cdr.detectChanges();
    this.produtoService.buscarCotacaoAtual(this.moeda).subscribe({
      next: (resposta) => {
        this.cotacaoAtual = resposta.cotacao;
        this.atualizarCustoEmBrlCalculado();
        this.carregandoCotacao = false;
        this.cdr.detectChanges();
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao carregar cotação atual:', erro);
        this.cotacaoAtual = null;
        this.custoEmBrlCalculado = null;
        this.carregandoCotacao = false;
        this.cdr.detectChanges();
      }
    });
  }

  private atualizarCustoEmBrlCalculado(): void {
    if (this.precoCusto === null || this.precoCusto <= 0) {
      this.custoEmBrlCalculado = null;
      return;
    }

    if (!this.moedaEstrangeiraSelecionada) {
      this.cotacaoAtual = 1;
      this.custoEmBrlCalculado = this.precoCusto;
      return;
    }

    if (this.cotacaoAtual === null || this.cotacaoAtual <= 0) {
      this.custoEmBrlCalculado = null;
      return;
    }

    this.custoEmBrlCalculado = this.precoCusto * this.cotacaoAtual;
  }

  private parseValorMonetarioMascara(valor: string, formatoEstrangeiro: boolean): number | null {
    const texto = (valor ?? '').trim();
    if (!texto) {
      return null;
    }

    const separadorMilhar = formatoEstrangeiro ? ',' : '.';
    const separadorDecimal = formatoEstrangeiro ? '.' : ',';

    const semMilhar = texto.split(separadorMilhar).join('');
    const normalizado = semMilhar.replace(separadorDecimal, '.');
    const numero = Number.parseFloat(normalizado);

    return Number.isFinite(numero) ? numero : null;
  }

  private sincronizarCamposMonetarios(): void {
    this.sincronizarPrecoCustoInput();
    this.precoVendaInput = this.precoVenda !== null ? this.formatarMoeda(this.precoVenda, false) : '';
  }

  private sincronizarPrecoCustoInput(): void {
    this.precoCustoInput = this.precoCusto !== null
      ? this.formatarMoeda(this.precoCusto, this.moedaEstrangeiraSelecionada)
      : '';
  }
}
