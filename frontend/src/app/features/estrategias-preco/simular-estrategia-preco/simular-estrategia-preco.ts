import {
  Component,
  OnInit,
  OnDestroy,
  AfterViewInit,
  signal,
  ViewChild,
  ElementRef,
  ChangeDetectorRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { Chart, DoughnutController, ArcElement, Tooltip, Legend } from 'chart.js';

import {
  EstrategiaPrecoService,
  EstrategiaPrecoResponse
} from '../services/estrategia-preco.service';
import {
  ProdutoService,
  ProdutoResponse
} from '../../produtos/services/produto.service';

Chart.register(DoughnutController, ArcElement, Tooltip, Legend);

@Component({
  selector: 'app-simular-estrategia-preco',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './simular-estrategia-preco.html',
  styleUrls: ['./simular-estrategia-preco.css'],
})
export class SimularEstrategiaPreco implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('graficoRosca') graficoRoscaRef?: ElementRef<HTMLCanvasElement>;

  produtoId: number | null = null;
  margemLucro: number | null = null;
  percentualImposto: number | null = null;

  resultadoSimulacao: EstrategiaPrecoResponse | null = null;
  produtos = signal<ProdutoResponse[]>([]);
  mensagemToast: string | null = null;

  private grafico: Chart | null = null;
  private viewInicializada = false;
  private toastSucessoTimeoutId: ReturnType<typeof setTimeout> | null = null;
  private redirecionarTimeoutId: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private estrategiaPrecoService: EstrategiaPrecoService,
    private produtoService: ProdutoService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.carregarProdutos();
  }

  ngAfterViewInit(): void {
    this.viewInicializada = true;
    this.criarOuAtualizarGrafico();
  }

  ngOnDestroy(): void {
    if (this.toastSucessoTimeoutId) {
      clearTimeout(this.toastSucessoTimeoutId);
      this.toastSucessoTimeoutId = null;
    }

    if (this.redirecionarTimeoutId) {
      clearTimeout(this.redirecionarTimeoutId);
      this.redirecionarTimeoutId = null;
    }
  }

  carregarProdutos(): void {
    this.produtoService.listarProdutos().subscribe({
      next: (resposta) => {
        this.produtos.set(resposta);
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao carregar produtos:', erro);
      }
    });
  }

  simularEstrategiaDePreco(): void {
    if (
      this.produtoId === null ||
      this.margemLucro === null ||
      this.percentualImposto === null
    ) {
      this.exibirToast('Preencha todos os campos.');
      return;
    }

    const estrategiaPrecoRequest = {
      produtoId: this.produtoId,
      margemLucro: this.margemLucro,
      percentualImposto: this.percentualImposto,
    };

    this.estrategiaPrecoService.simularEstrategiaPreco(estrategiaPrecoRequest).subscribe({
      next: (resposta) => {
        this.resultadoSimulacao = { ...resposta };
        this.cdr.markForCheck();

        setTimeout(() => {
          this.criarOuAtualizarGrafico();
        });
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro na simulação:', erro);
      }
    });
  }

  private criarOuAtualizarGrafico(): void {
    if (!this.viewInicializada || !this.graficoRoscaRef || !this.resultadoSimulacao) {
      return;
    }

    const precoSugerido = this.resultadoSimulacao.precoSugerido;
    const lucroUnitario = this.resultadoSimulacao.lucroUnitario;
    const percentualImposto = this.resultadoSimulacao.percentualImposto ?? 0;

    if (!precoSugerido || precoSugerido <= 0) {
      return;
    }

    const impostoUnitario = precoSugerido * (percentualImposto / 100);
    const custoUnitario = precoSugerido - lucroUnitario - impostoUnitario;

    const percentualCusto = this.calcularPercentual(custoUnitario, precoSugerido);
    const percentualLucro = this.calcularPercentual(lucroUnitario, precoSugerido);
    const percentualImpostoGrafico = this.calcularPercentual(impostoUnitario, precoSugerido);

    if (this.grafico) {
      this.grafico.destroy();
    }

    const canvas = this.graficoRoscaRef.nativeElement;

    this.grafico = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: ['Custo base', 'Margem', 'Impostos'],
        datasets: [
          {
            data: [percentualCusto, percentualLucro, percentualImpostoGrafico],
            backgroundColor: ['#2f6fe4', '#5b5ce6', '#f4a11a'],
            borderColor: '#ffffff',
            borderWidth: 2,
            hoverOffset: 4
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '58%',
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            callbacks: {
              label: (context) => {
                const valor = Number(context.raw).toFixed(2);
                return `${context.label}: ${valor}%`;
              }
            }
          }
        }
      }
    });
  }

  private calcularPercentual(valor: number, total: number): number {
    if (!total || total <= 0) {
      return 0;
    }

    return Number(((valor / total) * 100).toFixed(2));
  }

  salvarEstrategia(): void {
    if (!this.resultadoSimulacao) {
      this.exibirToast('Faça a simulação antes de salvar a estratégia.');
      return;
    }

    if (this.temAvisosCriticos()) {
      this.exibirToast('Não é possível salvar uma estratégia com demanda zerada. Ajuste os parâmetros.');
      return;
    }

    const estrategiaPrecoRequest = {
      produtoId: this.resultadoSimulacao.produtoId,
      margemLucro: this.resultadoSimulacao.margemLucro,
      percentualImposto: this.resultadoSimulacao.percentualImposto,
    };

    this.estrategiaPrecoService.salvarEstrategiaPreco(estrategiaPrecoRequest).subscribe({
      next: (resposta) => {
        this.resultadoSimulacao = { ...resposta };
        this.exibirToast('Estratégia salva com sucesso.');

        sessionStorage.setItem('estrategiaPrecoToastSucesso', 'Estratégia salva com sucesso.');

        if (this.redirecionarTimeoutId) {
          clearTimeout(this.redirecionarTimeoutId);
        }

        this.redirecionarTimeoutId = setTimeout(() => {
          this.router.navigate(['/app/estrategias-preco']);
        }, 1200);
      },
      error: (erro: HttpErrorResponse) => {
        console.error('Erro ao salvar estratégia:', erro);
        this.exibirToast('Ocorreu um erro ao salvar a estratégia. Tente novamente.');
      }
    });
  }

  private exibirToast(mensagem: string): void {
    this.mensagemToast = mensagem;

    if (this.toastSucessoTimeoutId) {
      clearTimeout(this.toastSucessoTimeoutId);
    }

    this.toastSucessoTimeoutId = setTimeout(() => {
      this.mensagemToast = null;
      this.toastSucessoTimeoutId = null;
    }, 4000);
  }

  fecharToast(): void {
    this.mensagemToast = null;

    if (this.toastSucessoTimeoutId) {
      clearTimeout(this.toastSucessoTimeoutId);
      this.toastSucessoTimeoutId = null;
    }
  }

  resetarFormulario(): void {
    this.produtoId = null;
    this.margemLucro = null;
    this.percentualImposto = null;
    this.resultadoSimulacao = null;

    if (this.grafico) {
      this.grafico.destroy();
      this.grafico = null;
    }
  }

  voltar(): void {
    this.router.navigate(['app/estrategias-preco']);
  }

  removerAviso(index: number): void {
    if (!this.resultadoSimulacao?.avisos) {
      return;
    }

    this.resultadoSimulacao.avisos.splice(index, 1);
  }

  removerAvisosCriticos(): void {
    if (!this.resultadoSimulacao?.avisos) {
      return;
    }

    this.resultadoSimulacao.avisos = this.resultadoSimulacao.avisos.filter(
      (aviso) => !this.ehAvisoCritico(aviso)
    );
  }

  removerAvisosNaoCriticos(): void {
    if (!this.resultadoSimulacao?.avisos) {
      return;
    }

    this.resultadoSimulacao.avisos = this.resultadoSimulacao.avisos.filter(
      (aviso) => this.ehAvisoCritico(aviso)
    );
  }

  cancelar(): void {
    this.router.navigate(['app/estrategias-preco']);
  }

  temAvisosEstrategia(): boolean {
    return (this.resultadoSimulacao?.avisos?.length ?? 0) > 0;
  }

  temAvisosCriticos(): boolean {
    return this.avisosCriticos.length > 0;
  }

  get avisosCriticos(): string[] {
    return this.resultadoSimulacao?.avisos?.filter((aviso) => this.ehAvisoCritico(aviso)) ?? [];
  }

  get avisosNaoCriticos(): string[] {
    return this.resultadoSimulacao?.avisos?.filter((aviso) => !this.ehAvisoCritico(aviso)) ?? [];
  }

  private ehAvisoCritico(aviso: string): boolean {
    const texto = aviso
      .toLowerCase()
      .normalize('NFD')
      .replace(/\p{Diacritic}/gu, '');

    return (
      texto.includes('demanda estimada zerada') ||
      texto.includes('lucro total zerado') ||
      (texto.includes('demanda') && texto.includes('critica'))
    );
  }

  get percentualCustoBase(): number {
    const margem = this.resultadoSimulacao?.margemLucro ?? 0;
    const imposto = this.resultadoSimulacao?.percentualImposto ?? 0;
    const valor = 100 - margem - imposto;

    return valor < 0 ? 0 : Number(valor.toFixed(2));
  }

  get margemLucroValor(): number {
    return this.resultadoSimulacao?.margemLucro ?? 0;
  }

  get percentualImpostoValor(): number {
    return this.resultadoSimulacao?.percentualImposto ?? 0;
  }

  private classificarMargem(margem: number): 'critica' | 'moderada' | 'saudavel' {
    if (margem < 20) {
      return 'critica';
    }
    if (margem < 50) {
      return 'moderada';
    }
    return 'saudavel';
  }

  get obterTextoMargem(): string {
    const margem = this.resultadoSimulacao?.margemLucro ?? 0;
    const classificacao = this.classificarMargem(margem);

    switch (classificacao) {
      case 'critica':
        return `${margem}% é uma margem crítica - pouco lucro`;
      case 'moderada':
        return `${margem}% é uma margem moderada - viável`;
      case 'saudavel':
        return `${margem}% é uma margem saudável - boa cobertura`;
    }
  }

  get posicaoIndicadorMargem(): number {
    const margem = this.resultadoSimulacao?.margemLucro ?? 0;

    if (margem < 0) {
      return 0;
    }

    if (margem > 100) {
      return 100;
    }

    return margem;
  }
}