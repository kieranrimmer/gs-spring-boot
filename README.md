
# Obfuscation app

## Basic Setup for Development

### Environmental variables locally

```bash
export GOOGLE_APPLICATION_CREDENTIALS="{path to service account json private key}"
```

### IntelliJ development

#### Setting Env Var for Maven

Go to  `IntelliJ Idea` > `Preferences` > `Build, Execution, Deployment` >  `Build Tools` > `Maven` >  `Runner`...

Then add the Env Var(s) as above manually.

#### Configurations

Add Maven configurations for running and testing the app using the IntelliJ UI.

Configure the working directory to the folder containing `src`, `target` subdirectories.
As of now, this is the `obfuscation` directory.

##### To run in dev mode

`Command line` option in IntelliJ profile:

```
spring-boot:run -Dspring.profiles.active=dev
```

##### To run tests

`Command line` option in IntelliJ profile:

```
test -Dspring.profiles.active=test -DforkMode=never
```

## Containerising and deploying with Docker / GKE

### Preparing

```bash
docker build -t obf-gcp-spring:0.1.1 .
docker run -p 8080:9080 obf-gcp-spring:0.1.1
```

```bash
docker run -p 9080:8080 obf-gcp-spring:0.1.1
```

### Pushing to GCR

```bash
docker tag obf-gcp-spring:0.1.1 asia.gcr.io/${PROJECT_ID}/obf-gcp-spring:0.1.1

docker push asia.gcr.io/${PROJECT_ID}/obf-gcp-spring:0.1.1
```

#### Preparing GKE

Eg: if you have a VPC with an existing subnet and want to do VPC-Native GKE Cluster setup:

```bash
gcloud compute networks subnets update test-kube-fragment \
    --add-secondary-ranges my-pods=10.0.0.0/21,my-services=10.0.8.0/24 \
    --region australia-southeast1


gcloud container clusters create my-cluster-02 \
    --enable-ip-alias --cluster-secondary-range-name=my-pods \
    --services-secondary-range-name=my-services \
    --zone=australia-southeast1-b --network=${NETWORK_NAME} \
    --subnetwork=test-kube-fragment \
    --scopes=cloud-platform
```

Once cluster is in place and you are auth'ed to it (will be in place if you followed above):
    
```bash    
kubectl run obf-gcp-spring --image=asia.gcr.io/${PROJECT_ID}/obf-gcp-spring:0.1.1 --port 8080


kubectl expose deployment obf-gcp-spring --type=LoadBalancer --port 80 --target-port 8080

```

#### Compute Engine Service Account Supporting GKE

Currently, the Service account running this has the following IAM roles:

```bash
Cloud KMS CryptoKey Encrypter/Decrypter
DLP User
Editor
Storage Object Admin
```

Troubleshooting:

```bash
kubectl exec -it ${IMAGE_ID} --container ${CONTAINER_ID} -- /bin/bash

```

On the box

```bash
apt-get update -y && upt-get install -y lsb-release

# Create environment variable for correct distribution
export CLOUD_SDK_REPO="cloud-sdk-$(lsb_release -c -s)"

# Add the Cloud SDK distribution URI as a package source
echo "deb http://packages.cloud.google.com/apt $CLOUD_SDK_REPO main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list

# Import the Google Cloud Platform public key
curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -

# Update the package list and install the Cloud SDK
apt-get update && apt-get -y install google-cloud-sdk
```

## Provenance

This repository is based upon: 

https://github.com/spring-projects/spring-boot

### Licensing

Please see [License.code.txt](./LICENSE.code.txt).


### Original README

Please see [original_README.adoc](./original_README.adoc).
