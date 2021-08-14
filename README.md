# MitoReport

Mitoreport is an application for Mitochondrial DNA variants analysis.

## Using mito-cli

Download the release jar:

```bash
wget 'https://github.com/bioinfomethods/mitoreport/releases/download/1.0.0-beta-1/mitoreport-1.0.0-beta-1-all.jar'
```

### Download Fixed Resources

To run, MitoReport needs some resources to annotate variant population frequencies and
calibrate and normalise its deletion plots.

We provide some basic default usable sets for these in a resources file that you can download:

```bash
wget 'https://bioinfomethods.github.io/mitoreport/resources/resources.tgz'
tar -zxvf resources.tgz 
```

Note that these contain control sample bam files that were generated from 1000 genomes 
WGS samples. If you would like to run MitoReport on other types of data (for example,
exome data or dedicated assays) then you may wish to replace these with 
BAM files that are derived from your own data sets.


### Downloading Annotations from MitoMap

Before running MitoReport, run this to download new MitoMap annotations to file: 

```bash
mkdir resources
java -jar mitoreport-1.0.0-beta-1-all.jar mito-map-download \
  --output resources/mito_map_annotations.json
```

### Running the Report

Mitoreport is a standalone CLI application that can be run on any computer
with at least 8gb of RAM and having java installed.

Below example commands will generate deletions and variants data including writing out the 
report into a `mitoreport` directory:

```bash
java -jar mitoreport-1.0.0-beta-1-all.jar mito-report \
  -sample test-sample \
  -mann ./resources/mito_map_annotations.json \
  -gnomad ./resources/gnomad.genomes.v3.1.sites.chrM.vcf.bgz \
  -vcf test-sample.vcf \
  test-sample.bam ./resources/controls/*.bam
```

A new directory `mitoreport-test-sample` should now be created.  Open `index.html` to run this
interactive report.

```bash
open ./test-sample/index.html
```


