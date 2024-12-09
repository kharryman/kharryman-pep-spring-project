package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    
    MessageService(MessageRepository newMessageRepository){
        this.messageRepository = newMessageRepository;
    }

    public Message createMessage(Message passMessage){
        Message newMessage = messageRepository.save(passMessage);       
        return newMessage;
    }

    public List<Message> getAllMessages(){
        List<Message> messages = messageRepository.findAll();
        return messages;
    }

    public List<Message> getAccountMessages(int accountId){
        List<Message> messages = messageRepository.findByPostedBy(accountId);
        return messages;
    }

    public Message getMessageById(int message_id){
        Optional<Message> foundMessage = messageRepository.findByMessageId(message_id);
        return foundMessage.orElse(null);
    }

    public boolean isExistsById(int message_id){
        return messageRepository.existsByMessageId(message_id);
    }

    //@Transactional
    public Integer deleteMessage(Message message){
        messageRepository.delete(message);
        return 1;
    }

   
}
