import groovy.json.JsonOutput

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

import static java.time.LocalDateTime.parse as ldt

Path filePath = Paths.get(args[0])
BasicFileAttributes fileAttr = Files.readAttributes(filePath, BasicFileAttributes)

def jsonData = [
        absolutePath: filePath.toAbsolutePath().toString(),
        fileName    : filePath.fileName.toString(),
        created     : timestampStrToLocal(fileAttr.creationTime().toString()),
        modified    : timestampStrToLocal(fileAttr.lastModifiedTime().toString()),
        accessed    : timestampStrToLocal(fileAttr.lastAccessTime().toString()),
]
String json = JsonOutput.prettyPrint(JsonOutput.toJson(jsonData))

println json

static String timestampStrToLocal(String timestamp) {
    String result = ldt(timestamp, DateTimeFormatter.ISO_DATE_TIME)
            .atOffset(ZoneOffset.of("+10:00"))
            .toLocalDateTime()
            .toString()

    return result
}
