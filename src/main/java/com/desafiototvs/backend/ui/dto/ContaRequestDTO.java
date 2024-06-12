package com.desafiototvs.backend.ui.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContaRequestDTO {
    LocalDate dataVencimento;
    LocalDate dataPagamento;
    BigDecimal valor;
    String descricao;
    String situacao;
}
