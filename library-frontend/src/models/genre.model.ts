export interface Genre {
  id?: number;
  name: string;
  description?: string;
}

export interface GenreCreateDTO {
  name: string;
  description?: string;
}

export interface GenreUpdateDTO extends Partial<GenreCreateDTO> {}