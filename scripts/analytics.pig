-- invoke with two arguments, the input file , and the output file. -input /bps/gen -output /bps/analytics

-- FYI...
-- If you run into errors, you can see them in
-- ./target/failsafe-reports/TEST-org.bigtop.bigpetstore.integration.BigPetStorePigIT.xml

-- First , we load data in from a file, as tuples.
-- in pig, relations like tables in a relational database
-- so each relation is just a bunch of tuples.
-- in this case csvdata will be a relation,
-- where each tuple is a single petstore transaction.
csvdata =
    LOAD '$input' using PigStorage()
        AS (
          dump:chararray,
          state:chararray,
          transaction:int,
          fname:chararray,
          lname:chararray,
          date:chararray,
          price:float,
          product:chararray);

-- RESULT:
-- (BigPetStore,storeCode_AK,1,jay,guy,Thu Dec 18 12:17:10 EST 1969,10.5,dog-food)
-- ...

-- Okay! Now lets group our data so we can do some stats.
-- lets create a new relation,
-- where each tuple will contain all transactions for a product in a state.

state_product = group csvdata by ( state, product ) ;

-- RESULT
-- ((storeCode_AK,dog-food) , {(BigPetStore,storeCode_AK,1,jay,guy,Thu Dec 18 12:17:10 EST 1969,10.5,dog-food)}) --
-- ...


-- Okay now lets make some summary stats so that the boss man can
-- decide which products are hottest in which states.

-- Note that for the "groups", we tease out each individual field here for formatting with
-- the BigPetStore visualization app.
summary1 = FOREACH state_product generate STRSPLIT(group.state,'_').$1 as sp, group.product, COUNT($1);


-- Okay, the stats look like this.  Lets clean them up.
-- (storeCode_AK,cat-food)      2530
-- (storeCode_AK,dog-food)      2540
-- (storeCode_AK,fuzzy-collar)     2495

dump summary1;

store summary1 into '$output';
