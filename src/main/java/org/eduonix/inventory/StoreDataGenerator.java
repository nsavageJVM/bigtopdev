package org.eduonix.inventory;


import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.pig.ExecType;
import org.eduonix.StoreInventoryRunner;
import org.eduonix.datagenerator.StoreJob;

import org.eduonix.etl.PigCSVCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ubu on 5/2/14.
 */
public class StoreDataGenerator {


    final static Logger log = LoggerFactory.getLogger(StoreDataGenerator.class);



    static Path output;



    static Path PIG_CLEANED;

    static {

        if(!StoreInventoryRunner.devMode) {
            output = new Path("/user/root/store/inventory");
            PIG_CLEANED = new Path("/analytics/cleaned");

        }  else {
            output = new Path("store/inventory");
            PIG_CLEANED = new Path("./analytics/cleaned");

        }
    }


    public void setUpMockData(int records) throws Exception {

        /**
         * Setup configuration with prop.
         */
        Configuration conf = new Configuration();
        conf.setInt(StoreJob.props.store_records.name(), records);
        Job createInput = StoreJob.createJob(output, conf);
        createInput.waitForCompletion(true);

    }


    public  Map<Path, Function<String, String>>  runAnalytics(final String matchTxt) {


       Map<Path, Function<String, String>> analytics = Maps.newHashMap();

        analytics.put( PIG_CLEANED,

            new Function<String, String>() {
                public String apply(String line) {

                    int counter = 0;

                   String[] tokens = line.split("\t");
                    for(String x :tokens ) {

                        if (x.contains(matchTxt)) {
                            return line;
                        }
                    }
                    return "noMatch";
                }
            }
    );

        return analytics;
    }


    public void runPig(Path inputPath, Path outputPath) throws Exception {

        PigCSVCleaner pigAgg = new  PigCSVCleaner(inputPath, outputPath);
        pigAgg.doAgg();

    }


}
