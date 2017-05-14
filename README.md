
   My Drive

   My Drive application allows to manage a transactional file system.This application allows to manage plain text files, applications and links.


   1) create database:

      $ mysql -p -u root

      Enter password: rootroot

      mysql> GRANT ALL PRIVILEGES ON *.* TO ’mydrive’@’localhost’
      IDENTIFIED BY ’mydriv3’ WITH GRANT OPTION;
      
      mysql> CREATE DATABASE drivedb;
      
      musql> \q

   2) clone and build:

      $ git clone https://github.com/tcrosado/myDrive.git

      $ cd myDrive

      $ mvn package

   3) execute: start from scratch with an basic filesystem (only with root user and his files)

      $ mvn exec:java

