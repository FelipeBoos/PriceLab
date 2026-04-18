package com.felipeboos.gestao_produtos.controller;

import com.felipeboos.gestao_produtos.dto.produto.ProdutoRequestDTO;
import com.felipeboos.gestao_produtos.dto.produto.ProdutoCotacaoResponseDTO;
import com.felipeboos.gestao_produtos.dto.produto.ProdutoResponseDTO;
import com.felipeboos.gestao_produtos.dto.produto.ProdutoUpdateDTO;
import com.felipeboos.gestao_produtos.entity.Moeda;
import com.felipeboos.gestao_produtos.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarProdutoPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutos(
            @RequestParam(required=false) String nome) {
        if (nome != null) {
            return ResponseEntity.ok(produtoService.buscarProdutoPorNome(nome));
        }

        return ResponseEntity.ok(produtoService.listarTodosOsProdutos());
    }

    @GetMapping("/cotacao-atual")
    public ResponseEntity<ProdutoCotacaoResponseDTO> buscarCotacaoAtual(@RequestParam Moeda moeda) {
        return ResponseEntity.ok(produtoService.buscarCotacaoAtual(moeda));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> salvarProduto(@RequestBody @Valid ProdutoRequestDTO produto) {
        ProdutoResponseDTO produtoSalvo = produtoService.salvarProduto(produto);
        return ResponseEntity.status(201).body(produtoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarProdutoPorId(@PathVariable Long id,
                                                      @RequestBody @Valid ProdutoUpdateDTO produto) {
        produtoService.atualizarProdutoPorId(id, produto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProdutoPorId(@PathVariable Long id) {
        produtoService.deletarProdutoPorId(id);
        return ResponseEntity.noContent().build();
    }
}
