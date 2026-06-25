import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { PageResponse } from '../../models/pagination.model';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  users: User[] = [];
  loading = true;
  error = '';
  page = 0;
  totalPages = 0;
  totalElements = 0;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.fetchUsers();
  }

  fetchUsers(): void {
    this.loading = true;
    this.userService.getAll({ page: this.page, size: 10 }).subscribe({
      next: (response: PageResponse<User>) => {
        this.users = response.content;
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

  deleteUser(id: number): void {
    if (confirm('Вы уверены, что хотите удалить этого пользователя?')) {
      this.userService.delete(id).subscribe({
        next: () => this.fetchUsers(),
        error: (err) => {
          this.error = 'Ошибка удаления';
          console.error(err);
        }
      });
    }
  }

  changePage(page: number): void {
    this.page = page;
    this.fetchUsers();
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

  getUserStatus(user: User): string {
    return user.isActive ? 'Активен' : 'Заблокирован';
  }

  getStatusBadge(user: User): string {
    return user.isActive ? 'badge bg-success' : 'badge bg-danger';
  }

  getRoleBadge(user: User): string {
    return user.role === 'ADMIN' ? 'badge bg-warning' : 'badge bg-info';
  }
}