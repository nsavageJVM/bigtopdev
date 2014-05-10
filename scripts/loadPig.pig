csvdata =  LOAD '/user/root/store/inventory/part-r-00000' using PigStorage(',') AS ( 

		  dump:chararray,
          state:chararray,
          transaction:int,
          fname:chararray,
          lname:chararray,
          date:chararray,
          price:float,
          product:chararray);

t_details = FOREACH csvdata GENERATE  $1  as code , $3 as fname, $5 as product;


store t_details into '/user/root/analytics/cleaned' using PigStorage('\t');