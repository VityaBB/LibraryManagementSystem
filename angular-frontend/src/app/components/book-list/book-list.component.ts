import { Component, OnInit } from '@angular/core';
import { BookService } from '../../services/book.service';
import { Book } from '../../models/book.model';
import { PageResponse } from '../../models/pagination.model';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css']
})
export class BookListComponent implements OnInit {
  books: Book[] = [];
  loading = true;
  error = '';
  page = 0;
  totalPages = 0;
  totalElements = 0;
  titleFilter = '';
  yearFilter: string = '';
  appliedTitle = '';
  appliedYear = '';

  constructor(private bookService: BookService) {}

  ngOnInit(): void {
    this.fetchBooks();
  }

  fetchBooks(): void {
    this.loading = true;
    const params: any = { page: this.page, size: 10 };
    if (this.appliedTitle) {
      params.title = this.appliedTitle;
    }
    if (this.appliedYear) {
      params.publicationYear = Number(this.appliedYear);
    }
    console.log('📤 Отправка параметров:', params);
    this.bookService.getAll(params).subscribe({
      next: (response: PageResponse<Book>) => {
        console.log('📥 Получен ответ:', response);
        this.books = response.content;
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

  applyTitleFilter(): void {
    this.appliedTitle = this.titleFilter.trim();
    this.page = 0;
    this.fetchBooks();
  }

  applyYearFilter(): void {
    this.appliedYear = this.yearFilter.toString().trim();
    this.page = 0;
    this.fetchBooks();
  }

  resetTitleFilter(): void {
    this.titleFilter = '';
    this.appliedTitle = '';
    this.page = 0;
    this.fetchBooks();
  }

  resetYearFilter(): void {
    this.yearFilter = '';
    this.appliedYear = '';
    this.page = 0;
    this.fetchBooks();
  }

  resetAllFilters(): void {
    this.titleFilter = '';
    this.yearFilter = '';
    this.appliedTitle = '';
    this.appliedYear = '';
    this.page = 0;
    this.fetchBooks();
  }

  deleteBook(id: number): void {
    if (confirm('Вы уверены, что хотите удалить эту книгу?')) {
      this.bookService.delete(id).subscribe({
        next: () => this.fetchBooks(),
        error: (err) => {
          this.error = 'Ошибка удаления';
          console.error(err);
        }
      });
    }
  }

  changePage(page: number): void {
    this.page = page;
    this.fetchBooks();
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

  getAuthors(book: Book): string {
    if (!book.authors || book.authors.length === 0) {
      return '—';
    }
    return book.authors.map(a => a.fullName).join(', ');
  }

  getGenres(book: Book): string {
    if (!book.genres || book.genres.length === 0) {
      return '—';
    }
    return book.genres.map(g => g.name).join(', ');
  }
}