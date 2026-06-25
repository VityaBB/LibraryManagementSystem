import { Component, OnInit } from '@angular/core';
import { AuthorService } from '../../services/author.service';
import { Author } from '../../models/author.model';
import { PageResponse } from '../../models/pagination.model';

@Component({
  selector: 'app-author-list',
  templateUrl: './author-list.component.html',
  styleUrls: ['./author-list.component.css']
})
export class AuthorListComponent implements OnInit {
  authors: Author[] = [];
  loading = true;
  error = '';
  page = 0;
  totalPages = 0;
  totalElements = 0;
  
  firstNameFilter = '';
  lastNameFilter = '';
  appliedFirstName = '';
  appliedLastName = '';

  constructor(private authorService: AuthorService) {}

  ngOnInit(): void {
    this.fetchAuthors();
  }

  fetchAuthors(): void {
    this.loading = true;
    const params: any = { page: this.page, size: 10 };
    if (this.appliedFirstName) {
      params.firstName = this.appliedFirstName;
    }
    if (this.appliedLastName) {
      params.lastName = this.appliedLastName;
    }
    console.log('📤 Отправка параметров:', params);
    this.authorService.getAll(params).subscribe({
      next: (response: PageResponse<Author>) => {
        console.log('📥 Получен ответ:', response);
        this.authors = response.content;
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

  applyFirstNameFilter(): void {
    this.appliedFirstName = this.firstNameFilter.trim();
    this.page = 0;
    this.fetchAuthors();
  }

  applyLastNameFilter(): void {
    this.appliedLastName = this.lastNameFilter.trim();
    this.page = 0;
    this.fetchAuthors();
  }

  resetFirstNameFilter(): void {
    this.firstNameFilter = '';
    this.appliedFirstName = '';
    this.page = 0;
    this.fetchAuthors();
  }

  resetLastNameFilter(): void {
    this.lastNameFilter = '';
    this.appliedLastName = '';
    this.page = 0;
    this.fetchAuthors();
  }

  resetAllFilters(): void {
    this.firstNameFilter = '';
    this.lastNameFilter = '';
    this.appliedFirstName = '';
    this.appliedLastName = '';
    this.page = 0;
    this.fetchAuthors();
  }

  deleteAuthor(id: number): void {
    if (confirm('Вы уверены, что хотите удалить этого автора?')) {
      this.authorService.delete(id).subscribe({
        next: () => this.fetchAuthors(),
        error: (err) => {
          this.error = 'Ошибка удаления';
          console.error(err);
        }
      });
    }
  }

  changePage(page: number): void {
    this.page = page;
    this.fetchAuthors();
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