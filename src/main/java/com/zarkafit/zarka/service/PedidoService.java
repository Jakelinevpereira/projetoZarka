package com.zarkafit.zarka.service;

import com.zarkafit.zarka.entity.Carrinho;
import com.zarkafit.zarka.entity.ItemCarrinho;
import com.zarkafit.zarka.entity.ItemPedido;
import com.zarkafit.zarka.entity.Pagamento;
import com.zarkafit.zarka.entity.Pedido;
import com.zarkafit.zarka.entity.Usuario;
import com.zarkafit.zarka.repository.CarrinhoRepository;
import com.zarkafit.zarka.repository.ItemCarrinhoRepository;
import com.zarkafit.zarka.repository.PedidoRepository;
import com.zarkafit.zarka.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         CarrinhoRepository carrinhoRepository,
                         ItemCarrinhoRepository itemCarrinhoRepository,
                         UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Pedido fecharPedido(Long usuarioId, String metodoPagamento) {
        Usuario usuario = buscarUsuario(usuarioId);
        Carrinho carrinho = carrinhoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrinho vazio"));

        List<ItemCarrinho> itensCarrinho = itemCarrinhoRepository.findByCarrinho(carrinho);
        if (itensCarrinho.isEmpty()) {
            throw new RuntimeException("Carrinho vazio");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);

        for (ItemCarrinho itemCarrinho : itensCarrinho) {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProduto(itemCarrinho.getProduto());
            itemPedido.setQuantidade(itemCarrinho.getQuantidade());
            itemPedido.setPrecoUnitario(itemCarrinho.getProduto().getPreco() != null
                    ? itemCarrinho.getProduto().getPreco()
                    : 0.0);
            itemPedido.atualizarSubtotal();
            pedido.adicionarItem(itemPedido);
        }

        pedido.atualizarTotal();

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setValor(pedido.getTotal());
        pagamento.setMetodo(normalizarMetodoPagamento(metodoPagamento));
        pagamento.setStatus("PENDENTE");
        pedido.setPagamento(pagamento);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        itemCarrinhoRepository.deleteAll(itensCarrinho);
        return pedidoSalvo;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidos(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        return pedidoRepository.findByUsuarioOrderByDataCriacaoDesc(usuario);
    }

    @Transactional(readOnly = true)
    public Pedido buscarPedido(Long usuarioId, Long pedidoId) {
        Usuario usuario = buscarUsuario(usuarioId);
        return pedidoRepository.findByIdAndUsuario(pedidoId, usuario)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private String normalizarMetodoPagamento(String metodoPagamento) {
        if (metodoPagamento == null || metodoPagamento.isBlank()) {
            return "NAO_INFORMADO";
        }
        return metodoPagamento.trim().toUpperCase();
    }
}
