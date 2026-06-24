import { api } from './api';
import { API_ENDPOINTS } from '../utils/constants';
import { Loan, LoanCreateDTO, LoanUpdateDTO } from '../models/loan.model';
import { PageResponse, PageRequest } from '../models/pagination.model';

export const loanService = {
  getAll: async (params?: PageRequest) => {
    const response = await api.get<PageResponse<Loan>>(API_ENDPOINTS.LOANS, {
      params: { page: params?.page || 0, size: params?.size || 10 }
    });
    return response.data;
  },

  getActive: async () => {
    const response = await api.get<Loan[]>(`${API_ENDPOINTS.LOANS}/active`);
    return response.data;
  },

  getById: async (id: number) => {
    const response = await api.get<Loan>(`${API_ENDPOINTS.LOANS}/${id}`);
    return response.data;
  },

  create: async (data: LoanCreateDTO) => {
    const response = await api.post<Loan>(API_ENDPOINTS.LOANS, data);
    return response.data;
  },

  returnBook: async (id: number, fineAmount?: number) => {
    const response = await api.put<Loan>(
      `${API_ENDPOINTS.LOANS}/${id}/return`,
      null,
      { params: { fineAmount } }
    );
    return response.data;
  },

  update: async (id: number, data: LoanUpdateDTO) => {
    const response = await api.put<Loan>(`${API_ENDPOINTS.LOANS}/${id}`, data);
    return response.data;
  },

  delete: async (id: number) => {
    await api.delete(`${API_ENDPOINTS.LOANS}/${id}`);
  }
};