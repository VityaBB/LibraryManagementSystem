import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { bookService } from '../../services/bookService';
import { authorService } from '../../services/authorService';
import { genreService } from '../../services/genreService';
import { publisherService } from '../../services/publisherService';

interface BookFormData {
  title: string;
  isbn: string;
  publicationYear: number;
  publisherId: number;
  totalCopies: number;
  pageCount: number;
  description: string;
  authors: { id: number }[];
  genres: { id: number }[];
}

const BookForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [authors, setAuthors] = useState<any[]>([]);
  const [genres, setGenres] = useState<any[]>([]);
  const [publishers, setPublishers] = useState<any[]>([]);

  const [formData, setFormData] = useState<BookFormData>({
    title: '',
    isbn: '',
    publicationYear: new Date().getFullYear(),
    publisherId: 0,
    totalCopies: 1,
    pageCount: 0,
    description: '',
    authors: [],
    genres: []
  });

  useEffect(() => {
    fetchData();
    if (isEdit) {
      fetchBook();
    }
  }, [id]);

  const fetchData = async () => {
    try {
      const [authorsRes, genresRes, publishersRes] = await Promise.all([
        authorService.getAll({ page: 0, size: 100 }),
        genreService.getAll({ page: 0, size: 100 }),
        publisherService.getAll({ page: 0, size: 100 })
      ]);
      setAuthors(authorsRes.content);
      setGenres(genresRes.content);
      setPublishers(publishersRes.content);
    } catch (err) {
      setError('Ошибка загрузки данных');
      console.error(err);
    }
  };

  const fetchBook = async () => {
    try {
      setLoading(true);
      const book = await bookService.getById(Number(id));
      setFormData({
        title: book.title,
        isbn: book.isbn,
        publicationYear: book.publicationYear,
        publisherId: book.publisherId,
        totalCopies: book.totalCopies,
        pageCount: book.pageCount,
        description: book.description,
        authors: book.authors.map((a: any) => ({ id: a.id! })),
        genres: book.genres.map((g: any) => ({ id: g.id! }))
      });
    } catch (err) {
      setError('Ошибка загрузки книги');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      if (isEdit) {
        await bookService.update(Number(id), formData);
      } else {
        await bookService.create(formData);
      }
      navigate('/books');
    } catch (err) {
      setError('Ошибка сохранения');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev: BookFormData) => ({
      ...prev,
      [name]: name === 'publicationYear' || name === 'publisherId' || name === 'totalCopies' || name === 'pageCount'
        ? Number(value)
        : value
    }));
  };

  const handleMultiSelect = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const { name, options } = e.target;
    const selected = Array.from(options)
      .filter(option => option.selected)
      .map(option => ({ id: Number(option.value) }));
    setFormData((prev: BookFormData) => ({
      ...prev,
      [name]: selected
    }));
  };

  if (loading && isEdit) {
    return <div className="text-center mt-5"><div className="spinner-border"></div></div>;
  }

  return (
    <div className="container mt-4">
      <div className="card">
        <div className="card-header">
          <h3>{isEdit ? '✏️ Редактирование книги' : '➕ Новая книга'}</h3>
        </div>
        <div className="card-body">
          {error && <div className="alert alert-danger">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="row">
              <div className="col-md-6 mb-3">
                <label className="form-label">Название *</label>
                <input
                  type="text"
                  className="form-control"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">ISBN</label>
                <input
                  type="text"
                  className="form-control"
                  name="isbn"
                  value={formData.isbn}
                  onChange={handleChange}
                />
              </div>
            </div>
            <div className="row">
              <div className="col-md-4 mb-3">
                <label className="form-label">Год публикации</label>
                <input
                  type="number"
                  className="form-control"
                  name="publicationYear"
                  value={formData.publicationYear}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-4 mb-3">
                <label className="form-label">Количество экземпляров</label>
                <input
                  type="number"
                  className="form-control"
                  name="totalCopies"
                  value={formData.totalCopies}
                  onChange={handleChange}
                  min="1"
                />
              </div>
              <div className="col-md-4 mb-3">
                <label className="form-label">Количество страниц</label>
                <input
                  type="number"
                  className="form-control"
                  name="pageCount"
                  value={formData.pageCount}
                  onChange={handleChange}
                  min="0"
                />
              </div>
            </div>
            <div className="mb-3">
              <label className="form-label">Издатель</label>
              <select
                className="form-select"
                name="publisherId"
                value={formData.publisherId}
                onChange={handleChange}
              >
                <option value={0}>Выберите издателя</option>
                {publishers.map((publisher: any) => (
                  <option key={publisher.id} value={publisher.id}>
                    {publisher.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="row">
              <div className="col-md-6 mb-3">
                <label className="form-label">Авторы</label>
                <select
                  className="form-select"
                  name="authors"
                  multiple
                  value={formData.authors.map((a: any) => String(a.id))}
                  onChange={handleMultiSelect}
                >
                  {authors.map((author: any) => (
                    <option key={author.id} value={author.id}>
                      {author.fullName}
                    </option>
                  ))}
                </select>
                <small className="text-muted">Держите Ctrl для выбора нескольких</small>
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">Жанры</label>
                <select
                  className="form-select"
                  name="genres"
                  multiple
                  value={formData.genres.map((g: any) => String(g.id))}
                  onChange={handleMultiSelect}
                >
                  {genres.map((genre: any) => (
                    <option key={genre.id} value={genre.id}>
                      {genre.name}
                    </option>
                  ))}
                </select>
                <small className="text-muted">Держите Ctrl для выбора нескольких</small>
              </div>
            </div>
            <div className="mb-3">
              <label className="form-label">Описание</label>
              <textarea
                className="form-control"
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows={3}
              />
            </div>
            <div className="d-flex gap-2 mt-3">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Сохранение...' : 'Сохранить'}
              </button>
              <button type="button" className="btn btn-secondary" onClick={() => navigate('/books')}>
                Отмена
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default BookForm;