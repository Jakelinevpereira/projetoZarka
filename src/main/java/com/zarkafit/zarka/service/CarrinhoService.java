package com.zarkafit.zarka.service;

import com.zarkafit.zarka.entity.Carrinho;
import com.zarkafit.zarka.entity.ItemCarrinho;
import com.zarkafit.zarka.entity.Produto;
import com.zarkafit.zarka.entity.Usuario;
import com.zarkafit.zarka.repository.CarrinhoRepository;
import com.zarkafit.zarka.repository.ItemCarrinhoRepository;
import com.zarkafit.zarka.repository.ProdutoRepository;
import com.zarkafit.zarka.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CarrinhoService {
    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                           ProdutoRepository produtoRepository,
                           ItemCarrinhoRepository itemCarrinhoRepository,
                           UsuarioRepository usuarioRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Buscar ou criar carrinho
    private Carrinho getCarrinho(Usuario usuario) {
        return carrinhoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Carrinho novo = new Carrinho();
                    novo.setUsuario(usuario);
                    return carrinhoRepository.save(novo);
                });
    }

    // Adicionar produto
    public void adicionarProduto(Long usuarioId, Long produtoId, Integer quantidade) {
        validarQuantidade(quantidade);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Carrinho carrinho = getCarrinho(usuario);

        // verifica se já existe
        ItemCarrinho item = itemCarrinhoRepository
                .findByCarrinhoAndProduto(carrinho, produto)
                .orElse(null);

        if (item != null) {
            item.setQuantidade(item.getQuantidade() + quantidade);
        } else {
            item = new ItemCarrinho();
            item.setCarrinho(carrinho);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
        }

        itemCarrinhoRepository.save(item);
    }

    //  Remover produto do carrinho
    public void removerProduto(Long usuarioId, Long produtoId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Carrinho carrinho = getCarrinho(usuario);

        ItemCarrinho item = itemCarrinhoRepository
                .findByCarrinhoAndProduto(carrinho, produto)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        itemCarrinhoRepository.delete(item);
    }

    //  Atualizar quantidade
    public void atualizarQuantidade(Long usuarioId, Long produtoId, Integer quantidade) {
        validarQuantidade(quantidade);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Carrinho carrinho = getCarrinho(usuario);

        ItemCarrinho item = itemCarrinhoRepository
                .findByCarrinhoAndProduto(carrinho, produto)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        item.setQuantidade(quantidade);

        itemCarrinhoRepository.save(item);
    }

    //  Listar carrinho
    public List<ItemCarrinho> listarCarrinho(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Carrinho carrinho = getCarrinho(usuario);

        return itemCarrinhoRepository.findByCarrinho(carrinho);
    }

    // Limpar carrinho
    public void limparCarrinho(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Carrinho carrinho = getCarrinho(usuario);

        List<ItemCarrinho> itens = itemCarrinhoRepository.findByCarrinho(carrinho);

        itemCarrinhoRepository.deleteAll(itens);
    }

    private void validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new RuntimeException("Quantidade inválida");
        }
    }
}
