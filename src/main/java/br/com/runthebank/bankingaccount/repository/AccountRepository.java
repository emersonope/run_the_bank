package br.com.runthebank.bankingaccount.repository;


import br.com.runthebank.bankingaccount.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByBranchNumberAndAccountNumber(String branchNumber, String accountNumber);
}
