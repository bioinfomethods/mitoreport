window.defaultSettings = {
    "igvHost": "http://localhost:60151",
    "geneCardsUrlPrefix": "https://www.genecards.org/cgi-bin/carddisp.pl",
    "hmtVarUrlPrefix": "https://www.hmtvar.uniba.it/results",
    "samples": [
        {
            "id": "MITOREPORT-TEST-SAMPLE",
            "metadata": {
                "applicationName": null,
                "version": null,
                "vendor": null,
                "buildTimestamp": null,
                "buildCommit": null,
                "mitoreportVersion": null,
                "gnomadSource": {
                    "absolutePath": "/Volumes/space3/mito/test-mito-report/./resources/gnomad.genomes.v3.1.sites.chrM.vcf.bgz",
                    "fileName": "gnomad.genomes.v3.1.sites.chrM.vcf.bgz",
                    "created": "2021-08-14T01:54:43",
                    "modified": "2021-08-14T01:54:43",
                    "accessed": "2021-08-14T06:42:25"
                },
                "absolutePath": "/Volumes/space3/mito/test-mito-report/./resources/gnomad.genomes.v3.1.sites.chrM.vcf.bgz",
                "fileName": "gnomad.genomes.v3.1.sites.chrM.vcf.bgz",
                "created": "2021-08-14T01:54:43",
                "modified": "2021-08-14T01:54:43",
                "accessed": "2021-08-14T06:42:25"
            },
            "qc": {
                "coverageStats": {
                    "means": {
                        "MITOREPORT-TEST-SAMPLE": 4989.796273291918,
                        "180716_K00164_0158_ML182322_GM12877_MAN-20180711_NEXTERAWGS": 328.09565217391264,
                        "180716_K00164_0158_ML182323_GM12878_MAN-20180711_NEXTERAWGS": 286.9490683229813,
                        "180716_K00164_0158_ML182324_GM12879_MAN-20180711_NEXTERAWGS": 336.99503105590026
                    },
                    "medians": {
                        "MITOREPORT-TEST-SAMPLE": 5024,
                        "180716_K00164_0158_ML182322_GM12877_MAN-20180711_NEXTERAWGS": 329,
                        "180716_K00164_0158_ML182323_GM12878_MAN-20180711_NEXTERAWGS": 289,
                        "180716_K00164_0158_ML182324_GM12879_MAN-20180711_NEXTERAWGS": 337
                    }
                }
            },
            "haplogrepClassification": {
                "sampleId": "MITOREPORT-TEST-SAMPLE",
                "hasMultipleBaseHaplogroups": false,
                "haplogrepResults": {
                    "1": {
                        "baseHaplogroup": "H",
                        "rank": 1,
                        "haplogroup": "H13a1a1a",
                        "quality": 0.9957
                    },
                    "2": {
                        "baseHaplogroup": "H",
                        "rank": 2,
                        "haplogroup": "H13a1a1",
                        "quality": 0.9646
                    },
                    "3": {
                        "baseHaplogroup": "H",
                        "rank": 3,
                        "haplogroup": "H13a1a1c",
                        "quality": 0.9331
                    },
                    "4": {
                        "baseHaplogroup": "H",
                        "rank": 4,
                        "haplogroup": "H13a1a1e",
                        "quality": 0.9319
                    },
                    "5": {
                        "baseHaplogroup": "H",
                        "rank": 5,
                        "haplogroup": "H13a1a1b",
                        "quality": 0.9271
                    }
                }
            },
            "bamDir": "/Volumes/space3/mito/test-mito-report/resources/test-sample/",
            "bamFilename": "mitoreport-test-sample.bam",
            "vcfDir": "/Volumes/space3/mito/test-mito-report/resources/test-sample/",
            "vcfFilename": "mitoreport-test-sample.vep.vcf.gz",
            "maternalVcfDir": null,
            "maternalVcfFilename": null,
            "variantSearches": [
                {
                    "name": "All",
                    "description": "No filters applied",
                    "custom": false,
                    "filterConfig": {
                        "posRange": [
                            0,
                            16569
                        ],
                        "allele": "",
                        "selectedMitoTIP": [
                            
                        ],
                        "selectedTypes": [
                            
                        ],
                        "selectedGenes": [
                            
                        ],
                        "selectedMasks": [
                            
                        ],
                        "selectedConsequences": [
                            
                        ],
                        "gnomADHap": [
                            
                        ],
                        "vafRange": [
                            0,
                            1
                        ],
                        "gbFreqTickIndex": 6,
                        "gnomADHetFreqTickIndex": 8,
                        "gnomADHomFreqTickIndex": 8,
                        "disease": "",
                        "diseaseShowBlank": false,
                        "curationSearch": "",
                        "importantCuration": false,
                        "mitoMap": "",
                        "mitoMapShowBlank": false,
                        "selectedCuratedRefName": "",
                        "hgvsp": "",
                        "hgvspShowBlank": false,
                        "hgvsc": "",
                        "hgvscShowBlank": false,
                        "hgvs": "",
                        "hgvsShowBlank": false
                    }
                }
            ],
            "variantTags": [
                {
                    "name": "Review",
                    "important": true,
                    "custom": false
                },
                {
                    "name": "Excluded",
                    "important": false,
                    "custom": false
                },
                {
                    "name": "FalsePositive",
                    "important": false,
                    "custom": false
                },
                {
                    "name": "Likely",
                    "important": true,
                    "custom": false
                },
                {
                    "name": "Match",
                    "important": true,
                    "custom": false
                },
                {
                    "name": "Mismatch",
                    "important": false,
                    "custom": false
                }
            ],
            "curations": {
                
            }
        }
    ]
}