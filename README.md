# AeroDataSync
Application used for synchronizing local data holdings with an offsite location.

## Download the Source
* Minimum requirements:
    * Java Development Kit (v1.8.0 or higher)
    * GIT (v1.7 or higher)
    * Maven (v3.3 or higher)
* Download source
```
# cd /var/local
# git clone https://github.com/carpenlc/AeroDataSync.git
```

## Build the Application
Execute the following Maven command to build the output WAR file.
```
# mvn clean package
```
Deployable EAR file will reside at:
```
~/AeroDataSync/target/AeroDataSync.ear
```
