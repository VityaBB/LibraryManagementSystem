import type { Author } from './author.model';
import type { Genre } from './genre.model';

export interface Book {
  id?: number;
  title: string;
  isbn: string;
  publicationYear: number;
  publisherId: number;
  publisherName?: string;
  totalCopies: number;
  availableCopies?: number;
  pageCount: number;
  description: string;
  authors: Author[];
  genres: Genre[];
}

export interface BookCreateDTO {
  title: string;
  isbn: string;
  publicationYear: number;
  publisherId: number;
  totalCopies: number;
  pageCount: number;
  description: string;
  authors: { id: number }[];
  genres: { id: number }[];
}

export interface BookUpdateDTO extends Partial<BookCreateDTO> {}