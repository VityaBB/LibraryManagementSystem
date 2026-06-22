package com.library.menu;

import com.library.dao.LoanDAO;
import com.library.dao.BookDAO;
import com.library.dao.UserDAO;
import com.library.models.Loan;
import com.library.models.Book;
import com.library.models.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static com.library.menu.ConsoleUtils.*;

public class LoanMenu {
    private final LoanDAO loanDAO;
    private final BookDAO bookDAO;
    private final UserDAO userDAO;

    public LoanMenu(Connection connection) {
        this.loanDAO = new LoanDAO(connection);
        this.bookDAO = new BookDAO(connection);
        this.userDAO = new UserDAO(connection);
    }

    public void showMenu() {
        while (true) {
            printHeader("УПРАВЛЕНИЕ ВЫДАЧАМИ");
            System.out.println("1. Добавить выдачу");
            System.out.println("2. Список всех выдач");
            System.out.println("3. Активные выдачи");
            System.out.println("4. Вернуть книгу");
            System.out.println("5. Удалить выдачу");
            System.out.println("0. Назад");
            printSeparator();

            int choice = readInt("Выберите действие: ", 0, 5);
            try {
                switch (choice) {
                    case 1 -> addLoan();
                    case 2 -> listAllLoans();
                    case 3 -> listActiveLoans();
                    case 4 -> returnBook();
                    case 5 -> deleteLoan();
                    case 0 -> { return; }
                    default -> printError("Неверный выбор");
                }
            } catch (SQLException e) {
                printError("Ошибка БД: " + e.getMessage());
            }
        }
    }

    private void addLoan() throws SQLException {
        printHeader("НОВАЯ ВЫДАЧА КНИГИ");
        int bookId = readInt("ID книги: ", 1, Integer.MAX_VALUE);

        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            printError("Книга с ID " + bookId + " не найдена");
            return;
        }

        int available = bookDAO.getAvailableCopies(bookId);
        if (available <= 0) {
            printError("Нет доступных экземпляров книги \"" + book.getTitle() + "\"");
            return;
        }

        int userId = readInt("ID пользователя: ", 1, Integer.MAX_VALUE);
        User user = userDAO.getUserById(userId);
        if (user == null) {
            printError("Пользователь с ID " + userId + " не найден");
            return;
        }

        List<Loan> userLoans = loanDAO.getActiveLoansByUserAndBook(userId, bookId);
        if (!userLoans.isEmpty()) {
            printError("Пользователь уже взял эту книгу и ещё не вернул");
            return;
        }

        Loan loan = new Loan();
        loan.setBookId(bookId);
        loan.setUserId(userId);

        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(30);

        loan.setLoanDate(loanDate.toString());
        loan.setDueDate(dueDate.toString());
        loan.setStatus("ACTIVE");

        loanDAO.addLoan(loan);
        printSuccess("Книга выдана! ID выдачи: " + loan.getId());
        printInfo("Дата возврата: " + loan.getDueDate());
        printInfo("Осталось доступных экземпляров: " + (available - 1));
    }

    private void listAllLoans() throws SQLException {
        printHeader("СПИСОК ВСЕХ ВЫДАЧ");
        List<Loan> loans = loanDAO.getAllLoans();
        if (loans.isEmpty()) {
            printInfo("Выдач не найдено");
            return;
        }
        System.out.println("┌────┬──────────┬────────────┬────────────┬────────────┬────────────┬──────────────┐");
        System.out.println("│ ID │ Книга    │ Читатель   │ Дата выдачи│ Срок       │ Статус     │ Штраф        │");
        System.out.println("├────┼──────────┼────────────┼────────────┼────────────┼────────────┼──────────────┤");
        for (Loan l : loans) {
            System.out.printf("│ %-2d │ %-8d │ %-10d │ %-10s │ %-10s │ %-10s │ %-12.2f │%n",
                l.getId(), l.getBookId(), l.getUserId(), l.getLoanDate(), l.getDueDate(), l.getStatus(), l.getFineAmount());
        }
        System.out.println("└────┴──────────┴────────────┴────────────┴────────────┴────────────┴──────────────┘");
        printInfo("Всего выдач: " + loans.size());
    }

    private void listActiveLoans() throws SQLException {
        printHeader("АКТИВНЫЕ ВЫДАЧИ");
        List<Loan> loans = loanDAO.getActiveLoans();
        if (loans.isEmpty()) {
            printInfo("Нет активных выдач");
            return;
        }
        System.out.println("┌────┬──────────┬────────────┬────────────┬────────────┬──────────────┐");
        System.out.println("│ ID │ Книга    │ Читатель   │ Дата выдачи│ Срок       │ Штраф        │");
        System.out.println("├────┼──────────┼────────────┼────────────┼────────────┼──────────────┤");
        for (Loan l : loans) {
            System.out.printf("│ %-2d │ %-8d │ %-10d │ %-10s │ %-10s │ %-12.2f │%n",
                l.getId(), l.getBookId(), l.getUserId(), l.getLoanDate(), l.getDueDate(), l.getFineAmount());
        }
        System.out.println("└────┴──────────┴────────────┴────────────┴────────────┴──────────────┘");
        printInfo("Всего активных выдач: " + loans.size());
    }

    private void returnBook() throws SQLException {
        printHeader("ВОЗВРАТ КНИГИ");
        int loanId = readInt("Введите ID выдачи: ", 1, Integer.MAX_VALUE);

        Loan loan = loanDAO.getLoanById(loanId);
        if (loan == null) {
            printError("Выдача с ID " + loanId + " не найдена");
            return;
        }

        if (!loan.getStatus().equals("ACTIVE") && !loan.getStatus().equals("OVERDUE")) {
            printError("Эта выдача уже завершена. Статус: " + loan.getStatus());
            return;
        }

        printInfo("Информация о выдаче:");
        System.out.println("  Книга ID: " + loan.getBookId());
        System.out.println("  Пользователь ID: " + loan.getUserId());
        System.out.println("  Дата выдачи: " + loan.getLoanDate());
        System.out.println("  Срок возврата: " + loan.getDueDate());
        System.out.println("  Статус: " + loan.getStatus());

        LocalDate today = LocalDate.now();
        LocalDate dueDate = LocalDate.parse(loan.getDueDate());

        double fine = 0;
        if (dueDate.isBefore(today)) {
            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
            double autoFine = daysOverdue * 0.50;

            System.out.println("\nКнига просрочена на " + daysOverdue + " дней");
            System.out.println("  Автоматический штраф: " + autoFine + " руб. (0.50 руб/день)");
            System.out.println("  Введите 0 для автоматического расчёта");

            double userFine = readDouble("  Введите сумму штрафа: ");
            if (userFine == 0) {
                fine = autoFine;
                printInfo("Использован автоматический расчёт: " + fine + " руб.");
            } else if (userFine > 0) {
                fine = userFine;
            } else {
                printError("Штраф не может быть отрицательным. Использован авторасчёт.");
                fine = autoFine;
            }
        } else {
            System.out.println("\nКнига возвращена вовремя, штраф не назначается");
            fine = 0;
        }

        loanDAO.returnBook(loanId, fine);
        int available = bookDAO.getAvailableCopies(loan.getBookId());
        printSuccess("Книга возвращена! Штраф: " + fine + " руб.");
        printInfo("Доступно экземпляров: " + available);
    }

    private void deleteLoan() throws SQLException {
        printHeader("УДАЛЕНИЕ ВЫДАЧИ");
        int id = readInt("Введите ID выдачи: ", 1, Integer.MAX_VALUE);
        if (readYesNo("Вы уверены? (y/n): ")) {
            loanDAO.deleteLoan(id);
            printSuccess("Выдача удалена!");
        } else {
            printInfo("Операция отменена");
        }
    }
}