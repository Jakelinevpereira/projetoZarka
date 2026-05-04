package com.zarkafit.zarka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zarkafit.zarka.entity.ItemCarrinho;
import com.zarkafit.zarka.service.CarrinhoService;

@RestController
@RequestMapping("/carrinho")
public class CarrinhoController {

     @Autowired
    private CarrinhoService carrinhoService;

    // Adicionar produto
    @PostMapping("/adicionar")
    public ResponseEntity<String> adicionarProduto(
            @RequestParam Long usuarioId,
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade) {

        carrinhoService.adicionarProduto(usuarioId, produtoId, quantidade);
        return ResponseEntity.ok("Produto adicionado ao carrinho");
    }

    // Remover produto
    @DeleteMapping("/remover")
    public ResponseEntity<String> removerProduto(
            @RequestParam Long usuarioId,
            @RequestParam Long produtoId) {

        carrinhoService.removerProduto(usuarioId, produtoId);
        return ResponseEntity.ok("Produto removido do carrinho");
    }

    // Atualizar quantidade
    @PutMapping("/atualizar")
    public ResponseEntity<String> atualizarQuantidade(
            @RequestParam Long usuarioId,
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade) {
                if (quantidade <= 0) {
                    return ResponseEntity.badRequest().body("Quantidade inválida");
                }

        carrinhoService.atualizarQuantidade(usuarioId, produtoId, quantidade);
        return ResponseEntity.ok("Quantidade atualizada");
    }

    // Listar carrinho
    @GetMapping
    public ResponseEntity<java.util.List<ItemCarrinho>> listarCarrinho(
            @RequestParam Long usuarioId) {

        java.util.List<ItemCarrinho> itens = carrinhoService.listarCarrinho(usuarioId);
        return ResponseEntity.ok(itens);
    }

    // Limpar carrinho
    @DeleteMapping("/limpar")
    public ResponseEntity<String> limparCarrinho(
            @RequestParam Long usuarioId) {

        carrinhoService.limparCarrinho(usuarioId);
        return ResponseEntity.ok("Carrinho limpo");
    }
    
}
