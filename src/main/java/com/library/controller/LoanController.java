package com.library.controller;

import com.library.dto.LoanDTO;
import com.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LoanController {
    private final LoanService loanService;

    @GetMapping
    public ResponseEntity<Page<LoanDTO>> getAllLoans(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(loanService.getAllLoans(pageable));
    }

    @GetMapping("/active")
    public ResponseEntity<List<LoanDTO>> getActiveLoans() {
        return ResponseEntity.ok(loanService.getActiveLoans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@RequestBody LoanDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.createLoan(dto));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<LoanDTO> returnBook(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal fineAmount) {
        return ResponseEntity.ok(loanService.returnBook(id, fineAmount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}