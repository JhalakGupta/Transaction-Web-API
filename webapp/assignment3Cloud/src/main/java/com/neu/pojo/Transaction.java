package com.neu.pojo;


import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


@Entity
public class Transaction {

   @Id
   @GeneratedValue(generator="system-uuid")
   @GenericGenerator(name="system-uuid", strategy = "uuid")
   @ApiModelProperty(notes = "The database generated product ID")
   private String id;

   @ApiModelProperty(notes = "The transaction description")
   private String description;


   @ApiModelProperty(notes = "The transaction merchant")
   private String merchant;

   @ApiModelProperty(notes = "The transaction amount")
   private String amount;


   @ApiModelProperty(notes = "The transaction date")
   private String date;

   @ApiModelProperty(notes = "The transaction category")
   private String category;

   private String username;

   public String getId() {
       return id;
   }

   public void setId(String id) {
       this.id = id;
   }


   public String getDescription() {
       return description;
   }

   public void setDescription(String description) {
       this.description = description;
   }

   public String getMerchant() {
       return merchant;
   }

   public void setMerchant(String merchant) {
       this.merchant = merchant;
   }

   public String getAmount() {
       return amount;
   }

   public void setAmount(String amount) {
       this.amount = amount;
   }

   public String getDate() {
       return date;
   }

   public void setDate(String date) {
       this.date = date;
   }

   public String getCategory() {
       return category;
   }

   public void setCategory(String category) {
       this.category = category;
   }


   public String getUsername() {
       return username;
   }

   public void setUsername(String username) {
       this.username = username;
   }
}
