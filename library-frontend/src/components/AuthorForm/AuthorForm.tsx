import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { authorService } from '../../services/authorService';

interface AuthorFormData {
  firstName: string;
  lastName: string;
  birthDate: string;
  biography: string;
}

const AuthorForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState<AuthorFormData>({
    firstName: '',
    lastName: '',
    birthDate: '',
    biography: ''
  });

  useEffect(() => {
    if (isEdit) {
      fetchAuthor();
    }
  }, [id]);

  const fetchAuthor = async () => {
    try {
      setLoading(true);
      const author = await authorService.getById(Number(id));
      setFormData({
        firstName: author.firstName,
        lastName: author.lastName,
        birthDate: author.birthDate || '',
        biography: author.biography || ''
      });
    } catch (err) {
      setError('Ошибка загрузки автора');
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
        await authorService.update(Number(id), formData);
      } else {
        await authorService.create(formData);
      }
      navigate('/authors');
    } catch (err) {
      setError('Ошибка сохранения');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev: AuthorFormData) => ({ ...prev, [name]: value }));
  };

  if (loading && isEdit) {
    return <div className="text-center mt-5"><div className="spinner-border"></div></div>;
  }

  return (
    <div className="container mt-4">
      <div className="card">
        <div className="card-header">
          <h3>{isEdit ? '✏️ Редактирование автора' : '➕ Новый автор'}</h3>
        </div>
        <div className="card-body">
          {error && <div className="alert alert-danger">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="row">
              <div className="col-md-6 mb-3">
                <label className="form-label">Имя *</label>
                <input
                  type="text"
                  className="form-control"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">Фамилия *</label>
                <input
                  type="text"
                  className="form-control"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>
            <div className="mb-3">
              <label className="form-label">Дата рождения</label>
              <input
                type="date"
                className="form-control"
                name="birthDate"
                value={formData.birthDate}
                onChange={handleChange}
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Биография</label>
              <textarea
                className="form-control"
                name="biography"
                value={formData.biography}
                onChange={handleChange}
                rows={4}
              />
            </div>
            <div className="d-flex gap-2 mt-3">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Сохранение...' : 'Сохранить'}
              </button>
              <button type="button" className="btn btn-secondary" onClick={() => navigate('/authors')}>
                Отмена
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AuthorForm;