package com.zarkafit.zarka.controller;

import com.zarkafit.zarka.entity.Pedido;
import com.zarkafit.zarka.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/fechar")
    public ResponseEntity<Pedido> fecharPedido(@RequestParam Long usuarioId,
                                               @RequestParam(required = false) String metodoPagamento) {
        return ResponseEntity.ok(pedidoService.fecharPedido(usuarioId, metodoPagamento));
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(pedidoService.listarPedidos(usuarioId));
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<Pedido> buscarPedido(@RequestParam Long usuarioId,
                                               @PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.buscarPedido(usuarioId, pedidoId));
    }
}
