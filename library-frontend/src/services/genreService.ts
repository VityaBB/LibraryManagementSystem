import { api } from './api';
import { API_ENDPOINTS } from '../utils/constants';
import   { Genre, GenreCreateDTO, GenreUpdateDTO } from '../models/genre.model';
import   { PageResponse, PageRequest } from '../models/pagination.model';

export const genreService = {
  getAll: async (params?: PageRequest) => {
    const response = await api.get<PageResponse<Genre>>(API_ENDPOINTS.GENRES, {
      params: { page: params?.page || 0, size: params?.size || 10 }
    });
    return response.data;
  },

  getById: async (id: number) => {
    const response = await api.get<Genre>(`${API_ENDPOINTS.GENRES}/${id}`);
    return response.data;
  },

  create: async (data: GenreCreateDTO) => {
    const response = await api.post<Genre>(API_ENDPOINTS.GENRES, data);
    return response.data;
  },

  update: async (id: number, data: GenreUpdateDTO) => {
    const response = await api.put<Genre>(`${API_ENDPOINTS.GENRES}/${id}`, data);
    return response.data;
  },

  delete: async (id: number) => {
    await api.delete(`${API_ENDPOINTS.GENRES}/${id}`);
  }
};