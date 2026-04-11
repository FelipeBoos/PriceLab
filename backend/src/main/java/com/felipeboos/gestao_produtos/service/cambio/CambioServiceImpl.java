package com.felipeboos.gestao_produtos.service.cambio;

import com.felipeboos.gestao_produtos.entity.Moeda;
import com.felipeboos.gestao_produtos.service.cambio.CambioService;
import com.felipeboos.gestao_produtos.service.cambio.client.CambioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CambioServiceImpl implements CambioService {

    private final CambioClient cambioClient;

    @Override
    public BigDecimal obterCotacao(Moeda moeda) {

        if (moeda == null || moeda == Moeda.BRL) {
            return BigDecimal.ONE;
        }

        return cambioClient.buscarCotacao(moeda.name());
    }
}