package com.felipeboos.gestao_produtos.controller;

import com.felipeboos.gestao_produtos.dto.categoria.CategoriaResponseDTO;
import com.felipeboos.gestao_produtos.dto.categoria.CategoriaUpdateDTO;
import com.felipeboos.gestao_produtos.service.CategoriaService;
import com.felipeboos.gestao_produtos.dto.categoria.CategoriaRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;


    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarCategoriaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarCategoriaPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias(
            @RequestParam(required = false) String nome) {

        if (nome != null) {
            return ResponseEntity.ok(categoriaService.buscarCategoriaPorNome(nome));
        }

        return ResponseEntity.ok(categoriaService.listarTodasAsCategorias());
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> salvarCategoria(@RequestBody @Valid CategoriaRequestDTO dto) {
        CategoriaResponseDTO categoriaSalva = categoriaService.salvarCategoria(dto);
        return ResponseEntity.status(201).body(categoriaSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarCategoriaPorId(@PathVariable Long id,
                                                        @RequestBody @Valid CategoriaUpdateDTO categoria) {
        categoriaService.atualizarCategoriaPorId(id, categoria);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPorId(@PathVariable Long id) {
        categoriaService.deletarCategoriaPorId(id);
        return ResponseEntity.noContent().build();
    }
}
