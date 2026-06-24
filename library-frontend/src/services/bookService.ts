import { api } from './api';
import { API_ENDPOINTS } from '../utils/constants';
import   { Book, BookCreateDTO, BookUpdateDTO } from '../models/book.model';
import   { PageResponse, PageRequest } from '../models/pagination.model';

export const bookService = {
  getAll: async (params?: PageRequest) => {
    const response = await api.get<PageResponse<Book>>(API_ENDPOINTS.BOOKS, {
      params: {
        page: params?.page || 0,
        size: params?.size || 10,
        title: params?.title,
        authorId: params?.authorId,
        genreId: params?.genreId
      }
    });
    return response.data;
  },

  getById: async (id: number) => {
    const response = await api.get<Book>(`${API_ENDPOINTS.BOOKS}/${id}`);
    return response.data;
  },

  create: async (data: BookCreateDTO) => {
    const response = await api.post<Book>(API_ENDPOINTS.BOOKS, data);
    return response.data;
  },

  update: async (id: number, data: BookUpdateDTO) => {
    const response = await api.put<Book>(`${API_ENDPOINTS.BOOKS}/${id}`, data);
    return response.data;
  },

  delete: async (id: number) => {
    await api.delete(`${API_ENDPOINTS.BOOKS}/${id}`);
  }
};