package com.felipeboos.gestao_produtos.controller;

import com.felipeboos.gestao_produtos.entity.Produto;
import com.felipeboos.gestao_produtos.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarProdutoPorId(id));
    }

    @GetMapping
    public ResponseEntity<Produto> buscarProdutoPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(produtoService.buscarProdutoPorNome(nome));
    }

    @PostMapping
    public ResponseEntity<Produto> salvarProduto(@RequestBody Produto produto) {
        Produto produtoSalvo = produtoService.salvarProduto(produto);
        return ResponseEntity.status(201).body(produtoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarProdutoPorId(@PathVariable Long id,
                                                      @RequestBody Produto produto) {
        produtoService.atualizarProdutoPorId(id, produto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Produto> deletarProdutoPorId(@RequestParam Long id) {
        produtoService.deletarProdutoPorId(id);
        return ResponseEntity.noContent().build();
    }
}
