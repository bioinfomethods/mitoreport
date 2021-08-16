# MitoReport

Mitoreport is an application for Mitochondrial DNA variants analysis.

## Setup

Download the release jar:

```bash
wget 'https://github.com/bioinfomethods/mitoreport/releases/download/1.0.0-beta-1/mitoreport-1.0.0-beta-1-all.jar'
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
java -jar mitoreport-1.0.0-beta-1-all.jar mito-map-download \
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
java -jar mitoreport-1.0.0-beta-1-all.jar mito-report \
    -sample MITOREPORT-TEST-SAMPLE \
    -mann ./resources/mito_map_annotations.json \
    -gnomad ./resources/gnomad.genomes.v3.1.sites.chrM.vcf.bgz \
    -vcf resources/test-sample/mitoreport-test-sample.vep.vcf.gz \
    resources/test-sample/mitoreport-test-sample.bam ./resources/controls/*.bam
```

A new directory `mitoreport-MITOREPORT-TEST-SAMPLE` should now be created.  Open `index.html` to run this interactive
report. For example, on MacOS use:

```bash
open ./mitoreport-MITOREPORT-TEST-SAMPLE/index.html
```

You can find an online example of this output here:

[https://bioinfomethods.github.io/mitoreport/examples/mitoreport-MITOREPORT-TEST-SAMPLE/index.html](https://bioinfomethods.github.io/mitoreport/examples/mitoreport-MITOREPORT-TEST-SAMPLE/index.html)

## Credits

MitoReport is developed by the Bioinformatics Methods group at the Murdoch Childrens Research Institute.

MitoReport has benefited from expertise, advice and input provided by many contributors at the Murdoch Children's
Research Institute (MCRI) and the Victorian Clinical Genetics Services (VCGS). In particular we acknowledge the VCGS
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
