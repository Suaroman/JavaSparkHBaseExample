package org.suaro;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;
import scala.Tuple4;

public class PatientRecordsReader {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder().appName("PatientRecordsReader").getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());

        Configuration conf = HBaseConfiguration.create();
        conf.set(TableInputFormat.INPUT_TABLE, "PatientRecords");

        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes("details"), Bytes.toBytes("name"));
        scan.addColumn(Bytes.toBytes("details"), Bytes.toBytes("age"));
        scan.addColumn(Bytes.toBytes("medical"), Bytes.toBytes("diagnosis"));
        scan.addColumn(Bytes.toBytes("medical"), Bytes.toBytes("treatment"));

        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRDD = sc.newAPIHadoopRDD(conf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
        
        JavaRDD<Tuple4<String, String, String, String>> patientRDD = hbaseRDD.map(new Function<Tuple2<ImmutableBytesWritable, Result>, Tuple4<String, String, String, String>>() {
            @Override
            public Tuple4<String, String, String, String> call(Tuple2<ImmutableBytesWritable, Result> entry) throws Exception {
                Result result = entry._2();
                String name = Bytes.toString(result.getValue(Bytes.toBytes("details"), Bytes.toBytes("name")));
                String age = Bytes.toString(result.getValue(Bytes.toBytes("details"), Bytes.toBytes("age")));
                String diagnosis = Bytes.toString(result.getValue(Bytes.toBytes("medical"), Bytes.toBytes("diagnosis")));
                String treatment = Bytes.toString(result.getValue(Bytes.toBytes("medical"), Bytes.toBytes("treatment")));
                return new Tuple4<>(name, age, diagnosis, treatment);
            }
        });

        // Action to trigger the execution and print the results
        patientRDD.foreach(new VoidFunction<Tuple4<String, String, String, String>>() {
            @Override
            public void call(Tuple4<String, String, String, String> record) throws Exception {
                System.out.println(record);
            }
        });

       /*
       // Delay finishing for debugging purposes
       // Wait for 5 minutes (300000 milliseconds) before stopping the application
       try {
           Thread.sleep(300000); // 300000 milliseconds = 5 minutes
       } catch (InterruptedException e) {
      
      e.printStackTrace();
       }
      */

        spark.stop();
    }
}

