package br.com.runthebank.bankingaccount.repository;

import br.com.runthebank.bankingaccount.enums.AccountType;
import br.com.runthebank.bankingaccount.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByCpfOrCnpj(String cpf, String cnpj);

    @Query("SELECT u FROM User u INNER JOIN u.accounts a " +
            "WHERE a.branchNumber = :branchNumber " +
            "AND a.accountNumber = :accountNumber " +
            "AND a.accountType = :accountType")
    Optional<User> findUserByAccount(@Param("branchNumber") String branchNumber,
                                     @Param("accountNumber") String accountNumber,
                                     @Param("accountType") AccountType accountType);

    @Query("SELECT u FROM User u INNER JOIN u.accounts a " +
            "WHERE a.branchNumber = :branchNumber " +
            "AND a.accountNumber = :accountNumber")
    Optional<User> findUserByAccountBranch(@Param("branchNumber") String branchNumber,
                                           @Param("accountNumber") String accountNumber);
}
