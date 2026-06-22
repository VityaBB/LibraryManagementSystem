package com.library.service;

import com.library.dto.PublisherDTO;
import com.library.model.Publisher;
import com.library.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherRepository publisherRepository;

    public Page<PublisherDTO> getAllPublishers(Pageable pageable) {
        return publisherRepository.findAll(pageable).map(this::convertToDTO);
    }

    public PublisherDTO getPublisherById(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Издатель не найден"));
        return convertToDTO(publisher);
    }

    @Transactional
    public PublisherDTO createPublisher(PublisherDTO dto) {
        Publisher publisher = new Publisher();
        publisher.setName(dto.getName());
        publisher.setAddress(dto.getAddress());
        publisher.setPhone(dto.getPhone());
        publisher.setEmail(dto.getEmail());
        publisher.setWebsite(dto.getWebsite());
        return convertToDTO(publisherRepository.save(publisher));
    }

    @Transactional
    public PublisherDTO updatePublisher(Long id, PublisherDTO dto) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Издатель не найден"));
        publisher.setName(dto.getName());
        publisher.setAddress(dto.getAddress());
        publisher.setPhone(dto.getPhone());
        publisher.setEmail(dto.getEmail());
        publisher.setWebsite(dto.getWebsite());
        return convertToDTO(publisherRepository.save(publisher));
    }

    @Transactional
    public void deletePublisher(Long id) {
        publisherRepository.deleteById(id);
    }

    private PublisherDTO convertToDTO(Publisher publisher) {
        PublisherDTO dto = new PublisherDTO();
        dto.setId(publisher.getId());
        dto.setName(publisher.getName());
        dto.setAddress(publisher.getAddress());
        dto.setPhone(publisher.getPhone());
        dto.setEmail(publisher.getEmail());
        dto.setWebsite(publisher.getWebsite());
        return dto;
    }
}