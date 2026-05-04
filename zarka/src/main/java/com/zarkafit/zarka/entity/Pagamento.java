package com.zarkafit.zarka.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    private Double valor;

    private String metodo;

    private String status;

    private LocalDateTime dataPagamento;

    @PrePersist
    void prePersist() {
        if (status == null || status.isBlank()) {
            status = "PENDENTE";
        }
        if (dataPagamento == null) {
            dataPagamento = LocalDateTime.now();
        }
    }
}
