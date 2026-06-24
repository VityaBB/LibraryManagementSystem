import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { publisherService } from '../../services/publisherService';

interface PublisherFormData {
  name: string;
  address: string;
  phone: string;
  email: string;
  website: string;
}

const PublisherForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState<PublisherFormData>({
    name: '',
    address: '',
    phone: '',
    email: '',
    website: ''
  });

  useEffect(() => {
    if (isEdit) {
      fetchPublisher();
    }
  }, [id]);

  const fetchPublisher = async () => {
    try {
      setLoading(true);
      const publisher = await publisherService.getById(Number(id));
      setFormData({
        name: publisher.name,
        address: publisher.address || '',
        phone: publisher.phone || '',
        email: publisher.email || '',
        website: publisher.website || ''
      });
    } catch (err) {
      setError('Ошибка загрузки издателя');
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
        await publisherService.update(Number(id), formData);
      } else {
        await publisherService.create(formData);
      }
      navigate('/publishers');
    } catch (err) {
      setError('Ошибка сохранения');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev: PublisherFormData) => ({ ...prev, [name]: value }));
  };

  if (loading && isEdit) {
    return <div className="text-center mt-5"><div className="spinner-border"></div></div>;
  }

  return (
    <div className="container mt-4">
      <div className="card">
        <div className="card-header">
          <h3>{isEdit ? '✏️ Редактирование издателя' : '➕ Новый издатель'}</h3>
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
              <label className="form-label">Адрес</label>
              <input
                type="text"
                className="form-control"
                name="address"
                value={formData.address}
                onChange={handleChange}
              />
            </div>
            <div className="row">
              <div className="col-md-6 mb-3">
                <label className="form-label">Телефон</label>
                <input
                  type="text"
                  className="form-control"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">Email</label>
                <input
                  type="email"
                  className="form-control"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                />
              </div>
            </div>
            <div className="mb-3">
              <label className="form-label">Веб-сайт</label>
              <input
                type="text"
                className="form-control"
                name="website"
                value={formData.website}
                onChange={handleChange}
              />
            </div>
            <div className="d-flex gap-2 mt-3">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Сохранение...' : 'Сохранить'}
              </button>
              <button type="button" className="btn btn-secondary" onClick={() => navigate('/publishers')}>
                Отмена
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default PublisherForm;