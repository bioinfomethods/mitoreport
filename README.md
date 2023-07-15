# MitoReport

![Unit Tests](https://github.com/bioinfomethods/mitoreport/actions/workflows/main.yml/badge.svg)

Mitoreport is an application for Mitochondrial DNA variants analysis.

## Prerequisites

* Java 11 installed, see [SdkMan](https://sdkman.io/) for installing and managing different Java versions.

## Setup

Download the release jar:

```bash
wget 'https://github.com/bioinfomethods/mitoreport/releases/download/1.0.0-beta-3/mitoreport-1.0.0-beta-3-all.jar'
```

### Download Fixed Resources

To run, MitoReport needs some resources to annotate variant population frequencies and calibrate and normalise its
deletion plots.

We provide some basic default usable sets for these in a resources file that you can download:

```bash
wget 'https://bioinfomethods.github.io/mitoreport/resources/resources.tgz'
tar -zxvf resources.tgz 
```

Note that these contain control sample bam files that were generated from 1000 genomes WGS samples. If you would like to
run MitoReport on other types of data (for example, exome data or dedicated assays) then you may wish to replace these
with BAM files that are derived from your own data sets.

### Downloading Annotations from MitoMap

Before running MitoReport, run this to download new MitoMap annotations to file: 

```bash
mkdir -p resources
java -jar mitoreport-1.0.0-beta-3-all.jar mito-map-download \
  --output resources/mito_map_annotations.json
```

## Creating Reports

Mitoreport is a standalone CLI application that can be run on any computer with at least 8gb of RAM and having java
installed.

To run MitoReport, you need two inputs:

- a VEP annotated VCF containing mitochondrial variants
  - if you have a VCF without VEP annotations and you do not have access  
    to an annotation pipeline, you may use the [online VEP interface](https://asia.ensembl.org/Tools/VEP) to annotate
    your VCF.
- a BAM file containing reads over the mitochondrial chromosome

A test sample is provided which shows the needed format for these files in the resource file.

Below example commands will generate deletions and variants data including writing out the report into a directory for
the test sample:

```bash
java -jar mitoreport-1.0.0-beta-3-all.jar mito-report \
    -vcf resources/test-sample/mitoreport-test-sample.vep.vcf.gz \
    -sample MITOREPORT-TEST-SAMPLE \
    -mann resources/mito_map_annotations.json \
    -gnomad resources/gnomad.genomes.v3.1.sites.chrM.vcf.bgz \
    --maternal-vcf resources/test-sample/mitoreport-test-sample.vep.vcf.gz \
    --maternal-sample MITOREPORT-TEST-SAMPLE \
    resources/test-sample/mitoreport-test-sample.bam ./resources/controls/*.bam
```

**Note: The `--maternal-vcf` using the same VCF as proband is for demonstration purposes only.  The results from this
would mean the Maternal Heteroplasmy column will show the same results as proband.**

A new directory `mitoreport-MITOREPORT-TEST-SAMPLE` should now be created.  Open `index.html` to run this interactive
report. For example, on MacOS use:

```bash
open ./mitoreport-MITOREPORT-TEST-SAMPLE/index.html
```

You can find an online example of this output here:

[https://bioinfomethods.github.io/mitoreport/examples/mitoreport-MITOREPORT-TEST-SAMPLE/index.html](https://bioinfomethods.github.io/mitoreport/examples/mitoreport-MITOREPORT-TEST-SAMPLE/index.html)

## CouchDB and PouchDB (feature in alpha)

### Additional Prerequisites

* [Docker](https://www.docker.com/products/docker-desktop/) installed

This Git repo also includes a docker-compose.yml file where you can start up a CouchDB instance.  This can serve
as a persistent store for Mitoreport where curation notes and tags can be saved persisted.

**Note: This feature is in alpha and is not meant for production use.**
**Note: Security of CouchDB and PouchDB is fairly basic and should be reviewed before use.**

### Getting Started

Override default username and password by setting ENV vars COUCHDB_USER and COUCHDB_PASSWORD before starting up
docker compose.

```bash
docker compose up -d

# Open CouchDB admin page
open http://localhost:5984/_utils/
```

By default, this feature is off.  To switch it on, provide `syncFeature=true` as a query parameter in the URL of the
mitoreport page.  For example:

```bash
# No sync, this is default
file:///tmp/resources/mcri/mitoreport/mitoreport-MITOREPORT-TEST-SAMPLE/index.html#/variants

# With sync, make sure you force refresh the browser
file:///tmp/resources/mcri/mitoreport/mitoreport-MITOREPORT-TEST-SAMPLE/index.html#/variants?syncFeature=true
```

After enabling feature the Mitoreport settings page (top-right cogwheel icon) will show a new CouchDB settings section
where you can enter username and password with the above `COUCHDB_USER` and `COUCHDB_PASSWORD` values.
```bash

## Credits

MitoReport is developed by the Bioinformatics Methods group at the Murdoch Childrens Research Institute.

MitoReport has benefited from expertise, advice and input provided by many contributors at the Murdoch Children's
Research Institute (MCRI) and the Victorian Clinical Genetics Services (VCGS). In particular, we acknowledge the VCGS
Clinical Genomics and Targeted Genomics Diagnostics (TGD) groups for their insight into the process for clinical
interpretation and processes for validation of mitochondrial testing. We further acknowledge the VCGS Clinical
Bioinformatics Unit and Miriam Fanjul Fernandez for the initial inspiration and groundwork that provided the basis for
MitoReport. Finally, we acknowledge the Brain and Mitochondrial Research Group at MCRI who provided us with significant
insight and advice into complex issues in mitochondrial variant interpretation.

We thank the Australian Genomics Health Alliance for allowing access to data that to test and validate the operation of
MitoReport, as well as the patients and families who allowed their genomic data to be used for research, making tools
like MitoReport possible.

Finally, we want to acknowledge the many broader groups who are working to make resources available that are used in
MitoReport. These include:

- [MitoMap](https://mitomap.org/MITOMAP)
- [gnomAD](https://gnomad.broadinstitute.org/about)
- [Haplogrep](https://github.com/seppinho/haplogrep-cmd)
