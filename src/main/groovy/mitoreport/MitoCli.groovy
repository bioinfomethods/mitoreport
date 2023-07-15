package mitoreport

import gngs.Utils
import groovy.util.logging.Slf4j
import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.core.io.ResourceLoader
import jakarta.inject.Inject
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.ParameterException
import picocli.CommandLine.Spec

import static mitoreport.MitoUtils.getManifestInfo

// Set below ENV to configure logging levels
// export LOGGER_LEVELS_IO_MICRONAUT=INFO
// export LOGGER_LEVELS_MITOREPORT=DEBUG
@Slf4j
@Command(name = 'mito-cli',
        description = 'Mito CLI',
        synopsisSubcommandLabel = "COMMAND",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider,
        subcommands = [MitoReportCommand, MitoMapDownloadCommand])
class MitoCli implements Runnable {

    static void main(String[] args) throws Exception {
        Utils.configureSimpleLogging()
        PicocliRunner.run(MitoCli.class, args)
    }

    @Spec
    CommandSpec spec

    @Override
    void run() {
        throw new ParameterException(spec.commandLine(), "Run with --help to see more commands.\n")
    }

    static class VersionProvider implements CommandLine.IVersionProvider {

        @Inject
        ResourceLoader resourceLoader

        @Override
        String[] getVersion() throws Exception {
            Optional<URL> maybeManifestUrl = resourceLoader.getResource('classpath:META-INF/MANIFEST.MF')
            String[] result = getManifestInfo(maybeManifestUrl).collect { k, v -> "$k: $v" }.toArray()
            return result
        }
    }
}
