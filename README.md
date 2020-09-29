# MitoReport

[![pipeline status](http://git.mcri.edu.au/simon.sadedin/mitoreport/badges/master/pipeline.svg)](http://git.mcri.edu.au/simon.sadedin/mitoreport/-/commits/master)
[![coverage report](http://git.mcri.edu.au/simon.sadedin/mitoreport/badges/master/coverage.svg)](http://git.mcri.edu.au/simon.sadedin/mitoreport/-/commits/master)

## Getting Started

Clone submodules:

```bash
git submodule init
git submodule update --init --recursive
```

Run tests, linting and coverage.  Detailed coverage reports can be found in `$PROJECT_DIR/ui/coverage/`

```
./gradlew check
```

Create project output artifacts in `$PROJECT_DIR/build/libs/`

```
./gradlew assemble
```

Run build, i.e. everything

```
./gradlew
```

## Run Reports and Generate Mitoreport

```bash
java -jar build/libs/mitoreport-0.1-all.jar \
  -sample 15G002035-GM12878K_20pc_10kb_200 \
  -vcf tmp/15G002035.unshifted.contamination.filtering.intermediatefilter.norm.dedup.mito_vep.vcf.gz \
  -ann tmp/mtDNAanalysis_annotations_20170501.csv \
  tmp/*.bam
```

## UI Development Only

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

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
