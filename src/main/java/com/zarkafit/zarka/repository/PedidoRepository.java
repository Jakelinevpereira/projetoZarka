package com.zarkafit.zarka.repository;

import com.zarkafit.zarka.entity.Pedido;
import com.zarkafit.zarka.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario);
    Optional<Pedido> findByIdAndUsuario(Long id, Usuario usuario);
}
