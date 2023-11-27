# hbasesparktest1

**Basic Spark Application Demonstrating Connectivity with HBase**

---

## Overview

`hbasesparktest1` is a Java-based Spark application that demonstrates basic connectivity between Apache Spark and Apache HBase in an HDInsight environment. This project, primarily educational, directly utilizes HBase APIs to establish this connection, offering a hands-on experience in integrating these technologies.

### Key Objectives and Educational Value

The application is designed to offer a practical understanding of how Spark interacts with HBase. This example, while not adhering to all best practices or showcasing the most optimal implementations, is an excellent starting point for grasping the basics of Spark and HBase connectivity in an HDInsight environment.

### Application Approach and Design Choices

Opting for a direct use of HBase APIs, the project diverges from approaches that use integration tools like the Apache Spark-HBase Connector. This hands-on method offers insights into the low-level interactions between Spark and HBase, presenting a unique learning opportunity. While not the most efficient or optimized approach for Spark-HBase integration, it lays a solid foundation for understanding the core concepts.

### Encouraging Exploration of Advanced Integration Methods

As users become more familiar with the basic interactions, exploring advanced methods like the Apache Spark-HBase Connector is highly recommended. These tools facilitate more efficient data processing and easier integration, particularly vital in scenarios demanding enhanced performance and scalability. This project, therefore, is a springboard into the world of Spark and HBase, encouraging users to explore and adopt more sophisticated tools and techniques as they progress.

---



### Java Implementation Details
The project uses `JavaPairRDD` to interface with HBase at the RDD (Resilient Distributed Dataset) level and enables direct and uncomplicated interaction with the HBase table 'PatientRecords'. Here's a brief overview of the implementation:

- **Spark Session and Context**: Initializes the Spark environment.
- **HBase Configuration**: Sets up the connection to the HBase table.
- **Scan Operation**: Defines the columns to be read from HBase.
- **JavaPairRDD**: Facilitates the reading of data from HBase as key-value pairs.
- **Data Processing**: Applies a transformation to convert HBase results into a more readable format.
- **Action Trigger**: Executes the Spark job and prints the results.

This implementation is a simple yet effective way to understand how Spark can read data from HBase.

### Alternative Methods
While this project uses `JavaPairRDD` for interacting with HBase, there are other methods to consider in different contexts:

- **DataFrame API**: For higher-level abstractions and more complex operations.
- **SparkSQL**: For SQL-like query capabilities in Spark.
- **Custom UDFs (User Defined Functions)**: For complex data processing within Spark.

Each of these alternatives offers different advantages and can be more suitable depending on the specific requirements of a Spark-HBase application.

---

## HBase Setup Instructions

This project requires setting up an HBase table and populating it with sample data. Follow these steps to configure the required environment.

### Prerequisites

- **HDInsight Cluster Setup**: You need two HDInsight clusters - one for Spark (HDI Spark) and one for HBase (HDI HBase). Both clusters should be operational and connected, either deployed into the same Virtual Network (VNet) or within peered VNets.

- **Knowledge of HBase**: Familiarity with HBase and its command-line tools will be beneficial for setting up and interacting with the database.

- **Configuration File**: The `hbase-site.xml` file from the HBase cluster (`/etc/hbase/conf/hbase-site.xml`) must be copied to the `/etc/spark3/conf/` folder on the machine within the Spark cluster where the job will be submitted.

- **DNS Configuration**: Please be sure DNS is configured so that the Fully Qualified Host Names (FQHN) of HBase Region Servers, as well as HBase Master nodes, are resolvable from the Spark cluster with the correct IP addresses.


### Configuring the Environment


- **Set HBase Configuration Directory**: Before running the HBase shell on the Spark cluster, specify the location of the HBase configuration:
  ```shell
  export HBASE_CONF_DIR=/etc/spark3/conf
  ```
- **Kerberos Authentication**: If your cluster is secured with Kerberos, authenticate with Kerberos before running the HBase shell.

### Table Creation and Data Insertion

We're creating a table named `PatientRecords` with two column families: `details` and `medical`.

#### Step 1: Create the HBase Table

Open the HBase shell and execute the following command to create the table:

```shell
create 'PatientRecords', 'details', 'medical'
```

#### Step 2: Populate the Table with Data

Insert sample patient records into the table using these commands:

```shell
put 'PatientRecords', 'patient1', 'details:name', 'Alice'
put 'PatientRecords', 'patient1', 'details:age', '30'
put 'PatientRecords', 'patient1', 'medical:diagnosis', 'Flu'
put 'PatientRecords', 'patient1', 'medical:treatment', 'Rest and hydration'

put 'PatientRecords', 'patient2', 'details:name', 'Bob'
put 'PatientRecords', 'patient2', 'details:age', '45'
put 'PatientRecords', 'patient2', 'medical:diagnosis', 'Asthma'
put 'PatientRecords', 'patient2', 'medical:treatment', 'Inhaler and medication'
```

#### Step 3: Verify Table Contents

To confirm the data was inserted correctly, scan the contents of the table:

```shell
scan 'PatientRecords'
```

---

---

## Spark Setup Instructions

This section guides you through the process of downloading, compiling, and executing the Spark application in the `hbasesparktest1` project.

### Prerequisites

- **Maven**: Ensure Maven is installed on your system. You can check this by running:
  ```shell
  mvn -version
  ```
  If Maven is not installed, install it using:
  ```shell
  sudo apt-get install maven
  ```

### Cloning and Compiling the Project

1. **Clone the Project**: Clone the `hbasesparktest1` repository from GitHub:
   ```shell
   git clone https://github.com/Suaroman/hbasesparktest1.git
   ```
2. **Change to Project Directory**:
   ```shell
   cd hbasesparktest1
   ```
3. **Compile the Project**: Use Maven to clean and package the application:
   ```shell
   mvn clean
   mvn package
   ```
   This process will create a JAR file in the `target` directory.

---

### Submitting the Spark Job

Before submitting the Spark job, please be sure that the `hbase-site.xml` file from the HBase cluster (`/etc/hbase/conf/hbase-site.xml`) is copied to the `/etc/spark3/conf/` folder on the machine within the Spark cluster where the job will be submitted. This step is critical for enabling the Spark application to communicate correctly with the HBase cluster.

Once the configuration is in place, submit the compiled project to your Spark cluster. Below is an example of using `spark-submit` to submit the job to a cluster with Kerberos authentication:

```shell
spark-submit --master yarn --deploy-mode cluster \
--principal alt-gregorys@HDINSIGHTCSS.COM --keytab /home/hdiuser/alt-gregorys.keytab \
--class org.suaro.PatientRecordsReader \
~/hbasesparktest1/target/hbasesparktest1-1.0-SNAPSHOT.jar
```

In this command:

- `--master yarn`: Specifies YARN as the cluster manager.
- `--deploy-mode cluster`: Runs the job on the cluster nodes.
- `--principal` and `--keytab`: Used for Kerberos authentication.
- `--class`: The main class of your application.
- The path to the JAR file should be accurate; it's usually located in the `target` directory after a successful Maven build.

---

### Note on the JAR Location

The JAR file is referenced from the `target` directory where it's built. Please be sure the path to the JAR file is correctly specified when using `spark-submit`.

---



### Additional Information
- **Table Structure**: The `PatientRecords` table consists of two column families:
  - `details`: Stores general patient information like name and age.
  - `medical`: Contains medical-specific data such as diagnosis and treatment.

- **Sample Data**: The provided commands will create records for two sample patients. These records are for demonstration purposes and can be modified as needed.

---


