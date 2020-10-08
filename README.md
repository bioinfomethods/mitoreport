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

## Run Reports and Generate Mitoreport

Mitoreport is a standalone CLI application intented to be run by administrators.  The below
command will generate deletions and variants data including writing out the Single Page Application
UI into a `mitoreport` directory ready for distribution.

```bash
java -jar build/libs/mitoreport-0.1-all.jar \
  -sample 15G002035-GM12878K_20pc_10kb_200 \
  -vcf tmp/15G002035.unshifted.contamination.filtering.intermediatefilter.norm.dedup.mito_vep.vcf.gz \
  -ann tmp/mtDNAanalysis_annotations_20170501.csv \
  -freq tmp/mitomap_polymorphisms_coding_and_rna_20201004.tsv \
  tmp/*.bam

# Open generated report in default browser
open mitoreport/index.html
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
