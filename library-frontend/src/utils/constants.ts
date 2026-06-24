export const API_BASE_URL = 'http://localhost:8080/api';

export const API_ENDPOINTS = {
  BOOKS: '/books',
  AUTHORS: '/authors',
  GENRES: '/genres',
  PUBLISHERS: '/publishers',
  USERS: '/users',
  LOANS: '/loans'
} as const;

export const DEFAULT_PAGE_SIZE = 10;