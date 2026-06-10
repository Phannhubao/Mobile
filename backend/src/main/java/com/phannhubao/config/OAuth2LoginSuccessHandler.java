package com.phannhubao.config;

import com.phannhubao.entity.RefreshToken;
import com.phannhubao.repository.RefreshTokenRepository;
import com.phannhubao.security.CustomUserDetails;
import com.phannhubao.security.HttpCookieOAuth2AuthorizationRequestRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Value("${app.oauth2.authorizedRedirectUris}")
    private List<String> authorizedRedirectUris;

    @Value("${app.jwt.refreshExpirationMs}")
    private Long refreshExpirationMs;

    public OAuth2LoginSuccessHandler(
            JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository,
            HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository
    ) {
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.println(">>> OAuth2LoginSuccessHandler: onAuthenticationSuccess triggered!");
        String targetUrl = determineTargetUrl(request, response, authentication);
        System.out.println(">>> OAuth2LoginSuccessHandler: Redirecting to targetUrl: " + targetUrl);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) {
        System.out.println(">>> OAuth2LoginSuccessHandler: determineTargetUrl triggered!");
        Optional<String> redirectUri = HttpCookieOAuth2AuthorizationRequestRepository.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(request, redirectUri.get())) {
            throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI (" + redirectUri.get() + ") and can't proceed with the authentication");
        }

        String baseUri = redirectUri.orElse(authorizedRedirectUris.isEmpty() ? "http://localhost:3000/oauth2/redirect" : authorizedRedirectUris.get(0));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);
        
        // Clean and create new refresh token
        refreshTokenRepository.deleteByUser(userDetails.getUser());
        refreshTokenRepository.flush();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(userDetails.getUser())
                .token(java.util.UUID.randomUUID().toString())
                .expiryDate(java.time.Instant.now().plusMillis(refreshExpirationMs))
                .build();
        refreshToken = refreshTokenRepository.save(refreshToken);

        return UriComponentsBuilder.fromUriString(baseUri)
                .queryParam("token", accessToken)
                .queryParam("refreshToken", refreshToken.getToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(HttpServletRequest request, String uri) {
        URI clientRedirectUri = URI.create(uri);

        if (isSameOriginAsRequest(request, clientRedirectUri)) {
            return true;
        }

        return authorizedRedirectUris.stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    String authorizedScheme = authorizedURI.getScheme();
                    String clientScheme = clientRedirectUri.getScheme();

                    // Schemes must match
                    if (!authorizedScheme.equalsIgnoreCase(clientScheme)) {
                        return false;
                    }

                    // For custom URI schemes (e.g. authapp://), host may be null — compare full URI string
                    String authorizedHost = authorizedURI.getHost();
                    String clientHost = clientRedirectUri.getHost();
                    if (authorizedHost == null || clientHost == null) {
                        // Fall back to comparing the full URI strings
                        return authorizedRedirectUri.equalsIgnoreCase(uri);
                    }

                    // Standard HTTP/HTTPS: match host and port
                    return authorizedHost.equalsIgnoreCase(clientHost)
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }

    private boolean isSameOriginAsRequest(HttpServletRequest request, URI clientRedirectUri) {
        if (!"/oauth2/redirect.html".equals(clientRedirectUri.getPath())) {
            return false;
        }

        String requestScheme = request.getScheme();
        String requestHost = request.getServerName();
        int requestPort = request.getServerPort();
        int clientPort = clientRedirectUri.getPort();

        return requestScheme.equalsIgnoreCase(clientRedirectUri.getScheme())
                && requestHost.equalsIgnoreCase(clientRedirectUri.getHost())
                && normalizePort(requestScheme, requestPort) == normalizePort(clientRedirectUri.getScheme(), clientPort);
    }

    private int normalizePort(String scheme, int port) {
        if (port != -1) {
            return port;
        }
        return "https".equalsIgnoreCase(scheme) ? 443 : 80;
    }
}
