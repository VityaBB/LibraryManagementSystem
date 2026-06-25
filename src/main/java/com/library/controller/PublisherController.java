package com.library.controller;

import com.library.dto.create.PublisherCreateDTO;
import com.library.dto.update.PublisherUpdateDTO;
import com.library.dto.response.PublisherResponseDTO;
import com.library.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/publishers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublisherController {
    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<Page<PublisherResponseDTO>> getAllPublishers(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String name) {
        System.out.println("📥 Получен запрос с параметрами:");
        System.out.println("  name: " + name);
        Page<PublisherResponseDTO> publishers = publisherService.searchPublishers(name, pageable);
        return ResponseEntity.ok(publishers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherResponseDTO> getPublisherById(@PathVariable Long id) {
        return ResponseEntity.ok(publisherService.getPublisherById(id));
    }

    @PostMapping
    public ResponseEntity<PublisherResponseDTO> createPublisher(@RequestBody PublisherCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(publisherService.createPublisher(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublisherResponseDTO> updatePublisher(@PathVariable Long id, @RequestBody PublisherUpdateDTO dto) {
        return ResponseEntity.ok(publisherService.updatePublisher(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}