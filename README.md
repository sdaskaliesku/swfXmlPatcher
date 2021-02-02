# XML patcher

Reads config file and patches specified XML file

## Usage

1. `Patch` mode:

Reads input XML file & json config file, populates new values in XML file and writes output to the specified file

`mvnw.cmd clean package exec:java -Dexec.args="-m patch -in <path to input xml> -config <path to config json> -out <path to output file>"`

2. `Config` mode:

`mvnw.cmd clean package exec:java -Dexec.args="-m config -orig <path to original XML file> -config <path to output json config file> -patched <path to modified XML file>"`

Compares two files and creates a config file, that can be used in `Patch` mode.
Some fields can be ignored (TODO: move config to a separate file, right now ignores just `fileOffset`)

If application was already built, run command can be shorted just to `mvnw.cmd exec:java -Dexec.args="<args>"`