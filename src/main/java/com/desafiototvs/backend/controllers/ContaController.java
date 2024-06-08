package com.desafiototvs.backend.controllers;

import com.desafiototvs.backend.model.Conta;
import com.desafiototvs.backend.services.ContaService;
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
    public ResponseEntity<Conta> createConta(@RequestBody Conta conta) {
        Conta createdConta = contaService.createConta(conta);
        return new ResponseEntity<>(createdConta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conta> updateConta(@PathVariable Long id, @RequestBody Conta conta) {
        Conta updatedConta = contaService.updateConta(id, conta);
        return new ResponseEntity<>(updatedConta, HttpStatus.OK);
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<Void> alterarSituacao(@PathVariable Long id, @RequestParam String situacao) {
        contaService.alterarSituacao(id, situacao);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<Page<Conta>> getContas(Pageable pageable) {
        Page<Conta> contas = contaService.getContas(pageable);
        return new ResponseEntity<>(contas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conta> getContaById(@PathVariable Long id) {
        Conta conta = contaService.getContaById(id);
        return new ResponseEntity<>(conta, HttpStatus.OK);
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

