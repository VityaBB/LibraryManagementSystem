export interface Loan {
  id?: number;
  bookId: number;
  bookTitle?: string;
  userId: number;
  userName?: string;
  loanDate: string;
  dueDate: string;
  returnDate?: string;
  status: 'ACTIVE' | 'RETURNED' | 'OVERDUE';
  fineAmount: number;
}

export interface LoanCreateDTO {
  bookId: number;
  userId: number;
}

export interface LoanUpdateDTO {
  bookId?: number;
  userId?: number;
  status?: 'ACTIVE' | 'RETURNED' | 'OVERDUE';
  fineAmount?: number;
}