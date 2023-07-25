package ibf2022batch03_miniproject.customer_invoice_manager.controller;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ibf2022batch03_miniproject.customer_invoice_manager.dto.UserDTO;
import ibf2022batch03_miniproject.customer_invoice_manager.event.NewUserEvent;
import ibf2022batch03_miniproject.customer_invoice_manager.exception.ApiException;
import ibf2022batch03_miniproject.customer_invoice_manager.form.LoginForm;
import ibf2022batch03_miniproject.customer_invoice_manager.form.NewPasswordForm;
import ibf2022batch03_miniproject.customer_invoice_manager.form.SettingsForm;
import ibf2022batch03_miniproject.customer_invoice_manager.form.UpdateForm;
import ibf2022batch03_miniproject.customer_invoice_manager.form.UpdatePasswordForm;
import ibf2022batch03_miniproject.customer_invoice_manager.model.HttpResponse;
import ibf2022batch03_miniproject.customer_invoice_manager.model.User;
import ibf2022batch03_miniproject.customer_invoice_manager.model.UserPrincipal;
import ibf2022batch03_miniproject.customer_invoice_manager.provider.TokenProvider;
import ibf2022batch03_miniproject.customer_invoice_manager.service.EventService;
import ibf2022batch03_miniproject.customer_invoice_manager.service.RoleService;
import ibf2022batch03_miniproject.customer_invoice_manager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static ibf2022batch03_miniproject.customer_invoice_manager.dtomapper.UserDTOMapper.toUser;
import static ibf2022batch03_miniproject.customer_invoice_manager.utils.UserUtils.getLoggedInUser;
import static ibf2022batch03_miniproject.customer_invoice_manager.utils.UserUtils.getAuthenticatedUser;
import static ibf2022batch03_miniproject.customer_invoice_manager.enumeration.EventType.*;
import static ibf2022batch03_miniproject.customer_invoice_manager.utils.ExceptionUtils.processError;
import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/api/user")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final EventService eventService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ApplicationEventPublisher publisher;

    @PostMapping (path = "/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) throws InterruptedException {
                TimeUnit.SECONDS.sleep(4);
                UserDTO userDto = userService.createUser(user);
                return ResponseEntity.created(getUri()).body(
                                HttpResponse.builder()
                                                .timeStamp(now().toString())
                                                .data(of("user", userDto))
                                                .message(String.format("User account created for user %s",
                                                                user.getFirstName()))
                                                .status(CREATED)
                                                .statusCode(CREATED.value())
                                                .build());
        }

    @PostMapping (path = "/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
           UserDTO user = authenticate(loginForm.getEmail(), loginForm.getPassword());
           return user.isUsingMfa() ? sendVerificationCode(user) : sendResponse(user);
        }

    @GetMapping(path = "/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication) {
                UserDTO user = userService.getUserByEmail(getAuthenticatedUser(authentication).getEmail());
                return ResponseEntity.ok().body(
                                HttpResponse.builder()
                                                .timeStamp(now().toString())
                                                .data(of("user", user, "events", eventService.getEventsByUserId(user.getId()),
                                                 "roles", roleService.getRoles()))
                                                .message("Profile Retrieved")
                                                .status(OK)
                                                .statusCode(OK.value())
                                                .build());
        }

    @PatchMapping(path = "/update")
    public ResponseEntity<HttpResponse> updateUser(@RequestBody @Valid UpdateForm user) {
           UserDTO updatedUser = userService.updateUserDetails(user);
                publisher.publishEvent(new NewUserEvent(updatedUser.getEmail(), PROFILE_UPDATE));
                return ResponseEntity.ok().body(
                                HttpResponse.builder()
                                                .timeStamp(now().toString())
                                                .data(of("user", updatedUser, "events",
                                                                eventService.getEventsByUserId(user.getId()), "roles",
                                                                roleService.getRoles()))
                                                .message("User updated")
                                                .status(OK)
                                                .statusCode(OK.value())
                                                .build());
        }

 // START - To reset password when user is not logged in

    @GetMapping(path = "/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) {
        UserDTO user = userService.verifyCode(email, code);
        publisher.publishEvent(new NewUserEvent(user.getEmail(), LOGIN_ATTEMPT_SUCCESS));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", user, "access_token", tokenProvider.createAccessToken(getUserPrincipal(user))
                                , "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(user))))
                        .message("Login Success")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping(path = "/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) {
        userService.resetPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Email sent. Please check your email to reset your password.")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping(path = "/verify/account/{key}")
    public ResponseEntity<HttpResponse> verifyAccount(@PathVariable("key") String key) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message(userService.verifyAccountKey(key).isEnabled() ? "Account already verified" : "Account verified")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping(path = "/verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyPasswordUrl(@PathVariable("key") String key) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        UserDTO user = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", user))
                        .message("Please enter a new password")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PutMapping(path = "/new/password")
    public ResponseEntity<HttpResponse> resetPasswordWithKey(@RequestBody @Valid NewPasswordForm form) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        userService.updatePassword(form.getUserId(), form.getPassword(), form.getConfirmPassword());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Password reset successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    // END - To reset password when user is not logged in

    @PatchMapping(path = "/update/password")
    public ResponseEntity<HttpResponse> updatePassword(Authentication authentication, @RequestBody @Valid UpdatePasswordForm form) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updatePassword(userDTO.getId(), form.getCurrentPassword(), form.getNewPassword(), form.getConfirmNewPassword());
        publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), PASSWORD_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userService.getUserById(userDTO.getId()), "events", eventService.getEventsByUserId(userDTO.getId()), 
                        "roles", roleService.getRoles()))
                        .message("Password updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PatchMapping (path = "/update/role/{roleName}")
    public ResponseEntity<HttpResponse> updateUserRole(Authentication authentication, @PathVariable("roleName") String roleName) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updateUserRole(userDTO.getId(), roleName);
        publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), ROLE_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(of("user", userService.getUserById(userDTO.getId()), "events", eventService.getEventsByUserId(userDTO.getId()),
                         "roles", roleService.getRoles()))
                        .timeStamp(now().toString())
                        .message("Role updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PatchMapping(path = "/update/settings")
    public ResponseEntity<HttpResponse> updateAccountSettings(Authentication authentication, @RequestBody @Valid SettingsForm form) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        userService.updateAccountSettings(userDTO.getId(), form.getEnabled(), form.getNotLocked());
        publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), ACCOUNT_SETTINGS_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(of("user", userService.getUserById(userDTO.getId()), "events", eventService.getEventsByUserId(userDTO.getId()), 
                        "roles", roleService.getRoles()))
                        .timeStamp(now().toString())
                        .message("Account settings updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PatchMapping(path = "/togglemfa")
    public ResponseEntity<HttpResponse> toggleMfa(Authentication authentication) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        UserDTO user = userService.toggleMfa(getAuthenticatedUser(authentication).getEmail());
        publisher.publishEvent(new NewUserEvent(user.getEmail(), MFA_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(of("user", user, "events", eventService.getEventsByUserId(user.getId()), 
                        "roles", roleService.getRoles()))
                        .timeStamp(now().toString())
                        .message("Multi-Factor Authentication updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PatchMapping("/update/image")
    public ResponseEntity<HttpResponse> updateProfileImage(Authentication authentication, @RequestParam("image") MultipartFile image) throws InterruptedException {
        UserDTO user = getAuthenticatedUser(authentication);
        userService.updateImage(user, image);
        publisher.publishEvent(new NewUserEvent(user.getEmail(), PROFILE_PICTURE_UPDATE));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(of("user", userService.getUserById(user.getId()), "events", eventService.getEventsByUserId(user.getId()), 
                        "roles", roleService.getRoles()))
                        .timeStamp(now().toString())
                        .message("Profile image updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping(path = "/image{fileName}", produces = MediaType.IMAGE_PNG_VALUE )
    public byte[] getProfileImage(@PathVariable("fileName") String fileName) throws Exception {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/Downloads/images/" + fileName));
    }

    @GetMapping(path = "/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {
        if(isHeaderAndTokenValid(request)) {
            String token = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
            UserDTO user = userService.getUserById(tokenProvider.getSubject(token, request));
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .data(of("user", user, "access_token", tokenProvider.createAccessToken(getUserPrincipal(user))
                                    , "refresh_token", token))
                            .message("Token refreshed")
                            .status(OK)
                            .statusCode(OK.value())
                            .build());
        } else {
            return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .reason("Refresh Token missing or invalid")
                            .developerMessage("Refresh Token missing or invalid")
                            .status(BAD_REQUEST)
                            .statusCode(BAD_REQUEST.value())
                            .build());
        }
    }

    @RequestMapping(path = "/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .reason("There is no mapping for a " + request.getMethod() + " request for this path on the server")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build());
    }

    private URI getUri() {
            return URI.create(fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }

    private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
            return ResponseEntity.ok().body(
                        HttpResponse.builder()
                                        .timeStamp(now().toString())
                                        .data(of("user", user, "access_token",
                                                        tokenProvider.createAccessToken(getUserPrincipal(user)),
                                                        "refresh_token",
                                                        tokenProvider.createRefreshToken(
                                                                        getUserPrincipal(user))))
                                        .message("Login Success")
                                        .status(OK)
                                        .statusCode(OK.value())
                                        .build());
    }

    private UserDTO authenticate(String email, String password) {
            UserDTO userByEmail = userService.getUserByEmail(email);
            try {
                    if (null != userByEmail) {
                            publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT));
                    }
                    Authentication authentication = authenticationManager
                                    .authenticate(unauthenticated(email, password));
                    UserDTO loggedInUser = getLoggedInUser(authentication);
                    if (!loggedInUser.isUsingMfa()) {
                            publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT_SUCCESS));
                    }
                    return loggedInUser;
            } catch (Exception exception) {
                    if (null != userByEmail) {
                            publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT_FAILURE));
                    }
                    processError(request, response, exception);
                    throw new ApiException(exception.getMessage());
            }
    }

    private UserPrincipal getUserPrincipal(UserDTO user) {
            return new UserPrincipal(toUser(userService.getUserByEmail(user.getEmail())),
                            roleService.getRoleByUserId(user.getId()));
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO user) {
            userService.sendVerificationCode(user);
            return ResponseEntity.ok().body(
                            HttpResponse.builder()
                                            .timeStamp(now().toString())
                                            .data(of("user", user))
                                            .message("Verification Code Sent")
                                            .status(OK)
                                            .statusCode(OK.value())
                                            .build());
    }

    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return  request.getHeader(AUTHORIZATION) != null
                &&  request.getHeader(AUTHORIZATION).startsWith("Bearer ")
                && tokenProvider.isTokenValid(
                        tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring("Bearer ".length()), request),
                        request.getHeader(AUTHORIZATION).substring("Bearer ".length())
                            );
    }
}
