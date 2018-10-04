package com.neu.controller;

import java.util.Base64;

import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.json.JSONObject;

@RestController
public class LoginController implements CommandLineRunner{

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping("/time")
    public ResponseEntity<?> getCurrentTime(HttpServletRequest request)
    {
        String authHeader =request.getHeader("Authorization");
        if(authHeader == null) {
            System.out.print("null header ");
            //return "Enter basic authentication header! You are not logged in !! ";
            return new ResponseEntity<>("Enter basic authentication header! You are not logged in !! ", HttpStatus.OK);

        }else if(authHeader.startsWith("Basic")){
            // Authorization: Basic base64credentials
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String pass= values[1];
            if(checkIfUserExists(username)){
                String sql = "SELECT password FROM usertable WHERE username = ?";
                String encryptedPassword = (String)jdbcTemplate.queryForObject(
                        sql, new Object[] { username }, String.class);
                if(encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {
                        //return "UserName and Password does not match !";
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.OK);

                    }
                }
            }else{
                //return "User not registered!";
                return new ResponseEntity<>("User not registered! ", HttpStatus.OK);
            }


        }


        // return "Current Time in milliseconds = "+System.currentTimeMillis();LocalDateTime.now();
        return new ResponseEntity<>(LocalDateTime.now(), HttpStatus.OK);

    }

    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    public String registerUser(@RequestBody String jos)
    {
        JSONObject jo = new JSONObject(jos);
        String name = (String) jo.get("name");
        String password = (String) jo.get("password");
        String encryptedPass = hashPassword(password);
        try {
            String[] s = new String[2];
            s[0] = name;
            s[1]=encryptedPass;
            if(checkIfUserExists(name)){
                return name+" "+ "This User is already registered";

            }else{
                run(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return name+" "+ encryptedPass;


    }




    @Override
    public void run(String... strings) throws Exception {

        System.out.print("Inserting");
        if(strings != null && strings.length>0) {
            System.out.print(strings[0]);
            System.out.print(strings[1]);
            String usr = strings[0];
            String pass = strings[1];
            String sql = "INSERT INTO usertable " +
                    "(username, password) VALUES (?, ?)";

            //jdbcTemplate.execute(sql);
            jdbcTemplate.update(sql, new Object[] { usr,
                    pass
            });

            //jdbcTemplate.batchUpdate("INSERT INTO usertable(username,password) values (usr,pass)");
        }
        // log.info("Querying for customer records where first_name = 'Josh':");

    }

    public static String hashPassword(String password_plaintext) {
        String salt = BCrypt.gensalt(12);
        String hashed_password = BCrypt.hashpw(password_plaintext, salt);

        return(hashed_password);
    }

    public boolean checkIfUserExists(String username){
        String sql = "SELECT count(username) FROM usertable WHERE username = ?";

        int count = (Integer)jdbcTemplate.queryForObject(
                sql, new Object[] { username }, Integer.class);
        System.out.print(count);
        if(count >0){
            System.out.print("user exists");
            return true;
        }
        return false;

    }

    public static boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return(password_verified);
    }
}
