import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoanListComponent } from './components/loan-list/loan-list.component';
import { LoanFormComponent } from './components/loan-form/loan-form.component';
import { BookListComponent } from './components/book-list/book-list.component';
import { BookFormComponent } from './components/book-form/book-form.component';
import { AuthorListComponent } from './components/author-list/author-list.component';
import { AuthorFormComponent } from './components/author-form/author-form.component';
import { GenreListComponent } from './components/genre-list/genre-list.component';
import { GenreFormComponent } from './components/genre-form/genre-form.component';
import { PublisherListComponent } from './components/publisher-list/publisher-list.component';
import { PublisherFormComponent } from './components/publisher-form/publisher-form.component';
import { UserListComponent } from './components/user-list/user-list.component';
import { UserFormComponent } from './components/user-form/user-form.component';

const routes: Routes = [
  { path: '', redirectTo: '/loans', pathMatch: 'full' },
  { path: 'loans', component: LoanListComponent },
  { path: 'loans/new', component: LoanFormComponent },
  { path: 'loans/:id/edit', component: LoanFormComponent },
  { path: 'books', component: BookListComponent },
  { path: 'books/new', component: BookFormComponent },
  { path: 'books/:id/edit', component: BookFormComponent },
  { path: 'authors', component: AuthorListComponent },
  { path: 'authors/new', component: AuthorFormComponent },
  { path: 'authors/:id/edit', component: AuthorFormComponent },
  { path: 'genres', component: GenreListComponent },
  { path: 'genres/new', component: GenreFormComponent },
  { path: 'genres/:id/edit', component: GenreFormComponent },
  { path: 'publishers', component: PublisherListComponent },
  { path: 'publishers/new', component: PublisherFormComponent },
  { path: 'publishers/:id/edit', component: PublisherFormComponent },
  { path: 'users', component: UserListComponent },
  { path: 'users/new', component: UserFormComponent },
  { path: 'users/:id/edit', component: UserFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }