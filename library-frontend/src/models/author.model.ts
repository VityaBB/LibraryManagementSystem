export interface Author {
  id?: number;
  firstName: string;
  lastName: string;
  fullName?: string;
  birthDate?: string;
  biography?: string;
}

export interface AuthorCreateDTO {
  firstName: string;
  lastName: string;
  birthDate?: string;
  biography?: string;
}

export interface AuthorUpdateDTO extends Partial<AuthorCreateDTO> {}