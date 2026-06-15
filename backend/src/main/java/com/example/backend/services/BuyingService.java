package com.example.backend.services;

import com.example.backend.dtos.in.buyings.CreateBuyingDto;
import com.example.backend.dtos.in.buyings.CreateManyBuyingsDto;
import com.example.backend.dtos.out.buyings.BuyingDto;
import com.example.backend.dtos.out.common.IdResponse;
import com.example.backend.dtos.out.common.StatusResponse;
import com.example.backend.entities.*;
import com.example.backend.exceptions.AccessForbiddenException;
import com.example.backend.exceptions.BadRequestException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.intefaces.BuyingNotificationHandler;
import com.example.backend.repositories.BuyingRepository;
import com.example.backend.repositories.ProfileRepository;
import com.example.backend.specifications.BuyingSpecifications;
import com.example.backend.types.Role;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class BuyingService {

    private final BuyingRepository buyingRepository;

    private final ProfileRepository profileRepository;

    private final BuyingNotificationHandler buyingNotificationHandler;

    public BuyingService(
             BuyingRepository buyingRepository,
             ProfileRepository profileRepository,
             BuyingNotificationHandler buyingNotificationHandler) {
        this.buyingRepository = buyingRepository;
        this.profileRepository = profileRepository;
        this.buyingNotificationHandler = buyingNotificationHandler;
    }

    @Transactional
    public IdResponse<Long> create(User user, CreateBuyingDto dto) {
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
                        dto.name(),
                        dto.quantity(),
                        dto.category(),
                        dto.isPublic()
                )
        );

        buyingNotificationHandler.handleBuyingCreate(createdBy, assignedTo);

        return new IdResponse<>(buying.getId());
    }

    @Transactional
    public List<IdResponse<Long>> createMany(User user, CreateManyBuyingsDto dto) {
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
                buyingDto.name(),
                buyingDto.quantity(),
                buyingDto.category(),
                dto.isPublic()
        )).toList());

        buyingNotificationHandler.handleBuyingCreate(createdBy, assignedTo);

        return buyings.stream().map(buying -> new IdResponse<>(buying.getId())).toList();
    }

    public List<BuyingDto> get(Integer apartmentId, User user, Integer assignedTo, Boolean isPublic) {
        Specification<Buying> spec = Specification
                .where(BuyingSpecifications.byApartment(apartmentId))
                .and(BuyingSpecifications.byAssignedTo(assignedTo))
                .and(BuyingSpecifications.byPublic(isPublic, user.getId()));

        return buyingRepository.findAll(spec, Sort.by("category")).stream().map(BuyingDto::new).toList();
    }

    public StatusResponse changeStatus(Integer apartmentId, User user, Long buyingId) {
        Buying buying = buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId).orElseThrow(
                () -> new ResourceNotFoundException("Buying not found.")
        );

        Profile currentProfile = user.getCurrentProfile();
        if (
                buying.getIsPublic() &&
                currentProfile.getRole() == Role.INHABITANT &&
                buying.getCompletedAt() != null &&
                !Objects.equals(buying.getCompletedBy().getId(), currentProfile.getId()) ||
                !buying.getIsPublic() &&
                !Objects.equals(buying.getCreatedBy().getId(), currentProfile.getId())
        )
            throw new AccessForbiddenException("You can't change status of others buyings.");

        if (buying.getCompletedAt() == null) {
            buying.setCompletedBy(currentProfile);
            buying.setCompletedAt(Instant.now());
        } else {
            buying.setCompletedBy(null);
            buying.setCompletedAt(null);
        }

        buyingRepository.save(buying);

        return new StatusResponse(buying.getCompletedAt() != null);
    }

    private void checkDeleteAuthority(Profile profile, Buying buying) {
        if (!Objects.equals(buying.getCreatedBy().getId(), profile.getId()))
            throw new AccessForbiddenException("You can't delete others buyings.");
    }

    public void deleteOne(Integer apartmentId, User user, Long buyingId) {
        Buying buying = buyingRepository.findByCreatedBy_Apartment_IdAndId(apartmentId, buyingId).orElseThrow(
                () -> new ResourceNotFoundException("Buying not found.")
        );

        Profile profile = user.getCurrentProfile();

        if (profile.getRole() != Role.INHABITANT) {
            buyingRepository.delete(buying);
            return;
        }

        checkDeleteAuthority(profile, buying);

        buyingRepository.delete(buying);
    }

    @Transactional
    public void deleteMany(User user, Integer apartmentId, List<Long> buyingIds) {
        List<Buying> buyings = buyingRepository.findAllByCreatedBy_Apartment_IdAndIdIn(apartmentId, buyingIds);

        Profile profile = user.getCurrentProfile();

        if (profile.getRole() != Role.INHABITANT) {
            buyingRepository.deleteAll(buyings);
            return;
        }

        for (Buying buying : buyings) {
            checkDeleteAuthority(profile, buying);
        }

        buyingRepository.deleteAll(buyings);
    }
}
