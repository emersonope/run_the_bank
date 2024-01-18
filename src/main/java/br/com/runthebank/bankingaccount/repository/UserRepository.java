package br.com.runthebank.bankingaccount.repository;

import br.com.runthebank.bankingaccount.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByCpfOrCnpj(String cpf, String cnpj);
}
