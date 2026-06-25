import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookService } from '../../services/book.service';
import { AuthorService } from '../../services/author.service';
import { GenreService } from '../../services/genre.service';
import { PublisherService } from '../../services/publisher.service';
import { BookCreateDTO, BookUpdateDTO } from '../../models/book.model';
import { Author } from '../../models/author.model';
import { Genre } from '../../models/genre.model';
import { Publisher } from '../../models/publisher.model';

@Component({
  selector: 'app-book-form',
  templateUrl: './book-form.component.html',
  styleUrls: ['./book-form.component.css']
})
export class BookFormComponent implements OnInit {
  isEdit = false;
  id?: number;
  loading = false;
  error = '';
  authors: Author[] = [];
  genres: Genre[] = [];
  publishers: Publisher[] = [];

  formData: BookCreateDTO & BookUpdateDTO = {
    title: '',
    isbn: '',
    publicationYear: new Date().getFullYear(),
    publisherId: 0,
    totalCopies: 1,
    pageCount: 0,
    description: '',
    authors: [],
    genres: []
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private bookService: BookService,
    private authorService: AuthorService,
    private genreService: GenreService,
    private publisherService: PublisherService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.isEdit = !!this.id;
    this.fetchData();
    if (this.isEdit) {
      this.fetchBook();
    }
  }

  fetchData(): void {
    this.authorService.getAll({ page: 0, size: 100 }).subscribe({
      next: (response) => this.authors = response.content,
      error: (err) => console.error(err)
    });
    this.genreService.getAll({ page: 0, size: 100 }).subscribe({
      next: (response) => this.genres = response.content,
      error: (err) => console.error(err)
    });
    this.publisherService.getAll({ page: 0, size: 100 }).subscribe({
      next: (response) => this.publishers = response.content,
      error: (err) => console.error(err)
    });
  }

  fetchBook(): void {
    this.loading = true;
    this.bookService.getById(this.id!).subscribe({
      next: (book) => {
        this.formData = {
          title: book.title,
          isbn: book.isbn,
          publicationYear: book.publicationYear,
          publisherId: book.publisherId,
          totalCopies: book.totalCopies,
          pageCount: book.pageCount,
          description: book.description,
          authors: book.authors.map(a => ({ id: a.id! })),
          genres: book.genres.map(g => ({ id: g.id! }))
        };
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Ошибка загрузки книги';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    const request = this.isEdit
      ? this.bookService.update(this.id!, this.formData)
      : this.bookService.create(this.formData as BookCreateDTO);
    request.subscribe({
      next: () => {
        this.router.navigate(['/books']);
      },
      error: (err) => {
        this.error = 'Ошибка сохранения';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onAuthorSelect(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const selectedOptions = Array.from(select.selectedOptions);
    this.formData.authors = selectedOptions.map(opt => ({ id: Number(opt.value) }));
  }

  onGenreSelect(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const selectedOptions = Array.from(select.selectedOptions);
    this.formData.genres = selectedOptions.map(opt => ({ id: Number(opt.value) }));
  }

  isAuthorSelected(authorId: number | undefined): boolean {
    if (authorId === undefined) return false;
    return this.formData.authors.some(a => a.id === authorId);
  }

  isGenreSelected(genreId: number | undefined): boolean {
    if (genreId === undefined) return false;
    return this.formData.genres.some(g => g.id === genreId);
  }
}