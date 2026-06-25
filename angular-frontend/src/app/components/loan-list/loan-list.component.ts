import { Component, OnInit } from '@angular/core';
import { LoanService } from '../../services/loan.service';
import { Loan } from '../../models/loan.model';
import { PageResponse } from '../../models/pagination.model';

@Component({
  selector: 'app-loan-list',
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.css']
})
export class LoanListComponent implements OnInit {
  loans: Loan[] = [];
  loading = true;
  error = '';
  page = 0;
  totalPages = 0;
  totalElements = 0;
  
  bookTitleFilter = '';
  userNameFilter = '';
  appliedBookTitle = '';
  appliedUserName = '';

  constructor(private loanService: LoanService) {}

  ngOnInit(): void {
    this.fetchLoans();
  }

  fetchLoans(): void {
    this.loading = true;
    const params: any = { page: this.page, size: 10 };
    if (this.appliedBookTitle) {
      params.bookTitle = this.appliedBookTitle;
    }
    if (this.appliedUserName) {
      params.userName = this.appliedUserName;
    }
    console.log('📤 Отправка параметров:', params);
    this.loanService.getAll(params).subscribe({
      next: (response: PageResponse<Loan>) => {
        console.log('📥 Получен ответ:', response);
        this.loans = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.error = '';
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Ошибка загрузки данных';
        console.error(err);
        this.loading = false;
      }
    });
  }

  applyBookTitleFilter(): void {
    this.appliedBookTitle = this.bookTitleFilter.trim();
    this.page = 0;
    this.fetchLoans();
  }

  applyUserNameFilter(): void {
    this.appliedUserName = this.userNameFilter.trim();
    this.page = 0;
    this.fetchLoans();
  }

  resetBookTitleFilter(): void {
    this.bookTitleFilter = '';
    this.appliedBookTitle = '';
    this.page = 0;
    this.fetchLoans();
  }

  resetUserNameFilter(): void {
    this.userNameFilter = '';
    this.appliedUserName = '';
    this.page = 0;
    this.fetchLoans();
  }

  resetAllFilters(): void {
    this.bookTitleFilter = '';
    this.userNameFilter = '';
    this.appliedBookTitle = '';
    this.appliedUserName = '';
    this.page = 0;
    this.fetchLoans();
  }

  deleteLoan(id: number): void {
    if (confirm('Вы уверены, что хотите удалить эту запись?')) {
      this.loanService.delete(id).subscribe({
        next: () => this.fetchLoans(),
        error: (err) => {
          this.error = 'Ошибка удаления';
          console.error(err);
        }
      });
    }
  }

  returnBook(id: number): void {
    this.loanService.returnBook(id).subscribe({
      next: () => this.fetchLoans(),
      error: (err) => {
        this.error = 'Ошибка при возврате книги';
        console.error(err);
      }
    });
  }

  getStatusBadge(status: string): string {
    const map: { [key: string]: string } = {
      'ACTIVE': 'bg-success',
      'RETURNED': 'bg-secondary',
      'OVERDUE': 'bg-danger'
    };
    return `badge ${map[status] || 'bg-info'}`;
  }

  changePage(page: number): void {
    this.page = page;
    this.fetchLoans();
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const total = Math.min(this.totalPages, 7);
    let start = 0;
    if (this.totalPages > 7) {
      if (this.page < 3) {
        start = 0;
      } else if (this.page > this.totalPages - 4) {
        start = this.totalPages - 7;
      } else {
        start = this.page - 3;
      }
    }
    for (let i = 0; i < total; i++) {
      pages.push(start + i);
    }
    return pages;
  }
}