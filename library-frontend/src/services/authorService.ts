import { api } from './api';
import { API_ENDPOINTS } from '../utils/constants';
import   { Author, AuthorCreateDTO, AuthorUpdateDTO } from '../models/author.model';
import   { PageResponse, PageRequest } from '../models/pagination.model';

export const authorService = {
  getAll: async (params?: PageRequest) => {
    const response = await api.get<PageResponse<Author>>(API_ENDPOINTS.AUTHORS, {
      params: { page: params?.page || 0, size: params?.size || 10 }
    });
    return response.data;
  },

  getById: async (id: number) => {
    const response = await api.get<Author>(`${API_ENDPOINTS.AUTHORS}/${id}`);
    return response.data;
  },

  create: async (data: AuthorCreateDTO) => {
    const response = await api.post<Author>(API_ENDPOINTS.AUTHORS, data);
    return response.data;
  },

  update: async (id: number, data: AuthorUpdateDTO) => {
    const response = await api.put<Author>(`${API_ENDPOINTS.AUTHORS}/${id}`, data);
    return response.data;
  },

  delete: async (id: number) => {
    await api.delete(`${API_ENDPOINTS.AUTHORS}/${id}`);
  }
};