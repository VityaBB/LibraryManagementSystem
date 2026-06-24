package com.library.service;

import com.library.dto.create.LoanCreateDTO;
import com.library.dto.update.LoanUpdateDTO;
import com.library.dto.response.LoanResponseDTO;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public Page<LoanResponseDTO> getAllLoans(Pageable pageable) {
        return loanRepository.findAll(pageable).map(this::convertToResponseDTO);
    }

    public List<LoanResponseDTO> getActiveLoans() {
        return loanRepository.findByUserIdAndStatusIn(null, List.of("ACTIVE", "OVERDUE"))
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public LoanResponseDTO getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Выдача не найдена"));
        return convertToResponseDTO(loan);
    }

    @Transactional
    public LoanResponseDTO createLoan(LoanCreateDTO dto) {
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        long activeLoansCount = loanRepository.countByBookIdAndStatusIn(book.getId(), List.of("ACTIVE", "OVERDUE"));
        int availableCopies = book.getTotalCopies() - (int) activeLoansCount;

        if (availableCopies <= 0) {
            throw new RuntimeException("Нет доступных экземпляров книги. Доступно: " + availableCopies);
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(30));
        loan.setStatus("ACTIVE");
        loan.setFineAmount(BigDecimal.ZERO);

        return convertToResponseDTO(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponseDTO returnBook(Long id, BigDecimal fineAmount) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Выдача не найдена"));

        if (loan.getStatus().equals("ACTIVE") || loan.getStatus().equals("OVERDUE")) {
            loan.setStatus("RETURNED");
            loan.setReturnDate(LocalDate.now());
            if (fineAmount != null) {
                loan.setFineAmount(fineAmount);
            }
        }

        return convertToResponseDTO(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponseDTO updateLoan(Long id, LoanUpdateDTO dto) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Выдача не найдена"));

        if (dto.getBookId() != null) {
            Book book = bookRepository.findById(dto.getBookId())
                    .orElseThrow(() -> new RuntimeException("Книга не найдена"));
            loan.setBook(book);
        }

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            loan.setUser(user);
        }

        if (dto.getStatus() != null) {
            loan.setStatus(dto.getStatus());
        }

        if (dto.getFineAmount() != null) {
            loan.setFineAmount(dto.getFineAmount());
        }

        return convertToResponseDTO(loanRepository.save(loan));
    }

    @Transactional
    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }

    private LoanResponseDTO convertToResponseDTO(Loan loan) {
        LoanResponseDTO dto = new LoanResponseDTO();
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