# MitoReport

## Build Back End

Clone submodules:

```
git submodule init
git submodule update --init --recursive
```

Build submodules:

```
cd gngs
./gradlew jar
```

## Run DeletionPlot to Create Deletion Data

```
./gngs/bin/gngstool DeletionPlot -covo cov.tsv -region chrM:200-16300 -sample sample1-GM12878K_20pc_10kb_200 -covplot cov.png -srplot sr.png -json deletion.json sample1.bam control1.bam control2.bam control3.bam
```

## Run Report Command to Create Variant Data

NOTE: need mito resource file which is not publicly available:

```
groovy -cp gngs/build/libs/groovy-ngs-utils.jar src/main/groovy/Report.groovy \
           -vcf sample1.vcf \
           -del deletion.json \
           -ann resources/mtDNAanalysis_annotations_20170501.csv \
           -o public
```


## Install / Build Front End

In ./ui path.

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
