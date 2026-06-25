import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { UserCreateDTO, UserUpdateDTO } from '../../models/user.model';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css']
})
export class UserFormComponent implements OnInit {
  isEdit = false;
  id?: number;
  loading = false;
  error = '';

  formData: UserCreateDTO & UserUpdateDTO = {
    email: '',
    passwordHash: '',
    firstName: '',
    lastName: '',
    phone: '',
    address: '',
    role: 'READER',
    isActive: true
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.isEdit = !!this.id;
    if (this.isEdit) {
      this.fetchUser();
    }
  }

  fetchUser(): void {
    this.loading = true;
    this.userService.getById(this.id!).subscribe({
      next: (user) => {
        this.formData = {
          email: user.email,
          passwordHash: '',
          firstName: user.firstName,
          lastName: user.lastName,
          phone: user.phone || '',
          address: user.address || '',
          role: user.role,
          isActive: user.isActive
        };
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Ошибка загрузки пользователя';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    const request = this.isEdit
      ? this.userService.update(this.id!, this.formData)
      : this.userService.create(this.formData as UserCreateDTO);
    request.subscribe({
      next: () => {
        this.router.navigate(['/users']);
      },
      error: (err) => {
        this.error = 'Ошибка сохранения';
        console.error(err);
        this.loading = false;
      }
    });
  }
}