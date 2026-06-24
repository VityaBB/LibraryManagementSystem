import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import LoanList from './components/LoanList/LoanList';
import LoanForm from './components/LoanForm/LoanForm';
import BookList from './components/BookList/BookList';
import BookForm from './components/BookForm/BookForm';
import AuthorList from './components/AuthorList/AuthorList';
import AuthorForm from './components/AuthorForm/AuthorForm';
import GenreList from './components/GenreList/GenreList';
import GenreForm from './components/GenreForm/GenreForm';
import PublisherList from './components/PublisherList/PublisherList';
import PublisherForm from './components/PublisherForm/PublisherForm';
import UserList from './components/UserList/UserList';
import UserForm from './components/UserForm/UserForm';
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
            <Route path="/loans" element={<LoanList />} />
            <Route path="/loans/new" element={<LoanForm />} />
            <Route path="/loans/:id/edit" element={<LoanForm />} />
            <Route path="/books" element={<BookList />} />
            <Route path="/books/new" element={<BookForm />} />
            <Route path="/books/:id/edit" element={<BookForm />} />
            <Route path="/authors" element={<AuthorList />} />
            <Route path="/authors/new" element={<AuthorForm />} />
            <Route path="/authors/:id/edit" element={<AuthorForm />} />
            <Route path="/genres" element={<GenreList />} />
            <Route path="/genres/new" element={<GenreForm />} />
            <Route path="/genres/:id/edit" element={<GenreForm />} />
            <Route path="/publishers" element={<PublisherList />} />
            <Route path="/publishers/new" element={<PublisherForm />} />
            <Route path="/publishers/:id/edit" element={<PublisherForm />} />
            <Route path="/users" element={<UserList />} />
            <Route path="/users/new" element={<UserForm />} />
            <Route path="/users/:id/edit" element={<UserForm />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;