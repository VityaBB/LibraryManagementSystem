export interface Publisher {
  id?: number;
  name: string;
  address?: string;
  phone?: string;
  email?: string;
  website?: string;
}

export interface PublisherCreateDTO {
  name: string;
  address?: string;
  phone?: string;
  email?: string;
  website?: string;
}

export interface PublisherUpdateDTO extends Partial<PublisherCreateDTO> {}