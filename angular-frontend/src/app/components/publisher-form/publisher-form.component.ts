import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PublisherService } from '../../services/publisher.service';
import { PublisherCreateDTO, PublisherUpdateDTO } from '../../models/publisher.model';

@Component({
  selector: 'app-publisher-form',
  templateUrl: './publisher-form.component.html',
  styleUrls: ['./publisher-form.component.css']
})
export class PublisherFormComponent implements OnInit {
  isEdit = false;
  id?: number;
  loading = false;
  error = '';

  formData: PublisherCreateDTO & PublisherUpdateDTO = {
    name: '',
    address: '',
    phone: '',
    email: '',
    website: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private publisherService: PublisherService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.isEdit = !!this.id;
    if (this.isEdit) {
      this.fetchPublisher();
    }
  }

  fetchPublisher(): void {
    this.loading = true;
    this.publisherService.getById(this.id!).subscribe({
      next: (publisher) => {
        this.formData = {
          name: publisher.name,
          address: publisher.address || '',
          phone: publisher.phone || '',
          email: publisher.email || '',
          website: publisher.website || ''
        };
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Ошибка загрузки издателя';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    const request = this.isEdit
      ? this.publisherService.update(this.id!, this.formData)
      : this.publisherService.create(this.formData as PublisherCreateDTO);
    request.subscribe({
      next: () => {
        this.router.navigate(['/publishers']);
      },
      error: (err) => {
        this.error = 'Ошибка сохранения';
        console.error(err);
        this.loading = false;
      }
    });
  }
}