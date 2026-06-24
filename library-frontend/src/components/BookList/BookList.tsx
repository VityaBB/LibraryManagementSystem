import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { bookService } from '../../services/bookService';
import { Book } from '../../models/book.model';
import { PageResponse } from '../../models/pagination.model';

const BookList: React.FC = () => {
  const navigate = useNavigate();
  const [books, setBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  useEffect(() => {
    fetchBooks();
  }, [page]);

  const fetchBooks = async () => {
    try {
      setLoading(true);
      const response: PageResponse<Book> = await bookService.getAll({ page, size: 10 });
      setBooks(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
      setError('');
    } catch (err: any) {
      setError('Ошибка загрузки данных: ' + (err.message || ''));
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Вы уверены, что хотите удалить эту книгу?')) {
      try {
        await bookService.delete(id);
        fetchBooks();
      } catch (err) {
        setError('Ошибка удаления');
        console.error(err);
      }
    }
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center mt-5">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Загрузка...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>📚 Управление книгами</h2>
        <div>
          <button 
            className="btn btn-primary me-2"
            onClick={() => navigate('/books/new')}
          >
            ➕ Новая книга
          </button>
          <span className="badge bg-primary">Всего: {totalElements}</span>
        </div>
      </div>

      {error && (
        <div className="alert alert-danger alert-dismissible fade show" role="alert">
          {error}
          <button type="button" className="btn-close" onClick={() => setError('')}></button>
        </div>
      )}

      <div className="table-responsive">
        <table className="table table-striped table-hover">
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Название</th>
              <th>ISBN</th>
              <th>Год</th>
              <th>Издатель</th>
              <th>Авторы</th>
              <th>Жанры</th>
              <th>Доступно</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            {books.length === 0 ? (
              <tr>
                <td colSpan={9} className="text-center text-muted py-3">Нет данных</td>
              </tr>
            ) : (
              books.map((book) => (
                <tr key={book.id}>
                  <td>{book.id}</td>
                  <td>{book.title}</td>
                  <td>{book.isbn || '—'}</td>
                  <td>{book.publicationYear || '—'}</td>
                  <td>{book.publisherName || '—'}</td>
                  <td>{book.authors?.map(a => a.fullName).join(', ') || '—'}</td>
                  <td>{book.genres?.map(g => g.name).join(', ') || '—'}</td>
                  <td>
                    <span className={`badge ${(book.availableCopies || 0) > 0 ? 'bg-success' : 'bg-danger'}`}>
                      {book.availableCopies || 0}
                    </span>
                  </td>
                  <td>
                    <button
                      className="btn btn-sm btn-info me-1"
                      onClick={() => navigate(`/books/${book.id}/edit`)}
                      title="Редактировать"
                    >
                      ✏️
                    </button>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDelete(book.id!)}
                      title="Удалить книгу"
                    >
                      🗑️
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <nav className="mt-3">
          <ul className="pagination justify-content-center">
            <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
              <button className="page-link" onClick={() => setPage(page - 1)}>
                Назад
              </button>
            </li>
            {[...Array(Math.min(totalPages, 7))].map((_, i) => {
              let pageNum = i;
              if (totalPages > 7) {
                if (page < 3) pageNum = i;
                else if (page > totalPages - 4) pageNum = totalPages - 7 + i;
                else pageNum = page - 3 + i;
              }
              return (
                <li key={pageNum} className={`page-item ${page === pageNum ? 'active' : ''}`}>
                  <button className="page-link" onClick={() => setPage(pageNum)}>
                    {pageNum + 1}
                  </button>
                </li>
              );
            })}
            <li className={`page-item ${page >= totalPages - 1 ? 'disabled' : ''}`}>
              <button className="page-link" onClick={() => setPage(page + 1)}>
                Вперед
              </button>
            </li>
          </ul>
        </nav>
      )}
    </div>
  );
};

export default BookList;