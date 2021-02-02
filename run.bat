mvnw.cmd clean package exec:java -Dexec.args="-m patch -in \"src/test/resources/original seventysixmenu swf.xml\" -config src\test\resources\config.json -out src\test\resources\patched.xml
:: Comment out first line and uncomment next one, if you already run this program
:: mvnw.cmd exec:java -Dexec.args="-i src\test\resources\config.json"