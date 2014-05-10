package org.eduonix;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.eduonix.inventory.StoreDataGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * Created by ubu on 5/9/14.
 */
public class StoreAnalytics {


    static  String  match  = "AK";
    static String searchPath;

    static {

        if(StoreInventoryRunner.devMode) {
            searchPath ="bigtopdev/analytics/cleaned";


        }  else {
            searchPath ="analytics/cleaned";

        }
    }




    public static void main(String[] args) throws Exception {


        System.out.println("Working Directory = " +System.getProperty("user.dir"));

        Path base = new Path(System.getProperty("user.dir"));

        FileSystem fsLocal = FileSystem.getLocal(new Configuration());
        FileStatus[] localFiles=fsLocal.listStatus(base);
        //print out all the files.
        for(FileStatus stat : localFiles){
            System.out.println("Local Working Directory files" +stat.getPath() +"  " + stat.getLen());
        }


        FileSystem fs = FileSystem.get(new Configuration());
        System.out.println("HDFS Home  Directory " +fs.getHomeDirectory() ) ;

        Path doSearch = new Path(fs.getHomeDirectory(),searchPath);
        System.out.println("HDFS  Directory " +doSearch.toString() ) ;

        FileStatus[] hdfsFiles=fs.listStatus(doSearch);
        //print out all the files.
        for(FileStatus stat : hdfsFiles){
            System.out.println("HDFS  Directory files " +stat.getPath() +"  " + stat.getLen());
        }
        Path partm = new Path(doSearch,"part-m-00000");
        Path partr = new Path(doSearch,"part-r-00000");
        Path p = fs.exists(partm)?partm:partr;
        System.out.println(doSearch.toString()+ " +p.toString() " +p.toString());


        StoreDataGenerator storeData = new StoreDataGenerator();

        Map<Path, Function<String, String>> analytics = storeData.runAnalytics(match);


        for(Map.Entry<Path, Function<String, String>>  e  : analytics.entrySet()) {

            searchOutput(e.getKey(), e.getValue());

        }


    }


    public static void searchOutput(Path base,  Function<String, String> validator) throws IOException {


        FileSystem fs = null;
        Path p;
        try {
            fs = FileSystem.get(new Configuration());
            Path doSearch = new Path(fs.getHomeDirectory(),searchPath);
            System.out.println("fs.open(base)" +fs.getHomeDirectory());
            System.out.println("fs.open(base)" +doSearch.toString());
           if(fs.exists(doSearch)) {
               FileStatus[] hdfsFiles=fs.listStatus(doSearch);

               // gives
               //fs.open(base)File /analytics/cleaned does not exist.

               //print out all the files.
               for(FileStatus stat : hdfsFiles){
                   System.out.println(base.toString()+ " HDFS  Directory files " +stat.getPath() +"  " + stat.getLen());
               }

               Path partm = new Path(doSearch,"part-m-00000");
               Path partr = new Path(doSearch,"part-r-00000");
               p = fs.exists(partm)?partm:partr;
               System.out.println(base.toString()+ " +p.toString() " +p.toString());
            }

           else
           {

               System.out.println(base.toString()+ " + does not exist returning" );
               return;

           }





        /**
         * Now we read through the file and search
         * its contents.
         */


        BufferedReader r = null;

            r = new BufferedReader( new InputStreamReader(fs.open(p)));


        List<String> searchResults = Lists.newLinkedList();

        while(r.ready()){

            String line = r.readLine();

            if(!validator.apply(line).equals("noMatch")) {

                searchResults.add(line);
            }
        }



        System.out.println("found "+ searchResults.size()+" results for match Text "+match);

        for (String s:searchResults ) {

            System.out.println(s);
        }

        } catch (IOException e) {

            System.out.println("fs.open(base)" +e.getLocalizedMessage());
            return;
        }

    }
}
