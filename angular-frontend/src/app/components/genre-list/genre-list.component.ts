import { Component, OnInit } from '@angular/core';
import { GenreService } from '../../services/genre.service';
import { Genre } from '../../models/genre.model';
import { PageResponse } from '../../models/pagination.model';

@Component({
  selector: 'app-genre-list',
  templateUrl: './genre-list.component.html',
  styleUrls: ['./genre-list.component.css']
})
export class GenreListComponent implements OnInit {
  genres: Genre[] = [];
  loading = true;
  error = '';
  page = 0;
  totalPages = 0;
  totalElements = 0;

  constructor(private genreService: GenreService) {}

  ngOnInit(): void {
    this.fetchGenres();
  }

  fetchGenres(): void {
    this.loading = true;
    this.genreService.getAll({ page: this.page, size: 10 }).subscribe({
      next: (response: PageResponse<Genre>) => {
        this.genres = response.content;
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

  deleteGenre(id: number): void {
    if (confirm('Вы уверены, что хотите удалить этот жанр?')) {
      this.genreService.delete(id).subscribe({
        next: () => this.fetchGenres(),
        error: (err) => {
          this.error = 'Ошибка удаления';
          console.error(err);
        }
      });
    }
  }

  changePage(page: number): void {
    this.page = page;
    this.fetchGenres();
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