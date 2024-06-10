package com.desafiototvs.backend.services;

import com.desafiototvs.backend.model.Conta;
import com.desafiototvs.backend.repositories.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaService {

    private static final String NO_FOUND = "Conta not found";
    public static final String FAILED_TO_PARSE_CSV_FILE = "Failed to parse CSV file: ";

    private final ContaRepository contaRepository;

    public Conta createConta(Conta conta) {
        return contaRepository.save(conta);
    }

    public Conta updateConta(Long id, Conta conta) {
        Conta existingConta = contaRepository.findById(id).orElseThrow(() -> new RuntimeException(NO_FOUND));
        existingConta.setDataVencimento(conta.getDataVencimento());
        existingConta.setDataPagamento(conta.getDataPagamento());
        existingConta.setValor(conta.getValor());
        existingConta.setDescricao(conta.getDescricao());
        existingConta.setSituacao(conta.getSituacao());
        return contaRepository.save(existingConta);
    }

    public void alterarSituacao(Long id, String situacao) {
        Conta conta = contaRepository.findById(id).orElseThrow(() -> new RuntimeException(NO_FOUND));
        conta.setSituacao(situacao);
        contaRepository.save(conta);
    }

    public Page<Conta> getContas(Pageable pageable) {
        return contaRepository.findAll(pageable);
    }

    public Conta getContaById(Long id) {
        return contaRepository.findById(id).orElseThrow(() -> new RuntimeException(NO_FOUND));
    }

    public BigDecimal getTotalValorPago(LocalDate startDate, LocalDate endDate) {
        return contaRepository.findTotalValorPago(startDate, endDate);
    }

    public void importContasFromCSV(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<Conta> contas = csvParser.getRecords().stream().map(this::csvRecordToConta).collect(Collectors.toList());
            contaRepository.saveAll(contas);

        } catch (IOException e) {
            throw new RuntimeException(FAILED_TO_PARSE_CSV_FILE + e.getMessage());
        }
    }

    private Conta csvRecordToConta(CSVRecord csvRecord) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return new Conta(
                null,
                LocalDate.parse(csvRecord.get("data_vencimento"), formatter),
                LocalDate.parse(csvRecord.get("data_pagamento"), formatter),
                new BigDecimal(csvRecord.get("valor")),
                csvRecord.get("descricao"),
                csvRecord.get("situacao")
        );
    }
}

