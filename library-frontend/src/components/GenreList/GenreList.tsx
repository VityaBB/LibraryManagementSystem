import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { genreService } from '../../services/genreService';
import { Genre } from '../../models/genre.model';
import { PageResponse } from '../../models/pagination.model';

const GenreList: React.FC = () => {
  const navigate = useNavigate();
  const [genres, setGenres] = useState<Genre[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  useEffect(() => {
    fetchGenres();
  }, [page]);

  const fetchGenres = async () => {
    try {
      setLoading(true);
      const response: PageResponse<Genre> = await genreService.getAll({ page, size: 10 });
      setGenres(response.content);
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
    if (window.confirm('Вы уверены, что хотите удалить этот жанр?')) {
      try {
        await genreService.delete(id);
        fetchGenres();
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
        <h2>🏷️ Управление жанрами</h2>
        <div>
          <button 
            className="btn btn-primary me-2"
            onClick={() => navigate('/genres/new')}
          >
            ➕ Новый жанр
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
              <th>Описание</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            {genres.length === 0 ? (
              <tr>
                <td colSpan={4} className="text-center text-muted py-3">Нет данных</td>
              </tr>
            ) : (
              genres.map((genre) => (
                <tr key={genre.id}>
                  <td>{genre.id}</td>
                  <td>{genre.name}</td>
                  <td>{genre.description || '—'}</td>
                  <td>
                    <button
                      className="btn btn-sm btn-info me-1"
                      onClick={() => navigate(`/genres/${genre.id}/edit`)}
                      title="Редактировать"
                    >
                      ✏️
                    </button>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDelete(genre.id!)}
                      title="Удалить жанр"
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

export default GenreList;