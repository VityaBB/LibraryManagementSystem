import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthorService } from '../../services/author.service';
import { AuthorCreateDTO, AuthorUpdateDTO } from '../../models/author.model';

@Component({
  selector: 'app-author-form',
  templateUrl: './author-form.component.html',
  styleUrls: ['./author-form.component.css']
})
export class AuthorFormComponent implements OnInit {
  isEdit = false;
  id?: number;
  loading = false;
  error = '';

  formData: AuthorCreateDTO & AuthorUpdateDTO = {
    firstName: '',
    lastName: '',
    birthDate: '',
    biography: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authorService: AuthorService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.isEdit = !!this.id;
    if (this.isEdit) {
      this.fetchAuthor();
    }
  }

  fetchAuthor(): void {
    this.loading = true;
    this.authorService.getById(this.id!).subscribe({
      next: (author) => {
        this.formData = {
          firstName: author.firstName,
          lastName: author.lastName,
          birthDate: author.birthDate || '',
          biography: author.biography || ''
        };
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Ошибка загрузки автора';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    const request = this.isEdit
      ? this.authorService.update(this.id!, this.formData)
      : this.authorService.create(this.formData as AuthorCreateDTO);
    request.subscribe({
      next: () => {
        this.router.navigate(['/authors']);
      },
      error: (err) => {
        this.error = 'Ошибка сохранения';
        console.error(err);
        this.loading = false;
      }
    });
  }
}