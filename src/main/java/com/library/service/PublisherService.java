package com.library.service;

import com.library.dto.create.PublisherCreateDTO;
import com.library.dto.update.PublisherUpdateDTO;
import com.library.dto.response.PublisherResponseDTO;
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

    public Page<PublisherResponseDTO> getAllPublishers(Pageable pageable) {
        return publisherRepository.findAll(pageable).map(this::convertToResponseDTO);
    }

    public PublisherResponseDTO getPublisherById(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Издатель не найден"));
        return convertToResponseDTO(publisher);
    }

    @Transactional
    public PublisherResponseDTO createPublisher(PublisherCreateDTO dto) {
        Publisher publisher = new Publisher();
        publisher.setName(dto.getName());
        publisher.setAddress(dto.getAddress());
        publisher.setPhone(dto.getPhone());
        publisher.setEmail(dto.getEmail());
        publisher.setWebsite(dto.getWebsite());
        return convertToResponseDTO(publisherRepository.save(publisher));
    }

    @Transactional
    public PublisherResponseDTO updatePublisher(Long id, PublisherUpdateDTO dto) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Издатель не найден"));
        if (dto.getName() != null) {
            publisher.setName(dto.getName());
        }
        if (dto.getAddress() != null) {
            publisher.setAddress(dto.getAddress());
        }
        if (dto.getPhone() != null) {
            publisher.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            publisher.setEmail(dto.getEmail());
        }
        if (dto.getWebsite() != null) {
            publisher.setWebsite(dto.getWebsite());
        }
        return convertToResponseDTO(publisherRepository.save(publisher));
    }

    @Transactional
    public void deletePublisher(Long id) {
        publisherRepository.deleteById(id);
    }

    private PublisherResponseDTO convertToResponseDTO(Publisher publisher) {
        PublisherResponseDTO dto = new PublisherResponseDTO();
        dto.setId(publisher.getId());
        dto.setName(publisher.getName());
        dto.setAddress(publisher.getAddress());
        dto.setPhone(publisher.getPhone());
        dto.setEmail(publisher.getEmail());
        dto.setWebsite(publisher.getWebsite());
        return dto;
    }
}