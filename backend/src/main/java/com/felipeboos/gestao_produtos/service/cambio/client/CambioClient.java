package com.felipeboos.gestao_produtos.service.cambio.client;

import com.felipeboos.gestao_produtos.dto.cambio.CambioApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CambioClient {

    private final RestClient restClient;

    public BigDecimal buscarCotacao(String moedaDestino) {
        System.out.println(">>> Chamando API de câmbio para: " + moedaDestino);
        try {
            CambioApiResponseDTO response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/latest")
                            .queryParam("base", moedaDestino)
                            .queryParam("symbols", "BRL")
                            .build())
                    .retrieve()
                    .body(CambioApiResponseDTO.class);

            System.out.println(">>> Response recebido: " + response);
            System.out.println(">>> Rates: " + (response != null ? response.getRates() : "null"));

            if (response == null || response.getRates() == null) {
                throw new RuntimeException("Resposta inválida da API de câmbio");
            }

            Map<String, BigDecimal> rates = response.getRates();
            BigDecimal cotacao = rates.get("BRL");

            System.out.println(">>> Cotação BRL: " + cotacao);

            if (cotacao == null) {
                throw new RuntimeException("Cotação não encontrada para a moeda: " + moedaDestino);
            }

            return cotacao;

        } catch (Exception e) {
            System.out.println(">>> ERRO na chamada: " + e.getClass().getName() + " - " + e.getMessage());
            throw e;
        }
    }
}