# ConditionalExpiry Usage

This is an example. The date and expiration logic in the java file needs to be adjusted to meet your needs. 

## What It Does
Creates a custom expiry tasks for MarketPrices region entries using the priceTimstamp field that older than a certain range. These tasks are created when a gemfire cluster is started or new entries are created.

https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire/10-1/gf/developing-expiration-configuring_data_expiration.html

## Compile and Package

### Prequisites
- Appropriate JDK installed to compile and package file
- GemFire installed and $GEMFIRE_HOME set on path.

### Compile and Package
Run the compile-expiry-task.sh script to compile ConditionalExpiry.java and builds custom-expiry-tasks.jar. These files will be in the custom-expiry-tasks/target directory if successful. 

```
./compile-expiry-tasks.sh
```

## How to Use

### 1. Deploy the JAR

```
gfsh> deploy --jar=/path/to/custom-expiry-tasks.jar
```

```
gfsh> alter region --name=MarketPrices --entry-time-to-live-custom-expiry=com.broadcom.expiry.ConditionalExpiry
```

Once applied the Custom Expiry Task will fire off. You do not want to do this on a very large region.


## Monitoring

Check remaining entries:
```
gfsh> query --query="SELECT COUNT(*) FROM /MarketPrices"
```

View logs on each server for detailed progress.