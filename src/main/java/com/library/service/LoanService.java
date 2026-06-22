package com.library.service;

import com.library.dto.LoanDTO;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public Page<LoanDTO> getAllLoans(Pageable pageable) {
        return loanRepository.findAll(pageable).map(this::convertToDTO);
    }

    public List<LoanDTO> getActiveLoans() {
        return loanRepository.findByUserIdAndStatusIn(null, List.of("ACTIVE", "OVERDUE"))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LoanDTO getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Выдача не найдена"));
        return convertToDTO(loan);
    }

    @Transactional
    public LoanDTO createLoan(LoanDTO dto) {
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(30));
        loan.setStatus("ACTIVE");
        loan.setFineAmount(BigDecimal.ZERO);

        return convertToDTO(loanRepository.save(loan));
    }

    @Transactional
    public LoanDTO returnBook(Long id, BigDecimal fineAmount) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Выдача не найдена"));

        if (loan.getStatus().equals("ACTIVE") || loan.getStatus().equals("OVERDUE")) {
            loan.setStatus("RETURNED");
            loan.setReturnDate(LocalDate.now());
            if (fineAmount != null) {
                loan.setFineAmount(fineAmount);
            }
        }

        return convertToDTO(loanRepository.save(loan));
    }

    @Transactional
    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }

    private LoanDTO convertToDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setBookId(loan.getBook().getId());
        dto.setBookTitle(loan.getBook().getTitle());
        dto.setUserId(loan.getUser().getId());
        dto.setUserName(loan.getUser().getFullName());
        dto.setLoanDate(loan.getLoanDate());
        dto.setDueDate(loan.getDueDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setStatus(loan.getStatus());
        dto.setFineAmount(loan.getFineAmount());
        return dto;
    }
}