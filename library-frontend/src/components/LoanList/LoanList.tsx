import React, { useState, useEffect } from 'react';
import { loanService } from '../../services/loanService';
import type { Loan } from '../../models/loan.model';
import type { PageResponse } from '../../models/pagination.model';

const LoanList: React.FC = () => {
  const [loans, setLoans] = useState<Loan[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  useEffect(() => {
    fetchLoans();
  }, [page]);

  const fetchLoans = async () => {
    try {
      setLoading(true);
      const response: PageResponse<Loan> = await loanService.getAll({ page, size: 10 });
      setLoans(response.content);
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
    if (window.confirm('Вы уверены, что хотите удалить эту запись?')) {
      try {
        await loanService.delete(id);
        fetchLoans();
      } catch (err) {
        setError('Ошибка удаления');
        console.error(err);
      }
    }
  };

  const handleReturn = async (id: number) => {
    try {
      await loanService.returnBook(id);
      fetchLoans();
    } catch (err) {
      setError('Ошибка при возврате книги');
      console.error(err);
    }
  };

  const getStatusBadge = (status: string) => {
    const statusMap: { [key: string]: string } = {
      'ACTIVE': 'bg-success',
      'RETURNED': 'bg-secondary',
      'OVERDUE': 'bg-danger'
    };
    return `badge ${statusMap[status] || 'bg-info'}`;
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
        <h2>📖 Управление выдачами книг</h2>
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
              <th>Книга</th>
              <th>Читатель</th>
              <th>Дата выдачи</th>
              <th>Срок возврата</th>
              <th>Статус</th>
              <th>Штраф</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            {loans.length === 0 ? (
              <tr>
                <td colSpan={8} className="text-center text-muted py-3">Нет данных</td>
              </tr>
            ) : (
              loans.map((loan) => (
                <tr key={loan.id}>
                  <td>{loan.id}</td>
                  <td>{loan.bookTitle || '—'}</td>
                  <td>{loan.userName || '—'}</td>
                  <td>{new Date(loan.loanDate).toLocaleDateString()}</td>
                  <td>{new Date(loan.dueDate).toLocaleDateString()}</td>
                  <td>
                    <span className={getStatusBadge(loan.status)}>
                      {loan.status}
                    </span>
                  </td>
                  <td>{(loan.fineAmount || 0).toFixed(2)}</td>
                  <td>
                    {(loan.status === 'ACTIVE' || loan.status === 'OVERDUE') && (
                      <button
                        className="btn btn-sm btn-success me-1"
                        onClick={() => handleReturn(loan.id!)}
                        title="Вернуть книгу"
                      >
                        ↩️
                      </button>
                    )}
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDelete(loan.id!)}
                      title="Удалить запись"
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

export default LoanList;