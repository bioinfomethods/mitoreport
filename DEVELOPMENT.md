# MCRI Development

This document provides setup instructions for developing MitoReport.

## Prerequisites

* Java 8 installed, see [SdkMan](https://sdkman.io/) for managing different Java versions.
* Java 8 AdoptOpenJDK "Hotspot" is recommended, e.g. 8.0.275.hs-adpt. j9 is too strict to allow the deprecated functions
  used in the gngs submodule.

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

## UI Development

Make sure you can run the steps in [Running the Report](#running-the-report) above as that'll be the easiest way to
procure test data for UI development.

```bash
cp $PROJECT_DIR/mitoreport-15G002035-GM12878K_20pc_10kb_200/*.js $PROJECT_DIR/ui/public/
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
