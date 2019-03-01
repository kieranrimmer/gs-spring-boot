
## Basic Setup for Development

### Environmental variables

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
As of now, this is the `complete` directory.

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

## Provenance

This repository is based upon: 

https://github.com/spring-projects/spring-boot

### Licensing

Please see [License.code.txt](./LICENSE.code.txt).


### Original README

Please see [original_README.adoc](./original_README.adoc).
