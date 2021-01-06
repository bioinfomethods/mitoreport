# MitoReport

[![pipeline status](http://git.mcri.edu.au/simon.sadedin/mitoreport/badges/master/pipeline.svg)](http://git.mcri.edu.au/simon.sadedin/mitoreport/-/commits/master)
[![coverage report](http://git.mcri.edu.au/simon.sadedin/mitoreport/badges/master/coverage.svg)](http://git.mcri.edu.au/simon.sadedin/mitoreport/-/commits/master)

Mitoreport is an application for Mitochondrial DNA variants analysis.

## Prerequisites

* Java 8 installed, see [SdkMan](https://sdkman.io/) for managing different Java versions.
* Access to MCRI filesystem group `bioi1.dl`.  If not, please submit request to [MCRI ServiceDesk](https://servicedesk.mcri.edu.au/).
* Access to `bio1` filesystem, preferably mounted locally.  Will refer to this mount as `MCRI_BIO1_MNT`.

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

Above diagram generated using [C4 model](https://c4model.com/).  Also see [C4-PlantUML](https://github.com/plantuml-stdlib/C4-PlantUML) on
how PlantUML can be used to document architectures using C4.  Unfortunately, MCRI Gitlab is probably on an old version and is not
rendering the C4 Markdown properly.  The image shown above is a screenshot taken from the Markdown Preview using
[VSCode PlantUML extension](https://marketplace.visualstudio.com/items?itemName=jebbs.plantuml).

## Using mito-cli

### Running the Report

Mitoreport is a standalone CLI application intended to be run by administrators.
A set of input files are required to run the report.  A prepared test version of
these files can be found at `/group/bioi1/simons/broad/mito/test_fixtures.tgz`.
Copy this to your project dir as below.

```bash
cp $MCRI_BIO1_MNT/simons/broad/mito/test_fixtures.tgz $PROJECT_DIR/

tar -zxvf $PROJECT_DIR/test_fixtures.tgz -C $PROJECT_DIR
```

Below example commands will generate deletions and variants data including writing out the Single Page Application
UI into a `mitoreport` directory ready for distribution.

```bash
java -jar $PROJECT_DIR/build/libs/mitoreport-0.1-all.jar mito-report \
  -sample "15G002035-GM12878K_20pc_10kb_200" \
  -vcf $PROJECT_DIR/test_fixtures/variants/15G002035.unshifted.contamination.filtering.intermediatefilter.norm.dedup.mito_vep.vcf.gz \
  -ann $PROJECT_DIR/test_fixtures/mtDNAanalysis_annotations_20170501.csv \
  -mann $PROJECT_DIR/test_fixtures/mito_map_annotations_20201207.json \
  -gnomad $PROJECT_DIR/test_fixtures/gnomad.genomes.v3.1.sites.chrM.vcf.bgz \
  "$PROJECT_DIR/test_fixtures/align/15G002035-GM12878K_20pc_10kb_200.unshifted.bam" $PROJECT_DIR/test_fixtures/controls/*.bam
```

A new directory `mitoreport-15G002035-GM12878K_20pc_10kb_200` should now be created.  Open `index.html`
to run this interactive report.

```bash
open $PROJECT_DIR/mitoreport-15G002035-GM12878K_20pc_10kb_200/index.html
```

### Downloading Annotations from MitoMap

Run this to download new MitoMap annotations to file.  The test fixtures data above
already includes this so it shouldn't be necessary unless you want a new version.

```bash
java -jar build/libs/mitoreport-0.1-all.jar mito-map-download \
  --output tmp/mito_map_annotations_20210106.json
```

## UI Development

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
npm run test:unit --verbose -- --watch
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
