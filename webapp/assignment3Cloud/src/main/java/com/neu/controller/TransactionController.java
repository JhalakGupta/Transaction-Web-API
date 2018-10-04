package com.neu.controller;


import com.neu.pojo.Transaction;
import com.neu.repository.TransactionRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.models.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Base64;

import static com.neu.controller.LoginController.checkPassword;

@RestController
@RequestMapping("/")
@Api(value="onlinestore", description="Operations pertaining to products in Online Store")
public class TransactionController {


    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;


    @RequestMapping(value = "/transaction", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity list(Model model,HttpServletRequest request) {


        String authHeader = request.getHeader("Authorization");
        ArrayList<Transaction> transactionList =new ArrayList<>();
        if (authHeader == null) {
            System.out.print("You are not logged in ! ");
            //return null;
            return new ResponseEntity<>("Enter basic authentication header! You are not logged in !! ", HttpStatus.UNAUTHORIZED);

        } else if (authHeader.startsWith("Basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String pass = values[1];
            if (checkIfUserExists(username)) {
                String sql = "SELECT password FROM user_details WHERE username = ?";
                String encryptedPassword = (String) jdbcTemplate.queryForObject(
                        sql, new Object[]{username}, String.class);
                if (encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {

                        //return null;
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.UNAUTHORIZED);

                    } else {


                        Iterable<Transaction> productList = transactionRepository.findAll();


                        for (Transaction transaction : productList) {
                            if (transaction.getUsername().equals(username)) {
                                transactionList.add(transaction);
                            }
                        }
                        //if no content found
                        if (transactionList != null && transactionList.size() == 0) {
                            return new ResponseEntity<>("No transactions found!", HttpStatus.BAD_REQUEST);
                        }

                    }
                }

            }else{
                return new ResponseEntity<>("User not registered! ", HttpStatus.UNAUTHORIZED);
            }
        }
        //return transactionList;
        return new ResponseEntity<>(transactionList, HttpStatus.OK);
    }


    @RequestMapping(value = "/transaction", method = RequestMethod.POST)
    public ResponseEntity createTransaction(@RequestBody Transaction transaction, HttpServletRequest request) {
        // productService.saveProduct(product);

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            System.out.print("null header ");
            //return "Enter basic authentication header! You are not logged in !! ";
            return new ResponseEntity<>("Enter basic authentication header! You are not logged in !! ", HttpStatus.UNAUTHORIZED);

        } else if (authHeader.startsWith("Basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String pass = values[1];
            if (checkIfUserExists(username)) {
                String sql = "SELECT password FROM user_details WHERE username = ?";
                String encryptedPassword = (String) jdbcTemplate.queryForObject(
                        sql, new Object[]{username}, String.class);
                if (encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {
                        //return "UserName and Password does not match !";
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.BAD_REQUEST);

                    }

                    transaction.setUsername(username);
                    transactionRepository.save(transaction);
                    return new ResponseEntity<>("Transaction saved successfully", HttpStatus.CREATED);

                }
            }else{
                return new ResponseEntity<>("User not registered! ", HttpStatus.UNAUTHORIZED);
            }
        }

        return new ResponseEntity<>("Transaction not saved", HttpStatus.BAD_REQUEST);
    }


        public boolean checkIfUserExists(String username){
        String sql = "SELECT count(username) FROM user_details WHERE username = ?";

        int count = (Integer)jdbcTemplate.queryForObject(
                sql, new Object[] { username }, Integer.class);
        System.out.print(count);
        if(count >0){
            System.out.print("User exists");
            return true;
        }
        return false;

    }

@RequestMapping(value="/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable String id,HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            System.out.print("null header ");
            //return null;
            return new ResponseEntity<>("Enter basic authentication header! You are not logged in !! ", HttpStatus.UNAUTHORIZED);
        } else if (authHeader.startsWith("Basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String pass = values[1];
            if (checkIfUserExists(username)) {
                String sql = "SELECT password FROM user_details WHERE username = ?";
                String encryptedPassword = (String) jdbcTemplate.queryForObject(
                        sql, new Object[]{username}, String.class);
                if (encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.UNAUTHORIZED);
                    } else {
                        Iterable<Transaction> productList = transactionRepository.findAll();
                        //find whether a transaction exists with gievn id
                        boolean isValidId = false;
                        for (Transaction transactions : productList) {
                            if (transactions.getId().equals(id)) {
                                isValidId = true;
                                break;
                            }
                        }
                        if (!isValidId) {
                            return new ResponseEntity<>("No transaction found with gievn ID", HttpStatus.NO_CONTENT);
                        }
                        //find whether user is authorized
                        boolean isAuthorized = false;
                        for (Transaction transactions : productList) {
                            if (transactions.getUsername().equals(username)) {
                                isAuthorized = true;
                                break;
                            }
                        }
                        if (!isAuthorized) {
                            return new ResponseEntity<>("You are not authorized to delete", HttpStatus.UNAUTHORIZED);
                        }
                        for (Transaction transactions : productList) {
                            if (transactions.getUsername().equals(username) && transactions.getId().equals(id)) {
                                transactionRepository.deleteById(id);
                                return new ResponseEntity<>("Transaction deleted successfully", HttpStatus.OK);
                            }
                        }
                    }
                }
            }else{
                return new ResponseEntity<>("User not registered! ", HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("Transaction did not delete", HttpStatus.BAD_REQUEST);
    }
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateProduct(@PathVariable String id, @RequestBody Transaction transaction,HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            System.out.print("null header ");
            //return null;
            return new ResponseEntity<>("Enter basic authentication header! You are not logged in !! ", HttpStatus.UNAUTHORIZED);
        } else if (authHeader.startsWith("Basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String pass = values[1];
            if (checkIfUserExists(username)) {
                String sql = "SELECT password FROM user_details WHERE username = ?";
                String encryptedPassword = (String) jdbcTemplate.queryForObject(
                        sql, new Object[]{username}, String.class);
                if (encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.UNAUTHORIZED);
                    } else {
                        Iterable<Transaction> productList = transactionRepository.findAll();
                        //find whether a transaction exists with given id
                        boolean isValidId = false;
                        for (Transaction transactions : productList) {
                            if (transactions.getId().equals(id)) {
                                isValidId = true;
                                break;
                            }
                        }
                        if (!isValidId) {
                            return new ResponseEntity<>("No transaction found with given ID", HttpStatus.NO_CONTENT);
                        }
                        //find whether user is authorized
                        boolean isAuthorized = false;
                        for (Transaction transactions : productList) {
                            if (transactions.getUsername().equals(username)) {
                                isAuthorized = true;
                                break;
                            }
                        }
                        if (!isAuthorized) {
                            return new ResponseEntity<>("You are not authorized to update", HttpStatus.UNAUTHORIZED);
                        }
                        for (Transaction transactions : productList) {
                            if (transactions.getUsername().equals(username) && transactions.getId().equals(id)) {
                                Transaction storedProduct = transactionRepository.getOne(id);
                                storedProduct.setDescription(transaction.getDescription());
                                storedProduct.setAmount(transaction.getAmount());
                                storedProduct.setCategory(transaction.getCategory());
                                storedProduct.setMerchant(transaction.getMerchant());
                                transactionRepository.save(storedProduct);
                                return new ResponseEntity<>("Transaction updated successfully", HttpStatus.CREATED);
                            }
                        }
                    }
                }
            }else{
                return new ResponseEntity<>("User not registered! ", HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("Transaction did not update", HttpStatus.BAD_REQUEST);
    }



}

