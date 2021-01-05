# MitoReport

[![pipeline status](http://git.mcri.edu.au/simon.sadedin/mitoreport/badges/master/pipeline.svg)](http://git.mcri.edu.au/simon.sadedin/mitoreport/-/commits/master)
[![coverage report](http://git.mcri.edu.au/simon.sadedin/mitoreport/badges/master/coverage.svg)](http://git.mcri.edu.au/simon.sadedin/mitoreport/-/commits/master)

Mitoreport is an application for Mitochondrial DNA variants analysis.

## Prerequisites

* Git installed
* Java 8 installed, see [SdkMan](https://sdkman.io/) for managing different Java versions.

## Getting Started

Clone submodules:

```bash
git submodule init
git submodule update --init --recursive
```

Run tests, linting and coverage.  Detailed coverage reports can be found in `$PROJECT_DIR/ui/coverage/`

```bash
./gradlew check
```

Create project output artifacts in `$PROJECT_DIR/build/libs/`

```bash
./gradlew assemble
```

Run build, i.e. everything

```bash
./gradlew
```

## Context and Architecture

```plantuml "mitoreport"
@startuml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

!define DEVICONS https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/devicons
!define FONTAWESOME https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/font-awesome-5

!include FONTAWESOME/users.puml

LAYOUT_TOP_DOWN()
' LAYOUT_AS_SKETCH()
LAYOUT_WITH_LEGEND()


Person(curators, "Curators", "", "users")
Person(admins, "Administrators", "", "users")

System_Boundary(c1, "mitoreport"){

    Container(ui, "Browser Application (SPA)", "Vue.js", "Allows curators to analyse and interpret mitochondria variants")

    Container(mitoreport, "CLI Application", "Java, Groovy, Micronaut", "Annotates VCF with external data sources and generates variants and deletions data")

    ContainerDb(browser_storage, "Browser Storage", "Stores all user settings including tags and curations")

}

System_Ext(gnomad, "Gnomad", "https://gnomad.broadinstitute.org")
System_Ext(mitomap, "MitoMap", "https://mitomap.org/")
System_Ext(mito_pipeline, "MitoPipeline", "/group/bioi1/simons/broad/mito/pipeline")
System_Ext(igv, "IGV", "pre-installed and running on users' computer")

Rel(curators, ui, "Opens/Uses", "HTTP local")
Rel(admins, mito_pipeline, "Runs", "cli")
Rel(mitoreport, ui, "Generates standalone browser UI report application", "filesystem")
Rel(mito_pipeline, mitoreport, "Uses mitoreport", "bpipe")
Rel(gnomad, mitoreport, "Downloads", "HTTP")
Rel(mitomap, mitoreport, "Downloads", "HTTP")
Rel(ui, igv, "Opens", "HTTP Link")

BiRel(ui, browser_storage, "Reads from and writes to", "localhost")

@enduml
```

Above diagram generated using [C4 model](https://c4model.com/).  Also see [C4-PlantUML](https://github.com/plantuml-stdlib/C4-PlantUML) on
how PlantUML can be used to document architectures using C4.

## Using mito-cli

Mitoreport is a standalone CLI application intended to be run by administrators.

To download annotations from MitoMap (this is required to run the actual report).

```bash
java -jar build/libs/mitoreport-0.1-all.jar mito-map-download \
  --output tmp/mito_map_annotations_20201013.json
```

Below example commands will generate deletions and variants data including writing out the Single Page Application
UI into a `mitoreport` directory ready for distribution.

```bash
java -jar build/libs/mitoreport-0.1-all.jar mito-report \
  -sample "15G002035-GM12878K_20pc_10kb_200" \
  -vcf tmp/variants/15G002035.unshifted.contamination.filtering.intermediatefilter.norm.dedup.mito_vep.vcf.gz \
  -ann tmp/mtDNAanalysis_annotations_20170501.csv \
  -mann tmp/mito_map_annotations_20201013.json \
  -gnomad tmp/gnomad.genomes.v3.1.sites.chrM.vcf.bgz \
  "tmp/align/15G002035-GM12878K_20pc_10kb_200.unshifted.bam" tmp/controls/*.bam

SAMPLE="VCGS_FAM1_1"
java -jar build/libs/mitoreport-0.1-all.jar mito-report \
  -sample "$SAMPLE" \
  -vcf "tmp/variants/$SAMPLE.unshifted.contamination.filtering.intermediatefilter.norm.dedup.mito_vep.vcf.gz" \
  -ann tmp/mtDNAanalysis_annotations_20170501.csv \
  -mann tmp/mito_map_annotations_20201013.json \
  -gnomad tmp/gnomad.genomes.v3.1.sites.chrM.vcf.bgz \
  "tmp/align/$SAMPLE.coverage.bam" tmp/controls/*.bam

# Run report for mother
SAMPLE="VCGS_FAM1_3"
java -jar build/libs/mitoreport-0.1-all.jar mito-report \
  -sample "$SAMPLE" \
  -vcf "tmp/variants/$SAMPLE.unshifted.contamination.filtering.intermediatefilter.norm.dedup.mito_vep.vcf.gz" \
  -ann tmp/mtDNAanalysis_annotations_20170501.csv \
  -mann tmp/mito_map_annotations_20201013.json \
  -gnomad tmp/gnomad.genomes.v3.1.sites.chrM.vcf.bgz \
  "tmp/align/$SAMPLE.coverage.bam" tmp/controls/*.bam
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
