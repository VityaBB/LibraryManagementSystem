import React, { useState, useEffect } from 'react';
import { authorService } from '../../services/authorService';
import type { Author } from '../../models/author.model';
import type { PageResponse } from '../../models/pagination.model';

const AuthorList: React.FC = () => {
  const [authors, setAuthors] = useState<Author[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  useEffect(() => {
    fetchAuthors();
  }, [page]);

  const fetchAuthors = async () => {
    try {
      setLoading(true);
      const response: PageResponse<Author> = await authorService.getAll({ page, size: 10 });
      setAuthors(response.content);
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
    if (window.confirm('Вы уверены, что хотите удалить этого автора?')) {
      try {
        await authorService.delete(id);
        fetchAuthors();
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
        <h2>✍️ Управление авторами</h2>
        <span className="badge bg-primary">Всего: {totalElements}</span>
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
              <th>Имя</th>
              <th>Фамилия</th>
              <th>Полное имя</th>
              <th>Дата рождения</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            {authors.length === 0 ? (
              <tr>
                <td colSpan={6} className="text-center text-muted py-3">Нет данных</td>
              </tr>
            ) : (
              authors.map((author) => (
                <tr key={author.id}>
                  <td>{author.id}</td>
                  <td>{author.firstName}</td>
                  <td>{author.lastName}</td>
                  <td>{author.fullName}</td>
                  <td>{author.birthDate ? new Date(author.birthDate).toLocaleDateString() : '—'}</td>
                  <td>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDelete(author.id!)}
                      title="Удалить автора"
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

export default AuthorList;