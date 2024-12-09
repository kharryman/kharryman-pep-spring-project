package com.example.controller;

import java.util.List;

import javax.websocket.server.PathParam;

import org.h2.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

@Controller
@ResponseBody
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    private String TAG = "SocialMediaController";
    private ObjectMapper objectMapper;

    SocialMediaController(AccountService accountService, MessageService messageService){
       this.accountService = accountService;
       this.messageService = messageService;
       this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account newAccount){        
        System.out.println(TAG + ".register called");
        //return new RequestEntity(HttpMethod.POST, );
        String username = newAccount.getUsername();
        String password = newAccount.getPassword();
        if(username == ""){
            System.out.println(TAG + ", username blank. Account NOT CREATED.");
            return ResponseEntity.status(400).body(null);
        } else if(password.length()<4){
            System.out.println(TAG + ", password<4. Account NOT CREATED.");
            return ResponseEntity.status(400).body(null);
        }else{
            Account accountExisting = accountService.getAccountByUsername(username);
            if(accountExisting != null){//RESOLVE WITH 409(CONFLICT)
                System.out.println(TAG + ", Username, " + username + ", already exists. Account NOT CREATED.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }else{
                accountService.register(newAccount);
                return ResponseEntity.ok().body(newAccount);
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Account passAccount){        
        String username = passAccount.getUsername();
        String password = passAccount.getPassword();
        if(username == ""){
            System.out.println(TAG + ", username blank. Account NOT CREATED.");
            return ResponseEntity.status(400).body(null);
        } else if(password.length()<4){
            System.out.println(TAG + ", password<4. Account NOT CREATED.");
            return ResponseEntity.status(400).body(null);
        }else{
            Account loginAccount = accountService.login(passAccount);
            if(loginAccount == null){
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }else{
                try{
                   String jsonString = objectMapper.writeValueAsString(loginAccount);
                   return ResponseEntity.ok().body(jsonString);
                }catch(Exception e){
                    return ResponseEntity.badRequest().body(null);
                }
                
            }
        }
    }

    @PostMapping("/messages")
    public ResponseEntity<String> createMessage(@RequestBody Message passMessage){
        String message = passMessage.getMessageText();
        if(message == ""){
            return ResponseEntity.status(400).body("Message can not be blank.");
        }else if(message.length()>=255){
            return ResponseEntity.status(400).body("Message can not be 255 characters long or more.");
        } else{
           int postedBy = passMessage.getPostedBy();
           Account checkAccount = accountService.getAccountById(postedBy);
           if(checkAccount == null){
             return ResponseEntity.status(400).body("Account does not exist.");
           }else{
             Message createdMessage = messageService.createMessage(passMessage);
             try{
                String jsonString = objectMapper.writeValueAsString(createdMessage);
                return ResponseEntity.ok().body(jsonString);
             }catch(Exception e){
                return ResponseEntity.status(400).body(null);
             }
           }
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<String> getMessages(){
        List<Message> messages = messageService.getAllMessages();
        try{
            String jsonString = objectMapper.writeValueAsString(messages);
            return ResponseEntity.ok().body(jsonString);
        }catch(Exception e){
            return ResponseEntity.ok().body("[]");
        }
    }

    @GetMapping("/messages/{message_id}")
    public ResponseEntity<String> getMessage(@PathVariable int message_id){
        Message message = messageService.getMessageById(message_id);
        if(message==null){
            return ResponseEntity.ok().body("");
        }else{
            try{
               String jsonString = objectMapper.writeValueAsString(message);
               return ResponseEntity.ok().body(jsonString);
            }catch(Exception e){
                return ResponseEntity.ok().body("");
            }
        }
    }

    @DeleteMapping("/messages/{message_id}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable int message_id){        
       Message message = messageService.getMessageById(message_id);
       if(message==null){
          return ResponseEntity.ok().body(null);
       }else{
          //messageService.deleteMessageById(message_id);
          messageService.deleteMessage(message);
          return ResponseEntity.ok().body(1);
       }        
    }

    @PatchMapping("/messages/{message_id}")
    public ResponseEntity<Integer> patchMessage(@PathVariable int message_id, @RequestBody Message passedMessage){
        Message message = messageService.getMessageById(message_id);
        if(message != null){
           String passedText = passedMessage.getMessageText();
           if(passedText.trim() == ""){
            System.out.println(TAG + ", Updated message text can not be blank.");
              return ResponseEntity.status(400).body(0);  
           }else if(passedText.length() > 255){
              System.out.println(TAG + ", Updated message text length can not be more than 255 characters.");
              return ResponseEntity.status(400).body(0);  
           }else{
               try{
                  Message updatedMessage = new Message(message_id, passedMessage.getPostedBy(), passedText, passedMessage.getTimePostedEpoch());
                  messageService.createMessage(updatedMessage);
                  return ResponseEntity.ok().body(1);  
               }catch(Exception e){
                  return ResponseEntity.ok().body(0);  
               }
           }
        }else{
            System.out.println(TAG + ", Message not updated, message does not exist.");
            return ResponseEntity.status(400).body(null);
        }
    }

    @GetMapping("/accounts/{account_id}/messages")
    public ResponseEntity<String> getAccountMessages(@PathVariable int account_id){
        List<Message> messages = messageService.getAccountMessages(account_id);
        try{
            String jsonString = objectMapper.writeValueAsString(messages);
            return ResponseEntity.ok().body(jsonString);
        }catch(Exception e){
            return ResponseEntity.ok().body("[]");
        }        
    }

}

