package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>{
    //GET ALL Messages:
    List<Message> findAll();
    //GET Message BY messageId:
    Optional<Message> findByMessageId(Integer messageId);    
    //DELETE Message BY messageId:
    //@Modifying
    //@Query("DELETE FROM message WHERE messageId:messageId")
    //int deleteByMessageId(@Param("messageId") int messageId);
    //void deleteByMessageId(Integer messageId);
   
    void delete(Message message);

    //CHECK IF Message EXISTS BY messageId:
    boolean existsByMessageId(Integer messageId);
    //GET ALL Messages BY postedBy:
    List<Message> findByPostedBy(Integer postedBy);
}
