import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GenreService } from '../../services/genre.service';
import { GenreCreateDTO, GenreUpdateDTO } from '../../models/genre.model';

@Component({
  selector: 'app-genre-form',
  templateUrl: './genre-form.component.html',
  styleUrls: ['./genre-form.component.css']
})
export class GenreFormComponent implements OnInit {
  isEdit = false;
  id?: number;
  loading = false;
  error = '';

  formData: GenreCreateDTO & GenreUpdateDTO = {
    name: '',
    description: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private genreService: GenreService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.isEdit = !!this.id;
    if (this.isEdit) {
      this.fetchGenre();
    }
  }

  fetchGenre(): void {
    this.loading = true;
    this.genreService.getById(this.id!).subscribe({
      next: (genre) => {
        this.formData = {
          name: genre.name,
          description: genre.description || ''
        };
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Ошибка загрузки жанра';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    const request = this.isEdit
      ? this.genreService.update(this.id!, this.formData)
      : this.genreService.create(this.formData as GenreCreateDTO);
    request.subscribe({
      next: () => {
        this.router.navigate(['/genres']);
      },
      error: (err) => {
        this.error = 'Ошибка сохранения';
        console.error(err);
        this.loading = false;
      }
    });
  }
}