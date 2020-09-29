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
  -region chrM:200-16300 \
  -sample 15G002035-GM12878K_20pc_10kb_200 \
  -vcf tmp/15G002035.unshifted.contamination.filtering.intermediatefilter.norm.dedup.mito_vep.vcf.gz \
  -ann tmp/mtDNAanalysis_annotations_20170501.csv \
  tmp/15G002035-GM12878K_20pc_10kb_200.unshifted.bam \
  tmp/190131_A00692_0007_ML182322_15G002033-GM12877_Man-20180711_NexteraDNAFLEX.unshifted.bam \
  tmp/190131_A00692_0007_ML182323_15G002035-GM12878K_Man-20180711_NexteraDNAFLEX.unshifted.bam \
  tmp/190131_A00692_0007_ML182324_15G002038-GM12879A_Man-20180711_NexteraDNAFLEX.unshifted.bam \
  tmp/190131_A00692_0007_ML182326_16G007898-NA12892_Man-20180711_NexteraDNAFLEX.unshifted.bam
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
