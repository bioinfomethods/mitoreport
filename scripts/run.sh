#!/usr/bin/env sh

PROJECT_DIR=$(pwd)
TODAY=$(date +"%Y%m%d")
APP_ARCHIVE_VERSION=$(git describe --abbrev=0)

echo "Building mitoreport"
echo "Project directory: $PROJECT_DIR"
echo "Java home: $JAVA_HOME"

java -version

# ./gradlew
# ./gradlew compileJava jar 

# ./gradlew -x check -x copyUiBuildToMitoOut -x buildGngs -x clean -x buildHaplogrepCmd -x test

#./gradlew -x ui

# ./gradlew -x test


# Download MITOMAP Annotations 
# java -jar "${PROJECT_DIR}/build/libs/mitoreport-${APP_ARCHIVE_VERSION}-all.jar" mito-map-download \
#   --output "${PROJECT_DIR}/test_fixtures/mito_map_annotations_${TODAY}.json"



java -jar ${PROJECT_DIR}/build/libs/mitoreport-${APP_ARCHIVE_VERSION}-all.jar mito-report \
  -d -sample "15G002035-GM12878K_20pc_10kb_200" \
  -vcf ${PROJECT_DIR}/test_fixtures/variants/15G002035.unshifted.contamination.filtering.intermediatefilter.norm.dedup.mito_vep.vcf.gz \
  --annotations ${PROJECT_DIR}/test_fixtures/mtDNAanalysis_annotations_20170501.csv \
  --mito-annotations ${PROJECT_DIR}/test_fixtures/mito_map_annotations_20201207.json \
  --gnomad-vcf ${PROJECT_DIR}/test_fixtures/gnomad.genomes.v3.1.sites.chrM.vcf.bgz \
  "${PROJECT_DIR}/test_fixtures/align/15G002035-GM12878K_20pc_10kb_200.unshifted.bam" ${PROJECT_DIR}/test_fixtures/controls/*.bam

#groovy scripts/FileMeta.groovy ${PROJECT_DIR}/test_fixtures/gnomad.genomes.v3.1.sites.chrM.vcf.bgz

