import { api } from './api';
import { API_ENDPOINTS } from '../utils/constants';
import   { User, UserCreateDTO, UserUpdateDTO } from '../models/user.model';
import   { PageResponse, PageRequest } from '../models/pagination.model';

export const userService = {
  getAll: async (params?: PageRequest) => {
    const response = await api.get<PageResponse<User>>(API_ENDPOINTS.USERS, {
      params: { page: params?.page || 0, size: params?.size || 10 }
    });
    return response.data;
  },

  getById: async (id: number) => {
    const response = await api.get<User>(`${API_ENDPOINTS.USERS}/${id}`);
    return response.data;
  },

  getByEmail: async (email: string) => {
    const response = await api.get<User>(`${API_ENDPOINTS.USERS}/email`, {
      params: { email }
    });
    return response.data;
  },

  create: async (data: UserCreateDTO) => {
    const response = await api.post<User>(API_ENDPOINTS.USERS, data);
    return response.data;
  },

  update: async (id: number, data: UserUpdateDTO) => {
    const response = await api.put<User>(`${API_ENDPOINTS.USERS}/${id}`, data);
    return response.data;
  },

  delete: async (id: number) => {
    await api.delete(`${API_ENDPOINTS.USERS}/${id}`);
  }
};