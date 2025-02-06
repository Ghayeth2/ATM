package com.atm.business.concretes;

import com.atm.business.abstracts.EmailServices;
import com.atm.core.utils.validators.UserNameExistsValidator;
import com.atm.dao.concretes.TempUserDaoImpl;
import com.atm.dao.daos.TempUserDao;
import com.atm.model.dtos.EmailDetails;
import com.atm.model.dtos.TempUser;
import com.atm.model.dtos.UserDto;
import com.atm.model.entities.ConfirmationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing the Temp User (stored temporary in-Memory database)
 * services.
 */
@ExtendWith(MockitoExtension.class)
class TempUserManagerTest {

    @Mock
    private TempUserDaoImpl tempUserDao;

    @Mock
    private ConfirmationTokenManager tokenManager;

    @Mock
    private MessageServices messageServices;

    @Mock
    private EmailServices emailSenderServicesManager;

    @Mock
    private UserNameExistsValidator userNameExistsValidator;

    @InjectMocks
    private TempUserManager tempUserManager;

    private TempUser tempUser;
    private ConfirmationToken cToken;

    /**
     * Setting the objects will be used in some of the tests, to keep away
     * from repeated codes.
     */
    @BeforeEach
    void setUp() {
        // Mocked TempUser
        tempUser = TempUser.builder().email("test@gmail.com").firstName("first")
                .lastName("last").build();
        // Mocked confirmation token
        cToken = ConfirmationToken.builder().token("token")
                .email(tempUser.getEmail()).build();
    }

    /**
     * This method is to save the temp user's data into Redis, and generate
     * confirmation token in case no existed email in database. Otherwise,
     * it will throw UserNameExistsException.
     */
    @Test
    void tempUserServices_SaveTempUser() {
        // Request data UserDto
        UserDto userDto = UserDto.builder().firstName("fs").lastName("las")
                        .email("em@gmail.com").password("pas").build();

        // Mocking validating username
        Mockito.when(userNameExistsValidator.validate(Mockito.anyString()))
                .thenReturn(Boolean.FALSE);
        // Mocking saving the temp user
        Mockito.doNothing().when(tempUserDao).save(Mockito.any(TempUser.class));
        // Mocking generating confirmation token
        Mockito.when(tokenManager.newConfirmationToken(Mockito.anyString()))
                .thenReturn(cToken);
        // Mocking saving the generated token to Redis
        Mockito.doNothing().when(tokenManager)
                .saveConfirmationToken(Mockito.any());
        // Mocking sending mail to registered user
        Mockito.doNothing().when(emailSenderServicesManager)
                .sendEmail(Mockito.any( EmailDetails.class));
        // Mocking response message from the service
        Mockito.when(messageServices.getMessage(Mockito.anyString()))
                .thenReturn("User registered successfully");
        // Calling the under-test service method
        String res = tempUserManager.save(userDto);
        // Asserting everything went ok
        assertEquals("User registered successfully", res);
    }

    /**
     * Testing TempUserService's findByToken which returns TempUser
     * object from Redis upon requesting from other services.
     */
    @Test
    void tempUserService_FindByToken() {
        // Mocking confirmation token response of returning token
        Mockito.when(tokenManager.findByToken(Mockito.anyString()))
                .thenReturn(cToken);
        // Mocking finding Temp User by email
        Mockito.when(tempUserDao.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(tempUser));
        // Calling the service
        TempUser res = tempUserManager.findByToken("token");
        // Asserting the result
        assertEquals(tempUser.getEmail(), res.getEmail());
    }

    /**
     * Updating temp user's notConfirmed field after user is confirmed Test.
     */
    @Test
    void tempUserService_UpdateNotConfirmed() {
        // Mocking finding temp user object
        Mockito.when(tempUserDao.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(tempUser));
        // Mocking saving new data of temp user
        Mockito.doNothing().when(tempUserDao).save(Mockito.any());
        // calling the service
        tempUserManager.updateNotConfirmed(tempUser.getEmail());
        // Verifying test is reaching last line of the method under test
        Mockito.verify(tempUserDao).save(Mockito.any());
    }

    /**
     * Testing findByUsername method
     */
    @Test
    void tempUserService_FindByUsername() {
        // Preparing fake response to return TempUser
        Mockito.when(tempUserDao.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(tempUser));
        // Calling the method under test
        Optional<TempUser> res = tempUserManager.findByUsername(tempUser.getEmail());
        // Asserting the result
        assertEquals(tempUser.getEmail(), res.get().getEmail());
    }

}