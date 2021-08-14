# MitoReport

Mitoreport is an application for Mitochondrial DNA variants analysis.
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

```bash
tar -zxvf $PROJECT_DIR/test_fixtures.tgz -C $PROJECT_DIR

Mitoreport is a standalone CLI application intended to be run by administrators.
A set of input files are required to run the report.  A prepared version of
these files can be found in the resources directory.


```bash
tar -zxvf $resoures/resources.tgz 
```

Below example commands will generate deletions and variants data including writing out the Single Page Application UI
into a `mitoreport` directory ready for distribution.

```bash
PROJECT_DIR=$(pwd)  # project root of mitoreport checkout
APP_ARCHIVE_VERSION=$(git describe --abbrev=0)

java -jar $PROJECT_DIR/build/libs/mitoreport-v00.01.00-all.jar mito-report \
  -sample test-sample \
  -vcf test.vcf \
  -ann $PROJECT_DIR/resources/mtDNAanalysis_annotations_20170501.csv \
  -mann $PROJECT_DIR/resources/mito_map_annotations_20201207.json \
  -gnomad $PROJECT_DIR/resources/gnomad.genomes.v3.1.sites.chrM.vcf.bgz \
  test.bam $PROJECT_DIR/resources/controls/*.bam
```

A new directory `mitoreport-test-sample` should now be created.  Open `index.html` to run this
interactive report.

```bash
open $PROJECT_DIR/test-sample/index.html
```
### Downloading Annotations from MitoMap

Run this to download new MitoMap annotations to file.  The test fixtures data above
already includes this so it shouldn't be necessary unless you want a new version.

```bash
java -jar build/libs/mitoreport-*-all.jar mito-map-download \
  --output resources/mito_map_annotations.json
```
