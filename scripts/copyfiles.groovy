import java.nio.file.*

String[] array = ['defaultSettings.js', 'mitoSettings.js', 'deletions.js', 'variants.js', 'metadata.js']

String mitoReportPathName = "mitoreport-15G002035-GM12878K_20pc_10kb_200"

array.each { filename ->
    println "Copying ${filename}"

    Files.copy(
        Paths.get(mitoReportPathName, filename),
        Paths.get("ui", "public", filename),
        StandardCopyOption.REPLACE_EXISTING
    )

//    def src = new File(Paths.get(mitoReportPathName, filename))
//    def dest = new File(Paths.get("ui", "pubilc", filename))
//    dest.write(src.text)


}



