package com.neu.controller;



import com.neu.pojo.TransactionAttachments;
import com.neu.pojo.TransactionDetails;
import com.neu.pojo.UserDetails;
import com.neu.repository.TransactionAttachmentsRepository;
import com.neu.repository.TransactionRepository;
import com.neu.repository.UserRepository;
import io.swagger.annotations.Api;
import com.google.gson.JsonObject;

//import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.neu.controller.LoginController.checkPassword;

@RestController
@RequestMapping("/")
@Api(value="onlinestore", description="Operations pertaining to products in Online Store")
public class TransactionController {


    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionAttachmentsRepository transactionAttachmentRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment environment;




    @RequestMapping(value = "/transaction", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity list(Model model, HttpServletRequest request) {


        String authHeader = request.getHeader("Authorization");
        ArrayList<TransactionDetails> transactionList = new ArrayList<>();
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
                String sql = "SELECT password FROM user_info WHERE username = ?";
                String encryptedPassword = (String) jdbcTemplate.queryForObject(
                        sql, new Object[]{username}, String.class);
                if (encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {

                        //return null;
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.UNAUTHORIZED);

                    } else {


                        Iterable<TransactionDetails> productList = transactionRepository.findAll();


                        for (TransactionDetails transaction : productList) {
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

            } else {
                return new ResponseEntity<>("User not registered! ", HttpStatus.UNAUTHORIZED);
            }
        }
        //return transactionList;
        return new ResponseEntity<>(transactionList, HttpStatus.OK);
    }


    @RequestMapping(value = "/transaction", method = RequestMethod.POST)
    public ResponseEntity createTransaction(@RequestBody TransactionDetails transaction, HttpServletRequest request) {
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
                String sql = "SELECT password FROM user_info WHERE username = ?";
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
            } else {
                return new ResponseEntity<>("User not registered! ", HttpStatus.UNAUTHORIZED);
            }
        }

        return new ResponseEntity<>("Transaction not saved", HttpStatus.BAD_REQUEST);
    }


    public boolean checkIfUserExists(String username) {
        String sql = "SELECT count(username) FROM user_info WHERE username = ?";

        int count = (Integer) jdbcTemplate.queryForObject(
                sql, new Object[]{username}, Integer.class);
        System.out.print(count);
        if (count > 0) {
            System.out.print("User exists");
            return true;
        }
        return false;

    }
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable String id, HttpServletRequest request) {
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
                        Iterable<TransactionDetails> productList = transactionRepository.findAll();
                        //find whether a transaction exists with gievn id
                        boolean isValidId = false;
                        for (TransactionDetails transactions : productList) {
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
                        for (TransactionDetails transactions : productList) {
                            if (transactions.getUsername().equals(username)) {
                                isAuthorized = true;
                                break;
                            }
                        }
                        if (!isAuthorized) {
                            return new ResponseEntity<>("You are not authorized to delete", HttpStatus.UNAUTHORIZED);
                        }
                        for (TransactionDetails transactions : productList) {

                            if (transactions.getUsername().equals(username) && transactions.getId().equals(id)) {
                                transactionRepository.deleteById(id);
                                return new ResponseEntity<>("Transaction deleted successfully", HttpStatus.OK);
                            }
                        }
                    }
                }
            } else {
                return new ResponseEntity<>("User not registered! ", HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("Transaction did not delete", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateProduct(@PathVariable String id, @RequestBody TransactionDetails transaction, HttpServletRequest request) {
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
                String sql = "SELECT password FROM user_info WHERE username = ?";
                String encryptedPassword = (String) jdbcTemplate.queryForObject(
                        sql, new Object[]{username}, String.class);
                if (encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.UNAUTHORIZED);
                    } else {
                        Iterable<TransactionDetails> productList = transactionRepository.findAll();
                        //find whether a transaction exists with given id
                        boolean isValidId = false;
                        for (TransactionDetails transactions : productList) {
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
                        for (TransactionDetails transactions : productList) {
                            if (transactions.getUsername().equals(username)) {
                                isAuthorized = true;
                                break;
                            }
                        }
                        if (!isAuthorized) {
                            return new ResponseEntity<>("You are not authorized to update", HttpStatus.UNAUTHORIZED);
                        }
                        for (TransactionDetails transactions : productList) {
                            if (transactions.getUsername().equals(username) && transactions.getId().equals(id)) {
                                TransactionDetails storedProduct = transactionRepository.getOne(id);
                                storedProduct.setDescription(transactions.getDescription());
                                storedProduct.setAmount(transactions.getAmount());
                                storedProduct.setCategory(transactions.getCategory());
                                storedProduct.setMerchant(transactions.getMerchant());
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
            } else {
                return new ResponseEntity<>("User not registered! ", HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("Transaction did not update", HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(value = "/{id}/attachments", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    ResponseEntity<String> getAttachments(@PathVariable(value = "id") String transactionId, HttpServletRequest request) {


//        UUID uid = UUID.fromString(transactionId);
//        TransactionDetails transactionDetail = transactionRepository.findTransactionDetailsByTransactionDetailsId(uid);
//        List<TransactionAttachments> attachments = transactionAttachmentRepo.findByTransactionDetails(transactionDetail);
        String authHeader = request.getHeader("Authorization");
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
                String sql = "SELECT password FROM user_info WHERE username = ?";
                String encryptedPassword = (String) jdbcTemplate.queryForObject(
                        sql, new Object[]{username}, String.class);
                if (encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.UNAUTHORIZED);

                    } else {

                        UUID uid = UUID.fromString(transactionId);
                        TransactionDetails transactionDetail = transactionRepository.findTransactionDetailsByTransactionDetailsId(uid);
                        if(transactionDetail == null){
                            return new ResponseEntity<>("No transaction id found!", HttpStatus.BAD_REQUEST);
                        }
                        if(transactionDetail.getUsername().equals(username)){
                            List<TransactionAttachments> attachments = transactionAttachmentRepo.findByTransactionDetails(transactionDetail);

                            JSONArray jsonArray = new JSONArray();
                            for (TransactionAttachments attachment : attachments) {
                                JSONObject json = new JSONObject();
                                json.put("id", attachment.getId().toString());
                                json.put("url", attachment.getFileName());
                                jsonArray.add(json);
                            }
                            return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
                        }else{
                            return new ResponseEntity<>("You are not authorized for this transaction!", HttpStatus.UNAUTHORIZED);
                        }

                    }
                }

            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }


    @RequestMapping(value = "/{id}/attachments", method = RequestMethod.POST, produces = "application/json", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<String> addAttachments(HttpServletRequest request, @PathVariable("id") String id,
                                                 @RequestParam("file") MultipartFile[] uploadfiles) {

        JsonObject json = new JsonObject();
        UUID uid = UUID.fromString(id);
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            System.out.print("You are not logged in ! ");
            return new ResponseEntity<>("Enter basic authentication header! You are not logged in !! ", HttpStatus.UNAUTHORIZED);

        } else if (authHeader.startsWith("Basic")) {
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String pass = values[1];
            if (checkIfUserExists(username)) {
                String sql = "SELECT password FROM user_info WHERE username = ?";
                String encryptedPassword = (String) jdbcTemplate.queryForObject(
                        sql, new Object[]{username}, String.class);
                if (encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.UNAUTHORIZED);

                    } else {
                        TransactionDetails transactionDetails = transactionRepository.findTransactionDetailsByTransactionDetailsId(uid);
                        if(transactionDetails == null){
                            return new ResponseEntity<>("No transaction id found!", HttpStatus.BAD_REQUEST);
                        }
                        if(transactionDetails != null && transactionDetails.getUsername().equals(username)){
                            String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename()).filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));
                            json.addProperty("message", "Saved the file(s)!");
                            try {

                                String keyValue = environment.getProperty("spring.profiles.active");
                                System.out.println(keyValue);
                                //saveUploadedFiles(Arrays.asList(uploadfiles), uploadedFileName, transactionDetails);
                                if(keyValue != null && keyValue.equals("dev")){
                                    UploadAttachmentS3BucketController uploadToS3 = new UploadAttachmentS3BucketController();
                                    for (MultipartFile file : uploadfiles) {

                                        String keyName = uploadToS3.uploadFileOnS3(transactionDetails, file);
                                        if (keyName.equals(null)) {
                                            json.addProperty("error", "An error occured while uploading files!!");
                                            return new ResponseEntity(json.toString(), HttpStatus.BAD_REQUEST);
                                        }
                                    }

                                    TransactionAttachments transactionAttachments = new TransactionAttachments();
                                    transactionRepository.save(transactionDetails);
                                    transactionAttachments.setFileName(uploadedFileName);
                                    transactionAttachments.setTransactionDetails(transactionDetails);
                                    transactionAttachmentRepo.save(transactionAttachments);

                                }else if(keyValue != null && keyValue.equals("default")){
                                    saveUploadedFiles(Arrays.asList(uploadfiles), uploadedFileName, transactionDetails);
                                }
                                else
                                {
                                    return new ResponseEntity("No profile set", HttpStatus.BAD_REQUEST);
                                }



                                return new ResponseEntity(json.toString(), HttpStatus.OK);
                            } catch (Exception exp) {
                                json.addProperty("error", "An error occured while uploading files!!");
                                return new ResponseEntity(json.toString(), HttpStatus.BAD_REQUEST);
                            }
                        }else{
                            return new ResponseEntity("You are  not authorized to add attachment !", HttpStatus.UNAUTHORIZED);
                        }

                    }
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }





    private void deleteFileFromLocal(String fileName, String dir)
    {
       // String workingDir = System.getProperty("user.dir");
       // System.out.println("Current working directory : " + workingDir);

      //  String UPLOADED_FOLDER = workingDir+"/";


        File file = new File(dir+"/" +fileName);

        if(file.delete())
        {
            System.out.println("File deleted successfully");
        }
        else
        {
            System.out.println("Failed to delete the file");
        }

    }

    private void updateUploadedFiles(List<MultipartFile> files, String uploadedFileName,
                                     TransactionDetails transactionDetails,String attachmentid) throws IOException {
        //Save the uploaded file to this folder
        //String UPLOADED_FOLDER = "/home/jhalak/Documents/";
      //  String UPLOADED_FOLDER = "/home/trialss/";

       // String UPLOADED_FOLDER = "/Downloads/demo/assignment3Cloud/";


        String workingDir = System.getProperty("user.dir");
        System.out.println("Current working directory : " + workingDir);

        String UPLOADED_FOLDER = workingDir+"/"+ transactionDetails.getTransactionDetailsId()+"/";

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; //next pls
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

        }


        TransactionAttachments transactionAttachments = transactionAttachmentRepo.findTransactionAttachmentsByTransactionAttachmentsId(Integer.parseInt(attachmentid));
        transactionRepository.save(transactionDetails);
        transactionAttachments.setFileName(uploadedFileName);
        transactionAttachments.setTransactionDetails(transactionDetails);
        transactionAttachmentRepo.save(transactionAttachments);
    }




    //@Profile("dev")
    private void saveUploadedFiles(List<MultipartFile> files, String uploadedFileName, TransactionDetails transactionDetails) throws IOException {
        //Save the uploaded file to this folder
//        String UPLOADED_FOLDER = "/home/jhalak/Documents/";



        String workingDir = System.getProperty("user.dir");
        System.out.println("Current working directory : " + workingDir);

        String UPLOADED_FOLDER = workingDir+"/";
        String dir=UPLOADED_FOLDER+transactionDetails.getTransactionDetailsId();


        File theDir = new File(dir);

        if (!theDir.exists()) {
            System.out.println("creating directory: " + theDir.getName());
            boolean result = false;

            try{
                theDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                //handle it
            }
            if(result) {
                System.out.println("DIR created");
            }
        }



        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; //next pls
            }

            byte[] bytes = file.getBytes();
            //Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());

            Path path = Paths.get(dir+"/" + file.getOriginalFilename());
            Files.write(path, bytes);

        }


        TransactionAttachments transactionAttachments = new TransactionAttachments();
        transactionRepository.save(transactionDetails);
        transactionAttachments.setFileName(uploadedFileName);
        transactionAttachments.setTransactionDetails(transactionDetails);
        transactionAttachmentRepo.save(transactionAttachments);
    }


@RequestMapping(value = "/{id}/attachments/{idAttachments}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> deleteAttachments(HttpServletRequest request, @PathVariable("idAttachments") String attachmentid, @PathVariable("id") String id) {

        JsonObject json = new JsonObject();
        DeleteAttachmentS3BucketController deleteFromS3 = new DeleteAttachmentS3BucketController();

        UUID uid = UUID.fromString(id);
        TransactionDetails transactionDetails = transactionRepository.findTransactionDetailsByTransactionDetailsId(uid);

        if (transactionDetails == null) {
            json.addProperty("message", "No such transaction found!!");
            return new ResponseEntity<>(json.toString(), HttpStatus.BAD_REQUEST);
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            System.out.print("You are not logged in ! ");
            return new ResponseEntity<>("Enter basic authentication header! You are not logged in !! ", HttpStatus.UNAUTHORIZED);

        } else if (authHeader.startsWith("Basic")) {
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String pass = values[1];
            if (checkIfUserExists(username)) {
                String sql = "SELECT password FROM user_info WHERE username = ?";
                String encryptedPassword = (String) jdbcTemplate.queryForObject(
                        sql, new Object[]{username}, String.class);
                if (encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.UNAUTHORIZED);

                    } else {
                        if(transactionDetails != null && transactionDetails.getUsername().equals(username)){
                            try {
                                int aid = 0;
                                try {
                                    aid = Integer.parseInt(attachmentid);
                                } catch (Exception e) {
                                    json.addProperty("message", "Attachment Id can only be numbers!!");
                                    return new ResponseEntity(json.toString(), HttpStatus.BAD_REQUEST);
                                }
                                TransactionAttachments ta = transactionAttachmentRepo.findTransactionAttachmentsByTransactionAttachmentsId(aid);

                                if (ta == null) {
                                    json.addProperty("message", "No such attachments with the Id!!");
                                    return new ResponseEntity(json.toString(), HttpStatus.BAD_REQUEST);
                                }
                                String path = ta.getFileName();
                                //Files.deleteIfExists(Paths.get(path));
                                String workingDir = System.getProperty("user.dir");
                                System.out.println("Current working directory : " + workingDir);

                                String UPLOADED_FOLDER = workingDir+"/";
                                String dir=UPLOADED_FOLDER+transactionDetails.getTransactionDetailsId();
                                deleteFileFromLocal(ta.getFileName(),dir);
                                String returnmsg = deleteFromS3.deleteFile(transactionDetails, ta.getFileName());
                                if (returnmsg.equalsIgnoreCase("deleted")) {
                                    System.out.println("SUCCESSFULLY DELETED FROM S3!!!");
                                    transactionAttachmentRepo.deleteById(aid);
                                    transactionRepository.save(transactionDetails);
                                }
                            } catch (Exception e) {
                                System.out.println("" + e.getMessage());
                            }

                            json.addProperty("message", "Deleted the attachment!");
                            return new ResponseEntity(json.toString(), HttpStatus.NO_CONTENT);
                        }else{
                            return new ResponseEntity<>("You are not authorized to delete!", HttpStatus.UNAUTHORIZED);
                        }



                    }
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }



    @RequestMapping(value = "/{id}/attachments/{idAttachments}", method=RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> UpdateAttachments(HttpServletRequest request, @PathVariable("idAttachments") String attachmentid,
                                                    @PathVariable("id") String id,
                                                    @RequestParam("file") MultipartFile[] uploadfiles) {



        JsonObject json = new JsonObject();

        UUID uid = UUID.fromString(id);

        TransactionDetails transactionDetails = transactionRepository.findTransactionDetailsByTransactionDetailsId(uid);

        if (transactionDetails == null) {
            json.addProperty("message", "No such transaction found!!");
            return new ResponseEntity<>(json.toString(), HttpStatus.BAD_REQUEST);
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            System.out.print("You are not logged in ! ");
            return new ResponseEntity<>("Enter basic authentication header! You are not logged in !! ", HttpStatus.UNAUTHORIZED);

        } else if (authHeader.startsWith("Basic")) {
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String pass = values[1];
            if (checkIfUserExists(username)) {
                String sql = "SELECT password FROM user_info WHERE username = ?";
                String encryptedPassword = (String) jdbcTemplate.queryForObject(
                        sql, new Object[]{username}, String.class);
                if (encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.UNAUTHORIZED);

                    } else {
                        if(transactionDetails != null && transactionDetails.getUsername().equals(username)){
                            int aidold = 0;
                            TransactionAttachments tas =null;
                            String oldfileName=null;
                            try {

                                aidold = Integer.parseInt(attachmentid);
                                tas=transactionAttachmentRepo.findTransactionAttachmentsByTransactionAttachmentsId(Integer.parseInt(attachmentid));
                                oldfileName=tas.getFileName();
                            } catch (Exception e) {
                                json.addProperty("message", "Attachment Id can only be numbers!!");
                                return new ResponseEntity(json.toString(), HttpStatus.BAD_REQUEST);
                            }

                            transactionDetails = transactionRepository.findTransactionDetailsByTransactionDetailsId(uid);
                            String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename()).filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));
                            json.addProperty("message", "Saved the file(s)!");
                            DeleteAttachmentS3BucketController deleteFromS3 = new DeleteAttachmentS3BucketController();



                            try
                            {
                                updateUploadedFiles(Arrays.asList(uploadfiles), uploadedFileName, transactionDetails,attachmentid);
                                UploadAttachmentS3BucketController uploadToS3 = new UploadAttachmentS3BucketController();
                                for (MultipartFile file : uploadfiles) {

                                    String keyName = uploadToS3.uploadFileOnS3(transactionDetails, file);
                                    if (keyName.equals(null)) {
                                        json.addProperty("error", "An error occured while uploading files!!");
                                        return new ResponseEntity(json.toString(), HttpStatus.BAD_REQUEST);
                                    }
                                }



                                try {
                                    String workingDir = System.getProperty("user.dir");
                                    System.out.println("Current working directory : " + workingDir);

                                    String UPLOADED_FOLDER = workingDir+"/";
                                    String dir=UPLOADED_FOLDER+transactionDetails.getTransactionDetailsId();
                                    deleteFileFromLocal(oldfileName,dir);
                                    String returnmsg = deleteFromS3.deleteFile(transactionDetails, oldfileName);
                                    if (returnmsg.equalsIgnoreCase("deleted")) {
                                        System.out.println("SUCCESSFULLY DELETED FROM S3!!!");
                                        //  transactionAttachmentRepo.deleteById(aidold);
                                        transactionRepository.save(transactionDetails);
                                    }
                                } catch (Exception e) {
                                    System.out.println("" + e.getMessage());
                                }



                                return new ResponseEntity(json.toString(), HttpStatus.OK);
                            } catch (Exception exp) {
                                json.addProperty("error", "An error occured while uploading files!!");
                                return new ResponseEntity(json.toString(), HttpStatus.BAD_REQUEST);
                            }
                        }else {
                            return new ResponseEntity<>("You are not authorized to update !! ", HttpStatus.UNAUTHORIZED);
                        }



                    }

                }

                return null;
            }

        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }


    @RequestMapping(value = "/{id}/attachments/{idAttachments}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> deleteAttachments (HttpServletRequest
                                                             request, @PathVariable("idAttachments") String attachmentid, @PathVariable("id") String id){

        JsonObject json = new JsonObject();
        DeleteAttachmentS3BucketController deleteFromS3 = new DeleteAttachmentS3BucketController();

        UUID uid = UUID.fromString(id);
        TransactionDetails transactionDetails = transactionRepository.findTransactionDetailsByTransactionDetailsId(uid);

        if (transactionDetails == null) {
            json.addProperty("message", "No such transaction found!!");
            return new ResponseEntity<>(json.toString(), HttpStatus.BAD_REQUEST);
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            System.out.print("You are not logged in ! ");
            return new ResponseEntity<>("Enter basic authentication header! You are not logged in !! ", HttpStatus.UNAUTHORIZED);

        } else if (authHeader.startsWith("Basic")) {
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String pass = values[1];
            if (checkIfUserExists(username)) {
                String sql = "SELECT password FROM user_info WHERE username = ?";
                String encryptedPassword = (String) jdbcTemplate.queryForObject(
                        sql, new Object[]{username}, String.class);
                if (encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.UNAUTHORIZED);

                    } else {
                        if (transactionDetails != null && transactionDetails.getUsername().equals(username)) {
                            try {
                                int aid = 0;
                                try {
                                    aid = Integer.parseInt(attachmentid);
                                } catch (Exception e) {
                                    json.addProperty("message", "Attachment Id can only be numbers!!");
                                    return new ResponseEntity(json.toString(), HttpStatus.BAD_REQUEST);
                                }
                                TransactionAttachments ta = transactionAttachmentRepo.findTransactionAttachmentsByTransactionAttachmentsId(aid);

                                if (ta == null) {
                                    json.addProperty("message", "No such attachments with the Id!!");
                                    return new ResponseEntity(json.toString(), HttpStatus.BAD_REQUEST);
                                }
                                String path = ta.getFileName();
                                //Files.deleteIfExists(Paths.get(path));
                                String workingDir = System.getProperty("user.dir");
                                System.out.println("Current working directory : " + workingDir);

                                String UPLOADED_FOLDER = workingDir + "/";
                                String dir = UPLOADED_FOLDER + transactionDetails.getTransactionDetailsId();
                                String keyValue = environment.getProperty("spring.profiles.active");
                                if(keyValue != null && keyValue.equals("default")){
                                    deleteFileFromLocal(ta.getFileName(), dir);
                                    transactionAttachmentRepo.deleteById(aid);
                                    transactionRepository.save(transactionDetails);
                                }else if(keyValue != null && keyValue.equals("dev")){
                                    String returnmsg = deleteFromS3.deleteFile(transactionDetails, ta.getFileName());
                                    if (returnmsg.equalsIgnoreCase("deleted")) {
                                        System.out.println("SUCCESSFULLY DELETED FROM S3!!!");
                                        transactionAttachmentRepo.deleteById(aid);
                                        transactionRepository.save(transactionDetails);
                                    }
                                }else{
                                    return new ResponseEntity("No profile set", HttpStatus.BAD_REQUEST);
                                }

                                //String returnmsg = deleteFromS3.deleteFile(transactionDetails, ta.getFileName());

                            } catch (Exception e) {
                                System.out.println("" + e.getMessage());
                            }

                            json.addProperty("message", "Deleted the attachment!");
                            return new ResponseEntity(json.toString(), HttpStatus.NO_CONTENT);
                        } else {
                            return new ResponseEntity<>("You are not authorized to delete!", HttpStatus.UNAUTHORIZED);
                        }


                    }
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }




    @RequestMapping(value = "/{id}/attachments/{idAttachments}", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> UpdateAttachments (HttpServletRequest
                                                             request, @PathVariable("idAttachments") String attachmentid,
                                                     @PathVariable("id") String id,
                                                     @RequestParam("file") MultipartFile[]uploadfiles){


        JsonObject json = new JsonObject();

        UUID uid = UUID.fromString(id);

        TransactionDetails transactionDetails = transactionRepository.findTransactionDetailsByTransactionDetailsId(uid);

        if (transactionDetails == null) {
            json.addProperty("message", "No such transaction found!!");
            return new ResponseEntity<>(json.toString(), HttpStatus.BAD_REQUEST);
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            System.out.print("You are not logged in ! ");
            return new ResponseEntity<>("Enter basic authentication header! You are not logged in !! ", HttpStatus.UNAUTHORIZED);

        } else if (authHeader.startsWith("Basic")) {
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String pass = values[1];
            if (checkIfUserExists(username)) {
                String sql = "SELECT password FROM user_info WHERE username = ?";
                String encryptedPassword = (String) jdbcTemplate.queryForObject(
                        sql, new Object[]{username}, String.class);
                if (encryptedPassword != null) {
                    if (!checkPassword(pass, encryptedPassword)) {
                        return new ResponseEntity<>("UserName and Password does not match !", HttpStatus.UNAUTHORIZED);

                    } else {
                        if (transactionDetails != null && transactionDetails.getUsername().equals(username)) {
                            int aidold = 0;
                            TransactionAttachments tas = null;
                            String oldfileName = null;
                            try {

                                aidold = Integer.parseInt(attachmentid);
                                tas = transactionAttachmentRepo.findTransactionAttachmentsByTransactionAttachmentsId(Integer.parseInt(attachmentid));
                                oldfileName = tas.getFileName();
                            } catch (Exception e) {
                                json.addProperty("message", "Attachment Id can only be numbers!!");
                                return new ResponseEntity(json.toString(), HttpStatus.BAD_REQUEST);
                            }

                            transactionDetails = transactionRepository.findTransactionDetailsByTransactionDetailsId(uid);
                            String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename()).filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));
                            json.addProperty("message", "Saved the file(s)!");
                            DeleteAttachmentS3BucketController deleteFromS3 = new DeleteAttachmentS3BucketController();


                            try {
                                String keyValue = environment.getProperty("spring.profiles.active");
                                if(keyValue != null && keyValue.equals("default")) {
                                    updateUploadedFiles(Arrays.asList(uploadfiles), uploadedFileName, transactionDetails, attachmentid);

                                }else if(keyValue != null && keyValue.equals("dev")){
                                    UploadAttachmentS3BucketController uploadToS3 = new UploadAttachmentS3BucketController();
                                    for (MultipartFile file : uploadfiles) {

                                        String keyName = uploadToS3.uploadFileOnS3(transactionDetails, file);
                                        if (keyName.equals(null)) {
                                            json.addProperty("error", "An error occured while uploading files!!");
                                            return new ResponseEntity(json.toString(), HttpStatus.BAD_REQUEST);
                                        }
                                    }
                                    TransactionAttachments transactionAttachments = transactionAttachmentRepo.findTransactionAttachmentsByTransactionAttachmentsId(Integer.parseInt(attachmentid));
                                    transactionRepository.save(transactionDetails);
                                    transactionAttachments.setFileName(uploadedFileName);
                                    transactionAttachments.setTransactionDetails(transactionDetails);
                                    transactionAttachmentRepo.save(transactionAttachments);
                                }else{
                                    return new ResponseEntity("No profile set", HttpStatus.BAD_REQUEST);
                                }



                                try {
                                    String workingDir = System.getProperty("user.dir");
                                    System.out.println("Current working directory : " + workingDir);

                                    String UPLOADED_FOLDER = workingDir + "/";
                                    String dir = UPLOADED_FOLDER + transactionDetails.getTransactionDetailsId();
                                    if(keyValue != null && keyValue.equals("default")) {
                                        deleteFileFromLocal(oldfileName, dir);
                                        transactionRepository.save(transactionDetails);
                                    }else if(keyValue != null && keyValue.equals("dev")) {
                                        String returnmsg = deleteFromS3.deleteFile(transactionDetails, oldfileName);
                                        if (returnmsg.equalsIgnoreCase("deleted")) {
                                            System.out.println("SUCCESSFULLY DELETED FROM S3!!!");
                                            //  transactionAttachmentRepo.deleteById(aidold);
                                            transactionRepository.save(transactionDetails);
                                        }
                                    }else{
                                        return new ResponseEntity("No profile set", HttpStatus.BAD_REQUEST);
                                    }
                                } catch (Exception e) {
                                    System.out.println("" + e.getMessage());
                                }


                                return new ResponseEntity(json.toString(), HttpStatus.OK);
                            } catch (Exception exp) {
                                json.addProperty("error", "An error occured while uploading files!!");
                                return new ResponseEntity(json.toString(), HttpStatus.BAD_REQUEST);
                            }
                        } else {
                            return new ResponseEntity<>("You are not authorized to update !! ", HttpStatus.UNAUTHORIZED);
                        }


                    }

                }

                return null;
            }

        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }



}

