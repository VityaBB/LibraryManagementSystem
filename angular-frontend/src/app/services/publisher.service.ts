import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Publisher, PublisherCreateDTO, PublisherUpdateDTO } from '../models/publisher.model';
import { PageResponse, PageRequest } from '../models/pagination.model';
import { API_BASE_URL, API_ENDPOINTS } from '../utils/constants';

@Injectable({
  providedIn: 'root'
})
export class PublisherService {
  private baseUrl = `${API_BASE_URL}${API_ENDPOINTS.PUBLISHERS}`;

  constructor(private http: HttpClient) {}

  getAll(params?: PageRequest & { name?: string }): Observable<PageResponse<Publisher>> {
    let httpParams = new HttpParams()
      .set('page', params?.page?.toString() || '0')
      .set('size', params?.size?.toString() || '10');
    if (params?.name) {
      httpParams = httpParams.set('name', params.name);
    }
    console.log('📤 HTTP запрос:', this.baseUrl, 'с параметрами:', httpParams.toString());
    return this.http.get<PageResponse<Publisher>>(this.baseUrl, { params: httpParams });
  }

  getById(id: number): Observable<Publisher> {
    return this.http.get<Publisher>(`${this.baseUrl}/${id}`);
  }

  create(data: PublisherCreateDTO): Observable<Publisher> {
    return this.http.post<Publisher>(this.baseUrl, data);
  }

  update(id: number, data: PublisherUpdateDTO): Observable<Publisher> {
    return this.http.put<Publisher>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}