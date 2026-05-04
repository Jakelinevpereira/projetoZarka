package com.zarkafit.zarka.repository;

import com.zarkafit.zarka.entity.Pagamento;
import com.zarkafit.zarka.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByPedido(Pedido pedido);
}
