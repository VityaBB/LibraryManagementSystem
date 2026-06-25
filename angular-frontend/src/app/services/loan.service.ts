import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Loan, LoanCreateDTO, LoanUpdateDTO } from '../models/loan.model';
import { PageResponse, PageRequest } from '../models/pagination.model';
import { API_BASE_URL, API_ENDPOINTS } from '../utils/constants';

@Injectable({
  providedIn: 'root'
})
export class LoanService {
  private baseUrl = `${API_BASE_URL}${API_ENDPOINTS.LOANS}`;

  constructor(private http: HttpClient) {}

  getAll(params?: PageRequest & { bookTitle?: string; userName?: string }): Observable<PageResponse<Loan>> {
    let httpParams = new HttpParams()
      .set('page', params?.page?.toString() || '0')
      .set('size', params?.size?.toString() || '10');
    if (params?.bookTitle) {
      httpParams = httpParams.set('bookTitle', params.bookTitle);
    }
    if (params?.userName) {
      httpParams = httpParams.set('userName', params.userName);
    }
    console.log('📤 HTTP запрос:', this.baseUrl, 'с параметрами:', httpParams.toString());
    return this.http.get<PageResponse<Loan>>(this.baseUrl, { params: httpParams });
  }

  getActive(): Observable<Loan[]> {
    return this.http.get<Loan[]>(`${this.baseUrl}/active`);
  }

  getById(id: number): Observable<Loan> {
    return this.http.get<Loan>(`${this.baseUrl}/${id}`);
  }

  create(data: LoanCreateDTO): Observable<Loan> {
    return this.http.post<Loan>(this.baseUrl, data);
  }

  returnBook(id: number, fineAmount?: number): Observable<Loan> {
    let params = new HttpParams();
    if (fineAmount) {
      params = params.set('fineAmount', fineAmount.toString());
    }
    return this.http.put<Loan>(`${this.baseUrl}/${id}/return`, null, { params });
  }

  update(id: number, data: LoanUpdateDTO): Observable<Loan> {
    return this.http.put<Loan>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}