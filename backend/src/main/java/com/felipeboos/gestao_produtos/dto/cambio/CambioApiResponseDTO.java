package com.felipeboos.gestao_produtos.dto.cambio;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Setter
@Getter
public class CambioApiResponseDTO {

    private String base;
    private String date;
    private Map<String, BigDecimal> rates;

}