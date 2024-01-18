package br.com.runthebank.bankingaccount.repository;


import br.com.runthebank.bankingaccount.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserCpfOrUserCnpj(String cpf, String cnpj);
}
