package com.example.websocketchat.repository;

import com.example.websocketchat.model.Message;
import com.example.websocketchat.model.User;
import com.example.websocketchat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRoomOrderByTimestampAsc(ChatRoom room);
    List<Message> findByRecipientAndSenderOrderByTimestampAsc(User recipient, User sender);
    List<Message> findBySenderAndRecipientOrderByTimestampAsc(User sender, User recipient);
    List<Message> findByRecipientOrderByTimestampAsc(User recipient);
}

