package com.desafiototvs.backend.domain.repository;


import com.desafiototvs.backend.domain.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    List<Conta> findByDataVencimentoBetween(LocalDate startDate, LocalDate endDate);
    List<Conta> findByDescricaoContaining(String descricao);
    @Query("SELECT SUM(c.valor) FROM Conta c WHERE c.dataPagamento BETWEEN :startDate AND :endDate")
    BigDecimal findTotalValorPago(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

