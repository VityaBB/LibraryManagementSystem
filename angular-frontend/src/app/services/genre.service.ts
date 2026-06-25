import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Genre, GenreCreateDTO, GenreUpdateDTO } from '../models/genre.model';
import { PageResponse, PageRequest } from '../models/pagination.model';
import { API_BASE_URL, API_ENDPOINTS } from '../utils/constants';

@Injectable({
  providedIn: 'root'
})
export class GenreService {
  private baseUrl = `${API_BASE_URL}${API_ENDPOINTS.GENRES}`;

  constructor(private http: HttpClient) {}

  getAll(params?: PageRequest): Observable<PageResponse<Genre>> {
    let httpParams = new HttpParams()
      .set('page', params?.page?.toString() || '0')
      .set('size', params?.size?.toString() || '10');
    return this.http.get<PageResponse<Genre>>(this.baseUrl, { params: httpParams });
  }

  getById(id: number): Observable<Genre> {
    return this.http.get<Genre>(`${this.baseUrl}/${id}`);
  }

  create(data: GenreCreateDTO): Observable<Genre> {
    return this.http.post<Genre>(this.baseUrl, data);
  }

  update(id: number, data: GenreUpdateDTO): Observable<Genre> {
    return this.http.put<Genre>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}