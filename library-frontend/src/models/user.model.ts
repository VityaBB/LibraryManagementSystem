export interface User {
  id?: number;
  email: string;
  firstName: string;
  lastName: string;
  fullName?: string;
  phone?: string;
  address?: string;
  registrationDate?: string;
  isActive: boolean;
  role: string;
}

export interface UserCreateDTO {
  email: string;
  passwordHash: string;
  firstName: string;
  lastName: string;
  phone?: string;
  address?: string;
  role?: string;
}

export interface UserUpdateDTO extends Partial<UserCreateDTO> {
  isActive?: boolean;
}