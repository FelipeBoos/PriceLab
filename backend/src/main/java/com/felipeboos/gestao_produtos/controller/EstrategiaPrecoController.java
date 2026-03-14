package com.felipeboos.gestao_produtos.controller;

import com.felipeboos.gestao_produtos.dto.estrategiapreco.EstrategiaPrecoRequestDTO;
import com.felipeboos.gestao_produtos.dto.estrategiapreco.EstrategiaPrecoResponseDTO;
import com.felipeboos.gestao_produtos.service.EstrategiaPrecoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estrategias-preco")
@RequiredArgsConstructor
public class EstrategiaPrecoController {

    private final EstrategiaPrecoService service;

    @GetMapping("/{id}")
    public ResponseEntity<EstrategiaPrecoResponseDTO> buscarEstrategiaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarEstrategiaPorId(id));
    }

    @GetMapping("/produto/{id}")
    public ResponseEntity<List<EstrategiaPrecoResponseDTO>> buscarEstrategiaPorProdutoId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarEstrategiaPorProdutoId(id));
    }

    @GetMapping
    public ResponseEntity<List<EstrategiaPrecoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodasAsEstrategiasDePreco());
    }

    @PostMapping("/simular")
    public ResponseEntity<EstrategiaPrecoResponseDTO> simularEstrategiaPreco(
            @RequestBody @Valid EstrategiaPrecoRequestDTO request) {
        return ResponseEntity.ok(service.simularPreco(request));
    }

    @PostMapping
    public ResponseEntity<EstrategiaPrecoResponseDTO> salvarEstrategiaPreco(
            @RequestBody @Valid EstrategiaPrecoRequestDTO request) {
        return ResponseEntity.status(201).body(service.criarEstrategiaPreco(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEstrategiaPorId(@PathVariable Long id) {
        service.deletarEstrategiaPorId(id);
        return ResponseEntity.noContent().build();
    }
}
