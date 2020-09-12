# MitoReport

## Getting Started

Clone submodules:

```bash
git submodule init
git submodule update --init --recursive
```

Run tests

```
./gradlew test
```

Run build

```
./gradlew build
```

Run everything

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

```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Run your unit tests
```
npm run test:unit
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).

## Notes

Example IGV Links:

* http://localhost:60151/load?file=http://www.broadinstitute.org/igvdata/exampleFiles/gbm_session.xml&merge=true
* http://localhost:60151/load?file=file:///Users/tommyli/Development/mcri/mitoreport/resources/15G002035-GM12878K_20pc_10kb_200.unshifted.bam&genome=NC_012920&locus=chrM:9500-10500
* http://localhost:60151/load?file=/Volumes/Root/Users/tommyli/Development/mcri/mitoreport/resources/15G002035-GM12878K_20pc_10kb_200.unshifted.bam&genome=NC_012920&locus=chrM:200-16300
