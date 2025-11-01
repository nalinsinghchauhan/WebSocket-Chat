package com.example.websocketchat.repository;

import com.example.websocketchat.model.ReadReceipt;
import com.example.websocketchat.model.Message;
import com.example.websocketchat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReadReceiptRepository extends JpaRepository<ReadReceipt, Long> {
    Optional<ReadReceipt> findByMessageAndUser(Message message, User user);
    boolean existsByMessageAndUser(Message message, User user);
}

