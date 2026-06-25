import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
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

@NgModule({
  declarations: [
    AppComponent,
    LoanListComponent,
    LoanFormComponent,
    BookListComponent,
    BookFormComponent,
    AuthorListComponent,
    AuthorFormComponent,
    GenreListComponent,
    GenreFormComponent,
    PublisherListComponent,
    PublisherFormComponent,
    UserListComponent,
    UserFormComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }