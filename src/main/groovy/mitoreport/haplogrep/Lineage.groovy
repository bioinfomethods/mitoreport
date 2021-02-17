package mitoreport.haplogrep

enum Lineage {

    EURASIAN('Eurasian'),
    AFRICAN('Afican'),
    ASIAN('Asian'),
    UNKNOWN('Unknown'),

    private String description

    Lineage(String description) {
        this.description = description
    }
}
