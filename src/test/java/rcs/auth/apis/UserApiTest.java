package rcs.auth.apis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import rcs.auth.models.api.AuthenticatedUser;
import rcs.auth.models.api.UpdatePasswordRequest;
import rcs.auth.models.api.UserRegistrationRequest;
import rcs.auth.models.db.UserAuthority;
import rcs.auth.services.UserCredentialsService;
import rcs.auth.utils.AuthUtils;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserApiTest {

    @Mock
    private AuthUtils authUtils;

    @Mock
    private UserCredentialsService userCredentialsService;

    @InjectMocks
    private UserApi target;

    @Test
    public void testGetLoggedInUser() {
        // Arrange
        User user = new User(
                "username",
                "password",
                UserAuthority.ADMIN.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));

        when(authUtils.tryGetLoggedInUser())
                .thenReturn(user);

        // Act
        ResponseEntity<AuthenticatedUser> actual = target.getLoggedInUser();

        // Assert
        assertThat(actual.getBody().getUsername()).isEqualTo(user.getUsername());
        assertThat(actual.getBody().getRoles())
                .containsAll(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));
    }

    @Test
    public void testCreateUser() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest("username", "password");

        // Act
        ResponseEntity<Void> actual = target.createUser(request);

        // Assert
        assertThat(actual.getStatusCodeValue()).isEqualTo(200);
        verify(userCredentialsService).save(request);
    }

    @Test
    public void testUpdatePassword() {
        // Arrange
        String username = "username";
        UpdatePasswordRequest request = new UpdatePasswordRequest("newPassword");

        // Act
        ResponseEntity<Void> actual = target.updatePassword(username, request);

        // Assert
        assertThat(actual.getStatusCodeValue()).isEqualTo(200);
        verify(userCredentialsService).updatePassword(username, request.getPassword());
    }

    @Test
    public void testDeleteUser() {
        // Arrange
        String username = "username";

        // Act
        ResponseEntity<Void> actual = target.deleteUser(username);

        // Assert
        assertThat(actual.getStatusCodeValue()).isEqualTo(200);
        verify(userCredentialsService).delete(username);
    }
}