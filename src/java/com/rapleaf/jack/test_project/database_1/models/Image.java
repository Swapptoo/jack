
/**
 * Autogenerated by Jack
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
/* generated from migration version 20110324000133 */
package com.rapleaf.jack.test_project.database_1.models;

import java.io.IOException;
import java.util.Set;

import com.rapleaf.jack.test_project.database_1.IDatabase1;

import com.rapleaf.jack.ModelWithId;
import com.rapleaf.jack.BelongsToAssociation;
import com.rapleaf.jack.HasManyAssociation;
import com.rapleaf.jack.HasOneAssociation;

import com.rapleaf.jack.test_project.IDatabases;

public class Image extends ModelWithId {
  // Fields
  private Integer __user_id;

  // Associations
  private BelongsToAssociation<User> __assoc_user;

  public enum _Fields {
    user_id,
  }

  public Image(int id, final Integer user_id, IDatabases databases) {
    super(id);
    this.__user_id = user_id;
    this.__assoc_user = new BelongsToAssociation<User>(databases.getDatabase1().users(), user_id);
  }

  public Image(int id, final Integer user_id) {
    super(id);
    this.__user_id = user_id;
  }

  public Image (Image other) {
    super(other.getId());
    this.__user_id = other.getUserId();
  }

  public Integer getUserId(){
    return __user_id;
  }

  public void setUserId(Integer newval){
    this.__user_id = newval;
  }

  public void setField(_Fields field, Object value) {
    switch (field) {
      case user_id:
        setUserId((Integer) value);
        break;
    }
    throw new IllegalStateException("Invalid field: " + field);
  }

  public User getUser() throws IOException {
    return __assoc_user.get();
  }

  @Override
  public Object getField(String fieldName) {
    if (fieldName.equals("id")) {
      return getId();
    }
    if (fieldName.equals("user_id")) {
      return getUserId();
    }
    throw new IllegalStateException("Invalid field name: " + fieldName);
  }

  public Object getField(_Fields field) {
    switch (field) {
      case user_id:
        return getUserId();
    }
    throw new IllegalStateException("Invalid field: " + field);
  }

  public String toString() {
    return "<Image"
      + " user_id: " + __user_id
      + ">";
  }
}
