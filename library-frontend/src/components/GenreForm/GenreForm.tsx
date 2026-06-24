import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { genreService } from '../../services/genreService';

interface GenreFormData {
  name: string;
  description: string;
}

const GenreForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState<GenreFormData>({
    name: '',
    description: ''
  });

  useEffect(() => {
    if (isEdit) {
      fetchGenre();
    }
  }, [id]);

  const fetchGenre = async () => {
    try {
      setLoading(true);
      const genre = await genreService.getById(Number(id));
      setFormData({
        name: genre.name,
        description: genre.description || ''
      });
    } catch (err) {
      setError('Ошибка загрузки жанра');
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
        await genreService.update(Number(id), formData);
      } else {
        await genreService.create(formData);
      }
      navigate('/genres');
    } catch (err) {
      setError('Ошибка сохранения');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev: GenreFormData) => ({ ...prev, [name]: value }));
  };

  if (loading && isEdit) {
    return <div className="text-center mt-5"><div className="spinner-border"></div></div>;
  }

  return (
    <div className="container mt-4">
      <div className="card">
        <div className="card-header">
          <h3>{isEdit ? '✏️ Редактирование жанра' : '➕ Новый жанр'}</h3>
        </div>
        <div className="card-body">
          {error && <div className="alert alert-danger">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Название *</label>
              <input
                type="text"
                className="form-control"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
              />
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
              <button type="button" className="btn btn-secondary" onClick={() => navigate('/genres')}>
                Отмена
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default GenreForm;