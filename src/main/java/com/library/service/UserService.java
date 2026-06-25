package com.library.service;

import com.library.dto.create.UserCreateDTO;
import com.library.dto.update.UserUpdateDTO;
import com.library.dto.response.UserResponseDTO;
import com.library.model.User;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToResponseDTO);
    }

    public Page<UserResponseDTO> searchUsers(String firstName, String lastName, String phone, Pageable pageable) {
        if (firstName != null && firstName.isEmpty()) {
            firstName = null;
        }
        if (lastName != null && lastName.isEmpty()) {
            lastName = null;
        }
        if (phone != null && phone.isEmpty()) {
            phone = null;
        }
        System.out.println("🔍 Поиск пользователей с параметрами:");
        System.out.println("  firstName: " + firstName);
        System.out.println("  lastName: " + lastName);
        System.out.println("  phone: " + phone);
        return userRepository.searchUsers(firstName, lastName, phone, pageable)
                .map(this::convertToResponseDTO);
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return convertToResponseDTO(user);
    }

    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return convertToResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO createUser(UserCreateDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPasswordHash());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRole(dto.getRole());
        user.setIsActive(true);
        return convertToResponseDTO(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPasswordHash() != null) {
            user.setPasswordHash(dto.getPasswordHash());
        }
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        if (dto.getIsActive() != null) {
            user.setIsActive(dto.getIsActive());
        }
        return convertToResponseDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setRegistrationDate(user.getRegistrationDate() != null ? user.getRegistrationDate().toString() : null);
        dto.setIsActive(user.getIsActive());
        dto.setRole(user.getRole());
        return dto;
    }
}