package com.example.backend.services;

import com.example.backend.dtos.in.buyings.CreateBuyingDto;
import com.example.backend.dtos.in.buyings.CreateManyBuyingsDto;
import com.example.backend.dtos.out.buyings.BuyingDto;
import com.example.backend.entities.*;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.BadRequestException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.repositories.ApartmentRepository;
import com.example.backend.repositories.BuyingRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.specifications.BuyingSpecifications;
import com.example.backend.types.Role;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuyingService {

    private final UserService userService;

    private final BuyingRepository buyingRepository;

    private final ProfileRepository profileRepository;

    private final ApartmentRepository apartmentRepository;

    public BuyingService(UserService userService,
                         BuyingRepository buyingRepository,
                         ProfileRepository profileRepository,
                         ApartmentRepository apartmentRepository) {
        this.userService = userService;
        this.buyingRepository = buyingRepository;
        this.profileRepository = profileRepository;
        this.apartmentRepository = apartmentRepository;
    }

    public Long create(Integer apartmentId, User user, CreateBuyingDto dto) {
        Role role = userService.getCurrentUserRoleInApartment(user, apartmentId);

        if (role == null)
            throw new AccessForbiddenException("You can't create buyings in this apartment.");

        if (!dto.isPublic() && dto.assignedTo() != null)
            throw new BadRequestException("Buying shouldn't be private and have assigned user at the same time.");

        Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(() ->
                new ResourceNotFoundException("ApartmentNotFound")
        );
        Profile createdBy = user.getCurrentProfile();
        Profile assignedTo = null;
        if (dto.assignedTo() != null) {
            assignedTo = profileRepository.findById(dto.assignedTo()).orElseThrow(() ->
                    new ResourceNotFoundException("Assigned user not found.")
            );
        }

        Buying buying = buyingRepository.save(
                new Buying(
                        createdBy,
                        assignedTo,
                        apartment,
                        dto.name(),
                        dto.quantity(),
                        dto.category(),
                        dto.isPublic()
                )
        );

        return buying.getId();
    }

    public List<Long> createMany(Integer apartmentId, User user, CreateManyBuyingsDto dto) {
        Role role = userService.getCurrentUserRoleInApartment(user, apartmentId);

        if (role == null)
            throw new AccessForbiddenException("You can't create buyings in this apartment.");

        if (!dto.isPublic() && dto.assignedTo() != null)
            throw new BadRequestException("Buying shouldn't be private and have assigned user at the same time.");

        Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(() ->
                new ResourceNotFoundException("ApartmentNotFound")
        );
        Profile createdBy = user.getCurrentProfile();
        Profile assignedTo;
        if (dto.assignedTo() != null) {
            assignedTo = profileRepository.findById(dto.assignedTo()).orElseThrow(() ->
                    new ResourceNotFoundException("Assigned user not found.")
            );
        } else {
            assignedTo = null;
        }

        List<Buying> buyings = buyingRepository.saveAll(dto.buyings().stream().map(buyingDto -> new Buying(
                createdBy,
                assignedTo,
                apartment,
                buyingDto.name(),
                buyingDto.quantity(),
                buyingDto.category(),
                dto.isPublic()
        )).toList());

        return buyings.stream().map(Buying::getId).toList();
    }

    public List<BuyingDto> get(Integer apartmentId, User user, Integer assignedTo, Boolean isPublic) {
        Role role = userService.getCurrentUserRoleInApartment(user, apartmentId);

        if (role == null)
            throw new AccessForbiddenException("You can't get buyings in this apartment.");

        if (assignedTo != null && isPublic != null && !isPublic)
            throw new BadRequestException("You can't view others buying lists.");

        Specification<Buying> spec = Specification
                .where(BuyingSpecifications.byApartment(apartmentId))
                .and(BuyingSpecifications.byAssignedTo(assignedTo))
                .and(BuyingSpecifications.byPublic(isPublic, user.getId()));

        return buyingRepository.findAll(spec).stream().map(BuyingDto::new).toList();
    }
}
