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
  const [selectedBookAvailable, setSelectedBookAvailable] = useState<number>(0);

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
      const book = books.find(b => b.id === loan.bookId);
      if (book) {
        setSelectedBookAvailable(book.availableCopies || 0);
      }
    } catch (err) {
      setError('Ошибка загрузки выдачи');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!isEdit) {
      const book = books.find(b => b.id === formData.bookId);
      if (book && (book.availableCopies || 0) <= 0) {
        setError('Нет доступных экземпляров этой книги!');
        return;
      }
    }

    try {
      setLoading(true);
      if (isEdit) {
        await loanService.update(Number(id), formData);
      } else {
        await loanService.create(formData);
      }
      navigate('/loans');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Ошибка сохранения';
      setError(errorMessage);
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

    if (name === 'bookId') {
      const book = books.find(b => b.id === Number(value));
      setSelectedBookAvailable(book?.availableCopies || 0);
    }
  };

  if (loading && isEdit) {
    return <div className="text-center mt-5"><div className="spinner-border"></div></div>;
  }

  const isBookAvailable = !isEdit && formData.bookId > 0 && selectedBookAvailable <= 0;

  return (
    <div className="container mt-4">
      <div className="card">
        <div className="card-header">
          <h3>{isEdit ? '✏️ Редактирование выдачи' : '➕ Новая выдача'}</h3>
        </div>
        <div className="card-body">
          {error && <div className="alert alert-danger">{error}</div>}

          {!isEdit && isBookAvailable && (
            <div className="alert alert-warning">
              ⚠️ У этой книги нет доступных экземпляров. Выдача невозможна.
            </div>
          )}

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
                  disabled={isEdit}
                >
                  <option value={0}>Выберите книгу</option>
                  {books.map((book: any) => (
                    <option key={book.id} value={book.id}>
                      {book.title} (доступно: {book.availableCopies || 0})
                    </option>
                  ))}
                </select>
                {!isEdit && formData.bookId > 0 && (
                  <small className="text-muted">
                    Доступно экземпляров: {selectedBookAvailable}
                  </small>
                )}
              </div>

              <div className="col-md-6 mb-3">
                <label className="form-label">Пользователь *</label>
                <select
                  className="form-select"
                  name="userId"
                  value={formData.userId}
                  onChange={handleChange}
                  required
                  disabled={isEdit}
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
              <button 
                type="submit" 
                className="btn btn-primary" 
                disabled={loading || (!isEdit && isBookAvailable)}
              >
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