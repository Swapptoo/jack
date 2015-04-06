
/**
 * Autogenerated by Jack
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package com.rapleaf.jack.test_project.database_1;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.GenericQuery;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IImagePersistence;
import com.rapleaf.jack.test_project.database_1.iface.IPostPersistence;
import com.rapleaf.jack.test_project.database_1.iface.IUserPersistence;

public interface IDatabase1 extends IDb {
  public GenericQuery.Builder createQuery();
  public ICommentPersistence comments();
  public IImagePersistence images();
  public IPostPersistence posts();
  public IUserPersistence users();

}
