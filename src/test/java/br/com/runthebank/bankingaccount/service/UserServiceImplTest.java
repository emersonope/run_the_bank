package br.com.runthebank.bankingaccount.service;

import br.com.runthebank.bankingaccount.dto.*;
import br.com.runthebank.bankingaccount.enums.AccountType;
import br.com.runthebank.bankingaccount.enums.ResponseCode;
import br.com.runthebank.bankingaccount.enums.ResponseMessage;
import br.com.runthebank.bankingaccount.model.Account;
import br.com.runthebank.bankingaccount.model.User;
import br.com.runthebank.bankingaccount.repository.AccountRepository;
import br.com.runthebank.bankingaccount.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser_Success() {
        // dados
        UseRequestDto useRequestDto = new UseRequestDto();
        useRequestDto.setFirstName("John");
        useRequestDto.setLastName("Doe");
        useRequestDto.setGender("Male");
        useRequestDto.setAddress("123 Main St");
        useRequestDto.setStateOfOriging("NY");
        useRequestDto.setEmail("john.doe@example.com");
        useRequestDto.setCpf("123.456.789-09");
        useRequestDto.setPhoneNumber("123-456-7890");
        useRequestDto.setAccountType(AccountType.PF);

        // Simular a criação da conta no repositório
        Account account = Account.createNewAccount(useRequestDto.getAccountType());
        Mockito.when(accountRepository.save(any())).thenReturn(account);

        // Simular a criação do usuário no repositório
        User user = User.createUserFromRequest(useRequestDto, account);
        Mockito.when(userRepository.save(any())).thenReturn(user);
        user.addAccount(account);
        user = userRepository.save(user);

        ResponseEntity<UserResponseDto> response = userService.createAccount(useRequestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(ResponseCode.SUCCESS, response.getBody().getResponseCode());
        assertEquals(ResponseMessage.USER_CREATED_SUCCESSFULLY, response.getBody().getResponseMessage());

        // Verificar se a conta foi associada ao usuário
        assertTrue(user.getAccounts().contains(account));
        assertEquals(user, account.getUser());
    }

    @Test
    void testCreateAccount_InvalidData() {
        UseRequestDto useRequestDto = new UseRequestDto();

        ResponseEntity<UserResponseDto> response = userService.createAccount(useRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAddAccount_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<UserResponseDto> response = userService.addAccount(userId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAddAccount_InvalidData() {
        Long userId = null;

        ResponseEntity<UserResponseDto> response = userService.addAccount(userId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
