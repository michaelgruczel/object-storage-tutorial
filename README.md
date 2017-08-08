# object storage for java developers

## what is object storage ?

Object storage manages data as objects, as opposed to other storage architectures like file systems which manage data as a file. The most object storage systems provide REST APIs to manage the containers and objects.
An object typically includes the data itself, a variable amount of metadata, and a globally unique identifier. Object storage explicitly separates file metadata from data to support additional capabilities as opposed to fixed metadata in file systems (filename, creation date, type, etc.). Instead of writing a file into a directory, an object will be stored in a bucket.

## when to use Object Storage?

Object storage systems allow retention of massive amounts of unstructured data.
Using a REST API and custom metadata is allows easy and convinient access from remote locations. 
Accessing data over network is often slower than local files, another drawback of object storage is that data consistency is achieved only eventually. Whenever you update a file, you may have to wait until the change is propagated to all of the replicas before requests will return the latest version. This makes object storage unsuitable for data that changes frequently. But it's a fit for unstructured data that doesn't change much, like backups, archives, video and audio files, and virtual machine images.

|          | Object Storage      | File based      | Block             |
| -------- | ------------------- |:---------------:| -----------------:|
| items    | object              | files           |            blocks |
| protocol | http/rest           | CIFS, NFS       |        SATA, SCSI |
| metadata | custom              | fixed file attr | fixed system attr |
| best for | scale, unstructured | files           |  high performance |

## Amazon S3

Amazon S3 is probably the best known object Storage offering. Normally the other provide offer a comatiple API.
Each Amazon S3 object has data, a key, and metadata. The Object key (or key name) uniquely identifies the object in a bucket. 
There are two kinds of Object Metadata: system metadata and user-defined metadata.

System-Defined Metadata:

For each object stored in a bucket, Amazon S3 maintains a set of system metadata. Amazon S3 processes this system metadata as needed. For example, Amazon S3 maintains object creation date and size metadata and uses this information as part of object management.
Metadata such as object creation date is system controlled where only Amazon S3 can modify the value.
Other system metadata such as the storage class configured for the object and whether the object has server-side encryption enabled are examples of system metadata whose values you contro

User-Defined Metadata:

When uploading an object, you can also assign metadata to the object. You provide this optional information as a name-value (key-value) pair when you send a PUT or POST request to create the object. When uploading objects using the REST API the optional user-defined metadata names must begin with "x-amz-meta-" to distinguish them from other HTTP headers.

http://docs.aws.amazon.com/AmazonS3/latest/dev/UsingMetadata.html

## EMC Elastic Cloud Storage

Dell EMC offers with Elastic Cloud Storage (https://www.dellemc.com/en-us/storage/ecs/index.htm) a powerfull solution whoich is usable for the on premise datacenter. 
ECS supports the most Amazon S3 APIs (https://www.emc.com/techpubs/ecs/ecs_s3_supported_features-1.htm).

## other object storage offerings

Additionally to the S3 API, the OpenStack Swift API is often used. Some object storage systems like Ceph support booth APIs. 

## EMC ECS Rest API 

For this tutorial I will use a free DellEMC testdrive test account https://portal.ecstestdrive.com/.
The same steps can be executed with different credentials with other providers as well.

After registering at you can get the Credentials in the credentials tab on the website.

![](https://github.com/michaelgruczel/object-storage-tutorial/raw/master/image1.PNG "")

They should look like this:

    AWS S3
    Endpoint: https://object.ecstestdrive.com
    Public Endpoint: http://<NAMESPACE>.public.ecstestdrive.com/{bucket_name}/{key_name}
    Access Key: <NAMESPACE>@ecstestdrive.emc.com
    Secret Key1: <SECRET_KEY>
    ...
    ECS Management
    Endpoint: https://portal.ecstestdrive.com
    Replication Group ID: ...
    Namespace: <NAMESPACE>
    Username: <NAMESPACE>-admin
    Password: <MANAGEMENT-PASSWORD>

Login via the ECS management API:

    curl -k https://portal.ecstestdrive.com/login -u "<USERNAME>:<PASSWORD>" -v

you will get a X-SDS-AUTH-TOKEN back, which we will use in our next rest calls:

    GET /login HTTP/1.1
    Host: portal.ecstestdrive.com
    Authorization: Basic ....
    User-Agent: curl/7.51.0
    Accept: */*
    HTTP/1.1 200 OK
    Date: Fri, 04 Aug 2017 07:00:32 GMT
    Content-Type: application/xml
    Content-Length: 113
    X-SDS-AUTH-TOKEN: <X-SDS-AUTH-TOKEN>
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?><loggedIn><user>xxxxx-admin</user></loggedIn>

In the following request you have to use the token <X-SDS-AUTH-TOKEN>:

    curl https://portal.ecstestdrive.com/user/whoami -k -H "X-SDS-AUTH-TOKEN: <X-SDS-AUTH-TOKEN>"

    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
      <user>
        <common_name>xxxx-admin</common_name>
        <distinguished_name></distinguished_name>
        <last_time_password_changed>2017-08-04 06:46:56</last_time_password_changed>
        <namespace>xxxx</namespace>
        <roles><role>NAMESPACE_ADMIN</role></roles>
       </user>

The API for the management contains for example features like:

* Authentication Provider /vdc/admin/authproviders
* Billing /object/billing
* creation of a Base URL that allows existing applications 	/object/baseurl
* provisioning and managing buckets /object/bucket
* API to enable bucket access to be locked /object/bucket/{bucketName}/lock
* create alert events for ConnectEMC /vdc/callhome/
* Capacity /object/capacity
* Namespace /object/namespaces
* User (Object)	/object/users

more API endpoints under http://www.emc.com/techpubs/ecs/ecs_api_object_control_service-1.htm#GUID-5426BCB4-A6BF-47C6-B83B-38854D0C2802

let's log out from the management API

    curl https://portal.ecstestdrive.com/logout -k -H "X-SDS-AUTH-TOKEN: <X-SDS-AUTH-TOKEN>"

A docu how to play with the S3 compatible REST endpoints (http://www.emc.com/techpubs/ecs/ecs_s3_supported_features-1.htm) will come soon (at least that is the plan). We will then create a bucket, add to objects to that bucket and delete them again. 

.... coming soon

## some S3 tools

Some tools like the S3 Browser (http://s3browser.com/), cyberduck (https://cyberduck.io/) or cloudberry (http://www.cloudberrylab.com/) provide UIs to access Amazon S3. It works for ECS as well.

Setup example for S3 browser to use ECS Testdrive:

![](https://github.com/michaelgruczel/object-storage-tutorial/raw/master/image2.PNG "")

![](https://github.com/michaelgruczel/object-storage-tutorial/raw/master/image3.png "")

There are some commandline tools available as well, for example:

* mc https://docs.minio.io/docs/minio-client-quickstart-guide
* S3cmd http://s3tools.org/s3cmd
* amazon cli https://aws.amazon.com/cli/

## Java examples

I will use a simple spring boot app to showcase some functionalty. The app offers:
* list buckets
* add a bucket
* remove a bucket
* upload a file into a bucket
* remove a file in a bucket
* download a file from a bucket

I will use the same app with 2 libs.

### Java and the EMC ECS jdk

One example for an emc lib can be found under java-s3-emc-lib
you have to add your credentials into the java-s3-emc-lib\src\main\resources\application.properties and build and run the app locally via

    # in java-s3-emc-lib folder 
    gradlew bootRun

open the app under http://localhost:8080

More examples under: 

* https://www.emc.com/techpubs/ecs/ecs_s3_supported_features-1.htm
* https://github.com/EMCECS/ecs-samples

### Java and the Amazon S3 jdk

For this sample I will use Minio (https://github.com/minio/minio).
Minio is an open source object storage server compatible with Amazon S3 APIs. 
Download minio for your os or start is as docker container. 

You should be able to start it with 

<PRE>
minio server <PATH-TO-A-DATA-FOLDER-WHICH-CAN-BE-EMPTY>
</PRE>

You should get something like this

<PRE>
Endpoint:  http://169.254.17.141:9000  http://152.62.208.84:9000  http://169.254.11.78:9000  http://10.0.0.1:9000  http://192.168.64.1:9000  http://192.168.11.1:9000  http://192.168.224.1:9000  http://127.0.0.1:9000
AccessKey: BN5J705K05IVP0TWAMGH
SecretKey: LDzL/pPIyHLNUr7HKjoiBxpGLg9k/6sUpQb/rjq8
Region:    us-east-1

Browser Access:
   http://169.254.17.141:9000  http://152.62.208.84:9000  http://169.254.11.78:9000  http://10.0.0.1:9000  http://192.168.64.1:9000  http://192.168.11.1:9000  http://192.168.224.1:9000  http://127.0.0.1:9000

Command-line Access: https://docs.minio.io/docs/minio-client-quickstart-guide
   $ mc.exe config host add myminio http://169.254.17.141:9000 BN5J705K05IVP0TWAMGH LDzL/pPIyHLNUr7HKjoiBxpGLg9k/6sUpQb/rjq8

Object API (Amazon S3 compatible):
   Go:         https://docs.minio.io/docs/golang-client-quickstart-guide
   Java:       https://docs.minio.io/docs/java-client-quickstart-guide
   Python:     https://docs.minio.io/docs/python-client-quickstart-guide
   JavaScript: https://docs.minio.io/docs/javascript-client-quickstart-guide
   .NET:       https://docs.minio.io/docs/dotnet-client-quickstart-guide

</PRE>

open the broser http://127.0.0.1:9000 and enter the creds you see in the shell when starting minio.

We will start again our sample, now using the amazon lib. 
Change the credentials to fit to oyur local minio instance in java-s3-aws-lib\src\main\resources\application.properties

    # in java-s3-aws-lib folder 
    gradlew bootRun

open the app under http://localhost:8080    

![](https://github.com/michaelgruczel/object-storage-tutorial/raw/master/image4.PNG "")

more infos under http://docs.aws.amazon.com/AmazonS3/latest/dev/ListingObjectKeysUsingJava.html and https://docs.minio.io/




