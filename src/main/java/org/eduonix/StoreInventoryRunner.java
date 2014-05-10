package org.eduonix;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.eduonix.inventory.StoreDataGenerator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * Created by ubu on 5/2/14.
 */
public class StoreInventoryRunner {

   static  int records = 200;
   static  String  match  = "AK";
   public static boolean devMode = false;


    public static void main(String[] args) throws Exception {


          StoreDataGenerator storeData = new StoreDataGenerator();
          storeData.setUpMockData(records);

    }

}
