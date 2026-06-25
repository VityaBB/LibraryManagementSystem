import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Book, BookCreateDTO, BookUpdateDTO } from '../models/book.model';
import { PageResponse, PageRequest } from '../models/pagination.model';
import { API_BASE_URL, API_ENDPOINTS } from '../utils/constants';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private baseUrl = `${API_BASE_URL}${API_ENDPOINTS.BOOKS}`;

  constructor(private http: HttpClient) {}

  getAll(params?: PageRequest & { publicationYear?: number }): Observable<PageResponse<Book>> {
    let httpParams = new HttpParams()
      .set('page', params?.page?.toString() || '0')
      .set('size', params?.size?.toString() || '10');
    if (params?.title) {
      httpParams = httpParams.set('title', params.title);
    }
    if (params?.publicationYear) {
      httpParams = httpParams.set('publicationYear', params.publicationYear.toString());
    }
    if (params?.authorId) {
      httpParams = httpParams.set('authorId', params.authorId.toString());
    }
    if (params?.genreId) {
      httpParams = httpParams.set('genreId', params.genreId.toString());
    }
    console.log('📤 HTTP запрос:', this.baseUrl, 'с параметрами:', httpParams.toString());
    return this.http.get<PageResponse<Book>>(this.baseUrl, { params: httpParams });
  }

  getById(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.baseUrl}/${id}`);
  }

  create(data: BookCreateDTO): Observable<Book> {
    return this.http.post<Book>(this.baseUrl, data);
  }

  update(id: number, data: BookUpdateDTO): Observable<Book> {
    return this.http.put<Book>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}