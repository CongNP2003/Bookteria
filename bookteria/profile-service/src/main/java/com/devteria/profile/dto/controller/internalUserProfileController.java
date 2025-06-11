package com.devteria.profile.dto.controller;

import com.devteria.profile.dto.request.ProfileCreationRequest;
import com.devteria.profile.dto.response.UserProfileResponse;
import com.devteria.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class internalUserProfileController {
    UserProfileService userProfileService;

    @PostMapping("/internal/users")
    UserProfileResponse creteProfile(@RequestBody ProfileCreationRequest response) {
        return userProfileService.createProfile(response);
    }

}
