import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Author, AuthorCreateDTO, AuthorUpdateDTO } from '../models/author.model';
import { PageResponse, PageRequest } from '../models/pagination.model';
import { API_BASE_URL, API_ENDPOINTS } from '../utils/constants';

@Injectable({
  providedIn: 'root'
})
export class AuthorService {
  private baseUrl = `${API_BASE_URL}${API_ENDPOINTS.AUTHORS}`;

  constructor(private http: HttpClient) {}

  getAll(params?: PageRequest): Observable<PageResponse<Author>> {
    let httpParams = new HttpParams()
      .set('page', params?.page?.toString() || '0')
      .set('size', params?.size?.toString() || '10');
    return this.http.get<PageResponse<Author>>(this.baseUrl, { params: httpParams });
  }

  getById(id: number): Observable<Author> {
    return this.http.get<Author>(`${this.baseUrl}/${id}`);
  }

  create(data: AuthorCreateDTO): Observable<Author> {
    return this.http.post<Author>(this.baseUrl, data);
  }

  update(id: number, data: AuthorUpdateDTO): Observable<Author> {
    return this.http.put<Author>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}