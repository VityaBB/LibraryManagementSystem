package com.library.models;

public class Loan {
    private int id;
    private int bookId;
    private int userId;
    private String loanDate;
    private String dueDate;
    private String returnDate;
    private String status;
    private double fineAmount;

    public Loan() {}

    public Loan(int id, int bookId, int userId, String loanDate, 
                String dueDate, String returnDate, String status, double fineAmount) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fineAmount = fineAmount;
    }

  
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getLoanDate() { return loanDate; }
    public void setLoanDate(String loanDate) { this.loanDate = loanDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    
}