import { Component } from '@angular/core';

@Component({
  selector: 'app-login',
  imports: [],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  mensagemToast: string | null = null;
  private toastTimeoutId: ReturnType<typeof setTimeout> | null = null;

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

  botaoEntrar(): void {
    this.exibirToast('Botão entrar: ainda não implementado.');
  }

  botaoCriarConta(): void {
    this.exibirToast('Botão criar conta: ainda não implementado.');
  }
}
