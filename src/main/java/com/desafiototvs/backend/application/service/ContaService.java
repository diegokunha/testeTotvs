package com.desafiototvs.backend.application.service;

import com.desafiototvs.backend.ui.dto.ContaRequestDTO;
import com.desafiototvs.backend.ui.dto.ContaResponseDTO;
import com.desafiototvs.backend.domain.model.Conta;
import com.desafiototvs.backend.domain.repository.ContaRepository;
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
    public static final String UTF_8 = "UTF-8";

    private final ContaRepository contaRepository;

    public ContaResponseDTO createConta(ContaRequestDTO contaRequestDTO) {
        Conta conta = convertToEntity(contaRequestDTO);
        Conta savedConta = contaRepository.save(conta);
        return convertToDTO(savedConta);
    }

    public ContaResponseDTO updateConta(Long id, ContaRequestDTO contaRequestDTO) {
        Conta conta = contaRepository.findById(id).orElseThrow(() -> new RuntimeException(NO_FOUND));
        conta.setDataVencimento(contaRequestDTO.getDataVencimento());
        conta.setDataPagamento(contaRequestDTO.getDataPagamento());
        conta.setValor(contaRequestDTO.getValor());
        conta.setDescricao(contaRequestDTO.getDescricao());
        conta.setSituacao(contaRequestDTO.getSituacao());
        Conta updatedConta = contaRepository.save(conta);
        return convertToDTO(updatedConta);
    }

    public void alterarSituacao(Long id, String situacao) {
        Conta conta = contaRepository.findById(id).orElseThrow(() -> new RuntimeException(NO_FOUND));
        conta.setSituacao(situacao);
        contaRepository.save(conta);
    }

    public Page<ContaResponseDTO> getContas(Pageable pageable) {
        Page<Conta> contasPage = contaRepository.findAll(pageable);
        return contasPage.map(this::convertToDTO);
    }

    public ContaResponseDTO getContaById(Long id) {
        Conta conta = contaRepository.findById(id).orElseThrow(() -> new RuntimeException(NO_FOUND));
        return convertToDTO(conta);
    }

    public BigDecimal getTotalValorPago(LocalDate startDate, LocalDate endDate) {
        return contaRepository.findTotalValorPago(startDate, endDate);
    }

    public void importContasFromCSV(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), UTF_8));
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

    private Conta convertToEntity(ContaRequestDTO contaRequestDTO) {
        Conta conta = new Conta();
        conta.setDataVencimento(contaRequestDTO.getDataVencimento());
        conta.setDataPagamento(contaRequestDTO.getDataPagamento());
        conta.setValor(contaRequestDTO.getValor());
        conta.setDescricao(contaRequestDTO.getDescricao());
        conta.setSituacao(contaRequestDTO.getSituacao());
        return conta;
    }

    private ContaResponseDTO convertToDTO(Conta conta) {
        ContaResponseDTO contaResponseDTO = new ContaResponseDTO();
        contaResponseDTO.setId(conta.getId());
        contaResponseDTO.setDataVencimento(conta.getDataVencimento());
        contaResponseDTO.setDataPagamento(conta.getDataPagamento());
        contaResponseDTO.setValor(conta.getValor());
        contaResponseDTO.setDescricao(conta.getDescricao());
        contaResponseDTO.setSituacao(conta.getSituacao());
        return contaResponseDTO;
    }
}

