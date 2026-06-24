import { api } from './api';
import { API_ENDPOINTS } from '../utils/constants';
import   { Publisher, PublisherCreateDTO, PublisherUpdateDTO } from '../models/publisher.model';
import   { PageResponse, PageRequest } from '../models/pagination.model';

export const publisherService = {
  getAll: async (params?: PageRequest) => {
    const response = await api.get<PageResponse<Publisher>>(API_ENDPOINTS.PUBLISHERS, {
      params: { page: params?.page || 0, size: params?.size || 10 }
    });
    return response.data;
  },

  getById: async (id: number) => {
    const response = await api.get<Publisher>(`${API_ENDPOINTS.PUBLISHERS}/${id}`);
    return response.data;
  },

  create: async (data: PublisherCreateDTO) => {
    const response = await api.post<Publisher>(API_ENDPOINTS.PUBLISHERS, data);
    return response.data;
  },

  update: async (id: number, data: PublisherUpdateDTO) => {
    const response = await api.put<Publisher>(`${API_ENDPOINTS.PUBLISHERS}/${id}`, data);
    return response.data;
  },

  delete: async (id: number) => {
    await api.delete(`${API_ENDPOINTS.PUBLISHERS}/${id}`);
  }
};