import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { loanService } from '../../services/loanService';
import { bookService } from '../../services/bookService';
import { userService } from '../../services/userService';

interface LoanFormData {
  bookId: number;
  userId: number;
  status: 'ACTIVE' | 'RETURNED' | 'OVERDUE';
  fineAmount: number;
}

const LoanForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEdit = !!id;

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [books, setBooks] = useState<any[]>([]);
  const [users, setUsers] = useState<any[]>([]);

  const [formData, setFormData] = useState<LoanFormData>({
    bookId: 0,
    userId: 0,
    status: 'ACTIVE',
    fineAmount: 0
  });

  useEffect(() => {
    fetchBooksAndUsers();
    if (isEdit) {
      fetchLoan();
    }
  }, [id]);

  const fetchBooksAndUsers = async () => {
    try {
      const [booksRes, usersRes] = await Promise.all([
        bookService.getAll({ page: 0, size: 100 }),
        userService.getAll({ page: 0, size: 100 })
      ]);
      setBooks(booksRes.content);
      setUsers(usersRes.content);
    } catch (err) {
      setError('Ошибка загрузки данных');
      console.error(err);
    }
  };

  const fetchLoan = async () => {
    try {
      setLoading(true);
      const loan = await loanService.getById(Number(id));
      setFormData({
        bookId: loan.bookId,
        userId: loan.userId,
        status: loan.status,
        fineAmount: loan.fineAmount
      });
    } catch (err) {
      setError('Ошибка загрузки выдачи');
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
        await loanService.update(Number(id), formData);
      } else {
        await loanService.create(formData);
      }
      navigate('/loans');
    } catch (err) {
      setError('Ошибка сохранения');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLSelectElement | HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev: LoanFormData) => ({
      ...prev,
      [name]: name === 'bookId' || name === 'userId' || name === 'fineAmount' 
        ? Number(value) 
        : value
    }));
  };

  if (loading && isEdit) {
    return <div className="text-center mt-5"><div className="spinner-border"></div></div>;
  }

  return (
    <div className="container mt-4">
      <div className="card">
        <div className="card-header">
          <h3>{isEdit ? '✏️ Редактирование выдачи' : '➕ Новая выдача'}</h3>
        </div>
        <div className="card-body">
          {error && <div className="alert alert-danger">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="row">
              <div className="col-md-6 mb-3">
                <label className="form-label">Книга *</label>
                <select
                  className="form-select"
                  name="bookId"
                  value={formData.bookId}
                  onChange={handleChange}
                  required
                >
                  <option value={0}>Выберите книгу</option>
                  {books.map((book: any) => (
                    <option key={book.id} value={book.id}>
                      {book.title} (доступно: {book.availableCopies || 0})
                    </option>
                  ))}
                </select>
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">Пользователь *</label>
                <select
                  className="form-select"
                  name="userId"
                  value={formData.userId}
                  onChange={handleChange}
                  required
                >
                  <option value={0}>Выберите пользователя</option>
                  {users.map((user: any) => (
                    <option key={user.id} value={user.id}>
                      {user.fullName} ({user.email})
                    </option>
                  ))}
                </select>
              </div>
            </div>
            {isEdit && (
              <div className="row">
                <div className="col-md-6 mb-3">
                  <label className="form-label">Статус</label>
                  <select
                    className="form-select"
                    name="status"
                    value={formData.status}
                    onChange={handleChange}
                  >
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="RETURNED">RETURNED</option>
                    <option value="OVERDUE">OVERDUE</option>
                  </select>
                </div>
                <div className="col-md-6 mb-3">
                  <label className="form-label">Штраф</label>
                  <input
                    type="number"
                    className="form-control"
                    name="fineAmount"
                    value={formData.fineAmount}
                    onChange={handleChange}
                    step="0.01"
                    min="0"
                  />
                </div>
              </div>
            )}
            <div className="d-flex gap-2 mt-3">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Сохранение...' : 'Сохранить'}
              </button>
              <button type="button" className="btn btn-secondary" onClick={() => navigate('/loans')}>
                Отмена
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoanForm;