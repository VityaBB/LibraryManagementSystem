import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoanService } from '../../services/loan.service';
import { BookService } from '../../services/book.service';
import { UserService } from '../../services/user.service';
import { LoanCreateDTO, LoanUpdateDTO } from '../../models/loan.model';
import { Book } from '../../models/book.model';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-loan-form',
  templateUrl: './loan-form.component.html',
  styleUrls: ['./loan-form.component.css']
})
export class LoanFormComponent implements OnInit {
  isEdit = false;
  id?: number;
  loading = false;
  error = '';
  books: Book[] = [];
  users: User[] = [];
  formData: LoanCreateDTO & LoanUpdateDTO = {
    bookId: 0,
    userId: 0,
    status: 'ACTIVE',
    fineAmount: 0
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private loanService: LoanService,
    private bookService: BookService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.isEdit = !!this.id;
    this.fetchBooksAndUsers();
    if (this.isEdit) {
      this.fetchLoan();
    }
  }

  fetchBooksAndUsers(): void {
    this.bookService.getAll({ page: 0, size: 100 }).subscribe({
      next: (response) => this.books = response.content,
      error: (err) => console.error(err)
    });
    this.userService.getAll({ page: 0, size: 100 }).subscribe({
      next: (response) => this.users = response.content,
      error: (err) => console.error(err)
    });
  }

  fetchLoan(): void {
    this.loading = true;
    this.loanService.getById(this.id!).subscribe({
      next: (loan) => {
        this.formData = {
          bookId: loan.bookId,
          userId: loan.userId,
          status: loan.status,
          fineAmount: loan.fineAmount
        };
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Ошибка загрузки выдачи';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    const request = this.isEdit
      ? this.loanService.update(this.id!, this.formData)
      : this.loanService.create(this.formData as LoanCreateDTO);
    request.subscribe({
      next: () => {
        this.router.navigate(['/loans']);
      },
      error: (err) => {
        this.error = 'Ошибка сохранения';
        console.error(err);
        this.loading = false;
      }
    });
  }
}