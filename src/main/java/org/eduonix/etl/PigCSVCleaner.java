package org.eduonix.etl;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.apache.pig.ExecType;
import org.apache.pig.PigServer;

import java.io.File;
import java.io.IOException;


/**
 * This class operates by ETL'ing the dataset into pig.
 * The pigServer is persisted through the life of the class, so that the
 * intermediate data sets created in the constructor can be reused.
 */
public class PigCSVCleaner {

    PigServer pigServer;

    Path inputPath;

    Path outputPath;
    
    public PigCSVCleaner(Path inputPath, Path outputPath) throws Exception {

        this.inputPath = inputPath;
        this.outputPath = outputPath;

        FileSystem fs = FileSystem.getLocal(new Configuration());

        fs.delete(outputPath, true);

        // run pig in local mode
        pigServer = new PigServer(ExecType.LOCAL);

    }


    public void doAgg() throws IOException {


        String queryLoad = "csvdata =  LOAD '<i>'  using PigStorage(',') AS ("+

                " dump:chararray, state:chararray, transaction:int,    fname:chararray," +
                " lname:chararray,  date:chararray,  price:float,   product:chararray );";



        pigServer.registerQuery(queryLoad.replaceAll("<i>", inputPath.toString()));

        String queryAggregate = " t_details = FOREACH csvdata GENERATE  $1  as code , $3 as fname, $5 as product;";

        pigServer.registerQuery(queryAggregate);

        String queryStore = " store t_details into '<i>'  using PigStorage('\t');";

        pigServer.registerQuery(queryStore.replaceAll("<i>", outputPath.toString()));

    }




}
