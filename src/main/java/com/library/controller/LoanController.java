package com.library.controller;

import com.library.dto.create.LoanCreateDTO;
import com.library.dto.update.LoanUpdateDTO;
import com.library.dto.response.LoanResponseDTO;
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
    public ResponseEntity<Page<LoanResponseDTO>> getAllLoans(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String bookTitle,
            @RequestParam(required = false) String userName) {
        System.out.println("📥 Получен запрос с параметрами:");
        System.out.println("  bookTitle: " + bookTitle);
        System.out.println("  userName: " + userName);
        Page<LoanResponseDTO> loans = loanService.searchLoans(bookTitle, userName, pageable);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/active")
    public ResponseEntity<List<LoanResponseDTO>> getActiveLoans() {
        return ResponseEntity.ok(loanService.getActiveLoans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDTO> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @PostMapping
    public ResponseEntity<LoanResponseDTO> createLoan(@RequestBody LoanCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.createLoan(dto));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<LoanResponseDTO> returnBook(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal fineAmount) {
        return ResponseEntity.ok(loanService.returnBook(id, fineAmount));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanResponseDTO> updateLoan(@PathVariable Long id, @RequestBody LoanUpdateDTO dto) {
        return ResponseEntity.ok(loanService.updateLoan(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}