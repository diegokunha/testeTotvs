package com.desafiototvs.backend.ui.controller;

import com.desafiototvs.backend.ui.dto.ContaRequestDTO;
import com.desafiototvs.backend.ui.dto.ContaResponseDTO;
import com.desafiototvs.backend.application.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("contas")
public class ContaController {

    private final ContaService contaService;

    @PostMapping
    public ResponseEntity<ContaResponseDTO> createConta(@RequestBody ContaRequestDTO contaRequestDTO) {
        ContaResponseDTO createdConta = contaService.createConta(contaRequestDTO);
        return new ResponseEntity<>(createdConta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> updateConta(@PathVariable Long id, @RequestBody ContaRequestDTO contaRequestDTO) {
        ContaResponseDTO updatedConta = contaService.updateConta(id, contaRequestDTO);
        return new ResponseEntity<>(updatedConta, HttpStatus.OK);
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<Void> alterarSituacao(@PathVariable Long id, @RequestParam String situacao) {
        contaService.alterarSituacao(id, situacao);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<Page<ContaResponseDTO>> getContas(Pageable pageable) {
        return new ResponseEntity<>(contaService.getContas(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> getContaById(@PathVariable Long id) {
        return new ResponseEntity<>(contaService.getContaById(id), HttpStatus.OK);
    }

    @GetMapping("/total-pago")
    public ResponseEntity<BigDecimal> getTotalValorPago(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        BigDecimal totalValorPago = contaService.getTotalValorPago(startDate, endDate);
        return new ResponseEntity<>(totalValorPago, HttpStatus.OK);
    }

    @PostMapping("/import")
    public ResponseEntity<Void> importContasFromCSV(@RequestParam("file") MultipartFile file) {
        contaService.importContasFromCSV(file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}

