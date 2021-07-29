# MitoReport

[![pipeline
status](http://git.mcri.edu.au/simon.sadedin/mitoreport/badges/master/pipeline.svg)](http://git.mcri.edu.au/simon.sadedin/mitoreport/-/commits/master)
[![coverage
report](http://git.mcri.edu.au/simon.sadedin/mitoreport/badges/master/coverage.svg)](http://git.mcri.edu.au/simon.sadedin/mitoreport/-/commits/master)

Mitoreport is an application for Mitochondrial DNA variants analysis.

## Prerequisites

* Java 8 installed, see [SdkMan](https://sdkman.io/) for managing different Java versions.
* Java 8 AdoptOpenJDK "Hotspot" is recommended, e.g. 8.0.275.hs-adpt. j9 is too strict to allow the deprecated functions
  used in the gngs submodule.
* Access to MCRI filesystem group `bioi1.dl`.  If not, please submit request to [MCRI
  ServiceDesk](https://servicedesk.mcri.edu.au/).
* Access to `/group/bioi1` filesystem on Meerkat, preferably mounted locally.  Will refer to this mount as
  `MCRI_BIO1_MNT`.

## TL;DR For Devs

```bash
PROJECT_DIR=$(pwd)
APP_ARCHIVE_VERSION=$(git describe --abbrev=0)

./gradlew

java -jar "$PROJECT_DIR/build/libs/mitoreport-$APP_ARCHIVE_VERSION-all.jar" mito-report \
  -d \
  -sample "15G002035-GM12878K_20pc_10kb_200" \
  -vcf "$PROJECT_DIR/test_fixtures/variants/15G002035.unshifted.contamination.filtering.intermediatefilter.norm.dedup.mito_vep.vcf.gz" \
  -mann "$PROJECT_DIR/test_fixtures/mito_map_annotations_20210721.json" \
  -gnomad "$PROJECT_DIR/test_fixtures/gnomad.genomes.v3.1.sites.chrM.vcf.bgz" \
  "$PROJECT_DIR/test_fixtures/align/15G002035-GM12878K_20pc_10kb_200.unshifted.bam" $PROJECT_DIR/test_fixtures/controls/*.bam
```

## Getting Started

```bash
# Git clone repo and cd into it then set PROJECT_DIR env
PROJECT_DIR=$(pwd)

# Clone submodules
git submodule init
git submodule update --init --recursive

# Run tests, linting and coverage.  Detailed coverage reports can be found in `$PROJECT_DIR/ui/coverage/`
./gradlew check

# Create project output artifacts in `$PROJECT_DIR/build/libs/`
./gradlew assemble

# Run build, i.e. everything
./gradlew
```

## Context and Architecture

```plantuml
@startuml

!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

LAYOUT_TOP_DOWN()
LAYOUT_WITH_LEGEND()

Person(curators, "Curators")
Person(admins, "Administrators")

System_Boundary(c1, "mitoreport"){

    Container(ui, "Browser Application (SPA)", "Vue.js", "Allows curators to analyse and interpret mitochondria variants")

    Container(mitoreport, "CLI Application", "Java, Groovy, Micronaut", "Annotates VCF with external data sources and generates variants and deletions data")

    ContainerDb(browser_storage, "Browser Storage", "Stores all user settings including tags and curations")

    ContainerDb(local_fs, "Local Filesystem", "Stores all user settings including tags and curations")

}

System_Ext(gnomad, "Gnomad", "https://gnomad.broadinstitute.org")
System_Ext(mitomap, "MitoMap", "https://mitomap.org")
System_Ext(mito_pipeline, "MitoPipeline", "/group/bioi1/simons/broad/mito/pipeline")
System_Ext(igv, "IGV", "pre-installed and running on users' computer")


Rel(curators, ui, "Opens/Uses", "HTTP local")
Rel(admins, mito_pipeline, "Runs", "cli")
Rel(mitoreport, ui, "Generates standalone browser UI report application", "filesystem")
Rel(mito_pipeline, mitoreport, "Uses mitoreport", "bpipe")
Rel(gnomad, mitoreport, "Downloads", "HTTP")
Rel(mitomap, mitoreport, "Downloads", "HTTP")
Rel(ui, igv, "Opens", "HTTP Link")

BiRel(ui, local_fs, "User triggered, reads from and writes to", "JSON file")
BiRel(ui, browser_storage, "Reads from and writes to", "localhost")

@enduml
```

[![arch-diagram-c1.png](http://git.mcri.edu.au/simon.sadedin/mitoreport/-/wikis/uploads/arch-diagram-c1.png)](http://git.mcri.edu.au/simon.sadedin/mitoreport/-/wikis/uploads/arch-diagram-c1.png)

Above diagram generated using [C4 model](https://c4model.com/).  Also see
[C4-PlantUML](https://github.com/plantuml-stdlib/C4-PlantUML) on how PlantUML can be used to document architectures
using C4.  Unfortunately, MCRI Gitlab is probably on an old version and is not rendering the C4 Markdown properly.  The
image shown above is a screenshot taken from the Markdown Preview using [VSCode PlantUML
extension](https://marketplace.visualstudio.com/items?itemName=jebbs.plantuml).

## Using mito-cli

### Downloading Annotations from MitoMap

Run this to download new MitoMap annotations to file.  The test fixtures data above already includes this so it
shouldn't be necessary unless you want a new version.

```bash
PROJECT_DIR=$(pwd)
TODAY=$(date +"%Y%m%d")
APP_ARCHIVE_VERSION=$(git describe --abbrev=0)

java -jar "$PROJECT_DIR/build/libs/mitoreport-$APP_ARCHIVE_VERSION-all.jar" mito-map-download \
  --output "$PROJECT_DIR/test_fixtures/mito_map_annotations_$TODAY.json"
```

### Running the Report

Mitoreport is a standalone CLI application intended to be run by administrators. A set of input files are required to
run the report.  A prepared test version of these files can be found at
`/hpc/bpipeLibrary/shared/cpipe-wgs/pipeline/mito_pipeline_resources/mitomap/test_fixtures.tgz`. 
Copy this to your project dir and extract as below.

```bash
tar -zxvf $PROJECT_DIR/test_fixtures.tgz -C $PROJECT_DIR
```

Below example commands will generate deletions and variants data including writing out the Single Page Application UI
into a `mitoreport` directory ready for distribution.

```bash
PROJECT_DIR=$(pwd)  # project root of mitoreport checkout
APP_ARCHIVE_VERSION=$(git describe --abbrev=0)

java -jar "$PROJECT_DIR/build/libs/mitoreport-$APP_ARCHIVE_VERSION-all.jar" mito-report \
  -sample "15G002035-GM12878K_20pc_10kb_200" \
  -sample-output "anonymous-sample-id" \
  -vcf $PROJECT_DIR/test_fixtures/variants/15G002035.unshifted.contamination.filtering.intermediatefilter.norm.dedup.mito_vep.vcf.gz \
  -mann $PROJECT_DIR/test_fixtures/mito_map_annotations_20210721.json \
  -gnomad $PROJECT_DIR/test_fixtures/gnomad.genomes.v3.1.sites.chrM.vcf.bgz \
  "$PROJECT_DIR/test_fixtures/align/15G002035-GM12878K_20pc_10kb_200.unshifted.bam" $PROJECT_DIR/test_fixtures/controls/*.bam
```

A new directory `mitoreport-15G002035-GM12878K_20pc_10kb_200` should now be created.  Open `index.html` to run this
interactive report.

```bash
open $PROJECT_DIR/mitoreport-anonymous-sample-id/index.html
```

## UI Development

Make sure you can run the steps in [Running the Report](#running-the-report) above as that'll be the easiest way to
procure test data for UI development.

```bash
cp $PROJECT_DIR/mitoreport-anonymous-sample-id/*.js $PROJECT_DIR/ui/public/
```

In `$PROJECT_DIR/ui`

```bash
npm install
```

### Compiles and hot-reloads for development

```bash
npm run serve
```

### Compiles and minifies for production

```bash
npm run build
```

### Run your unit tests

```bash
npm run test:unit
```

During development, it's often handy to run tests in watch and verbose mode.

```bash
npm run test:unit:watch
```

### Lints and fixes files

```bash
npm run lint
```

Watch for auto fixed linting errors and commit such style changes as a separate commit.

### Customize configuration

See [Configuration Reference](https://cli.vuejs.org/config/).

### Recommended [Visual Studio Code](https://code.visualstudio.com/) Plugins

* [Vetur](https://marketplace.visualstudio.com/items?itemName=octref.vetur)
* [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode)
* [ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint)

## Troubleshooting

### Maven User or Group ID too big error during Gradle task buildHaplogrepCmd()

The error is something like this:

```bash
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-assembly-plugin:2.5.5:single (attachConfig) on project cmr-impl: Execution attachConfig of goal org.apache.maven.plugins:maven-assembly-plugin:2.5.5:single failed: group id '1377585961' is too big ( > 2097151 ). Use STAR or POSIX extensions to overcome this limit -> [Help 1]
org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal org.apache.maven.plugins:maven-assembly-plugin:2.5.5:single (attachConfig) on project cmr-impl: Execution attachConfig of goal org.apache.maven.plugins:maven-assembly-plugin:2.5.5:single failed: group id '1377585961' is too big ( > 2097151 ). Use STAR or POSIX extensions to overcome this limit
    at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:225)
    at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:153)
```

There's a [good workaround from Stack Overflow](https://stackoverflow.com/a/56278471) but this requires changing
`haplogrep-cmd/pom.xml` which is a problem because `haplogrep-cmd` Git submodule is not a fork.

At MCRI, it is suspected that all users' laptops that have their computer user accounts integrated with MCRI AD will be
impacted. This is because the uid will exceed the max value of UID/GUID as described in Stack Overflow post above. This
is also mentioned in [Maven Assembly Plugin
FAQ](http://maven.apache.org/plugins/maven-assembly-plugin/faq.html#tarFileModes)
.

The Gradle task `buildHaplogrepCmd` that runs this Maven build packages up `haplogrep-cmd` tool into a jar called
`haplogrep.jar` (found in `haplogrep-cmd/target/haplogrep.jar`). Unfortunately, this jar cannot be found on any public
maven repos hence this jar is built locally as part of mitoreport build. Since this jar only requires re-packaging when
we want to utilise new features of [haplogrep](https://github.com/seppinho/haplogrep-cmd) from upstream, the
StackOverflow workaround should suffice for now.

### Missing SSL Certificate to MitoMap

When [downloading MitoMap annotations](#downloading-annotations-from-mitomap), you may run into error below:

```text
MitoMapAnnotationsLoader  [1] SEVERE  |3:17:18 Error downloading page https://mitomap.org/foswiki/bin/view/MITOMAP/PolymorphismsCoding
Exception:
javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
  at sun.security.ssl.Alert.createSSLException(Alert.java:131)
...
Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
  at sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:456)
  at sun.security.validator.PKIXValidator.engineValidate(PKIXValidator.java:323)
...
Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
  at sun.security.provider.certpath.SunCertPathBuilder.build(SunCertPathBuilder.java:141)
  at sun.security.provider.certpath.SunCertPathBuilder.engineBuild(SunCertPathBuilder.java:126)
  at java.security.cert.CertPathBuilder.build(CertPathBuilder.java:280)
  at sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:451)
  ... 66 more
Exception in thread "main" picocli.CommandLine$ExecutionException: Error while calling command (mitoreport.MitoMapDownloadCommand@655f7ea): java.lang.RuntimeException: javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
...
Caused by: java.lang.RuntimeException: javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
  at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
  at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
...
Caused by: javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
  at sun.security.ssl.Alert.createSSLException(Alert.java:131)
  at sun.security.ssl.TransportContext.fatal(TransportContext.java:324)
...
Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
  at sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:456)
  at sun.security.validator.PKIXValidator.engineValidate(PKIXValidator.java:323)
...
Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
  at sun.security.provider.certpath.SunCertPathBuilder.build(SunCertPathBuilder.java:141)
  at sun.security.provider.certpath.SunCertPathBuilder.engineBuild(SunCertPathBuilder.java:126)
```

This is because https://mitomap.org uses an SSL certificate that is not recognised by your JVM installation.  To fix
this, download and import `https://mitomap.org` SSL certificate to your JVM installation keystore.

```bash
# Download mitomap.org SSL certificate to file mitomap.org.pem
openssl s_client -showcerts -connect mitomap.org:443 -servername mitomap.org  </dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > mitomap.org.pem

# Import mitomap.org.pem to your Java installation keystore
keytool -importcert -alias mitomap-chain -file mitomap.org.pem -keystore "$JAVA_HOME/jre/lib/security/cacerts" -storepass changeit -noprompt
```
