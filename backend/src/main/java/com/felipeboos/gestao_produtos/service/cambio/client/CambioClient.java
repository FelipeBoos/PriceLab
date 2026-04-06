package com.felipeboos.gestao_produtos.service.cambio.client;

import com.felipeboos.gestao_produtos.service.cambio.dto.CambioApiResponseDTO;
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
        CambioApiResponseDTO response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/rates")
                        .queryParam("base", "BRL")
                        .queryParam("symbols", moedaDestino)
                        .build())
                .retrieve()
                .body(CambioApiResponseDTO.class);

        if (response == null || response.getRates() == null) {
            throw new RuntimeException("Resposta inválida da API de câmbio");
        }

        Map<String, BigDecimal> rates = response.getRates();
        BigDecimal cotacao = rates.get(moedaDestino);

        if (cotacao == null) {
            throw new RuntimeException("Cotação não encontrada para a moeda: " + moedaDestino);
        }

        return cotacao;
    }
}