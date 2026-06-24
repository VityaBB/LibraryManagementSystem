import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import LoanList from './components/LoanList/LoanList';
import BookList from './components/BookList/BookList';
import AuthorList from './components/AuthorList/AuthorList';
import GenreList from './components/GenreList/GenreList';
import PublisherList from './components/PublisherList/PublisherList';
import UserList from './components/UserList/UserList';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
          <div className="container-fluid">
            <Link className="navbar-brand" to="/">
              📚 Library Management System
            </Link>
            <button
              className="navbar-toggler"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#navbarNav"
            >
              <span className="navbar-toggler-icon"></span>
            </button>
            <div className="collapse navbar-collapse" id="navbarNav">
              <ul className="navbar-nav ms-auto">
                <li className="nav-item">
                  <Link className="nav-link" to="/">📖 Выдачи</Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/books">📚 Книги</Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/authors">✍️ Авторы</Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/genres">🏷️ Жанры</Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/publishers">🏢 Издатели</Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/users">👤 Пользователи</Link>
                </li>
              </ul>
            </div>
          </div>
        </nav>
        <div className="container-fluid">
          <Routes>
            <Route path="/" element={<LoanList />} />
            <Route path="/books" element={<BookList />} />
            <Route path="/authors" element={<AuthorList />} />
            <Route path="/genres" element={<GenreList />} />
            <Route path="/publishers" element={<PublisherList />} />
            <Route path="/users" element={<UserList />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;