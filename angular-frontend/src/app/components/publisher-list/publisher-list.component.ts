import { Component, OnInit } from '@angular/core';
import { PublisherService } from '../../services/publisher.service';
import { Publisher } from '../../models/publisher.model';
import { PageResponse } from '../../models/pagination.model';

@Component({
  selector: 'app-publisher-list',
  templateUrl: './publisher-list.component.html',
  styleUrls: ['./publisher-list.component.css']
})
export class PublisherListComponent implements OnInit {
  publishers: Publisher[] = [];
  loading = true;
  error = '';
  page = 0;
  totalPages = 0;
  totalElements = 0;
  
  nameFilter = '';
  appliedName = '';

  constructor(private publisherService: PublisherService) {}

  ngOnInit(): void {
    this.fetchPublishers();
  }

  fetchPublishers(): void {
    this.loading = true;
    const params: any = { page: this.page, size: 10 };
    if (this.appliedName) {
      params.name = this.appliedName;
    }
    console.log('📤 Отправка параметров:', params);
    this.publisherService.getAll(params).subscribe({
      next: (response: PageResponse<Publisher>) => {
        console.log('📥 Получен ответ:', response);
        this.publishers = response.content;
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

  applyNameFilter(): void {
    this.appliedName = this.nameFilter.trim();
    this.page = 0;
    this.fetchPublishers();
  }

  resetNameFilter(): void {
    this.nameFilter = '';
    this.appliedName = '';
    this.page = 0;
    this.fetchPublishers();
  }

  deletePublisher(id: number): void {
    if (confirm('Вы уверены, что хотите удалить этого издателя?')) {
      this.publisherService.delete(id).subscribe({
        next: () => this.fetchPublishers(),
        error: (err) => {
          this.error = 'Ошибка удаления';
          console.error(err);
        }
      });
    }
  }

  changePage(page: number): void {
    this.page = page;
    this.fetchPublishers();
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