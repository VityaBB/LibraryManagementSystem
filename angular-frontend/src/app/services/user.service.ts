import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, UserCreateDTO, UserUpdateDTO } from '../models/user.model';
import { PageResponse, PageRequest } from '../models/pagination.model';
import { API_BASE_URL, API_ENDPOINTS } from '../utils/constants';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = `${API_BASE_URL}${API_ENDPOINTS.USERS}`;

  constructor(private http: HttpClient) {}

  getAll(params?: PageRequest): Observable<PageResponse<User>> {
    let httpParams = new HttpParams()
      .set('page', params?.page?.toString() || '0')
      .set('size', params?.size?.toString() || '10');
    return this.http.get<PageResponse<User>>(this.baseUrl, { params: httpParams });
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/${id}`);
  }

  getByEmail(email: string): Observable<User> {
    let params = new HttpParams().set('email', email);
    return this.http.get<User>(`${this.baseUrl}/email`, { params });
  }

  create(data: UserCreateDTO): Observable<User> {
    return this.http.post<User>(this.baseUrl, data);
  }

  update(id: number, data: UserUpdateDTO): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}