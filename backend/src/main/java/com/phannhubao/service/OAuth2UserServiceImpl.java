package com.phannhubao.service;

import com.phannhubao.entity.Role;
import com.phannhubao.entity.User;
import com.phannhubao.repository.RoleRepository;
import com.phannhubao.repository.UserRepository;
import com.phannhubao.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        String providerId = oAuth2User.getAttribute("sub"); // Google
        if (providerId == null) {
            providerId = oAuth2User.getAttribute("id"); // Facebook
        }
        if (!StringUtils.hasText(providerId)) {
            throw new OAuth2AuthenticationException("Provider ID not found from OAuth2 provider");
        }

        String email = oAuth2User.getAttribute("email");
        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }
        email = email.trim().toLowerCase();

        String name = oAuth2User.getAttribute("name");
        if (!StringUtils.hasText(name)) {
            name = email.split("@")[0];
        }

        String avatarUrl = extractAvatarUrl(provider, oAuth2User);

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider().equalsIgnoreCase(provider)) {
                // Update provider info to allow seamless testing of multiple social logins with the same email
                user.setProvider(provider);
                user.setProviderId(providerId);
            }
            user = updateExistingUser(user, name, avatarUrl);
        } else {
            user = registerNewUser(provider, providerId, name, email, avatarUrl);
        }

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private String extractAvatarUrl(String provider, OAuth2User oAuth2User) {
        if ("google".equals(provider)) {
            return oAuth2User.getAttribute("picture");
        }
        if ("facebook".equals(provider)) {
            Object picture = oAuth2User.getAttribute("picture");
            if (picture instanceof Map) {
                Map<?, ?> pictureMap = (Map<?, ?>) picture;
                Object data = pictureMap.get("data");
                if (data instanceof Map) {
                    Map<?, ?> dataMap = (Map<?, ?>) data;
                    Object url = dataMap.get("url");
                    if (url instanceof String) {
                        return (String) url;
                    }
                }
            }
        }
        return null;
    }

    private User registerNewUser(String provider, String providerId, String name, String email, String avatarUrl) {
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));

        User user = User.builder()
                .name(name)
                .email(email)
                .provider(provider)
                .providerId(providerId)
                .avatarUrl(avatarUrl)
                .roles(Collections.singleton(userRole))
                .build();

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, String name, String avatarUrl) {
        existingUser.setName(name);
        if (avatarUrl != null) {
            existingUser.setAvatarUrl(avatarUrl);
        }
        return userRepository.save(existingUser);
    }
}
